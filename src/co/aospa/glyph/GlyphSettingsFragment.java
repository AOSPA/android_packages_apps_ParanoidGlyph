/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
 *               2020 Paranoid Android
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

package co.aospa.glyph;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.widget.Switch;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import co.aospa.glyph.R;

public class GlyphSettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener,
        OnMainSwitchChangeListener {

    private MainSwitchPreference mSwitchBar;

    private SeekBarPreference mBrightnessPreference;
    private SwitchPreference mChargingDotPreference;
    private SwitchPreference mChargingLevelPreference;

    private Handler mHandler = new Handler();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.glyph_settings);

        SharedPreferences prefs = getActivity().getSharedPreferences("glyph_settings",
                Activity.MODE_PRIVATE);
        if (savedInstanceState == null && !prefs.getBoolean("first_help_shown", false)) {
            showHelp();
        }

        boolean glyphEnabled = GlyphSettingsManager.isGlyphEnabled(getActivity());

        mSwitchBar = (MainSwitchPreference) findPreference(GlyphConstants.GLYPH_ENABLE);
        mSwitchBar.addOnSwitchChangeListener(this);
        mSwitchBar.setChecked(glyphEnabled);

        mBrightnessPreference = (SeekBarPreference) findPreference(GlyphConstants.GLYPH_BRIGHTNESS);
        mBrightnessPreference.setEnabled(glyphEnabled);
        mBrightnessPreference.setMin(1);
        mBrightnessPreference.setMax(4);
        mBrightnessPreference.setUpdatesContinuously(true);
        mBrightnessPreference.setOnPreferenceChangeListener(this);

        mChargingDotPreference = (SwitchPreference) findPreference(GlyphConstants.GLYPH_CHARGING_DOT_ENABLE);
        mChargingDotPreference.setEnabled(glyphEnabled);
        mChargingDotPreference.setOnPreferenceChangeListener(this);
        mChargingLevelPreference = (SwitchPreference) findPreference(GlyphConstants.GLYPH_CHARGING_LEVEL_ENABLE);
        mChargingLevelPreference.setEnabled(glyphEnabled);
        mChargingLevelPreference.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        mHandler.post(() -> GlyphUtils.checkGlyphService(getActivity()));

        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        GlyphSettingsManager.enableGlyph(getActivity(), isChecked);
        GlyphUtils.checkGlyphService(getActivity());

        mSwitchBar.setChecked(isChecked);

        mChargingDotPreference.setEnabled(isChecked);
        mChargingLevelPreference.setEnabled(isChecked);
    }

    private static class HelpDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.glyph_settings_help_title)
                    .setMessage(R.string.glyph_settings_help_text)
                    .setNegativeButton(R.string.glyph_settings_dialog_ok, (dialog, which) -> dialog.cancel())
                    .create();
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            getActivity().getSharedPreferences("glyph_settings", Activity.MODE_PRIVATE)
                    .edit()
                    .putBoolean("first_help_shown", true)
                    .commit();
        }
    }

    private void showHelp() {
        HelpDialogFragment fragment = new HelpDialogFragment();
        fragment.show(getFragmentManager(), "help_dialog");
    }
}
