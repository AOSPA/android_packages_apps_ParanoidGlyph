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

package co.aospa.glyph.Preference;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.res.TypedArrayUtils;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.R;
import co.aospa.glyph.Utils.ResourceUtils;

public class GlyphAnimationPreference extends Preference {

    private final String TAG = "GlyphAnimationPreference";
    private final boolean DEBUG = true;

    private Activity mActivity;

    private String animationName;
    private boolean animationTerminated;
    private boolean animationPaused = true;
    private int animationTimeBetween = 0;

    private Drawable[] mSlugDrawables;
    private int[] mSlugIds;

    public GlyphAnimationPreference(Context context) {
        this(context, null);
    }

    public GlyphAnimationPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.preferenceStyle,
                android.R.attr.preferenceStyle));
    }

    public GlyphAnimationPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public GlyphAnimationPreference(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setLayoutResource(R.layout.glyph_settings_preview);
        setActivity(context);

        try (TypedArray drawableIds = context.getResources().obtainTypedArray(R.array.slug_ids)) {
            final int idCount = drawableIds.length();
            mSlugDrawables = new Drawable[idCount];
            mSlugIds = new int[idCount];
            for (int i = 0; i < idCount; i++) {
                mSlugIds[i] = drawableIds.getResourceId(i, 0);
            }
        }
    }

    private void setActivity(Context context) {
        if (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                mActivity = (Activity) context;
            } else {
                setActivity(((ContextWrapper) context).getBaseContext());
            }
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        final LayerDrawable phonePreviewDrawable = (LayerDrawable) ((ImageView) holder.findViewById(
                R.id.phone_preview)).getDrawable();
        for (int i = 0; i < mSlugIds.length; i++) {
            mSlugDrawables[i] = phonePreviewDrawable.findDrawableByLayerId(mSlugIds[i]);
            mSlugDrawables[i].setAlpha((int) (0.3f * 255));
        }
    }

    @Override
    public void onAttached() {
        super.onAttached();
        if (DEBUG) Log.d(TAG, "onAttached");
        startAnimation();
    }

    @Override
    public void onDetached() {
        super.onDetached();
        if (DEBUG) Log.d(TAG, "onDetached");
        stopAnimation();
    }

    private void startAnimation() {
        animationThread.start();
    }

    private void stopAnimation() {
        animationTerminated = true;
        animationThread.interrupt();
    }

    public void updateAnimation(boolean play) {
        updateAnimation(play, animationName, 0);
    }

    public void updateAnimation(boolean play, String name) {
        updateAnimation(play, name, 0);
    }

    public void updateAnimation(boolean play, String name, int time) {
        animationTimeBetween = time;
        animationName = name;
        animationPaused = !play;
        animationThread.interrupt();
    }

    Thread animationThread = new Thread() {
        @Override
        public void run() {
            while (!animationTerminated) {
                while (animationPaused) {
                }
                if (DEBUG) Log.d(TAG, "Displaying animation | name: " + animationName);
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(ResourceUtils.getAnimation(animationName)))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.endsWith(",") ? line.substring(0, line.length() - 1) : line;
                        String[] split = line.split(",");
                        if (mSlugIds.length == 5
                                && split.length == 5) { // Phone (1) pattern on Phone (1)
                            mActivity.runOnUiThread(() -> {
                                for (int i = 0; i < mSlugIds.length; i++) {
                                    setGlyphsDrawable(mSlugDrawables[i],
                                            Integer.parseInt(split[i]));
                                }
                            });
                        } else if (mSlugIds.length == 11
                                && split.length == 5) { // Phone (1) pattern on Phone (2)
                            mActivity.runOnUiThread(() -> {
                                setGlyphsDrawable(mSlugDrawables[0], Integer.parseInt(split[0]));
                                setGlyphsDrawable(mSlugDrawables[1], Integer.parseInt(split[0]));
                                setGlyphsDrawable(mSlugDrawables[2], Integer.parseInt(split[1]));
                                setGlyphsDrawable(mSlugDrawables[3], Integer.parseInt(split[2]));
                                setGlyphsDrawable(mSlugDrawables[4], Integer.parseInt(split[2]));
                                setGlyphsDrawable(mSlugDrawables[5], Integer.parseInt(split[2]));
                                setGlyphsDrawable(mSlugDrawables[6], Integer.parseInt(split[2]));
                                setGlyphsDrawable(mSlugDrawables[7], Integer.parseInt(split[2]));
                                setGlyphsDrawable(mSlugDrawables[8], Integer.parseInt(split[2]));
                                setGlyphsDrawable(mSlugDrawables[9], Integer.parseInt(split[3]));
                                setGlyphsDrawable(mSlugDrawables[10], Integer.parseInt(split[4]));
                            });
                        } else if (mSlugIds.length == 11
                                && split.length == 33) { // Phone (2) pattern on Phone (2)
                            mActivity.runOnUiThread(() -> {
                                setGlyphsDrawable(mSlugDrawables[0], Integer.parseInt(split[0]));
                                setGlyphsDrawable(mSlugDrawables[1], Integer.parseInt(split[1]));
                                setGlyphsDrawable(mSlugDrawables[2], Integer.parseInt(split[2]));
                                setGlyphsDrawable(mSlugDrawables[3], Integer.parseInt(split[3]));
                                setGlyphsDrawable(mSlugDrawables[4], Integer.parseInt(split[19]));
                                setGlyphsDrawable(mSlugDrawables[5], Integer.parseInt(split[20]));
                                setGlyphsDrawable(mSlugDrawables[6], Integer.parseInt(split[21]));
                                setGlyphsDrawable(mSlugDrawables[7], Integer.parseInt(split[22]));
                                setGlyphsDrawable(mSlugDrawables[8], Integer.parseInt(split[23]));
                                setGlyphsDrawable(mSlugDrawables[9], Integer.parseInt(split[25]));
                                setGlyphsDrawable(mSlugDrawables[10], Integer.parseInt(split[24]));
                            });
                        } else {
                            if (DEBUG) {
                                Log.d(TAG, "Animation line length mismatch | name: " + animationName
                                        + " | line: " + line);
                            }
                            updateAnimation(false);
                        }
                        Thread.sleep(20);
                    }
                    Thread.sleep(animationTimeBetween);
                } catch (Exception e) {
                    if (DEBUG) {
                        Log.d(TAG, "Exception while displaying animation | name: " + animationName
                                + " | exception: " + e);
                    }
                } finally {
                    if (animationPaused) {
                        if (DEBUG) {
                            Log.d(TAG, "Pause displaying animation | name: " + animationName);
                        }
                        mActivity.runOnUiThread(() -> {
                            for (int i = 0; i < mSlugIds.length; i++) {
                                setGlyphsDrawable(mSlugDrawables[i], 0);
                            }
                        });
                    }
                }
            }
        }

        private void setGlyphsDrawable(Drawable drawable, int brightness) {
            if (brightness <= 0) {
                drawable.setAlpha((int) (0.3f * 255));
            } else {
                int brightnessFactor = (int) (0.4f * 255 + 0.6f * 255 * (brightness
                        / (double) Constants.getMaxBrightness()));
                drawable.setAlpha(brightnessFactor);
            }
        }
    };
}
