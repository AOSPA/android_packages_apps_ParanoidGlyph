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
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.FrameLayout;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import co.aospa.glyph.R;
import co.aospa.glyph.Manager.SettingsManager;

public class GlyphAnimationPreference extends Preference {

    private final String TAG = "GlyphAnimationPreference";
    private final boolean DEBUG = true;

    private Activity mActivity;

    private String animationName;
    private boolean animationTerminated;
    private boolean animationPaused = true;
    private int animationTimeBetween = 0;
    private String[] animationSlugs;
    private ImageView[] animationImgs;

    private View mRootView;
    private final View.OnClickListener mClickListener = v -> performClick(v);

    public GlyphAnimationPreference(Context context) {
        super(context);
        setActivity(context);
        setLayout(R.layout.glyph_settings_preview);
    }
    public GlyphAnimationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setActivity(context);
        setLayout(R.layout.glyph_settings_preview);
    }
    public GlyphAnimationPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setActivity(context);
        setLayout(R.layout.glyph_settings_preview);
    }
    public GlyphAnimationPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        setActivity(context);
        setLayout(defStyleRes);
    }

    private void setLayout(int layoutResource) {
        setLayoutResource(R.layout.glyph_settings_preview_frame);
        mRootView = LayoutInflater.from(getContext())
                .inflate(layoutResource, null, false);
        setShouldDisableView(false);
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

    private <T extends View> T findViewById(int id) {
        return mRootView.findViewById(id);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        holder.itemView.setOnClickListener(mClickListener);

        final boolean selectable = isSelectable();
        holder.itemView.setFocusable(isSelectable());
        holder.itemView.setClickable(isSelectable());

        FrameLayout layout = (FrameLayout) holder.itemView;
        layout.removeAllViews();
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        layout.addView(mRootView);
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
        animationSlugs = mActivity.getResources().getStringArray(R.array.glyph_settings_animations_slugs);
        animationImgs = new ImageView[animationSlugs.length];
        for (int i = 0; i< animationSlugs.length; i++){
            animationImgs[i] = (ImageView) findViewById(mActivity.getResources().getIdentifier("img_"+animationSlugs[i], "id", mActivity.getPackageName()));
        }
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
                while (animationPaused) {}
                if (DEBUG) Log.d(TAG, "Displaying animation | name: " + animationName);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        mActivity.getResources().openRawResource(mActivity.getResources().getIdentifier("anim_"+animationName, "raw", mActivity.getPackageName()))))) {
                    while (true) {
                        String line = reader.readLine(); if (line == null) break;
                        String[] split = line.split(",");
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i< animationSlugs.length; i++){
                                    setGlyphsDrawable(animationImgs[i], animationSlugs[i], Integer.parseInt(split[i]));
                                }
                            }
                        });
                        Thread.sleep(20);
                    }
                    Thread.sleep(animationTimeBetween);
                } catch (Exception e) {
                    if (DEBUG) Log.d(TAG, "Exception while displaying animation | name: " + animationName + " | exception: " + e);
                } finally {
                    if (animationPaused) {
                        if (DEBUG) Log.d(TAG, "Pause displaying animation | name: " + animationName);
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i< animationSlugs.length; i++){
                                    setGlyphsDrawable(animationImgs[i], animationSlugs[i], 0);
                                }
                            }
                        });
                    }
                }
            }
        }
        private void setGlyphsDrawable(ImageView imageView, String slug, int brightness) {
            int imgOn = mActivity.getResources().getIdentifier("ic_"+slug+"_on", "drawable", mActivity.getPackageName());
            int imgOff = mActivity.getResources().getIdentifier("ic_"+slug+"_off", "drawable", mActivity.getPackageName());;
            if (brightness <= 0) {
                imageView.setImageResource(imgOff);
                return;
            }
            imageView.setImageResource(imgOn);
            imageView.getDrawable().setAlpha(brightness*255/4095);
        }
    };
}