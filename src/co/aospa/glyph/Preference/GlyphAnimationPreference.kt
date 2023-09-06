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

package co.aospa.glyph.Preference

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.content.res.TypedArrayUtils
import androidx.core.content.res.getResourceIdOrThrow
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import co.aospa.glyph.Constants.Constants
import co.aospa.glyph.R
import co.aospa.glyph.Utils.AnimationUtils
import co.aospa.glyph.animation.GlyphAnimation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch

public class GlyphAnimationPreference(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : Preference(context, attributeSet, defStyleAttr, defStyleRes) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, TypedArrayUtils.getAttr(context, R.attr.preferenceStyle, android.R.attr.preferenceStyle))
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : this(context, attributeSet, defStyleAttr, 0)

    private val mSlugDrawables: Array<Drawable?>
    private val mSlugIds: IntArray

    private val mAnimationUtils = AnimationUtils(context)

    private var currentAnimation: GlyphAnimation? = null
    private var currentAnimationJob: Job? = null
    private var animationReplayDelay = 0L

    private val scope = CoroutineScope(Job() + Dispatchers.Main)

    init {
        layoutResource = R.layout.glyph_settings_preview
        context.resources.obtainTypedArray(R.array.slug_ids).use { drawableIds ->
            val idCount = drawableIds.length()
            mSlugDrawables = arrayOfNulls(idCount)
            mSlugIds = IntArray(idCount)
            for (i in 0 until idCount) {
                mSlugIds[i] = drawableIds.getResourceIdOrThrow(i)
            }
        }
    }

    private fun startAnimation() {
        val maxBrightness = Constants.getMaxBrightness()
        currentAnimationJob = scope.launch {
            mAnimationUtils.playAnimation(currentAnimation!!, 20L, animationReplayDelay) {
                for (i in it.indices) {
                    val alpha = (0.4f * 255 + 0.6f * 255 * it[i] / maxBrightness).toInt()
                    mSlugDrawables[i]?.alpha = alpha
                }
                ensureActive()
            }
        }
    }

    public fun updateAnimation(play: Boolean, animation: GlyphAnimation, replayDelay: Long = 0L) {
        if (currentAnimationJob != null) {
            currentAnimationJob!!.cancel()
        }

        currentAnimation = animation
        animationReplayDelay = replayDelay

        if (play) {
            startAnimation()
        } else {
            // Reset drawables
            for (i in mSlugIds.indices) {
                mSlugDrawables[i]?.alpha = (0.3f * 255).toInt()
            }
        }
    }

    override fun onBindViewHolder(holder: PreferenceViewHolder?) {
        super.onBindViewHolder(holder)

        // Find drawables and configure their brightness
        val phonePreviewDrawable = (holder!!.findViewById(R.id.phone_preview) as ImageView).drawable as LayerDrawable
        for (i in mSlugIds.indices) {
            mSlugDrawables[i] = phonePreviewDrawable.findDrawableByLayerId(mSlugIds[i])
            mSlugDrawables[i]?.alpha = (0.3f * 255).toInt()
        }
    }

    override fun onAttached() {
        super.onAttached()
        if (currentAnimation != null) {
            startAnimation()
        }
    }

    override fun onDetached() {
        super.onDetached()
        scope.cancel()
    }
}