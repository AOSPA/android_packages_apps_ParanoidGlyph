/*
 * SPDX-FileCopyrightText: 2023 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.glyph.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import co.aospa.glyph.manager.AnimationManager
import com.nothing.thirdparty.IGlyphService

class ThirdPartyService : Service() {

    private val binder = object : IGlyphService.Stub() {
        override fun setFrameColors(iArray: IntArray?) {
            Log.d("ThirdPartyService", "received data: ${iArray.contentToString()}")
            AnimationManager.updateLedFrame(iArray)
        }

        override fun openSession() {
            Log.d("IGlyphServiceImpl", "openSession")
        }

        override fun closeSession() {
            Log.d("IGlyphServiceImpl", "closeSession")
        }

        override fun register(str: String) = true
        override fun registerSDK(str1: String, str2: String) = true
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }
}
