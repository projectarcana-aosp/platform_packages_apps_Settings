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
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.fingerprint.FingerprintManager;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import androidx.preference.PreferenceCategory;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.arcana.ArcanaUtils;
import com.android.internal.util.arcana.udfps.UdfpsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.arcana.support.preference.SystemSettingSwitchPreference;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SearchIndexable(forTarget = SearchIndexable.ALL & ~SearchIndexable.ARC)
public class Lockscreen extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String KEY_RIPPLE_EFFECT = "enable_ripple_effect";
    private static final String UDFPS_HAPTIC_FEEDBACK = "udfps_haptic_feedback";
    private static final String UDFPS_CATEGORY = "udfps_category";
    
    private SystemSettingSwitchPreference mRippleEffect;
    private SystemSettingSwitchPreference mUdfpsHapticFeedback;
    private FingerprintManager mFingerprintManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.grimoire_lockscreen);

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

        boolean udfpsResPkgInstalled = ArcanaUtils.isPackageInstalled(getContext(),
                "org.aospextended.udfps.resources");
        PreferenceCategory udfps = (PreferenceCategory) prefSet.findPreference(UDFPS_CATEGORY);
        if (!udfpsResPkgInstalled) {
            prefSet.removePreference(udfps);
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ARCANA;
    }
    

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
    	final ContentResolver resolver = getActivity().getContentResolver();
        if (preference == mRippleEffect) {
            boolean value = (Boolean) objValue;
            Settings.System.putInt(resolver,
                    Settings.System.ENABLE_RIPPLE_EFFECT, value ? 1 : 0);
            return true;
        }
        return false;
    }
    
    /**
     * For Search.
     */
    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.grimoire_lockscreen);
} 
