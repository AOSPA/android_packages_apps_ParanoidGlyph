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

package co.aospa.glyph.Sensors;

import android.app.NotificationManager;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.util.Log;

public class FlipToGlyphSensor implements SensorEventListener {

    private static final boolean DEBUG = true;
    private static final String TAG = "FlipToGlyphSensor";

    private int zenMode;

    private boolean faceDown = false;
    private boolean frontCovered = false;

    private boolean flipped = false;
    private boolean wasFlipped = false;

    private NotificationManager mNotificationManager;
    private SensorManager mSensorManager;
    private Sensor mSensorAccelerometer;
    private Sensor mSensorProximity;
    private Context mContext;

    public FlipToGlyphSensor(Context context) {
        mContext = context;
        mNotificationManager = mContext.getSystemService(NotificationManager.class);
        mSensorManager = mContext.getSystemService(SensorManager.class);
        mSensorAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER, false);
        mSensorProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY, false);

        zenMode = mNotificationManager.getZenMode();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            if (event.values[2] < -6 && (event.values[0] < 2 && event.values[0] > -2) && (event.values[1] < 2 && event.values[1] > -2)) {
                faceDown = true;
            } else {
                faceDown = false;
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] < 5) {
                frontCovered = true;
            } else {
                frontCovered = false;
            }
        }
        update(faceDown && frontCovered);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private void update(boolean flipped) {
        if (flipped != wasFlipped) {
            if (DEBUG) Log.d(TAG, "flipped: " + Boolean.toString(flipped) + " || faceDown: " + Boolean.toString(faceDown) + " || frontCovered: " + Boolean.toString(frontCovered));
            if (flipped) {
                zenMode = mNotificationManager.getZenMode();
                if (zenMode != Settings.Global.ZEN_MODE_ALARMS && zenMode != Settings.Global.ZEN_MODE_NO_INTERRUPTIONS) {
                    mNotificationManager.setZenMode(Settings.Global.ZEN_MODE_ALARMS, null, TAG);
                }
            } else {
                if (zenMode != Settings.Global.ZEN_MODE_ALARMS && zenMode != Settings.Global.ZEN_MODE_NO_INTERRUPTIONS) {
                    mNotificationManager.setZenMode(zenMode, null, TAG);
                }
            }
            wasFlipped = flipped;
        }
    }

    public void enable() {
        if (DEBUG) Log.d(TAG, "Enabling Sensor");
        mSensorManager.registerListener(this, mSensorAccelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mSensorProximity,
                    SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void disable() {
        if (DEBUG) Log.d(TAG, "Disabling Sensor");
        update(false);
        mSensorManager.unregisterListener(this, mSensorAccelerometer);
        mSensorManager.unregisterListener(this, mSensorProximity);
    }

}