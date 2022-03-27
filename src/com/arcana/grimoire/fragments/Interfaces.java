/*
 * Copyright (C) 2019-2021 The ConquerOS Project
 * Copyright (C) 2021 xdroid, xyzprjkt
 * Copyright (C) 2022 ProjectArcana
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

package com.arcana.grimoire.fragments;

import android.app.ActivityManagerNative;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.IWindowManager;
import android.view.View;
import android.view.WindowManagerGlobal;

import androidx.preference.ListPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceScreen;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.internal.util.arcana.ArcanaUtils;
import com.android.internal.util.arcana.udfps.UdfpsUtils;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aospextended.support.preference.CustomSeekBarPreference;
import org.aospextended.support.preference.SystemSettingSwitchPreference;
import org.aospextended.support.preference.SystemSettingSeekBarPreference;
import org.aospextended.support.preference.SystemSettingListPreference;
import org.aospextended.support.preference.SecureSettingSwitchPreference;
import com.android.internal.util.arcana.ArcanaUtils;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class Interfaces extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD = "network_traffic_autohide_threshold";
    private static final String NETWORK_TRAFFIC_LOCATION = "network_traffic_location";
    private static final String NETWORK_TRAFFIC_REFRESH_INTERVAL = "network_traffic_refresh_interval";
    private static final String ALERT_SLIDER_PREF = "alert_slider_notifications";
    private static final String SETTINGS_DASHBOARD_GMS = "settings_dashboard_gms";
    private static final String STATUS_BAR_CLOCK_STYLE = "status_bar_clock";
    private static final String COMBINED_STATUSBAR_ICONS = "show_combined_status_bar_signal_icons";
    private static final String CONFIG_RESOURCE_NAME = "flag_combined_status_bar_signal_icons";
    private static final String SYSTEMUI_PACKAGE = "com.android.systemui";
    private static final String KEY_RIPPLE_EFFECT = "enable_ripple_effect";
    private static final String RETICKER_STATUS = "reticker_status";
    private static final String UDFPS_HAPTIC_FEEDBACK = "udfps_haptic_feedback";

    private CustomSeekBarPreference mThreshold;
    private SystemSettingSeekBarPreference mInterval;
    private ListPreference mNetTrafficLocation;
    private Preference mAlertSlider;
    private SystemSettingListPreference mSettingsDashBoardGms;
    private SystemSettingListPreference mStatusBarClock;
    private SystemSettingSwitchPreference mRippleEffect;
    private FingerprintManager mFingerprintManager;
    private SystemSettingSwitchPreference mRetickerStatus;
    private SystemSettingSwitchPreference mUdfpsHapticFeedback;
    
    SecureSettingSwitchPreference mCombinedIcons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.grimoire_interfaces);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefSet = getPreferenceScreen();
        final Context mContext = getActivity().getApplicationContext();
        final Resources res = mContext.getResources();
        final PackageManager mPm = getActivity().getPackageManager();
        
        mUdfpsHapticFeedback = (SystemSettingSwitchPreference) findPreference(UDFPS_HAPTIC_FEEDBACK);
        if (!UdfpsUtils.hasUdfpsSupport(getContext())) {
            prefSet.removePreference(mUdfpsHapticFeedback);
        }
        
        mRippleEffect = findPreference(KEY_RIPPLE_EFFECT);
        if (mPm.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT) &&
                 mFingerprintManager != null) {
            if (!mFingerprintManager.isHardwareDetected()){
                prefSet.removePreference(mRippleEffect);
            } else {
                mRippleEffect.setChecked((Settings.System.getInt(getContentResolver(),
                        Settings.System.ENABLE_RIPPLE_EFFECT, 1) == 1));
                mRippleEffect.setOnPreferenceChangeListener(this);
            }
        } else {
            prefSet.removePreference(mRippleEffect);
        }

        mSettingsDashBoardGms = (SystemSettingListPreference) findPreference(SETTINGS_DASHBOARD_GMS);
        mSettingsDashBoardGms.setOnPreferenceChangeListener(this);

        boolean udfpsResPkgInstalled = ArcanaUtils.isPackageInstalled(getContext(),
                "org.aospextended.udfps.resources");
        PreferenceCategory udfps = (PreferenceCategory) prefSet.findPreference("udfps_category");
        if (!udfpsResPkgInstalled) {
            prefSet.removePreference(udfps);
        }
        
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
        
        mAlertSlider = (Preference) findPreference(ALERT_SLIDER_PREF);
        boolean mAlertSliderAvailable = res.getBoolean(
                com.android.internal.R.bool.config_hasAlertSlider);
        if (!mAlertSliderAvailable)
            prefSet.removePreference(mAlertSlider);
            
        mRetickerStatus = findPreference(RETICKER_STATUS);
        mRetickerStatus.setChecked((Settings.System.getInt(resolver,
                Settings.System.RETICKER_STATUS, 0) == 1));
        mRetickerStatus.setOnPreferenceChangeListener(this);
            
        mStatusBarClock =
                (SystemSettingListPreference) findPreference(STATUS_BAR_CLOCK_STYLE);

        // Adjust status bar preferences for RTL
        if (getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            mStatusBarClock.setEntries(R.array.status_bar_clock_position_entries_rtl);
            mStatusBarClock.setEntryValues(R.array.status_bar_clock_position_values_rtl);
        }
        
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
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.ARCANA;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mNetTrafficLocation) {
            int location = Integer.valueOf((String) newValue);
            int index = mNetTrafficLocation.findIndexOfValue((String) newValue);
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
            int val = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.NETWORK_TRAFFIC_AUTOHIDE_THRESHOLD, val,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mInterval) {
            int val = (Integer) newValue;
            Settings.System.putIntForUser(resolver,
                    Settings.System.NETWORK_TRAFFIC_REFRESH_INTERVAL, val,
                    UserHandle.USER_CURRENT);
            return true;
        } else if (preference == mSettingsDashBoardGms) {
            ArcanaUtils.showSettingsRestartDialog(getContext());
            return true;
	} else if (preference == mCombinedIcons) {
            boolean enabled = (boolean) newValue;
            Settings.Secure.putInt(resolver,
                    COMBINED_STATUSBAR_ICONS, enabled ? 1 : 0);
            return true;
        } else if (preference == mRetickerStatus) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.RETICKER_STATUS, value ? 1 : 0);
            ArcanaUtils.showSystemUiRestartDialog(getContext());
            return true;
        } else if (preference == mRippleEffect) {
            boolean value = (Boolean) newValue;
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.ENABLE_RIPPLE_EFFECT, value ? 1 : 0);
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
            new BaseSearchIndexProvider(R.xml.grimoire_interfaces);
}
