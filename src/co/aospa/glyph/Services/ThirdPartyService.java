/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.glyph.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Arrays;

import co.aospa.glyph.Manager.AnimationManager;
import co.aospa.glyph.Manager.StatusManager;
import co.aospa.glyph.Sdk.IGlyphService;

public class ThirdPartyService extends Service {

    private static final String TAG = "GlyphThirdPartyService";
    private static final boolean DEBUG = true;

    private IGlyphService.Stub mGlyphServiceStub = new GlyphServiceStub();

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (DEBUG) Log.d(TAG, "Binding service");
        return mGlyphServiceStub;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (DEBUG) Log.d(TAG, "Unbinding service");
        return true;
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");
        super.onDestroy();
    }

    class GlyphServiceStub extends IGlyphService.Stub {
        GlyphServiceStub() { }

        @Override
        public void setFrameColors(int[] colors) throws RemoteException {
            if (DEBUG) Log.d(TAG, "GlyphServiceStub: setFrameColors | colors: " + Arrays.toString(colors));
            if (StatusManager.isAllLedActive()) throw new RemoteException("All LEDs are active, exiting");
            AnimationManager.updateLedFrame(colors);
        }

        @Override
        public void openSession() throws RemoteException {
            if (DEBUG) Log.d(TAG, "GlyphServiceStub: openSession");
            StatusManager.setThirdPartyActive(true);
        }

        @Override
        public void closeSession() throws RemoteException {
            if (DEBUG) Log.d(TAG, "GlyphServiceStub: closeSession");
            if (!StatusManager.isAllLedActive()) AnimationManager.updateLedFrame(new float[5]);
            StatusManager.setThirdPartyActive(false);
        }

        @Override
        public boolean register(String key) throws RemoteException {
            if (DEBUG) Log.d(TAG, "GlyphServiceStub: register | key: " + key);
            return true;
        }

        @Override
        public boolean registerSDK(String key, String device) throws RemoteException {
            if (DEBUG) Log.d(TAG, "GlyphServiceStub: registerSDK | key: " + key + " | device: " + device);
            return true;
        }
    }
}