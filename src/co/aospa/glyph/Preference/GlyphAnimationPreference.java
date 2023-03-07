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
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.android.settingslib.widget.LayoutPreference;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import co.aospa.glyph.R;
import co.aospa.glyph.Manager.SettingsManager;

public class GlyphAnimationPreference extends LayoutPreference {

    private final String TAG = "GlyphAnimationPreference";
    private final boolean DEBUG = true;

    private boolean animationStopped;
    private boolean animationPaused;
    private boolean animationReset;

    private Thread animationThread;

    public GlyphAnimationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public GlyphAnimationPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public GlyphAnimationPreference(Context context, int resource) {
        super(context, resource);
    }
    public GlyphAnimationPreference(Context context, View view) {
        super(context, view);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
    }

    public void initAnimation(Activity activity, Boolean isCall) {
        animationThread = new Thread() {
            @Override
            public void run() {
                String[] slugs = activity.getResources().getStringArray(R.array.glyph_settings_animations_slugs);
                ImageView[] imgs = new ImageView[slugs.length];
                for (int i = 0; i< slugs.length; i++){
                    imgs[i] = (ImageView) findViewById(activity.getResources().getIdentifier("img_"+slugs[i], "id", activity.getPackageName()));
                }
                while (!animationStopped) {
                    String name = SettingsManager.getGlyphCallAnimation(activity);
                    if (!isCall) name = SettingsManager.getGlyphNotifsAnimation(activity);
                    if (DEBUG) Log.d(TAG, "Displaying animation | name: " + name);
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                            activity.getResources().openRawResource(activity.getResources().getIdentifier("anim_"+name, "raw", activity.getPackageName()))))) {
                        while (true) {
                            if (animationReset || animationStopped) throw new InterruptedException();
                            String line = reader.readLine(); if (line == null) break;
                            String[] split = line.split(",");
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i< slugs.length; i++){
                                        setGlyphsDrawable(imgs[i], slugs[i], Integer.parseInt(split[i]));
                                    }
                                }
                            });
                            Thread.sleep(20);
                        }
                        if (!isCall) {
                            Thread.sleep(1480);
                        }
                    } catch (Exception e) {
                        if (DEBUG) Log.d(TAG, "Exception while displaying animation | name: " + name + " | exception: " + e);
                        if (animationReset) animationReset = false;
                    } finally {
                        if (animationPaused || animationStopped) {
                            if (DEBUG) Log.d(TAG, "Done displaying animation | name: " + name);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    for (int i = 0; i< slugs.length; i++){
                                        setGlyphsDrawable(imgs[i], slugs[i], 0);
                                    }
                                }
                            });
                            while (animationPaused) {}
                        }
                    }
                }
            }
            private void setGlyphsDrawable(ImageView imageView, String slug, int brightness) {
                int imgOn = activity.getResources().getIdentifier("ic_"+slug+"_on", "drawable", activity.getPackageName());
                int imgOff = activity.getResources().getIdentifier("ic_"+slug+"_off", "drawable", activity.getPackageName());;
                if (brightness <= 0) {
                    imageView.setImageResource(imgOff);
                    return;
                }
                imageView.setImageResource(imgOn);
                imageView.getDrawable().setAlpha(brightness*255/4095);
            }
        };
    }

    public void startAnimation() {
        if (animationThread != null) {
            animationThread.start();
        }
    }

    public void stopAnimation() {
        animationStopped = true;
    }

    public void updateAnimation(Boolean b) {
        animationPaused = !b;
        animationReset = true;
    }
}