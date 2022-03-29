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
import android.view.IWindowManager;
import android.view.View;
import android.view.WindowManagerGlobal;

import androidx.preference.Preference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceScreen;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.nano.MetricsProto;

import org.arcana.support.preference.CustomSeekBarPreference;
import org.arcana.support.preference.SystemSettingSwitchPreference;
import org.arcana.support.preference.SystemSettingSeekBarPreference;
import org.arcana.support.preference.SystemSettingListPreference;
import org.arcana.support.preference.SecureSettingSwitchPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class Statusbar extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD = "network_traffic_autohide_threshold";
    private static final String NETWORK_TRAFFIC_LOCATION = "network_traffic_location";
    private static final String NETWORK_TRAFFIC_REFRESH_INTERVAL = "network_traffic_refresh_interval";
    private static final String STATUS_BAR_CLOCK_STYLE = "status_bar_clock";
    private static final String COMBINED_STATUSBAR_ICONS = "show_combined_status_bar_signal_icons";
    private static final String CONFIG_RESOURCE_NAME = "flag_combined_status_bar_signal_icons";
    private static final String SYSTEMUI_PACKAGE = "com.android.systemui";
    
    private CustomSeekBarPreference mThreshold;
    private SystemSettingSeekBarPreference mInterval;
    private ListPreference mNetTrafficLocation;
    private SystemSettingListPreference mStatusBarClock;
    
    SecureSettingSwitchPreference mCombinedIcons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.grimoire_statusbar);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();
        final Context mContext = getActivity().getApplicationContext();
        final Resources res = mContext.getResources();
        
        // Network traffic location
        mNetTrafficLocation = (ListPreference) findPreference(NETWORK_TRAFFIC_LOCATION);
        int location = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_LOCATION, 0, UserHandle.USER_CURRENT);
        mNetTrafficLocation.setOnPreferenceChangeListener(this);

        int value = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, 1, UserHandle.USER_CURRENT);
        mThreshold = (CustomSeekBarPreference) findPreference(NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD);
        mThreshold.setValue(value);
        mThreshold.setOnPreferenceChangeListener(this);

        int val = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_REFRESH_INTERVAL, 1, UserHandle.USER_CURRENT);
        mInterval = (SystemSettingSeekBarPreference) findPreference(NETWORK_TRAFFIC_REFRESH_INTERVAL);
        mInterval.setValue(val);
        mInterval.setOnPreferenceChangeListener(this);

        int netMonitorEnabled = Settings.System.getIntForUser(resolver,
                Settings.System.NETWORK_TRAFFIC_STATE, 0, UserHandle.USER_CURRENT);
        if (netMonitorEnabled == 1) {
            mNetTrafficLocation.setValue(String.valueOf(location+1));
            updateTrafficLocation(location+1);
        } else {
            mNetTrafficLocation.setValue("0");
            updateTrafficLocation(0);
        }
        mNetTrafficLocation.setSummary(mNetTrafficLocation.getEntry());
        // Network traffic location
        
        // statusbar clock
        mStatusBarClock =
                (SystemSettingListPreference) findPreference(STATUS_BAR_CLOCK_STYLE);

        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            mStatusBarClock.setEntries(R.array.status_bar_clock_position_entries_rtl);
            mStatusBarClock.setEntryValues(R.array.status_bar_clock_position_values_rtl);
        }
        // statusbar clock
        
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
        
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ARCANA;
    }
    

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNetTrafficLocation) {
            int location = Integer.valueOf((String) objValue);
            int index = mNetTrafficLocation.findIndexOfValue((String) objValue);
            mNetTrafficLocation.setSummary(mNetTrafficLocation.getEntries()[index]);
            if (location > 0) {
                // Convert the selected location mode from our list {0,1,2} and store it to "view location" setting: 0=sb; 1=expanded sb
                Settings.System.putIntForUser(resolver,
                        Settings.System.NETWORK_TRAFFIC_LOCATION, location-1, UserHandle.USER_CURRENT);
                // And also enable the net monitor
                Settings.System.putIntForUser(resolver,
                        Settings.System.NETWORK_TRAFFIC_STATE, 1, UserHandle.USER_CURRENT);
            } else { // Disable net monitor completely
                Settings.System.putIntForUser(resolver,
                        Settings.System.NETWORK_TRAFFIC_STATE, 0, UserHandle.USER_CURRENT);
            }
            updateTrafficLocation(location);
            return true;
        } else if (preference == mThreshold) {
            int val = (Integer) objValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, val,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mInterval) {
            int val = (Integer) objValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.NETWORK_TRAFFIC_REFRESH_INTERVAL, val,
                    UserHandle.USER_CURRENT);
            return true;
	} else if (preference == mCombinedIcons) {
            boolean enabled = (boolean) objValue;
            Settings.Secure.putInt(resolver,
                    COMBINED_STATUSBAR_ICONS, enabled ? 1 : 0);
            return true;
        }
        return false;
    }
    
    public void updateTrafficLocation(int location) {
        switch(location){
            case 0:
                mThreshold.setEnabled(false);
                mInterval.setEnabled(false);
                break;
            case 1:
            case 2:
                mThreshold.setEnabled(true);
                mInterval.setEnabled(true);
                break;
            default:
                break;
        }
    }

    /**
     * For Search.
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.grimoire_statusbar);
} 
