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

package co.aospa.glyph;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GlyphCallReceiverService extends Service {

    private static final String TAG = "GlyphCallReceiverService";
    private static final boolean DEBUG = true;

    private ExecutorService mExecutorService;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");

        mExecutorService = Executors.newSingleThreadExecutor();

        IntentFilter callReceiver = new IntentFilter();
        callReceiver.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(mCallReceiver, callReceiver);
    }

    private Future<?> submit(Runnable runnable) {
        return mExecutorService.submit(runnable);
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
        this.unregisterReceiver(mCallReceiver);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void enableCallAnimation() {
        if (GlyphStatusManager.isCallLedActive()) return;
        if (DEBUG) Log.d(TAG, "Enabling Charging Dot Animation");
        GlyphStatusManager.setCallLedEnabled(true);
        submit(() -> {
            GlyphStatusManager.setCallLedActive(true);
            while (GlyphStatusManager.isCallLedEnabled()) {
                try {
                    while (true) {
                        GlyphFileUtils.writeLine(GlyphConstants.CENTERRINGLEDPATH, GlyphConstants.BRIGHTNESS);
                        Thread.sleep(100);
                        if (!GlyphStatusManager.isCallLedEnabled() || GlyphStatusManager.isAllLedEnabled()) throw new InterruptedException();
                        GlyphFileUtils.writeLine(GlyphConstants.CENTERRINGLEDPATH, 0);
                        Thread.sleep(100);
                        if (!GlyphStatusManager.isCallLedEnabled() || GlyphStatusManager.isAllLedEnabled()) throw new InterruptedException();
                        GlyphFileUtils.writeLine(GlyphConstants.CENTERRINGLEDPATH, GlyphConstants.BRIGHTNESS);
                        Thread.sleep(100);
                        if (!GlyphStatusManager.isCallLedEnabled() || GlyphStatusManager.isAllLedEnabled()) throw new InterruptedException();
                        GlyphFileUtils.writeLine(GlyphConstants.CENTERRINGLEDPATH, 0);
                        Thread.sleep(300);
                        if (!GlyphStatusManager.isCallLedEnabled() || GlyphStatusManager.isAllLedEnabled()) throw new InterruptedException();
                    }
                } catch (InterruptedException e) {
                    if (GlyphStatusManager.isAllLedEnabled()) {
                        while (GlyphStatusManager.isAllLedActive()) {};
                    } else {
                        GlyphFileUtils.writeLine(GlyphConstants.CENTERRINGLEDPATH, 0);
                    }
                }
            }
            GlyphStatusManager.setCallLedActive(false);
        });
    };

    private void disableCallAnimation() {
        if (DEBUG) Log.d(TAG, "Disabling Charging Dot Animation");
        GlyphStatusManager.setCallLedEnabled(false);
    }

    private BroadcastReceiver mCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    if (DEBUG) Log.d(TAG, "EXTRA_STATE_RINGING");
                    enableCallAnimation();
                }
                if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
                    if (DEBUG) Log.d(TAG, "EXTRA_STATE_OFFHOOK");
                    disableCallAnimation();
                }
                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                    if (DEBUG) Log.d(TAG, "EXTRA_STATE_IDLE");
                    disableCallAnimation();
                }
            }
        }
    };
}
