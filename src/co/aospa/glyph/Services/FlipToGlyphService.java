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
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

import co.aospa.glyph.Manager.AnimationManager;
import co.aospa.glyph.Sensors.FlipToGlyphSensor;

public class FlipToGlyphService extends Service {

    private static final String TAG = "FlipToGlyphService";
    private static final boolean DEBUG = true;

    private boolean isFlipped;
    private int ringerMode;

    private AudioManager mAudioManager;
    private FlipToGlyphSensor mFlipToGlyphSensor;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");

        mFlipToGlyphSensor = new FlipToGlyphSensor(this, this::onFlip);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        mFlipToGlyphSensor.enable();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");
        mFlipToGlyphSensor.disable();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void onFlip(boolean flipped) {
        if (flipped == isFlipped) return;
        if (DEBUG) Log.d(TAG, "Flipped: " + flipped);
        if (flipped) {
            AnimationManager.playCsv("flip");
            ringerMode = mAudioManager.getRingerModeInternal();
            mAudioManager.setRingerModeInternal(AudioManager.RINGER_MODE_SILENT);
        } else {
            mAudioManager.setRingerModeInternal(ringerMode);
        }
        isFlipped = flipped;
    }
}
