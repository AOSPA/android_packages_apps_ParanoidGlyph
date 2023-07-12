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

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.Utils.FileUtils;
import co.aospa.glyph.Utils.ResourceUtils;

public final class AnimationManager {

    private static final String TAG = "GlyphAnimationManager";
    private static final boolean DEBUG = true;

    private static Future<?> submit(Runnable runnable) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        return executorService.submit(runnable);
    }

    private static boolean check(String name, boolean wait) {
        if (DEBUG) Log.d(TAG, "Playing animation | name: " + name + " | waiting: " + Boolean.toString(wait));

        if (StatusManager.isAllLedActive()) {
            if (DEBUG) Log.d(TAG, "All LEDs are active, exiting animation | name: " + name);
            return false;
        }

        if (StatusManager.isCallLedActive()) {
            if (DEBUG) Log.d(TAG, "Call animation is currently active, exiting animation | name: " + name);
            return false;
        }

        if (StatusManager.isAnimationActive() ) {
            long start = System.currentTimeMillis();
            if (name == "volume" && StatusManager.isVolumeLedActive()) {
                if (DEBUG) Log.d(TAG, "There is already a volume animation playing, update");
                StatusManager.setVolumeLedUpdate(true);
                while (StatusManager.isVolumeLedUpdate()) {
                    if (System.currentTimeMillis() - start >= 2500) return false;
                }
            } else if (wait) {
                if (DEBUG) Log.d(TAG, "There is already an animation playing, wait | name: " + name);
                while (StatusManager.isAnimationActive()) {
                    if (System.currentTimeMillis() - start >= 2500) return false;
                }
            } else {
                if (DEBUG) Log.d(TAG, "There is already an animation playing, exiting | name: " + name);
                return false;
            }
        }

        return true;
    }

    private static boolean checkInterruption(String name) {
        if (StatusManager.isAllLedActive()
                || (name != "call" && StatusManager.isCallLedEnabled())
                || (name == "call" && !StatusManager.isCallLedEnabled())
                || (name == "volume" && StatusManager.isVolumeLedUpdate())) {
            return true;
        }
        return false;
    }

    public static void playCsv(String name) {
        playCsv(name, false);
    }

    public static void playCsv(String name, boolean wait) {
        submit(() -> {
            if (!check(name, wait))
                return;

            StatusManager.setAnimationActive(true);

            String[] slugs = ResourceUtils.getStringArray("glyph_settings_animations_slugs");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                    ResourceUtils.getAnimation(name)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (checkInterruption("csv")) throw new InterruptedException();
                    line = line.endsWith(",") ? line.substring(0, line.length() - 1) : line;
                    String[] split = line.split(",");
                    for (int i = 0; i < slugs.length; i++) {
                        FileUtils.writeLineFromSlug(slugs[i], Float.parseFloat(split[i]) / Constants.getMaxBrightness() * Constants.getBrightness());
                    }
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                if (DEBUG) Log.d(TAG, "Exception while playing animation | name: " + name + " | exception: " + e);
            } finally {
                for (String slug : slugs) {
                    FileUtils.writeLineFromSlug(slug, 0);
                }
                StatusManager.setAnimationActive(false);
                if (DEBUG) Log.d(TAG, "Done playing animation | name: " + name);
            }
        });
    }

    public static void playCharging(int batteryLevel, boolean wait) {
        submit(() -> {
            if (!check("charging", wait))
                return;

            StatusManager.setAnimationActive(true);

            int batteryBase = ResourceUtils.getInteger("glyph_settings_battery_dot");
            int[] batteryArrayAll = ResourceUtils.getIntArray("glyph_settings_battery_levels");
            int amount = (int) (Math.floor((batteryLevel / 100.0) * (batteryArrayAll.length - 1)) + 1);
            int[] batteryArray = Arrays.copyOfRange(batteryArrayAll, 0, amount);

            try {
                if (checkInterruption("charging")) throw new InterruptedException();
                FileUtils.writeSingleLed(batteryBase, Constants.getBrightness());
                for (int i : batteryArray) {
                    if (checkInterruption("charging")) throw new InterruptedException();
                    FileUtils.writeSingleLed(i, Constants.getBrightness());
                    Thread.sleep(15);
                }
                Thread.sleep(1000);
                for (int i = batteryArray.length - 1; i >= 0; i--) {
                    if (checkInterruption("charging")) throw new InterruptedException();
                    FileUtils.writeSingleLed(batteryArray[i], 0);
                    Thread.sleep(15);
                }
                FileUtils.writeSingleLed(batteryBase, 0);
                Thread.sleep(730);
            } catch (InterruptedException e) {
                if (DEBUG) Log.d(TAG, "Exception while playing animation, interrupted | name: charging");
                if (!StatusManager.isAllLedActive()) {
                    for (int i : batteryArrayAll) {
                        FileUtils.writeSingleLed(i, 0);
                    }
                    FileUtils.writeSingleLed(batteryBase, 0);
                }
            } finally {
                StatusManager.setAnimationActive(false);
                if (DEBUG) Log.d(TAG, "Done playing animation | name: charging");
            }
        });
    }

    public static void playVolume(int volumeLevel, boolean wait) {
        submit(() -> {
            if (!check("volume", wait))
                return;

            StatusManager.setVolumeLedActive(true);
            StatusManager.setAnimationActive(true);

            int[] volumeArray = ResourceUtils.getIntArray("glyph_settings_volume_levels");
            int amount = (int) (Math.floor((volumeLevel / 100.0) * (volumeArray.length - 1)) + 1);

            try {
                if (checkInterruption("volume")) throw new InterruptedException();
                for (int i = 0; i < volumeArray.length; i++) {
                    if (checkInterruption("volume")) throw new InterruptedException();
                    if ( i <= amount - 1 && volumeLevel > 0) {
                        FileUtils.writeSingleLed(volumeArray[i], Constants.getBrightness());
                    } else {
                        FileUtils.writeSingleLed(volumeArray[i], 0);
                    }
                    Thread.sleep(15);
                }
                long start = System.currentTimeMillis();
                while (System.currentTimeMillis() - start <= 1800) {
                    if (checkInterruption("volume")) throw new InterruptedException();
                }
                for (int i = volumeArray.length - 1; i >= 0; i--) {
                    if (checkInterruption("volume")) throw new InterruptedException();
                    FileUtils.writeSingleLed(volumeArray[i], 0);
                    Thread.sleep(15);
                }
                long start2 = System.currentTimeMillis();
                while (System.currentTimeMillis() - start2 <= 800) {
                    if (checkInterruption("volume")) throw new InterruptedException();
                }
            } catch (InterruptedException e) {
                if (DEBUG) Log.d(TAG, "Exception while playing animation, interrupted | name: volume");
                if (!StatusManager.isAllLedActive() && !StatusManager.isVolumeLedUpdate()) {
                    for (int i : volumeArray) {
                        FileUtils.writeSingleLed(i, 0);
                    }
                }
            } finally {
                if (!StatusManager.isVolumeLedUpdate()) {
                    StatusManager.setAnimationActive(false);
                    StatusManager.setVolumeLedActive(false);
                }
                StatusManager.setVolumeLedUpdate(false);
                if (DEBUG) Log.d(TAG, "Done playing animation | name: volume");
            }
        });
    }

    public static void playCall(String name) {
        submit(() -> {
            StatusManager.setCallLedEnabled(true);

            if (!check("call: " + name, true))
                return;

            StatusManager.setCallLedActive(true);

            String[] slugs = ResourceUtils.getStringArray("glyph_settings_animations_slugs");

            while (StatusManager.isCallLedEnabled()) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        ResourceUtils.getCallAnimation(name)))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (checkInterruption("call")) throw new InterruptedException();
                        line = line.endsWith(",") ? line.substring(0, line.length() - 1) : line;
                        String[] split = line.split(",");
                        for (int i = 0; i < slugs.length; i++) {
                            FileUtils.writeLineFromSlug(slugs[i], Float.parseFloat(split[i]) / Constants.getMaxBrightness() * Constants.getBrightness());
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
            for (String slug : slugs) {
                FileUtils.writeLineFromSlug(slug, 0);
            }
            StatusManager.setCallLedActive(false);
            if (DEBUG) Log.d(TAG, "Done playing animation | name: " + name);
        });
    }

    public static void stopCall() {
        if (DEBUG) Log.d(TAG, "Disabling Call Animation");
        StatusManager.setCallLedEnabled(false);
    }

    public static void playMusic(String name) {
        submit(() -> {
            String path = null;

            switch (name) {
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
                    if (DEBUG) Log.d(TAG, "Name doesn't match any zone, returning | name: " + name);
                    return;
            }

            try {
                FileUtils.writeLineFromSlug(path, Constants.getBrightness());
                Thread.sleep(90);
            } catch (Exception e) {
                if (DEBUG) Log.d(TAG, "Exception while playing animation | name: music: " + name + " | exception: " + e);
            } finally {
                FileUtils.writeLineFromSlug(path, 0);
                if (DEBUG) Log.d(TAG, "Done playing animation | name: " + name);
            }
        });
    }

}
