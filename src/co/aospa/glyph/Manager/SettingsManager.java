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
import android.provider.Settings;
import android.util.Log;

import androidx.preference.PreferenceManager;

import co.aospa.glyph.R;
import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.Utils.FileUtils;

public final class SettingsManager {

    private static final String TAG = "GlyphSettingsManager";
    private static final boolean DEBUG = true;

    private static Context context = Constants.CONTEXT;

    public static boolean enableGlyph(boolean enable) {
        return Settings.Secure.putInt(context.getContentResolver(),
                Constants.GLYPH_ENABLE, enable ? 1 : 0);
    }

    public static boolean isGlyphEnabled() {
        return Settings.Secure.getInt(context.getContentResolver(),
                Constants.GLYPH_ENABLE, 1) != 0;
    }

    public static boolean isGlyphFlipEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constants.GLYPH_FLIP_ENABLE, false) && isGlyphEnabled();
    }

    public static int getGlyphBrightness() {
        int d = 3; if (FileUtils.readLine("/mnt/vendor/persist/color") == "white") d = 2;
        int[] levels = context.getResources().getIntArray(R.array.glyph_settings_animations_brightness_levels);
        int brightness = PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(Constants.GLYPH_BRIGHTNESS, d);
        switch (brightness) {
            case 1:
                return levels[0];
            case 2:
                return levels[1];
            case 3:
                return levels[2];
            default:
                return levels[3];
        }
    }

    public static int getGlyphBrightnessSetting() {
        int d = 3; if (FileUtils.readLine("/mnt/vendor/persist/color") == "white") d = 2;
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(Constants.GLYPH_BRIGHTNESS, d);
    }

    public static boolean isGlyphChargingEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constants.GLYPH_CHARGING_LEVEL_ENABLE, false) && isGlyphEnabled();
    }

    public static boolean isGlyphCallEnabled() {
        return Settings.Secure.getInt(context.getContentResolver(),
                Constants.GLYPH_CALL_ENABLE, 1) != 0 && isGlyphEnabled();
    }

    public static boolean setGlyphCallEnabled(boolean enable) {
        return Settings.Secure.putInt(context.getContentResolver(),
                Constants.GLYPH_CALL_ENABLE, enable ? 1 : 0);
    }

    public static String getGlyphCallAnimation() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Constants.GLYPH_CALL_SUB_ANIMATIONS,
                    context.getString(R.string.glyph_settings_call_animations_default));
    }

    public static boolean isGlyphMusicVisualizerEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constants.GLYPH_MUSIC_VISUALIZER_ENABLE, false) && isGlyphEnabled();
    }

    public static boolean isGlyphNotifsEnabled() {
        return Settings.Secure.getInt(context.getContentResolver(),
                Constants.GLYPH_NOTIFS_ENABLE, 1) != 0 && isGlyphEnabled();
    }

    public static boolean setGlyphNotifsEnabled(boolean enable) {
        return Settings.Secure.putInt(context.getContentResolver(),
                Constants.GLYPH_NOTIFS_ENABLE, enable ? 1 : 0);
    }

    public static String getGlyphNotifsAnimation() {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(Constants.GLYPH_NOTIFS_SUB_ANIMATIONS,
                    context.getString(R.string.glyph_settings_notifs_animations_default));
    }

    public static boolean isGlyphNotifsAppEnabled(String app) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(app, true) && isGlyphNotifsEnabled();
    }
}
