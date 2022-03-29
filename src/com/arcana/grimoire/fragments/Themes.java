/*
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

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.arcana.ArcanaUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aospextended.support.preference.SystemSettingListPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class Themes extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String SETTINGS_DASHBOARD_GMS = "settings_dashboard_gms";
    
    private SystemSettingListPreference mSettingsDashBoardGms;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.grimoire_themes);
        
        mSettingsDashBoardGms = (SystemSettingListPreference) findPreference(SETTINGS_DASHBOARD_GMS);
        mSettingsDashBoardGms.setOnPreferenceChangeListener(this);
        
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ARCANA;
    }
    

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
    	final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mSettingsDashBoardGms) {
            ArcanaUtils.showSettingsRestartDialog(getContext());
            return true;
        }
        return false;
    }
    
    /**
     * For Search.
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.grimoire_themes);
} 
