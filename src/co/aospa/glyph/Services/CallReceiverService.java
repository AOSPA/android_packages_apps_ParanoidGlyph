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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import co.aospa.glyph.Manager.AnimationManager;

public class CallReceiverService extends Service {

    private static final String TAG = "GlyphCallReceiverService";
    private static final boolean DEBUG = true;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");

        IntentFilter callReceiver = new IntentFilter();
        callReceiver.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
        registerReceiver(mCallReceiver, callReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");
        this.unregisterReceiver(mCallReceiver);
        disableCallAnimation();
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
