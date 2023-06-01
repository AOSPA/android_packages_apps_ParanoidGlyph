/*
 * Copyright (C) 2022-2023 Paranoid Android
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.aospa.glyph.Manager;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import co.aospa.glyph.R;
import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.Utils.FileUtils;

public final class AnimationManager {

    private static final String TAG = "GlyphAnimationManager";
    private static final boolean DEBUG = Constants.DEBUG;

    private static Context context = Constants.CONTEXT;

    private static Future<?> submit(Runnable runnable) {
        ExecutorService mExecutorService = Executors.newSingleThreadExecutor();
        return mExecutorService.submit(runnable);
    }

    private static boolean check(String name, boolean wait) {
        if (DEBUG) Log.d(TAG, "Playing animation | name: " + name + " | waiting: " + Boolean.toString(wait));

        if (StatusManager.isAllLedActive()) {
            if (DEBUG) Log.d(TAG, "All LEDs are active, exiting animation | name: " + name);
            return false;
        }

        if (StatusManager.isCallLedActive()) {
            if (DEBUG) Log.d(TAG, "Call animation ist currently active, exiting animation | name: " + name);
            return false;
        }

        if (StatusManager.isAnimationActive()) {
            if (wait) {
                if (DEBUG) Log.d(TAG, "There is already an animation playing, wait | name: " + name);
                long start = System.currentTimeMillis();
                while (StatusManager.isAnimationActive()){
                    if (System.currentTimeMillis() - start >= 2500 ) return false;
                }
            } else {
                if (DEBUG) Log.d(TAG, "There is already an animation playing, exiting | name: " + name);
                return false;
            }
        }

        return true;
    }

    public static void playCsv(String name) {
        playCsv(name, false);
    }

    public static void playCsv(String name, boolean wait) {
        submit(() -> {

            if (!check(name, wait))
                return;

            StatusManager.setAnimationActive(true);

            String[] slugs = context.getResources().getStringArray(R.array.glyph_settings_animations_slugs);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    context.getResources().openRawResource(context.getResources().getIdentifier("anim_"+name, "raw", context.getPackageName()))))) {
                while (true) {
                    if (DEBUG) Log.d(TAG, "1");
                    String line = reader.readLine(); if (line == null) break;
                    if (DEBUG) Log.d(TAG, "2");
                    if (StatusManager.isCallLedEnabled() || StatusManager.isAllLedActive()) throw new InterruptedException();
                    if (DEBUG) Log.d(TAG, "3");
                    String[] split = line.split(",");
                    if (DEBUG) Log.d(TAG, "4");
                    for (int i = 0; i< slugs.length; i++){
                        FileUtils.writeLineFromSlug(slugs[i], Float.parseFloat(split[i]) / 4095 * Constants.BRIGHTNESS);
                    }
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                if (DEBUG) Log.d(TAG, "Exception while playing animation | name: " + name + " | exception: " + e);
            }

            if (DEBUG) Log.d(TAG, "Done playing animation | name: " + name);
            for (int i = 0; i< slugs.length; i++){
                FileUtils.writeLineFromSlug(slugs[i], 0);
            }

            StatusManager.setAnimationActive(false);
        });
    }

    public static void playCharging(int batteryLevel, boolean wait) {
        submit(() -> {
            
            if (!check("charging", wait))
                return;

            StatusManager.setAnimationActive(true);

            int[] batteryArray = new int[]{};
            if (batteryLevel == 100 ) {
                batteryArray = new int[]{16, 13, 11, 9, 12, 10, 14, 15, 8};
            } else if (batteryLevel >= 88) {
                batteryArray = new int[]{16, 13, 11, 9, 12, 10, 14, 15};
            } else if (batteryLevel >= 75) {
                batteryArray = new int[]{16, 13, 11, 9, 12, 10, 14};
            } else if (batteryLevel >= 62) {
                batteryArray = new int[]{16, 13, 11, 9, 12, 10};
            } else if (batteryLevel >= 49) {
                batteryArray = new int[]{16, 13, 11, 9, 12};
            } else if (batteryLevel >= 36) {
                batteryArray = new int[]{16, 13, 11, 9};
            } else if (batteryLevel >= 24) {
                batteryArray = new int[]{16, 13, 11};
            } else if (batteryLevel >= 12) {
                batteryArray = new int[]{16, 13};
            } else {
                return;
            }

            try {
                for (int i : batteryArray) {
                    if (StatusManager.isCallLedEnabled() || StatusManager.isAllLedActive()) throw new InterruptedException();
                    FileUtils.writeSingleLed(i, Constants.BRIGHTNESS);
                    Thread.sleep(10);
                }
                Thread.sleep(1000);
                for (int i=batteryArray.length-1; i>=0; i--) {
                    if (StatusManager.isCallLedEnabled() || StatusManager.isAllLedActive()) throw new InterruptedException();
                    FileUtils.writeSingleLed(batteryArray[i], 0);
                    Thread.sleep(10);
                }
                Thread.sleep(730);
            } catch (InterruptedException e) {
                if (DEBUG) Log.d(TAG, "Exception while playing animation, interrupted | name: charging");
                if (!StatusManager.isAllLedActive()) {
                    for (int i : new int[]{8, 15, 14, 10, 12, 9, 11, 13, 16}) {
                        FileUtils.writeSingleLed(i, 0);
                    }
                }
            }

            if (DEBUG) Log.d(TAG, "Done playing animation | name: charging");

            StatusManager.setAnimationActive(false);
        });
    }

    public static void playCall(String name) {
        submit(() -> {

            StatusManager.setCallLedEnabled(true);

            if (!check("call: " + name, true))
                return;

            StatusManager.setCallLedActive(true);

            String[] slugs = context.getResources().getStringArray(R.array.glyph_settings_animations_slugs);

            while (StatusManager.isCallLedEnabled()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        context.getResources().openRawResource(context.getResources().getIdentifier("anim_"+name, "raw", context.getPackageName()))))) {
                    while (true) {
                        String line = reader.readLine(); if (line == null) break;
                        if (!StatusManager.isCallLedEnabled() || StatusManager.isAllLedActive()) throw new InterruptedException();
                        String[] split = line.split(",");
                        for (int i = 0; i< slugs.length; i++){
                            FileUtils.writeLineFromSlug(slugs[i], Float.parseFloat(split[i]) / 4095 * Constants.BRIGHTNESS);
                        }
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    if (DEBUG) Log.d(TAG, "Exception while playing animation | name: " + name + " | exception: " + e);
                } finally {
                    if (StatusManager.isAllLedActive()) {
                        if (DEBUG) Log.d(TAG, "All LED active, pause playing animation | name: " + name);
                        while (StatusManager.isAllLedActive()) {}
                    }
                }
            }

            if (DEBUG) Log.d(TAG, "Done playing animation | name: " + name);
            for (int i = 0; i< slugs.length; i++){
                FileUtils.writeLineFromSlug(slugs[i], 0);
            }

            StatusManager.setCallLedActive(false);
        });
    }

    public static void stopCall() {
        if (DEBUG) Log.d(TAG, "Disabling Call Animation");
        StatusManager.setCallLedEnabled(false);
    }

    public static void playMusic(String name) {
        submit(() -> {

            //if (!check("music_"+name, true))
                //return;

            //StatusManager.setAnimationActive(true);

            String path = null;

            switch(name) {
                case "low":
                    path = "dot";
                    break;
                case "mid_low":
                    path = "bar";
                    break;
                case "mid":
                    path = "center";
                    break;
                case "mid_high":
                    path = "camera";
                    break;
                case "high":
                    path = "slant";
                    break;
                default:
                    if (DEBUG) Log.d(TAG, "Name doesnt match any zone, returning | name: " + name);
                    return;
            }

            try {
                FileUtils.writeLineFromSlug(path, Constants.BRIGHTNESS);
                Thread.sleep(90);
            } catch (Exception e) {
                if (DEBUG) Log.d(TAG, "Exception while playing animation | name: music: " + name + " | exception: " + e);
            } finally {
                if (DEBUG) Log.d(TAG, "Done playing animation | name: " + name);
                FileUtils.writeLineFromSlug(path, 0);
            }

            //StatusManager.setAnimationActive(false);

        });
    }

}
