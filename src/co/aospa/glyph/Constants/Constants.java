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

package co.aospa.glyph.Constants;

public final class Constants {

    private static final String TAG = "GlyphConstants";
    private static final boolean DEBUG = true;

    public static int BRIGHTNESS = 4095;
    public static int BRIGHTNESS_MIN = 0;
    public static int BRIGHTNESS_MAX = 4095;

    public static final String GLYPH_ENABLE = "glyph_enable";
    public static final String GLYPH_FLIP_ENABLE = "glyph_settings_flip_toggle";
    public static final String GLYPH_BRIGHTNESS = "glyph_settings_brightness";
    public static final String GLYPH_CHARGING_CATEGORY = "glyph_settings_charging";
    public static final String GLYPH_CHARGING_LEVEL_ENABLE = "glyph_settings_charging_level";
    public static final String GLYPH_CALL_CATEGORY = "glyph_settings_call";
    public static final String GLYPH_CALL_ENABLE = "glyph_settings_call_toggle";
    public static final String GLYPH_NOTIFS_ENABLE = "glyph_settings_notifs_toggle";
    public static final String GLYPH_NOTIFS_SUB_CATEGORY = "glyph_settings_notifs_sub";
    public static final String GLYPH_NOTIFS_SUB_ENABLE = "glyph_settings_notifs_sub_toggle";

    public static final String BASELEDPATH = "/sys/class/leds/aw210xx_led/";                     // Base LED Path
    public static final String ALLWHITELEDPATH = BASELEDPATH + "all_white_leds_br";              // All LEDs
    public static final String CAMERARINGLEDPATH =  BASELEDPATH + "rear_cam_led_br";             // Camera Ring
    public static final String CENTERRINGLEDPATH =  BASELEDPATH + "round_leds_br";               // Center Ring Section
    public static final String EXCLAMATIONBARLEDPATH =  BASELEDPATH + "vline_leds_br";           // Exclamation Mark Bar
    public static final String EXCLAMATIONDOTLEDPATH =  BASELEDPATH + "dot_led_br";              // Exclamation Mark Dot
    public static final String EXCLAMATIONMARKLEDPATH =  BASELEDPATH + "horse_race_leds_br";     // Exclamation Mark Full
    public static final String SINGLELEDPATH =  BASELEDPATH + "single_led_br";                   // Single LED
    public static final String SLANTLEDPATH =  BASELEDPATH + "front_cam_led_br";                 // Slanting Line

    public static final String[] APPSTOIGNORENOTIFS = {
                                                        "android",
                                                        "com.android.traceur",
                                                        //"com.google.android.dialer",
                                                        "com.google.android.setupwizard",
                                                        "dev.kdrag0n.dyntheme.privileged.sys"
                                                    };
    public static final String[] CHANNELSTOIGNORENOTIFS = {
                                                        "phone_incoming_call",
                                                        "phone_ongoing_call"
                                                    };

    public static void setBrightness(int b) {
        BRIGHTNESS = b;
    }

}
