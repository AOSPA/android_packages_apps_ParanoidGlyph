/*
 * Copyright (C) 2022-2023 Paranoid Android
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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.android.internal.util.ArrayUtils;

import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.Manager.AnimationManager;
import co.aospa.glyph.Manager.SettingsManager;

public class NotificationService extends NotificationListenerService {

    private static final String TAG = "GlyphNotification";
    private static final boolean DEBUG = true;

    private NotificationManager mNotificationManager;
    private PowerManager mPowerManager;
    private WakeLock mWakeLock;

    @Override
    public void onCreate() {
        if (DEBUG) Log.d(TAG, "Creating service");
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "Starting service");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "Destroying service");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        String packageName = sbn.getPackageName();
        String packageChannelID = sbn.getNotification().getChannelId();
        int packageImportance = -1;
        boolean packageCanBypassDnd = false;
        int interruptionFilter = mNotificationManager.getCurrentInterruptionFilter();
        try {
            Context packageContext = createPackageContext(packageName, 0);
            NotificationManager packageNotificationManager = (NotificationManager) packageContext.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel packageChannel = packageNotificationManager.getNotificationChannel(packageChannelID);
            if (packageChannel != null) {
                packageImportance = packageChannel.getImportance();
                packageCanBypassDnd = packageChannel.canBypassDnd();
            }
        } catch (PackageManager.NameNotFoundException e) {}
        if (DEBUG) Log.d(TAG, "onNotificationPosted: package:" + packageName + " | channel id: " + packageChannelID + " | importance: " + packageImportance + " | can bypass dnd: " + packageCanBypassDnd);
        if (SettingsManager.isGlyphNotifsAppEnabled(packageName)
                        && !sbn.isOngoing()
                        && !ArrayUtils.contains(Constants.APPS_TO_IGNORE, packageName)
                        && !ArrayUtils.contains(Constants.NOTIFS_TO_IGNORE, packageName + ":" + packageChannelID)
                        && (packageImportance >= NotificationManager.IMPORTANCE_DEFAULT || packageImportance == -1)
                        && (interruptionFilter <= NotificationManager.INTERRUPTION_FILTER_ALL || packageCanBypassDnd)) {
            mWakeLock.acquire(2500);
            AnimationManager.playCsv(SettingsManager.getGlyphNotifsAnimation());
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        if (DEBUG) Log.d(TAG, "onNotificationRemoved: package:" + sbn.getPackageName() + " | channel id: " + sbn.getNotification().getChannelId());
    }
}