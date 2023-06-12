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

public final class MigrationUtils {

    private static String[] callPatternsOld = {
        "abra", "beetle", "beetle_custom", "bug", "burrow", "coded", "flutter", "forever", "karha", "latency", "molitor", "pepelu", "pet", "plot", "pneumatic", "radiate", "scribble", "snaps", "squirrels", "tennis", "woo_yeh", "wow"
    };
    private static String[] callPatternsNew = {
        "Abra", "Beetle", "Beetle (Custom)", "Bug", "Burrow", "Coded", "Flutter", "Forever", "Karha", "Latency", "Molitor", "Pepelu", "Pet", "Plot", "Pneumatic", "Radiate", "Scribble", "Snaps", "Squirrels", "Tennis", "Woo Yeh", "Wow!"
    };

    private static String[] notificationPatternsOld = {
        "break", "break_custom", "bulb_one", "bulb_two", "cough", "flashslant", "fox", "gamma", "gargle", "guiro", "isolator", "nope", "oi", "pep", "simmer", "Skim", "squiggle", "volley", "why", "woo", "yeh", "zip"
    };
    private static String[] notificationPatternsNew = {
        "Break", "Break (Custom)", "Bulb One", "Bulb Two", "Cough", "Flash Slant", "Fox", "Gamma", "Gargle", "Guiro", "Isolator", "Nope", "Oi!", "Pep", "Simmer", "Skim", "Squiggle", "Volley", "Why", "Woo", "Yeh", "Zip"
    };

    public static String getNewCallPattern(String old) {
        for (int i = 0; i < callPatternsOld.length; i++) {
            if (callPatternsOld[i].equals(old)) {
                return callPatternsNew[i];
            }
        }
        return ResourceUtils.getString("glyph_call_animation_default");
    }

    public static String getNewNotificationPattern(String old) {
        for (int i = 0; i < notificationPatternsOld.length; i++) {
            if (notificationPatternsOld[i].equals(old)) {
                return notificationPatternsNew[i];
            }
        }
        return ResourceUtils.getString("glyph_notification_animation_default");
    }

}
