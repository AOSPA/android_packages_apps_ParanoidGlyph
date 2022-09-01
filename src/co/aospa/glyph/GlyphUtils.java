/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
 *               2020 Paranoid Android
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

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.display.AmbientDisplayConfiguration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import androidx.preference.PreferenceManager;

public final class GlyphUtils {

    private static final String TAG = "GlyphUtils";
    private static final boolean DEBUG = true;

    protected static final String GLYPH_ENABLE = "glyph_enable";
    protected static final String GLYPH_CHARGING_ENABLE = "glyph_settings_charger";

    public static void startGlyphChargingService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting service");
        context.startServiceAsUser(new Intent(context, GlyphChargingService.class),
                UserHandle.CURRENT);
    }

    protected static void stopGlyphChargingService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping service");
        context.stopServiceAsUser(new Intent(context, GlyphChargingService.class),
                UserHandle.CURRENT);
    }

    public static void checkGlyphService(Context context) {
        if (isGlyphEnabled(context)) {
            if (isGlyphChargingEnabled(context)) {
                startGlyphChargingService(context);
            } else {
                stopGlyphChargingService(context);
            }
        } else {
            stopGlyphChargingService(context);
        }
    }

    protected static boolean enableGlyph(Context context, boolean enable) {
        return Settings.Secure.putInt(context.getContentResolver(),
                GLYPH_ENABLE, enable ? 1 : 0);
    }

    public static boolean isGlyphEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(),
                GLYPH_ENABLE, 1) != 0;
    }

    public static boolean isGlyphChargingEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(GLYPH_CHARGING_ENABLE, false);
    }
}
