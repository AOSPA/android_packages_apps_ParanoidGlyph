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

package co.aospa.glyph.Manager;

import android.util.Log;

public final class StatusManager {

    private static final String TAG = "GlyphStatusManager";
    private static final boolean DEBUG = true;

    private static boolean allLedActive = false;
    private static boolean callLedActive = false;
    private static boolean chargingLedActive = false;
    private static boolean chargingLevelLedActive = false;
    private static boolean notificationLedActive = false;

    private static boolean allLedEnabled = false;
    private static boolean callLedEnabled = false;
    private static boolean chargingLedEnabled = false;
    private static boolean chargingLevelLedEnabled = false;
    private static boolean notificationLedEnabled = false;

    public static boolean isAllLedActive() {
        return allLedActive;
    }

    public static void setAllLedsActive(boolean status) {
        allLedActive = status;
    }

    public static boolean isAllLedEnabled() {
        return allLedEnabled;
    }

    public static void setAllLedsEnabled(boolean status) {
        allLedEnabled = status;
    }

    public static boolean isCallLedActive() {
        return callLedActive;
    }

    public static void setCallLedActive(boolean status) {
        callLedActive = status;
    }

    public static boolean isCallLedEnabled() {
        return callLedEnabled;
    }

    public static void setCallLedEnabled(boolean status) {
        callLedEnabled = status;
    }

    public static boolean isChargingLedActive() {
        return chargingLedActive;
    }

    public static void setChargingLedActive(boolean status) {
        chargingLedActive = status;
    }

    public static boolean isChargingLedEnabled() {
        return chargingLedEnabled;
    }

    public static void setChargingLedEnabled(boolean status) {
        chargingLedEnabled = status;
    }

    public static boolean isChargingLevelLedActive() {
        return chargingLevelLedActive;
    }

    public static void setChargingLevelLedActive(boolean status) {
        chargingLevelLedActive = status;
    }

    public static boolean isChargingLevelLedEnabled() {
        return chargingLevelLedEnabled;
    }

    public static void setChargingLevelLedEnabled(boolean status) {
        chargingLevelLedEnabled = status;
    }

    public static boolean isNotificationLedActive() {
        return notificationLedActive;
    }

    public static void setNotificationLedActive(boolean status) {
        notificationLedActive = status;
    }

    public static boolean isNotificationLedEnabled() {
        return notificationLedEnabled;
    }

    public static void setNotificationLedEnabled(boolean status) {
        notificationLedEnabled = status;
    }

}
