/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
 *               2020-2022 Paranoid Android
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
import android.os.UserHandle;
import android.util.Log;

public final class GlyphUtils {

    private static final String TAG = "GlyphUtils";
    private static final boolean DEBUG = true;

    public static void startGlyphCallReceiverService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting Glyph call receiver service");
        context.startServiceAsUser(new Intent(context, GlyphCallReceiverService.class),
                UserHandle.CURRENT);
    }

    protected static void stopGlyphCallReceiverService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping Glyph call receiver service");
        context.stopServiceAsUser(new Intent(context, GlyphCallReceiverService.class),
                UserHandle.CURRENT);
    }

    public static void startGlyphChargingService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting Glyph charging service");
        context.startServiceAsUser(new Intent(context, GlyphChargingService.class),
                UserHandle.CURRENT);
    }

    protected static void stopGlyphChargingService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping Glyph charging service");
        context.stopServiceAsUser(new Intent(context, GlyphChargingService.class),
                UserHandle.CURRENT);
    }

    public static void checkGlyphService(Context context) {
        if (GlyphSettingsManager.isGlyphEnabled(context)) {
            GlyphConstants.setBrightness(GlyphSettingsManager.getGlyphBrightness(context));
            if (GlyphSettingsManager.isGlyphChargingEnabled(context)) {
                startGlyphChargingService(context);
            } else {
                stopGlyphChargingService(context);
            }
            if (GlyphSettingsManager.isGlyphCallEnabled(context)) {
                startGlyphCallReceiverService(context);
            } else {
                stopGlyphCallReceiverService(context);
            }
        } else {
            stopGlyphChargingService(context);
            stopGlyphCallReceiverService(context);
        }
    }
}
