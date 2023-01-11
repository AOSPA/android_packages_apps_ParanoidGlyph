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

package co.aospa.glyph.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import co.aospa.glyph.Manager.AnimationManager;

public class CallReceiverService extends Service {

    private static final String TAG = "GlyphCallReceiverService";
    private static final boolean DEBUG = true;
    private AudioManager mAudioManager;
    private ExecutorService mExecutorService;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");

        mExecutorService = Executors.newSingleThreadExecutor();

        mAudioManager = getSystemService(AudioManager.class);
        mAudioManager.addOnModeChangedListener(mExecutorService, mAudioManagerOnModeChangedListener);
        mAudioManagerOnModeChangedListener.onModeChanged(mAudioManager.getMode());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");
        disableCallAnimation();
        mAudioManager.removeOnModeChangedListener(mAudioManagerOnModeChangedListener);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void enableCallAnimation() {
        if (DEBUG) Log.d(TAG, "enableCallAnimation");
        AnimationManager.playCall("radiate", this);
    }

    private void disableCallAnimation() {
        if (DEBUG) Log.d(TAG, "disableCallAnimation");
        AnimationManager.stopCall();
    }

    private AudioManager.OnModeChangedListener mAudioManagerOnModeChangedListener = new AudioManager.OnModeChangedListener() {
        @Override
        public void onModeChanged(int mode) {
            if (DEBUG) Log.d(TAG, "mAudioManagerOnModeChangedListener: " + mode);
            if (mode == AudioManager.MODE_RINGTONE) {
                enableCallAnimation();
            } else {
                disableCallAnimation();
            }
        }
    };
}
