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
import androidx.preference.PreferenceCategory;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.arcana.ArcanaUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aospextended.support.preference.SystemSettingSwitchPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class Notifications extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String RETICKER_STATUS = "reticker_status";
    
    private boolean alertslider_supported;
    private SystemSettingSwitchPreference mRetickerStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.grimoire_notifications);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();
        final Context mContext = getActivity().getApplicationContext();
        final Resources res = mContext.getResources();
            
        final PreferenceCategory AlertCat = (PreferenceCategory) prefSet
                .findPreference("alert_slider_category");

        alertslider_supported = getResources().getBoolean(
                    com.android.internal.R.bool.config_hasAlertSlider);

        if (!alertslider_supported) {
            prefSet.removePreference(AlertCat);
        }
        
        mRetickerStatus = findPreference(RETICKER_STATUS);
        mRetickerStatus.setChecked((Settings.System.getInt(resolver,
                Settings.System.RETICKER_STATUS, 0) == 1));
        mRetickerStatus.setOnPreferenceChangeListener(this);

    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ARCANA;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
    	final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mRetickerStatus) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                    Settings.System.RETICKER_STATUS, value ? 1 : 0);
            ArcanaUtils.showSystemUiRestartDialog(getContext());
            return true;
        }
        return false;
    }
    
    /**
     * For Search.
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.grimoire_notifications);
} 
