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

package co.aospa.glyph.Utils

import android.content.Context
import co.aospa.glyph.R
import co.aospa.glyph.animation.GlyphAnimation
import kotlinx.coroutines.delay
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class AnimationUtils(val context: Context) {
    companion object {
        private fun loadAnimationFromIs(inputStream: InputStream, animationSlugCount: Int): ShortArray {
            val reader = BufferedReader(InputStreamReader(inputStream))
            val brightnessList = ArrayList<Short>(animationSlugCount * 128)

            reader.mark(256) // > 33 * 5 = 165 (max expected line length)
            val slugCountInFile = reader.readLine().count { it == ',' }
            reader.reset()

            val addToList: (List<String>) -> Unit = if (animationSlugCount == slugCountInFile) {
                // Phone1 with Phone1 pattern
                { values: List<String> ->
                    for (i in 0 until animationSlugCount) {
                        brightnessList.add(values[i].toShort())
                    }
                }
            } else if (slugCountInFile == 5 && animationSlugCount == 11) {
                // Phone2 with Phone1 pattern
                { values: List<String> ->
                    brightnessList.add(values[0].toShort())
                    brightnessList.add(values[0].toShort())
                    brightnessList.add(values[1].toShort())
                    brightnessList.add(values[2].toShort())
                    brightnessList.add(values[2].toShort())
                    brightnessList.add(values[2].toShort())
                    brightnessList.add(values[2].toShort())
                    brightnessList.add(values[2].toShort())
                    brightnessList.add(values[2].toShort())
                    brightnessList.add(values[3].toShort())
                    brightnessList.add(values[4].toShort())
                }
            } else if (slugCountInFile == 33 && animationSlugCount == 11) {
                // Phone2 with Phone2 pattern
                { values: List<String> ->
                    brightnessList.add(values[0].toShort())
                    brightnessList.add(values[1].toShort())
                    brightnessList.add(values[2].toShort())
                    brightnessList.add(values[3].toShort())
                    brightnessList.add(values[19].toShort())
                    brightnessList.add(values[20].toShort())
                    brightnessList.add(values[21].toShort())
                    brightnessList.add(values[22].toShort())
                    brightnessList.add(values[23].toShort())
                    brightnessList.add(values[25].toShort())
                    brightnessList.add(values[24].toShort())
                }
            } else {
                throw UnsupportedOperationException()
            }

            reader.forEachLine {
                val values = it.split(",")
                addToList(values)
            }
            return brightnessList.toShortArray()
        }
    }

    private val slugCount: Int = context.resources.getInteger(R.integer.slug_count)

    private var cachedCallAnimation: GlyphAnimation? = null
    private var cachedNotificationAnimation: GlyphAnimation? = null

    fun listAvailableCallAnimations(): Array<String> {
        val assets = context.assets.list("call")
        for (i in assets!!.indices) {
            assets[i] = assets[i].removeSuffix(".csv")
        }
        return assets
    }

    fun listAvailableNotificationAnimations(): Array<String> {
        val assets = context.assets.list("notification")
        for (i in assets!!.indices) {
            assets[i] = assets[i].removeSuffix(".csv")
        }
        return assets
    }

    fun getCallAnimation(name: String): GlyphAnimation {
        if (cachedCallAnimation?.name == name) {
            return cachedCallAnimation!!
        }

        val inputStream = context.assets.open("call/$name.csv")
        cachedCallAnimation = GlyphAnimation(name, loadAnimationFromIs(inputStream, slugCount))
        return cachedCallAnimation!!
    }

    fun getNotificationAnimation(name: String): GlyphAnimation {
        if (cachedNotificationAnimation?.name == name) {
            return cachedNotificationAnimation!!
        }

        val inputStream = context.assets.open("notification/$name.csv")
        cachedNotificationAnimation = GlyphAnimation(name, loadAnimationFromIs(inputStream, slugCount))
        return cachedNotificationAnimation!!
    }

    fun getOtherAnimation(name: String): GlyphAnimation {
        val inputStream = context.assets.open("$name.csv")
        return GlyphAnimation(name, loadAnimationFromIs(inputStream, slugCount))
    }

    suspend fun playAnimationOnce(animation: GlyphAnimation, stepDelay: Long, work: (brightness: List<Short>) -> Unit) {
        for (i in 0 until animation.brightness.size step slugCount) {
            work(animation.brightness.slice(i until (i + slugCount)))
            delay(stepDelay)
        }
    }

    suspend fun playAnimation(animation: GlyphAnimation, stepDelay: Long, replayDelay: Long, work: (brightness: List<Short>) -> Unit) {
        while (true) {
            for (i in 0 until animation.brightness.size step slugCount) {
                work(animation.brightness.slice(i until (i + slugCount)))
                delay(stepDelay)
            }
            delay(replayDelay)
        }
    }
}