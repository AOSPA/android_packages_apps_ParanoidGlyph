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

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceScreen;
import androidx.preference.SwitchPreference;

import com.android.internal.util.ArrayUtils;
import com.android.settingslib.widget.MainSwitchPreference;
import com.android.settingslib.widget.OnMainSwitchChangeListener;

import java.util.Collections;
import java.util.List;

import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.Manager.SettingsManager;
import co.aospa.glyph.Preference.GlyphAnimationPreference;
import co.aospa.glyph.R;
import co.aospa.glyph.Utils.AnimationUtils;
import co.aospa.glyph.Utils.ResourceUtils;
import co.aospa.glyph.Utils.ServiceUtils;
import co.aospa.glyph.animation.GlyphAnimation;

public class NotifsSettingsFragment extends PreferenceFragment implements
        OnPreferenceChangeListener,
        OnMainSwitchChangeListener {

    private GlyphAnimationPreference mGlyphAnimationPreference;

    private AnimationUtils mAnimationUtils = null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.glyph_notifs_settings);

        mAnimationUtils = new AnimationUtils(getContext());

        getActivity().setTitle(R.string.glyph_settings_notifs_toggle_title);

        MainSwitchPreference switchBar = (MainSwitchPreference) findPreference(
                Constants.GLYPH_NOTIFS_SUB_ENABLE);
        switchBar.addOnSwitchChangeListener(this);
        switchBar.setChecked(SettingsManager.isGlyphNotifsEnabled());

        PreferenceCategory category = (PreferenceCategory) findPreference(
                Constants.GLYPH_NOTIFS_SUB_CATEGORY);

        ListPreference listPreference = (ListPreference) findPreference(
                Constants.GLYPH_NOTIFS_SUB_ANIMATIONS);
        listPreference.setOnPreferenceChangeListener(this);
        listPreference.setEntries(ResourceUtils.getNotificationAnimations());
        listPreference.setEntryValues(ResourceUtils.getNotificationAnimations());

        mGlyphAnimationPreference = (GlyphAnimationPreference) findPreference(
                Constants.GLYPH_NOTIFS_SUB_PREVIEW);

        PackageManager packageManager = getActivity().getPackageManager();
        List<ApplicationInfo> apps = packageManager.getInstalledApplications(
                PackageManager.GET_GIDS);
        Collections.sort(apps, new ApplicationInfo.DisplayNameComparator(packageManager));
        for (ApplicationInfo app : apps) {
            if (packageManager.getLaunchIntentForPackage(app.packageName) != null
                    && !ArrayUtils.contains(Constants.APPS_TO_IGNORE,
                    app.packageName)) { // apps with launcher intent
                SwitchPreference mSwitchPreference = new SwitchPreference(getContext());
                mSwitchPreference.setKey(app.packageName);
                mSwitchPreference.setTitle(" " + app.loadLabel(
                        packageManager)); // add this space since the layout looks
                // off otherwise
                mSwitchPreference.setIcon(app.loadIcon(packageManager));
                mSwitchPreference.setDefaultValue(true);
                mSwitchPreference.setOnPreferenceChangeListener(this);
                category.addPreference(mSwitchPreference);
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final GlyphAnimation animation = mAnimationUtils.getNotificationAnimation(
                SettingsManager.getGlyphNotifsAnimation());
        mGlyphAnimationPreference.updateAnimation(SettingsManager.isGlyphNotifsEnabled(), animation,
                1500);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final String preferenceKey = preference.getKey();

        if (preferenceKey.equals(Constants.GLYPH_NOTIFS_SUB_ANIMATIONS)) {
            final GlyphAnimation animation = mAnimationUtils.getNotificationAnimation(
                    newValue.toString());
            mGlyphAnimationPreference.updateAnimation(SettingsManager.isGlyphNotifsEnabled(),
                    animation, 1500);
        }

        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        SettingsManager.setGlyphNotifsEnabled(isChecked);
        ServiceUtils.checkGlyphService();
        final GlyphAnimation animation = mAnimationUtils.getNotificationAnimation(
                SettingsManager.getGlyphNotifsAnimation());
        mGlyphAnimationPreference.updateAnimation(isChecked, animation, 1500);
    }

}
