/*
 * Copyright (C) 2022 Paranoid Android
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
import android.os.Handler;
import android.widget.Switch;

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

import co.aospa.glyph.R;
import co.aospa.glyph.Constants.Constants;
import co.aospa.glyph.Manager.SettingsManager;
import co.aospa.glyph.Utils.ServiceUtils;

public class NotifsSettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener,
        OnMainSwitchChangeListener {

    private PreferenceScreen mScreen;

    private MainSwitchPreference mSwitchBar;
    private PreferenceCategory mCategory;

    private List<ApplicationInfo> mApps;
    private PackageManager mPackageManager;

    private Handler mHandler = new Handler();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.glyph_notifs_settings);

        mScreen = this.getPreferenceScreen();
        getActivity().setTitle(R.string.glyph_settings_notifs_toggle_title);

        mSwitchBar = (MainSwitchPreference) findPreference(Constants.GLYPH_NOTIFS_SUB_ENABLE);
        mSwitchBar.addOnSwitchChangeListener(this);
        mSwitchBar.setChecked(SettingsManager.isGlyphNotifsEnabled(getActivity()));

        mCategory = (PreferenceCategory) findPreference(Constants.GLYPH_NOTIFS_SUB_CATEGORY);

        mPackageManager = getActivity().getPackageManager();
        mApps = mPackageManager.getInstalledApplications(PackageManager.GET_GIDS);
        Collections.sort(mApps, new ApplicationInfo.DisplayNameComparator(mPackageManager));
        for (ApplicationInfo app : mApps) {
            if(mPackageManager.getLaunchIntentForPackage(app.packageName) != null  && !ArrayUtils.contains(Constants.APPSTOIGNORENOTIFS, app.packageName)) { // apps with launcher intent
                SwitchPreference mSwitchPreference = new SwitchPreference(mScreen.getContext());
                mSwitchPreference.setKey(app.packageName);
                mSwitchPreference.setTitle(" " + app.loadLabel(mPackageManager).toString()); // add this space since the layout looks off otherwise
                mSwitchPreference.setIcon(app.loadIcon(mPackageManager));
                mSwitchPreference.setDefaultValue(true);
                mSwitchPreference.setOnPreferenceChangeListener(this);
                mCategory.addPreference(mSwitchPreference);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        //mHandler.post(() -> ServiceUtils.checkGlyphService(getActivity()));

        return true;
    }

    @Override
    public void onSwitchChanged(Switch switchView, boolean isChecked) {
        SettingsManager.setGlyphNotifsEnabled(getActivity(), isChecked);
        ServiceUtils.checkGlyphService(getActivity());
    }

}
