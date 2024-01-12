/*
 * Copyright (C) 2022-2024 Paranoid Android
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
                    if (Constants.getDevice().equals("phone1") && split.length == 5) { // Phone (1) pattern on Phone (1)
                        for (int i = 0; i < slugs.length; i++) {
                            updateLedZone(slugs[i], split[i]);
                        }
                    } else if (Constants.getDevice().equals("phone2") && split.length == 5) { // Phone (1) pattern on Phone (2)
                        updateLedSingle(12, split[0]);
                        updateLedSingle(0, split[0]);
                        updateLedSingle(24, split[1]);
                        updateLedSingle(2, split[2]);
                        updateLedSingle(3, split[2]);
                        updateLedSingle(4, split[2]);
                        updateLedSingle(5, split[2]);
                        updateLedSingle(6, split[2]);
                        updateLedSingle(7, split[2]);
                        updateLedSingle(8, split[2]);
                        updateLedSingle(14, split[2]);
                        updateLedSingle(15, split[2]);
                        updateLedSingle(16, split[2]);
                        updateLedSingle(17, split[2]);
                        updateLedSingle(18, split[2]);
                        updateLedSingle(19, split[2]);
                        updateLedSingle(20, split[2]);
                        updateLedSingle(26, split[2]);
                        updateLedSingle(27, split[2]);
                        updateLedSingle(28, split[2]);
                        updateLedSingle(29, split[2]);
                        updateLedSingle(30, split[2]);
                        updateLedSingle(31, split[2]);
                        updateLedSingle(23, split[2]);
                        updateLedSingle(9, split[3]);
                        updateLedSingle(21, split[4]);
                        updateLedSingle(33, split[4]);
                        updateLedSingle(10, split[4]);
                        updateLedSingle(22, split[4]);
                        updateLedSingle(34, split[4]);
                        updateLedSingle(11, split[4]);
                        updateLedSingle(23, split[4]);
                        updateLedSingle(35, split[4]);
                    } else if (Constants.getDevice().equals("phone2") && split.length == 33) { // Phone (2) pattern on Phone (2)
                        updateLedSingle(12, split[0]);
                        updateLedSingle(0, split[1]);
                        updateLedSingle(24, split[2]);
                        updateLedSingle(2, split[3]);
                        updateLedSingle(3, split[4]);
                        updateLedSingle(4, split[5]);
                        updateLedSingle(5, split[6]);
                        updateLedSingle(6, split[7]);
                        updateLedSingle(7, split[8]);
                        updateLedSingle(8, split[9]);
                        updateLedSingle(14, split[10]);
                        updateLedSingle(15, split[11]);
                        updateLedSingle(16, split[12]);
                        updateLedSingle(17, split[13]);
                        updateLedSingle(18, split[14]);
                        updateLedSingle(19, split[15]);
                        updateLedSingle(20, split[16]);
                        updateLedSingle(26, split[17]);
                        updateLedSingle(27, split[18]);
                        updateLedSingle(28, split[19]);
                        updateLedSingle(29, split[20]);
                        updateLedSingle(30, split[21]);
                        updateLedSingle(31, split[22]);
                        updateLedSingle(23, split[23]);
                        updateLedSingle(9, split[24]);
                        updateLedSingle(21, split[25]);
                        updateLedSingle(33, split[26]);
                        updateLedSingle(10, split[27]);
                        updateLedSingle(22, split[28]);
                        updateLedSingle(34, split[29]);
                        updateLedSingle(11, split[30]);
                        updateLedSingle(23, split[31]);
                        updateLedSingle(35, split[32]);
                    } else {
                        if (DEBUG) Log.d(TAG, "Animation line length mismatch | name: " + name + " | line: " + line);
                        throw new InterruptedException();
                    }
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                if (DEBUG) Log.d(TAG, "Exception while playing animation | name: " + name + " | exception: " + e);
            } finally {
                if (Constants.getDevice().equals("phone1")) {
                    for (String slug : slugs) {
                        updateLedZone(slug, 0);
                    }
                } else if (Constants.getDevice().equals("phone2")) {
                    for (int led=0; led <= 35; led++) {
                        updateLedSingle(led, 0);
                    }
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
                        if (Constants.getDevice().equals("phone1") && split.length == 5) { // Phone (1) pattern on Phone (1)
                            for (int i = 0; i < slugs.length; i++) {
                                updateLedZone(slugs[i], split[i]);
                            }
                        } else if (Constants.getDevice().equals("phone2") && split.length == 5) { // Phone (1) pattern on Phone (2)
                            updateLedSingle(12, split[0]);
                            updateLedSingle(0, split[0]);
                            updateLedSingle(24, split[1]);
                            updateLedSingle(2, split[2]);
                            updateLedSingle(3, split[2]);
                            updateLedSingle(4, split[2]);
                            updateLedSingle(5, split[2]);
                            updateLedSingle(6, split[2]);
                            updateLedSingle(7, split[2]);
                            updateLedSingle(8, split[2]);
                            updateLedSingle(14, split[2]);
                            updateLedSingle(15, split[2]);
                            updateLedSingle(16, split[2]);
                            updateLedSingle(17, split[2]);
                            updateLedSingle(18, split[2]);
                            updateLedSingle(19, split[2]);
                            updateLedSingle(20, split[2]);
                            updateLedSingle(26, split[2]);
                            updateLedSingle(27, split[2]);
                            updateLedSingle(28, split[2]);
                            updateLedSingle(29, split[2]);
                            updateLedSingle(30, split[2]);
                            updateLedSingle(31, split[2]);
                            updateLedSingle(23, split[2]);
                            updateLedSingle(9, split[3]);
                            updateLedSingle(21, split[4]);
                            updateLedSingle(33, split[4]);
                            updateLedSingle(10, split[4]);
                            updateLedSingle(22, split[4]);
                            updateLedSingle(34, split[4]);
                            updateLedSingle(11, split[4]);
                            updateLedSingle(23, split[4]);
                            updateLedSingle(35, split[4]);
                        } else if (Constants.getDevice().equals("phone2") && split.length == 33) { // Phone (2) pattern on Phone (2)
                            updateLedSingle(12, split[0]);
                            updateLedSingle(0, split[1]);
                            updateLedSingle(24, split[2]);
                            updateLedSingle(2, split[3]);
                            updateLedSingle(3, split[4]);
                            updateLedSingle(4, split[5]);
                            updateLedSingle(5, split[6]);
                            updateLedSingle(6, split[7]);
                            updateLedSingle(7, split[8]);
                            updateLedSingle(8, split[9]);
                            updateLedSingle(14, split[10]);
                            updateLedSingle(15, split[11]);
                            updateLedSingle(16, split[12]);
                            updateLedSingle(17, split[13]);
                            updateLedSingle(18, split[14]);
                            updateLedSingle(19, split[15]);
                            updateLedSingle(20, split[16]);
                            updateLedSingle(26, split[17]);
                            updateLedSingle(27, split[18]);
                            updateLedSingle(28, split[19]);
                            updateLedSingle(29, split[20]);
                            updateLedSingle(30, split[21]);
                            updateLedSingle(31, split[22]);
                            updateLedSingle(23, split[23]);
                            updateLedSingle(9, split[24]);
                            updateLedSingle(21, split[25]);
                            updateLedSingle(33, split[26]);
                            updateLedSingle(10, split[27]);
                            updateLedSingle(22, split[28]);
                            updateLedSingle(34, split[29]);
                            updateLedSingle(11, split[30]);
                            updateLedSingle(23, split[31]);
                            updateLedSingle(35, split[32]);
                        } else {
                            if (DEBUG) Log.d(TAG, "Animation line length mismatch | name: " + name + " | line: " + line);
                            throw new InterruptedException();
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
            if (Constants.getDevice().equals("phone1")) {
                for (String slug : slugs) {
                    updateLedZone(slug, 0);
                }
            } else if (Constants.getDevice().equals("phone2")) {
                for (int led=0; led <= 35; led++) {
                    updateLedSingle(led, 0);
                }
            }
            StatusManager.setCallLedActive(false);
            if (DEBUG) Log.d(TAG, "Done playing animation | name: " + name);
        });
    }

    public static void stopCall() {
        if (DEBUG) Log.d(TAG, "Disabling Call Animation");
        StatusManager.setCallLedEnabled(false);
    }

    public static void playEssential() {
        if (DEBUG) Log.d(TAG, "Playing Essential Animation");
        String slug = ResourceUtils.getString("glyph_settings_notifs_essential_slug");
        int led = ResourceUtils.getInteger("glyph_settings_notifs_essential_led");
        if (!StatusManager.isEssentialLedActive()) {
            submit(() -> {
                if (!check("essential", true))
                    return;
                
                StatusManager.setAnimationActive(true);

                try {
                    if (checkInterruption("charging")) throw new InterruptedException();
                    int[] steps = {1, 2, 4, 7};
                    for (int i : steps) {
                        if (checkInterruption("essential")) throw new InterruptedException();
                        if (Constants.getDevice().equals("phone1")) {
                            updateLedZone(slug, Constants.getMaxBrightness() / 100 * i);
                        } else if (Constants.getDevice().equals("phone2")) {
                            updateLedSingle(led, Constants.getMaxBrightness() / 100 * i);
                        }
                        Thread.sleep(25);
                    }
                    Thread.sleep(250);
                } catch (InterruptedException e) {}

                StatusManager.setAnimationActive(false);
                StatusManager.setEssentialLedActive(true);
                if (DEBUG) Log.d(TAG, "Done playing animation | name: essential");
            });
        } else {
            if (Constants.getDevice().equals("phone1")) {
                updateLedZone(slug, Constants.getMaxBrightness() / 100 * 7);
            } else if (Constants.getDevice().equals("phone2")) {
                updateLedSingle(led, Constants.getMaxBrightness() / 100 * 7);
            }
            return;
        }

    }

    public static void stopEssential() {
        if (DEBUG) Log.d(TAG, "Disabling Essential Animation");
        StatusManager.setEssentialLedActive(false);
        if (!StatusManager.isAnimationActive() && !StatusManager.isAllLedActive()) {
            String slug = ResourceUtils.getString("glyph_settings_notifs_essential_slug");
            int led = ResourceUtils.getInteger("glyph_settings_notifs_essential_led");
            if (Constants.getDevice().equals("phone1")) {
                updateLedZone(slug, 0);
            } else if (Constants.getDevice().equals("phone2")) {
                updateLedSingle(led, 0);
            }
        }
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
                if (Constants.getDevice().equals("phone1")) {
                    updateLedZone(path, Constants.getMaxBrightness());
                } else if (Constants.getDevice().equals("phone2")) {
                    // Not implemented yet for phone2
                }
                Thread.sleep(90);
            } catch (Exception e) {
                if (DEBUG) Log.d(TAG, "Exception while playing animation | name: music: " + name + " | exception: " + e);
            } finally {
                if (Constants.getDevice().equals("phone1")) {
                    updateLedZone(path, 0);
                } else if (Constants.getDevice().equals("phone2")) {
                    // Not implemented yet for phone2
                }
                if (DEBUG) Log.d(TAG, "Done playing animation | name: " + name);
            }
        });
    }

    private static void updateLedSingle(int led, String brightness) {
        updateLedSingle(led, Float.parseFloat(brightness));
    }

    private static void updateLedSingle(int led, int brightness) {
        updateLedSingle(led, (float) brightness);
    }

    private static void updateLedSingle(int led, float brightness) {
        //if (DEBUG) Log.d(TAG, "Updating led | led: " + led + " | brightness: " + brightness);
        float maxBrightness = (float) Constants.getMaxBrightness();
        int essentialLed = ResourceUtils.getInteger("glyph_settings_notifs_essential_led");
        if (StatusManager.isEssentialLedActive()
                && led == essentialLed
                && brightness < (maxBrightness / 100 * 7)) {
            brightness = maxBrightness / 100 * 7;
        }
        FileUtils.writeSingleLed(led, brightness / maxBrightness * Constants.getBrightness());
    }

    private static void updateLedZone(String slug, String brightness) {
        updateLedZone(slug, Float.parseFloat(brightness));
    }

    private static void updateLedZone(String slug, int brightness) {
        updateLedZone(slug, (float) brightness);
    }

    private static void updateLedZone(String slug, float brightness) {
        //if (DEBUG) Log.d(TAG, "Updating slug | slug: " + slug + " | brightness: " + brightness);
        float maxBrightness = (float) Constants.getMaxBrightness();
        String essentialSlug = ResourceUtils.getString("glyph_settings_notifs_essential_slug");
        if (StatusManager.isEssentialLedActive()
                && slug.equals(essentialSlug)
                && brightness < (maxBrightness / 100 * 7)) {
            brightness = maxBrightness / 100 * 7;
        }
        FileUtils.writeLineFromSlug(slug, brightness / maxBrightness * Constants.getBrightness());
    }

}
