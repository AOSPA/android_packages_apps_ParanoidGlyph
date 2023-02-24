/*
 * Copyright (C) 2023 Paranoid Android
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

package co.aospa.glyph.Sensors;

import android.annotation.NonNull;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Consumer;

public class FaceDownSensor implements SensorEventListener {

    private static final boolean DEBUG = true;
    private static final String TAG = "FaceDownSensor";
    private static final int SENSOR_ID = 65538;

    private final Consumer<Boolean> mScreenUpwards;

    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Context mContext;

    public FaceDownSensor(Context context, @NonNull Consumer<Boolean> isScreenUpwards) {
        mContext = context;
        mScreenUpwards = Objects.requireNonNull(isScreenUpwards);
        mSensorManager = mContext.getSystemService(SensorManager.class);
        mSensor = mSensorManager.getDefaultSensor(SENSOR_ID, true);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] == 1.0) {
            if (DEBUG) Log.d(TAG, "Screen is indeed upwards");
            isScreenUpwards(true);
        } else {
            if (DEBUG) Log.d(TAG, "Screen is downwards");
            isScreenUpwards(false);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void isScreenUpwards(boolean upwards) {
        if (DEBUG) Log.d(TAG, "Upwards: " + upwards);
        mScreenUpwards.accept(upwards);
    }

    public void enable() {
        if (DEBUG) Log.d(TAG, "Enabling Sensor");
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void disable() {
        if (DEBUG) Log.d(TAG, "Disabling Sensor");
        isScreenUpwards(false);
        mSensorManager.unregisterListener(this, mSensor);
    }
}
