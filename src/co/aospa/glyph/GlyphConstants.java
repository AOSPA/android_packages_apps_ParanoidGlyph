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

    protected static final String GLYPH_ENABLE = "glyph_enable";
    protected static final String GLYPH_BRIGHTNESS = "glyph_settings_brightness";
    protected static final String GLYPH_CHARGING_CATEGORY = "glyph_settings_charging";
    protected static final String GLYPH_CHARGING_DOT_ENABLE = "glyph_settings_charging_dot";
    protected static final String GLYPH_CHARGING_LEVEL_ENABLE = "glyph_settings_charging_level";
    protected static final String GLYPH_CALL_CATEGORY = "glyph_settings_call";
    protected static final String GLYPH_CALL_ENABLE = "glyph_settings_call_toggle";

    protected static final String ALLWHITELEDSPATH = "/sys/class/leds/aw210xx_led/all_white_leds_br";
    protected static final String BATTERYLEVELPATH = "/sys/class/power_supply/battery/capacity";
    protected static final String SINGLELEDPATH =  "/sys/class/leds/aw210xx_led/single_led_br";

    protected static void setBrightness(int b) {
        BRIGHTNESS = b;
    }

}
