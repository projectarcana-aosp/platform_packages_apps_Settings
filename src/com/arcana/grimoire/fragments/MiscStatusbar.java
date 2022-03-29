/*
 * Copyright (C) 2019-2021 The ConquerOS Project
 * Copyright (C) 2021 xdroid, xyzprjkt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arcana.grimoire.fragments;

import android.os.Bundle;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;
import org.arcana.support.preference.SecureSettingSwitchPreference;
import org.arcana.support.preference.SystemSettingSwitchPreference;
import org.arcana.support.preference.SystemSettingListPreference;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.arcana.ArcanaUtils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class MiscStatusbar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String COMBINED_STATUSBAR_ICONS = "show_combined_status_bar_signal_icons";
    private static final String CONFIG_RESOURCE_NAME = "flag_combined_status_bar_signal_icons";
    private static final String SYSTEMUI_PACKAGE = "com.android.systemui";
    private static final String KEY_SHOW_VOLTE = "show_volte_icon";
    private static final String KEY_SHOW_VOWIFI = "show_vowifi_icon";
    private static final String KEY_SHOW_FOURG = "show_fourg_icon";
    private static final String KEY_SHOW_DATA_DISABLED = "data_disabled_icon";
    private static final String KEY_USE_OLD_MOBILETYPE = "use_old_mobiletype";

    private SystemSettingSwitchPreference mShowFourg;
    private SystemSettingSwitchPreference mDataDisabled;
    private SystemSettingSwitchPreference mOldMobileType;
    private SystemSettingSwitchPreference mShowVolte;
    private SystemSettingListPreference mShowVowifi;
    SecureSettingSwitchPreference mCombinedIcons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.grimoire_misc_statusbar);
        
        final ContentResolver resolver = getActivity().getContentResolver();
        final Context mContext = getActivity().getApplicationContext();
        final PreferenceScreen prefScreen = getPreferenceScreen();
        
        // combined signal icons
	mCombinedIcons = (SecureSettingSwitchPreference)
                findPreference(COMBINED_STATUSBAR_ICONS);
        Resources sysUIRes = null;
        boolean def = false;
        int resId = 0;
        try {
            sysUIRes = getActivity().getPackageManager()
                    .getResourcesForApplication(SYSTEMUI_PACKAGE);
        } catch (Exception ignored) {
            // If you don't have system UI you have bigger issues
        }
        if (sysUIRes != null) {
            resId = sysUIRes.getIdentifier(
                    CONFIG_RESOURCE_NAME, "bool", SYSTEMUI_PACKAGE);
            if (resId != 0) def = sysUIRes.getBoolean(resId);
        }
        boolean enabled = Settings.Secure.getInt(resolver,
                COMBINED_STATUSBAR_ICONS, def ? 1 : 0) == 1;
        mCombinedIcons.setChecked(enabled);
        mCombinedIcons.setOnPreferenceChangeListener(this);
        // combined signal icons

        mShowVolte = (SystemSettingSwitchPreference) findPreference(KEY_SHOW_VOLTE);
        mShowVowifi = (SystemSettingListPreference) findPreference(KEY_SHOW_VOWIFI);
        mShowFourg = (SystemSettingSwitchPreference) findPreference(KEY_SHOW_FOURG);
        mDataDisabled = (SystemSettingSwitchPreference) findPreference(KEY_SHOW_DATA_DISABLED);
        mOldMobileType = (SystemSettingSwitchPreference) findPreference(KEY_USE_OLD_MOBILETYPE);
        
        if (!ArcanaUtils.isVoiceCapable(getActivity())) {
            prefScreen.removePreference(mShowVolte);
            prefScreen.removePreference(mShowVowifi);
            prefScreen.removePreference(mShowFourg);
            prefScreen.removePreference(mDataDisabled);
            prefScreen.removePreference(mOldMobileType);
        }
        
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ARCANA;
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
	if (preference == mCombinedIcons) {
            boolean enabled = (boolean) objValue;
            Settings.Secure.putInt(resolver,
                    COMBINED_STATUSBAR_ICONS, enabled ? 1 : 0);
            return true;
        }
        return false;
    }

    /**
     * For Search.
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.grimoire_misc_statusbar);
} 
