/*
 * Copyright (C) 2022 Paranoid Android
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
import android.provider.Settings;
import android.util.Log;

import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import co.aospa.glyph.R;
import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.Manager.StatusManager;
import co.aospa.glyph.Utils.FileUtils;

public final class AnimationManager {

    private static final String TAG = "GlyphAnimationManager";
    private static final boolean DEBUG = true;

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

    public static void playCsv(String name, Context context) {
        playCsv(name, false, context);
    }

    public static void playCsv(String name, boolean wait, Context context) {
        submit(() -> {

            if (!check(name, wait))
                return;

            StatusManager.setAnimationActive(true);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    context.getResources().openRawResource(context.getResources().getIdentifier("anim_"+name, "raw", context.getPackageName()))))) {
                if (reader.readLine() == null) throw new Exception();
                while (true) {
                    String line = reader.readLine(); if (line == null) break;
                    if (StatusManager.isCallLedEnabled() || StatusManager.isAllLedActive()) throw new InterruptedException();
                    final String[] split = line.split(",");
                    FileUtils.writeLine(Constants.CAMERARINGLEDPATH, Float.parseFloat(split[0]) / 4095 * Constants.BRIGHTNESS);
                    FileUtils.writeLine(Constants.SLANTLEDPATH, Float.parseFloat(split[1]) / 4095 * Constants.BRIGHTNESS);
                    FileUtils.writeLine(Constants.CENTERRINGLEDPATH, Float.parseFloat(split[2]) / 4095 * Constants.BRIGHTNESS);
                    FileUtils.writeLine(Constants.EXCLAMATIONBARLEDPATH, Float.parseFloat(split[3]) / 4095 * Constants.BRIGHTNESS);
                    FileUtils.writeLine(Constants.EXCLAMATIONDOTLEDPATH, Float.parseFloat(split[4]) / 4095 * Constants.BRIGHTNESS);
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                if (DEBUG) Log.d(TAG, "Exception while playing animation | name: " + name + " | exception: " + e);
            } finally {
                if (DEBUG) Log.d(TAG, "Done playing animation | name: " + name);
                FileUtils.writeLine(Constants.CAMERARINGLEDPATH, 0);
                FileUtils.writeLine(Constants.SLANTLEDPATH, 0);
                FileUtils.writeLine(Constants.CENTERRINGLEDPATH, 0);
                FileUtils.writeLine(Constants.EXCLAMATIONBARLEDPATH, 0);
                FileUtils.writeLine(Constants.EXCLAMATIONDOTLEDPATH, 0);
            }

            StatusManager.setAnimationActive(false);
        });
    }

    public static void playCharging(int batteryLevel, Context context) {
        submit(() -> {
            
            if (!check("charging", true))
                return;

            StatusManager.setAnimationActive(true);

            try {
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
                }
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
            } finally {
                if (DEBUG) Log.d(TAG, "Done playing animation | name: charging");
            }

            StatusManager.setAnimationActive(false);
        });
    }

    public static void playCall(String name, Context context) {
        submit(() -> {

            StatusManager.setCallLedEnabled(true);

            if (!check("call: " + name, true))
                return;

            StatusManager.setCallLedActive(true);

            while (StatusManager.isCallLedEnabled()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        context.getResources().openRawResource(context.getResources().getIdentifier("anim_"+name, "raw", context.getPackageName()))))) {
                    if (reader.readLine() == null) throw new Exception();
                    while (true) {
                        String line = reader.readLine(); if (line == null) break;
                        if (!StatusManager.isCallLedEnabled() || StatusManager.isAllLedActive()) throw new InterruptedException();
                        final String[] split = line.split(",");
                        FileUtils.writeLine(Constants.CAMERARINGLEDPATH, Float.parseFloat(split[0]) / 4095 * Constants.BRIGHTNESS);
                        FileUtils.writeLine(Constants.SLANTLEDPATH, Float.parseFloat(split[1]) / 4095 * Constants.BRIGHTNESS);
                        FileUtils.writeLine(Constants.CENTERRINGLEDPATH, Float.parseFloat(split[2]) / 4095 * Constants.BRIGHTNESS);
                        FileUtils.writeLine(Constants.EXCLAMATIONBARLEDPATH, Float.parseFloat(split[3]) / 4095 * Constants.BRIGHTNESS);
                        FileUtils.writeLine(Constants.EXCLAMATIONDOTLEDPATH, Float.parseFloat(split[4]) / 4095 * Constants.BRIGHTNESS);
                        Thread.sleep(10);
                    }
                } catch (Exception e) {
                    if (DEBUG) Log.d(TAG, "Exception while playing animation | name: " + name + " | exception: " + e);
                } finally {
                    if (StatusManager.isAllLedActive()) {
                        if (DEBUG) Log.d(TAG, "All LED active, pause playing animation | name: " + name);
                        while (StatusManager.isAllLedActive()) {};
                    }
                }
            }

            if (DEBUG) Log.d(TAG, "Done playing animation | name: " + name);
            FileUtils.writeLine(Constants.CAMERARINGLEDPATH, 0);
            FileUtils.writeLine(Constants.CENTERRINGLEDPATH, 0);
            FileUtils.writeLine(Constants.EXCLAMATIONBARLEDPATH, 0);
            FileUtils.writeLine(Constants.EXCLAMATIONDOTLEDPATH, 0);
            FileUtils.writeLine(Constants.SLANTLEDPATH, 0);

            StatusManager.setCallLedActive(false);
        });
    }

    public static void stopCall() {
        if (DEBUG) Log.d(TAG, "Disabling Call Animation");
        StatusManager.setCallLedEnabled(false);
    }


}
