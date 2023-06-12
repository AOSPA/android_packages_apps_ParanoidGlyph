/*
 * Copyright (C) 2023 Paranoid Android
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
import android.util.Log;

import java.io.InputStream;

import co.aospa.glyph.R;
import co.aospa.glyph.Constants.Constants;

public final class ResourceUtils {

    private static final String TAG = "GlyphResourceUtils";
    private static final boolean DEBUG = true;

    private static Context context = Constants.CONTEXT;

    public static int getIdentifier(String id, String type) {
        return context.getResources().getIdentifier(id, type, context.getPackageName());
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

    public static InputStream openRawResource(String id) {
        return context.getResources().openRawResource(getIdentifier(id, "raw"));
    }

}
