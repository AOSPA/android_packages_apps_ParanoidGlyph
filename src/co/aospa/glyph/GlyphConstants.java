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

package co.aospa.glyph;

public final class GlyphConstants {

    private static final String TAG = "GlyphConstants";
    private static final boolean DEBUG = true;

    protected static int BRIGHTNESS = 4095;
    protected static int BRIGHTNESS_MIN = 0;
    protected static int BRIGHTNESS_MAX = 4095;

    protected static final String GLYPH_ENABLE = "glyph_enable";
    protected static final String GLYPH_BRIGHTNESS = "glyph_settings_brightness";
    protected static final String GLYPH_CHARGING_CATEGORY = "glyph_settings_charging";
    protected static final String GLYPH_CHARGING_DOT_ENABLE = "glyph_settings_charging_dot";
    protected static final String GLYPH_CHARGING_LEVEL_ENABLE = "glyph_settings_charging_level";
    protected static final String GLYPH_CALL_CATEGORY = "glyph_settings_call";
    protected static final String GLYPH_CALL_ENABLE = "glyph_settings_call_toggle";

    protected static final String BATTERYLEVELPATH = "/sys/class/power_supply/battery/capacity";
    
    protected static final String BASELEDPATH = "/sys/class/leds/aw210xx_led/";                     // Base LED Path
    protected static final String ALLWHITELEDPATH = BASELEDPATH + "all_white_leds_br";              // All LEDs
    protected static final String CAMERARINGLEDPATH =  BASELEDPATH + "rear_cam_led_br";             // Camera Ring
    protected static final String CENTERRINGLEDPATH =  BASELEDPATH + "round_leds_br";               // Center Ring Section
    protected static final String EXCLAMATIONBARLEDPATH =  BASELEDPATH + "vline_leds_br";           // Exclamation Mark Bar
    protected static final String EXCLAMATIONDOTLEDPATH =  BASELEDPATH + "dot_led_br";              // Exclamation Mark Dot
    protected static final String EXCLAMATIONMARKLEDPATH =  BASELEDPATH + "horse_race_leds_br";     // Exclamation Mark Full
    protected static final String SINGLELEDPATH =  BASELEDPATH + "single_led_br";                   // Single LED
    protected static final String SLANTLEDPATH =  BASELEDPATH + "front_cam_led_br";                 // Slanting Line

    protected static void setBrightness(int b) {
        BRIGHTNESS = b;
    }

}
