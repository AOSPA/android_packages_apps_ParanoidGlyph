/*
 * Copyright (C) 2023-2024 Paranoid Android
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

package co.aospa.glyph.Utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;

import com.android.internal.util.ArrayUtils;

import java.io.InputStream;
import java.io.IOException;

import co.aospa.glyph.R;
import co.aospa.glyph.Constants.Constants;

public final class ResourceUtils {

    private static final String TAG = "GlyphResourceUtils";
    private static final boolean DEBUG = true;

    private static final Context context = Constants.CONTEXT;

    private static final AssetManager assetManager = context.getAssets();
    private static final Resources resources = context.getResources();

    private static String[] callAnimations = null;
    private static String[] notificationAnimations = null;

    public static int getIdentifier(String id, String type) {
        return context.getResources().getIdentifier(id, type, context.getPackageName());
    }

    public static Boolean getBoolean(String id) {
        return context.getResources().getBoolean(getIdentifier(id, "bool"));
    }

    public static String getString(String id) {
        return context.getResources().getString(getIdentifier(id, "string"));
    }

    public static int getInteger(String id) {
        return context.getResources().getInteger(getIdentifier(id, "integer"));
    }

    public static String[] getStringArray(String id) {
        return context.getResources().getStringArray(getIdentifier(id, "array"));
    }

    public static int[] getIntArray(String id) {
        return context.getResources().getIntArray(getIdentifier(id, "array"));
    }

    public static String[] getCallAnimations() {
        if (callAnimations == null) {
            try {
                String[] assets = assetManager.list("call");
                for (int i=0; i < assets.length; i++) {
                    assets[i] = assets[i].replaceAll(".csv", "");
                }
                callAnimations = assets;
            } catch (IOException e) { }
        }
        return callAnimations;
    }

    public static String[] getNotificationAnimations() {
        if (notificationAnimations == null) {
            try {
                String[] assets = assetManager.list("notification");
                for (int i=0; i < assets.length; i++) {
                    assets[i] = assets[i].replaceAll(".csv", "");
                }
                notificationAnimations = assets;
            } catch (IOException e) { }
        }
        return notificationAnimations;
    }

    public static InputStream getCallAnimation(String name) throws IOException {
        if (callAnimations == null) getCallAnimations();

        if (ArrayUtils.contains(callAnimations, name))
            return assetManager.open("call/" + name + ".csv");

        return assetManager.open("call/" + ResourceUtils.getString("glyph_settings_call_animations_default") + ".csv");
    }

    public static InputStream getNotificationAnimation(String name) throws IOException {
        if (notificationAnimations == null) getNotificationAnimations();

        if (ArrayUtils.contains(notificationAnimations, name))
            return assetManager.open("notification/" + name + ".csv");

        return assetManager.open("call/" + ResourceUtils.getString("glyph_settings_notifs_animations_default") + ".csv");
    }

    public static InputStream getAnimation(String name) throws IOException {
        if (callAnimations == null) getCallAnimations();
        if (notificationAnimations == null) getNotificationAnimations();

        if (ArrayUtils.contains(callAnimations, name)) {
            return getCallAnimation(name);
        }

        if (ArrayUtils.contains(notificationAnimations, name)) {
            return getNotificationAnimation(name);
        }

        return assetManager.open(name + ".csv");
    }

}
