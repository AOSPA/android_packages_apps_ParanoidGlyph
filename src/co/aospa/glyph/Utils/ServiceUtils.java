/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
 *               2020-2023 Paranoid Android
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

package co.aospa.glyph.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;

import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.Manager.SettingsManager;
import co.aospa.glyph.Services.CallReceiverService;
import co.aospa.glyph.Services.ChargingService;
import co.aospa.glyph.Services.FlipToGlyphService;
import co.aospa.glyph.Services.MusicVisualizerService;
import co.aospa.glyph.Services.NotificationService;

public final class ServiceUtils {

    private static final String TAG = "GlyphServiceUtils";
    private static final boolean DEBUG = true;

    private static Context context = Constants.CONTEXT;

    private static void startCallReceiverService() {
        if (DEBUG) Log.d(TAG, "Starting Glyph call receiver service");
        context.startServiceAsUser(new Intent(context, CallReceiverService.class),
                UserHandle.CURRENT);
    }

    private static void stopCallReceiverService() {
        if (DEBUG) Log.d(TAG, "Stopping Glyph call receiver service");
        context.stopServiceAsUser(new Intent(context, CallReceiverService.class),
                UserHandle.CURRENT);
    }

    private static void startChargingService() {
        if (DEBUG) Log.d(TAG, "Starting Glyph charging service");
        context.startServiceAsUser(new Intent(context, ChargingService.class),
                UserHandle.CURRENT);
    }

    private static void stopChargingService() {
        if (DEBUG) Log.d(TAG, "Stopping Glyph charging service");
        context.stopServiceAsUser(new Intent(context, ChargingService.class),
                UserHandle.CURRENT);
    }

    private static void startFlipToGlyphService() {
        if (DEBUG) Log.d(TAG, "Starting Flip to Glyph service");
        context.startServiceAsUser(new Intent(context, FlipToGlyphService.class),
                UserHandle.CURRENT);
    }

    private static void stopFlipToGlyphService() {
        if (DEBUG) Log.d(TAG, "Stopping Flip to Glyph service");
        context.stopServiceAsUser(new Intent(context, FlipToGlyphService.class),
                UserHandle.CURRENT);
    }

    public static void startMusicVisualizerService() {
        if (DEBUG) Log.d(TAG, "Starting Music Visualizer service");
        context.startServiceAsUser(new Intent(context, MusicVisualizerService.class),
                UserHandle.CURRENT);
    }

    protected static void stopMusicVisualizerService() {
        if (DEBUG) Log.d(TAG, "Stopping Music Visualizer service");
        context.stopServiceAsUser(new Intent(context, MusicVisualizerService.class),
                UserHandle.CURRENT);
    }

    private static void startNotificationService() {
        if (DEBUG) Log.d(TAG, "Starting Glyph notifs service");
        context.startServiceAsUser(new Intent(context, NotificationService.class),
                UserHandle.CURRENT);
    }

    private static void stopNotificationService() {
        if (DEBUG) Log.d(TAG, "Stopping Glyph notifs service");
        context.stopServiceAsUser(new Intent(context, NotificationService.class),
                UserHandle.CURRENT);
    }

    public static void checkGlyphService() {
        if (SettingsManager.isGlyphEnabled()) {
            Constants.setBrightness(SettingsManager.getGlyphBrightness());
            if (SettingsManager.isGlyphChargingEnabled()) {
                startChargingService();
            } else {
                stopChargingService();
            }
            if (SettingsManager.isGlyphCallEnabled()) {
                startCallReceiverService();
            } else {
                stopCallReceiverService();
            }
            if (SettingsManager.isGlyphNotifsEnabled()) {
                startNotificationService();
            } else {
                stopNotificationService();
            }
            if (SettingsManager.isGlyphFlipEnabled()) {
                startFlipToGlyphService();
            } else {
                stopFlipToGlyphService();
            }
            if (SettingsManager.isGlyphMusicVisualizerEnabled()) {
                startMusicVisualizerService();
            } else {
                stopMusicVisualizerService();
            }
        } else {
            stopChargingService();
            stopCallReceiverService();
            stopNotificationService();
            stopFlipToGlyphService();
            stopMusicVisualizerService();
        }
    }
}
