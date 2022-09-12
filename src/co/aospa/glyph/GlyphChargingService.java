/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2018 The LineageOS Project
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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class GlyphChargingService extends Service {
    private static final String TAG = "GlyphChargingService";
    private static final boolean DEBUG = true;

    private static final String BATTERYLEVELPATH = "/sys/class/power_supply/battery/capacity";
    private static final String SINGLELEDPATH =  "/sys/class/leds/aw210xx_led/single_led_br";

    private static final float[] ANIMATION_DOT = {0.01f, 0.02f, 0.03f, 0.04f, 0.05f, 0.06f, 0.07f, 0.08f, 0.09f, 0.1f, 0.11f, 0.12f, 0.13f, 0.14f, 0.15f, 0.16f, 0.17f, 0.18f, 0.19f, 0.2f, 0.21f, 0.22f, 0.23f, 0.24f, 0.25f, 0.26f, 0.27f, 0.28f, 0.29f, 0.3f, 0.31f, 0.32f, 0.33f, 0.34f, 0.35f, 0.36f, 0.37f, 0.38f, 0.39f, 0.4f, 0.41f, 0.42f, 0.43f, 0.44f, 0.45f, 0.46f, 0.47f, 0.48f, 0.49f, 0.5f, 0.51f, 0.52f, 0.53f, 0.54f, 0.55f, 0.56f, 0.57f, 0.58f, 0.59f, 0.6f, 0.61f, 0.62f, 0.63f, 0.64f, 0.65f, 0.66f, 0.67f, 0.68f, 0.69f, 0.7f, 0.71f, 0.72f, 0.73f, 0.74f, 0.75f, 0.76f, 0.77f, 0.78f, 0.79f, 0.8f, 0.81f, 0.82f, 0.83f, 0.84f, 0.85f, 0.86f, 0.87f, 0.88f, 0.89f, 0.9f, 0.91f, 0.92f, 0.93f, 0.94f, 0.95f, 0.96f, 0.97f, 0.98f, 0.99f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 1f, 0.99f, 0.98f, 0.97f, 0.96f, 0.95f, 0.94f, 0.93f, 0.92f, 0.91f, 0.9f, 0.89f, 0.88f, 0.87f, 0.86f, 0.85f, 0.84f, 0.83f, 0.82f, 0.81f, 0.8f, 0.79f, 0.78f, 0.77f, 0.76f, 0.75f, 0.74f, 0.73f, 0.72f, 0.71f, 0.7f, 0.69f, 0.68f, 0.67f, 0.66f, 0.65f, 0.64f, 0.63f, 0.62f, 0.61f, 0.6f, 0.59f, 0.58f, 0.57f, 0.56f, 0.55f, 0.54f, 0.53f, 0.52f, 0.51f, 0.5f, 0.49f, 0.48f, 0.47f, 0.46f, 0.45f, 0.44f, 0.43f, 0.42f, 0.41f, 0.4f, 0.39f, 0.38f, 0.37f, 0.36f, 0.35f, 0.34f, 0.33f, 0.32f, 0.31f, 0.3f, 0.29f, 0.28f, 0.27f, 0.26f, 0.25f, 0.24f, 0.23f, 0.22f, 0.21f, 0.2f, 0.19f, 0.18f, 0.17f, 0.16f, 0.15f, 0.14f, 0.13f, 0.12f, 0.11f, 0.1f, 0.09f, 0.08f, 0.07f, 0.06f, 0.05f, 0.04f, 0.03f, 0.02f, 0.01f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};

    private int brightness = 4095;

    private boolean mDotAnimationEnabled = false;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");

		BatteryManager myBatteryManager = (BatteryManager) this.getSystemService(Context.BATTERY_SERVICE);
		if (myBatteryManager.isCharging()) {
			onPowerConnected();
		}

        IntentFilter powerMonitor = new IntentFilter();
        powerMonitor.addAction(Intent.ACTION_POWER_CONNECTED);
        powerMonitor.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(mPowerMonitor, powerMonitor);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");

		onPowerDisconnected();

        super.onDestroy();
        this.unregisterReceiver(mPowerMonitor);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void onPowerConnected() {
        if (DEBUG) Log.d(TAG, "Power connected");
        if (DEBUG) Log.d(TAG, "Battery level: " + GlyphFileUtils.readLineInt(BATTERYLEVELPATH));
        chargingDotAnimation.start();
        chargingIndicatorAnimation.start();
    }

    private void onPowerDisconnected() {
        if (DEBUG) Log.d(TAG, "Power disconnected");
        chargingDotAnimation.interrupt();
        chargingIndicatorAnimation.interrupt();
    }

    Thread chargingDotAnimation = new Thread("Charging Dot Animation") {
        public void run() {
            try {
                while (true) {
                    for (float f: ANIMATION_DOT) {
                        GlyphFileUtils.writeLedFloat(SINGLELEDPATH, 16, f * brightness);
                        Thread.sleep(10);
                    }
                }
            } catch (InterruptedException e) {
                GlyphFileUtils.writeLedInt(SINGLELEDPATH, 16, 0);
            }
        }
    };

    Thread chargingIndicatorAnimation = new Thread("Charging Indicator Animation") {
        public void run() {
            try {
                int batteryLevel = GlyphFileUtils.readLineInt(BATTERYLEVELPATH);
                int[] batteryArray = new int[]{};
                if (batteryLevel == 100 ) {
                    batteryArray = new int[]{13, 11, 9, 12, 10, 14, 15, 8};
                } else if (batteryLevel >= 88) {
                    batteryArray = new int[]{13, 11, 9, 12, 10, 14, 15};
                } else if (batteryLevel >= 75) {
                    batteryArray = new int[]{13, 11, 9, 12, 10, 14};
                } else if (batteryLevel >= 62) {
                    batteryArray = new int[]{13, 11, 9, 12, 10};
                } else if (batteryLevel >= 49) {
                    batteryArray = new int[]{13, 11, 9, 12};
                } else if (batteryLevel >= 36) {
                    batteryArray = new int[]{13, 11, 9};
                } else if (batteryLevel >= 24) {
                    batteryArray = new int[]{13, 11};
                } else if (batteryLevel >= 12) {
                    batteryArray = new int[]{13};
                }
                for (int i : batteryArray) {
                    GlyphFileUtils.writeLedInt(SINGLELEDPATH, i, brightness);
                    Thread.sleep(10);
                }
                Thread.sleep(1000);
                for (int i=batteryArray.length-1; i>=0; i--) {
                    GlyphFileUtils.writeLedInt(SINGLELEDPATH, batteryArray[i], 0);
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                for (int i : new int[]{8, 15, 14, 10, 12, 9, 11, 13}) {
                    GlyphFileUtils.writeLedInt(SINGLELEDPATH, i, 0);
                }
            }

        }
    };

    public BroadcastReceiver mPowerMonitor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                onPowerConnected();
            } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                onPowerDisconnected();
            }
        }
    };
}
