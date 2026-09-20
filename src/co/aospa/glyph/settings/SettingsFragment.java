/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
 *               2020-2024 Paranoid Android
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

package co.aospa.glyph.settings;

import android.os.Bundle;
import android.os.Handler;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.settingslib.PrimarySwitchPreference;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.SettingsBasePreferenceFragment;
import com.android.settingslib.widget.SliderPreference;

import java.util.Arrays;

import co.aospa.glyph.R;
import co.aospa.glyph.utils.Constants;
import co.aospa.glyph.manager.AnimationManager;
import co.aospa.glyph.manager.SettingsManager;
import co.aospa.glyph.utils.ServiceUtils;

public class SettingsFragment extends SettingsBasePreferenceFragment implements OnPreferenceChangeListener {

    private PrimarySwitchPreference mFlipPreference;
    private PrimarySwitchPreference mNotifsPreference;
    private PrimarySwitchPreference mCallPreference;
    private PrimarySwitchPreference mChargingLevelPreference;
    private PrimarySwitchPreference mChargingPowersharePreference;
    private PrimarySwitchPreference mVolumeLevelPreference;
    private PrimarySwitchPreference mMusicVisualizerPreference;

    private Handler mHandler = new Handler();
    private Runnable mBrightnessPreviewOff;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.glyph_settings);

        boolean glyphEnabled = SettingsManager.isGlyphEnabled();

        MainSwitchPreference switchBar = findPreference(Constants.GLYPH_ENABLE);
        switchBar.setOnPreferenceChangeListener(this);
        switchBar.setChecked(glyphEnabled);

        mFlipPreference = (PrimarySwitchPreference) findPreference(Constants.GLYPH_FLIP_ENABLE);
        mFlipPreference.setOnPreferenceChangeListener(this);

        SliderPreference brightnessPreference = findPreference(Constants.GLYPH_BRIGHTNESS);
        brightnessPreference.setMin(1);
        brightnessPreference.setMax(Constants.getBrightnessLevels().length);
        brightnessPreference.setValue(SettingsManager.getGlyphBrightnessSetting());
        brightnessPreference.setUpdatesContinuously(true);
        brightnessPreference.setSliderIncrement(1);
        brightnessPreference.setTickVisible(true);
        brightnessPreference.setIconStart(R.drawable.ic_remove_24dp);
        brightnessPreference.setIconStartContentDescription(
                R.string.glyph_settings_brightness_decrease_desc);
        brightnessPreference.setIconEnd(R.drawable.ic_add_24dp);
        brightnessPreference.setIconEndContentDescription(
                R.string.glyph_settings_brightness_increase_desc);
        brightnessPreference.setOnPreferenceChangeListener(this);

        mNotifsPreference = (PrimarySwitchPreference) findPreference(Constants.GLYPH_NOTIFS_ENABLE);
        mNotifsPreference.setChecked(SettingsManager.isGlyphNotifsEnabled());
        mNotifsPreference.setOnPreferenceChangeListener(this);

        mCallPreference = (PrimarySwitchPreference) findPreference(Constants.GLYPH_CALL_ENABLE);
        mCallPreference.setChecked(SettingsManager.isGlyphCallEnabled());
        mCallPreference.setOnPreferenceChangeListener(this);

        mChargingLevelPreference = (PrimarySwitchPreference) findPreference(Constants.GLYPH_CHARGING_LEVEL_ENABLE);
        mChargingLevelPreference.setOnPreferenceChangeListener(this);

        mChargingPowersharePreference = (PrimarySwitchPreference) findPreference(Constants.GLYPH_CHARGING_POWERSHARE_ENABLE);
        mChargingPowersharePreference.setOnPreferenceChangeListener(this);
        if (!Constants.isPowershareSupported()) {
            mChargingPowersharePreference.setVisible(false);
        }

        mVolumeLevelPreference = (PrimarySwitchPreference) findPreference(Constants.GLYPH_VOLUME_LEVEL_ENABLE);
        mVolumeLevelPreference.setOnPreferenceChangeListener(this);

        mMusicVisualizerPreference = (PrimarySwitchPreference) findPreference(Constants.GLYPH_MUSIC_VISUALIZER_ENABLE);
        mMusicVisualizerPreference.setOnPreferenceChangeListener(this);
        updateEnabledState();

        mHandler.post(() -> ServiceUtils.checkGlyphService());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String preferenceKey = preference.getKey();

        if (preferenceKey.equals(Constants.GLYPH_ENABLE)) {
            boolean isChecked = (Boolean) newValue;
            SettingsManager.enableGlyph(isChecked);
            mHandler.post(this::updateEnabledState);
        }

        if (preferenceKey.equals(Constants.GLYPH_BRIGHTNESS)) {
            int brightness = Constants.getBrightnessLevels()[(Integer) newValue - 1];
            Constants.setBrightness(brightness);
            int patternLen = Constants.getSupportedAnimationPatternLengths()[0];
            int[] preview = new int[patternLen];
            Arrays.fill(preview, Constants.getMaxBrightness());
            new Thread(() -> AnimationManager.updateLedFrame(preview)).start();
            if (mBrightnessPreviewOff != null) mHandler.removeCallbacks(mBrightnessPreviewOff);
            mBrightnessPreviewOff = () -> new Thread(() -> AnimationManager.updateLedFrame(new int[patternLen])).start();
            mHandler.postDelayed(mBrightnessPreviewOff, 500);
        }

        if (preferenceKey.equals(Constants.GLYPH_CALL_ENABLE)) {
            SettingsManager.setGlyphCallEnabled(!mCallPreference.isChecked());
        }

        if (preferenceKey.equals(Constants.GLYPH_NOTIFS_ENABLE)) {
            SettingsManager.setGlyphNotifsEnabled(!mNotifsPreference.isChecked());
        }

        if (preferenceKey.equals(Constants.GLYPH_MUSIC_VISUALIZER_ENABLE)) {
            mHandler.post(this::updateEnabledState);
        }

        mHandler.post(() -> ServiceUtils.checkGlyphService());

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        mFlipPreference.setChecked(SettingsManager.isGlyphFlipEnabled());
        mChargingLevelPreference.setChecked(SettingsManager.isGlyphChargingEnabled());
        mChargingPowersharePreference.setChecked(SettingsManager.isGlyphPowershareEnabled());
        mVolumeLevelPreference.setChecked(SettingsManager.isGlyphVolumeLevelEnabled());
        mCallPreference.setChecked(SettingsManager.isGlyphCallEnabled());
        mNotifsPreference.setChecked(SettingsManager.isGlyphNotifsEnabled());
        mMusicVisualizerPreference.setChecked(SettingsManager.isGlyphMusicVisualizerEnabled());
        updateEnabledState();
    }

    private void updateEnabledState() {
        boolean glyphEnabled = SettingsManager.isGlyphEnabled();
        boolean enabled = glyphEnabled && !SettingsManager.isGlyphMusicVisualizerEnabled();
        for (PrimarySwitchPreference preference : new PrimarySwitchPreference[] {
                mFlipPreference, mNotifsPreference, mCallPreference, mChargingLevelPreference,
                mChargingPowersharePreference, mVolumeLevelPreference}) {
            preference.setEnabled(enabled);
            preference.setSwitchEnabled(enabled);
        }
        mMusicVisualizerPreference.setEnabled(glyphEnabled);
        mMusicVisualizerPreference.setSwitchEnabled(glyphEnabled);
    }

    @Override
    public void onDestroyView() {
        if (mBrightnessPreviewOff != null) {
            mHandler.removeCallbacks(mBrightnessPreviewOff);
            int patternLen = Constants.getSupportedAnimationPatternLengths()[0];
            new Thread(() -> AnimationManager.updateLedFrame(new int[patternLen])).start();
            mBrightnessPreviewOff = null;
        }
        super.onDestroyView();
    }
}
