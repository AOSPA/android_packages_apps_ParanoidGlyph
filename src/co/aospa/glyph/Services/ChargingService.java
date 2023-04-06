/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2018 The LineageOS Project
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

package co.aospa.glyph.Services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

import co.aospa.glyph.Manager.AnimationManager;

public class ChargingService extends Service {

    private static final String TAG = "GlyphChargingService";
    private static final boolean DEBUG = true;

    private BatteryManager mBatteryManager;
    private SensorManager mSensorManager;

    private Sensor mAccelerometerSensor;
    private static final float ACCELEROMETER_THRESHOLD = 10.0f;
    private static final float ZFACEDOWN_THRESHOLD = -5.0f;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");

        mBatteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int getBatteryLevel() {
        return mBatteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
    }

    private void onPowerConnected() {
        if (DEBUG) Log.d(TAG, "Power connected");
        if (DEBUG) Log.d(TAG, "Battery level: " + getBatteryLevel());
        playChargingAnimation(true);
        mSensorManager.registerListener(mSensorEventListener,
            mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void onPowerDisconnected() {
        if (DEBUG) Log.d(TAG, "Power disconnected");
	    mSensorManager.unregisterListener(mSensorEventListener);
    }

    private void playChargingAnimation(boolean wait) {
        AnimationManager.playCharging(getBatteryLevel(), wait);
    }

    private final BroadcastReceiver mPowerMonitor = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                onPowerConnected();
            } else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                onPowerDisconnected();
            }
        }
    };

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
	    @Override
	    public void onSensorChanged(SensorEvent event) {
		    float x = event.values[0];
		    float y = event.values[1];
		    float z = event.values[2];
		    float acceleration = (float) Math.sqrt(x * x + y * y + z * z);

		    if (acceleration > ACCELEROMETER_THRESHOLD && z <= ZFACEDOWN_THRESHOLD) {
			    playChargingAnimation(false);
		    }
	    }

	    @Override
	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
	    }
    };
}
