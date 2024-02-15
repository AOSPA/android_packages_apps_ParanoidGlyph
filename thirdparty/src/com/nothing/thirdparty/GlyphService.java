/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.nothing.thirdparty;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.Arrays;

public class GlyphService extends Service {

    private static final String TAG = "GlyphThirdPartyGlyphService";
    private static final boolean DEBUG = true;

    private IGlyphService mService;
    private IGlyphService.Stub mGlyphServiceStub = new GlyphServiceStub();
    private RemoteServiceConnection mConnection = new RemoteServiceConnection();

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        bindGlyphService();
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
        unbindGlyphService();
        super.onDestroy();
    }

    private void bindGlyphService() {
        Intent launchService = new Intent();
        launchService.setPackage("co.aospa.glyph");
        launchService.setAction("co.aospa.glyph.bind_glyphservice");
        launchService.setComponent(new ComponentName("co.aospa.glyph", "co.aospa.glyph.Services.ThirdPartyService"));
        bindService(launchService, mConnection, Service.BIND_AUTO_CREATE);
    }

    private void unbindGlyphService() {
        unbindService(mConnection);
    }

    class GlyphServiceStub extends IGlyphService.Stub {
        GlyphServiceStub() { }

        @Override
        public void setFrameColors(int[] colors) throws RemoteException {
            //if (DEBUG) Log.d(TAG, "GlyphServiceStub: setFrameColors | colors: " + Arrays.toString(colors));
            mService.setFrameColors(colors);
        }

        @Override
        public void openSession() throws RemoteException {
            //if (DEBUG) Log.d(TAG, "GlyphServiceStub: openSession");
            mService.openSession();
        }

        @Override
        public void closeSession() throws RemoteException {
            //if (DEBUG) Log.d(TAG, "GlyphServiceStub: closeSession");
            mService.closeSession();
        }

        @Override
        public boolean register(String key) throws RemoteException {
            //if (DEBUG) Log.d(TAG, "GlyphServiceStub: register | key: " + key);
            return mService.register(key);
        }

        @Override
        public boolean registerSDK(String key, String device) throws RemoteException {
            //if (DEBUG) Log.d(TAG, "GlyphServiceStub: registerSDK | key: " + key + " | device: " + device);
            return mService.registerSDK(key, device);
        }
    }

    private class RemoteServiceConnection implements ServiceConnection {
        private RemoteServiceConnection() {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (DEBUG) Log.d(TAG, "Service connected");
            mService = IGlyphService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (DEBUG) Log.d(TAG, "Service disconnected");
            mService = null;
        }

        @Override
        public void onBindingDied(ComponentName name) {
            if (DEBUG) Log.d(TAG, "Binding died");
            mService = null;
        }
    }
}