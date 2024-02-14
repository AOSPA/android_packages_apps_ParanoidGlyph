/*
 * Copyright (C) 2024 Paranoid Android
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.nothing.thirdparty;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

    private static final boolean DEBUG = true;
    private static final String TAG = "ParanoidGlyphThirdParty";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (DEBUG) Log.d(TAG, "Received boot completed intent");
        context.startServiceAsUser(new Intent(context, GlyphService.class),
                UserHandle.CURRENT);
    }
}
