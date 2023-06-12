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

package co.aospa.glyph.Constants;

import android.content.Context;

import co.aospa.glyph.Utils.ResourceUtils;

public final class Constants {

    private static final String TAG = "GlyphConstants";
    private static final boolean DEBUG = true;

    public static Context CONTEXT;

    private static int BRIGHTNESS = -1;
    private static int BRIGHTNESSMAX = -1;
    private static int[] BRIGHTNESSLEVELS = null;

    public static final String GLYPH_ENABLE = "glyph_enable";
    public static final String GLYPH_FLIP_ENABLE = "glyph_settings_flip_toggle";
    public static final String GLYPH_BRIGHTNESS = "glyph_settings_brightness";
    public static final String GLYPH_CHARGING_CATEGORY = "glyph_settings_charging";
    public static final String GLYPH_CHARGING_LEVEL_ENABLE = "glyph_settings_charging_level";
    public static final String GLYPH_CHARGING_POWERSHARE_ENABLE = "glyph_settings_charging_powershare";
    public static final String GLYPH_CALL_CATEGORY = "glyph_settings_call";
    public static final String GLYPH_CALL_ENABLE = "glyph_settings_call_toggle";
    public static final String GLYPH_CALL_SUB_PREVIEW = "glyph_settings_call_sub_preview";
    public static final String GLYPH_CALL_SUB_ANIMATIONS = "glyph_settings_call_sub_animations";
    public static final String GLYPH_CALL_SUB_ENABLE = "glyph_settings_call_sub_toggle";
    public static final String GLYPH_MUSIC_VISUALIZER_ENABLE = "glyph_settings_music_visualizer_toggle";
    public static final String GLYPH_NOTIFS_ENABLE = "glyph_settings_notifs_toggle";
    public static final String GLYPH_NOTIFS_SUB_PREVIEW = "glyph_settings_notifs_sub_preview";
    public static final String GLYPH_NOTIFS_SUB_ANIMATIONS = "glyph_settings_notifs_sub_animations";
    public static final String GLYPH_NOTIFS_SUB_CATEGORY = "glyph_settings_notifs_sub";
    public static final String GLYPH_NOTIFS_SUB_ENABLE = "glyph_settings_notifs_sub_toggle";

    public static final String[] APPSTOIGNORE = {
                                                        "android",
                                                        "com.android.traceur",
                                                        //"com.google.android.dialer",
                                                        "com.google.android.setupwizard",
                                                        "dev.kdrag0n.dyntheme.privileged.sys"
                                                    };
    public static final String[] NOTIFSTOIGNORE = {
                                                        "com.google.android.dialer:phone_incoming_call",
                                                        "com.google.android.dialer:phone_ongoing_call",
                                                        "com.android.systemui:BAT"
                                                    };

    public static boolean setBrightness(int b) {
        if (b > ResourceUtils.getInteger("glyph_settings_brightness_max"))
            return false;

        BRIGHTNESS = b;
        return true;
    }

    public static int getBrightness() {
        if (BRIGHTNESS == -1)
            BRIGHTNESS = ResourceUtils.getInteger("glyph_settings_brightness_max");

        return BRIGHTNESS;
    }

    public static int getMaxBrightness() {
        if (BRIGHTNESSMAX == -1)
            BRIGHTNESSMAX = ResourceUtils.getInteger("glyph_settings_brightness_max");

        return BRIGHTNESSMAX;
    }

    public static int[] getBrightnessLevels() {
        if (BRIGHTNESSLEVELS == null)
            BRIGHTNESSLEVELS = ResourceUtils.getIntArray("glyph_settings_brightness_levels");

        return BRIGHTNESSLEVELS;
    }

}
