/*
 * Copyright (C) 2022-2023 Paranoid Android
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

package co.aospa.glyph.Settings;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Switch;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;

import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import co.aospa.glyph.R;
import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.Manager.SettingsManager;
import co.aospa.glyph.Preference.GlyphAnimationPreference;
import co.aospa.glyph.Utils.ServiceUtils;

public class CallSettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener,
        OnMainSwitchChangeListener {

    private PreferenceScreen mScreen;

    private MainSwitchPreference mSwitchBar;

    private ListPreference mListPreference;

    private GlyphAnimationPreference mGlyphAnimationPreference;

    private Handler mHandler = new Handler();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.glyph_call_settings);

        mScreen = this.getPreferenceScreen();
        getActivity().setTitle(R.string.glyph_settings_call_toggle_title);

        mSwitchBar = (MainSwitchPreference) findPreference(Constants.GLYPH_CALL_SUB_ENABLE);
        mSwitchBar.addOnSwitchChangeListener(this);
        mSwitchBar.setChecked(SettingsManager.isGlyphCallEnabled());

        mListPreference = (ListPreference) findPreference(Constants.GLYPH_CALL_SUB_ANIMATIONS);
        mListPreference.setOnPreferenceChangeListener(this);

        mGlyphAnimationPreference = (GlyphAnimationPreference) findPreference(Constants.GLYPH_CALL_SUB_PREVIEW);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGlyphAnimationPreference.updateAnimation(SettingsManager.isGlyphCallEnabled(),
                SettingsManager.getGlyphCallAnimation());
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String preferenceKey = preference.getKey();

        if (preferenceKey.equals(Constants.GLYPH_CALL_SUB_ANIMATIONS)) {
            mGlyphAnimationPreference.updateAnimation(SettingsManager.isGlyphCallEnabled(),
                newValue.toString());
        }

        //mHandler.post(() -> ServiceUtils.checkGlyphService());

        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        SettingsManager.setGlyphCallEnabled(isChecked);
        ServiceUtils.checkGlyphService();
        mGlyphAnimationPreference.updateAnimation(isChecked,
                SettingsManager.getGlyphCallAnimation());
    }

}
