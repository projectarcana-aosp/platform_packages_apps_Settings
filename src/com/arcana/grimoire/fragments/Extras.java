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
import android.content.Context;
import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import androidx.preference.Preference;
import android.os.SystemProperties;
import androidx.preference.SwitchPreference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.nano.MetricsProto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Extras extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String KEY_PHOTOS_SPOOF = "use_photos_spoof";
    private static final String KEY_GAMES_SPOOF = "use_games_spoof";
    private static final String KEY_STREAM_SPOOF = "use_stream_spoof";

    private static final String SYS_GAMES_SPOOF = "persist.sys.pixelprops.games";
    private static final String SYS_PHOTOS_SPOOF = "persist.sys.pixelprops.gphotos";
    private static final String SYS_STREAM_SPOOF = "persist.sys.pixelprops.streaming";

    private SwitchPreference mPhotosSpoof;
    private SwitchPreference mGamesSpoof;
    private SwitchPreference mStreamSpoof;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.grimoire_extras);
        
        mGamesSpoof = (SwitchPreference) findPreference(KEY_GAMES_SPOOF);
        mGamesSpoof.setChecked(SystemProperties.getBoolean(SYS_GAMES_SPOOF, false));
        mGamesSpoof.setOnPreferenceChangeListener(this);

        mPhotosSpoof = (SwitchPreference) findPreference(KEY_PHOTOS_SPOOF);
        mPhotosSpoof.setChecked(SystemProperties.getBoolean(SYS_PHOTOS_SPOOF, true));
        mPhotosSpoof.setOnPreferenceChangeListener(this);

        mStreamSpoof = (SwitchPreference) findPreference(KEY_STREAM_SPOOF);
        mStreamSpoof.setChecked(SystemProperties.getBoolean(SYS_STREAM_SPOOF, true));
        mStreamSpoof.setOnPreferenceChangeListener(this);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.ARCANA;
    }
    

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mGamesSpoof) {
            boolean value = (Boolean) objValue;
            SystemProperties.set(SYS_GAMES_SPOOF, value ? "true" : "false");
            return true;
        } else if (preference == mPhotosSpoof) {
            boolean value = (Boolean) objValue;
            SystemProperties.set(SYS_PHOTOS_SPOOF, value ? "true" : "false");
            return true;
        } else if (preference == mStreamSpoof) {
            boolean value = (Boolean) objValue;
            SystemProperties.set(SYS_STREAM_SPOOF, value ? "true" : "false");
            return true;
        }
        return false;
    }
} 
