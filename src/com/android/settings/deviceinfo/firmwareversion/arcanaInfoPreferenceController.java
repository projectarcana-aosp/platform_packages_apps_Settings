/*
 * Copyright (C) 2020 Wave-OS
 * Copyright (C) 2022 Project Arcana
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

package com.android.settings.deviceinfo.firmwareversion;

import android.content.Context;
import android.os.Build;
import android.os.SystemProperties;
import android.widget.TextView;

import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

public class arcanaInfoPreferenceController extends AbstractPreferenceController {

    private static final String KEY_ARCANA_INFO = "arcana_info";

    private static final String PROP_ARCANA_VERSION = "ro.arcana.version";
    private static final String PROP_ARCANA_VERSION_CODE = "ro.arcana.code";
    private static final String PROP_ARCANA_RELEASETYPE = "ro.arcana.releasetype";
    private static final String PROP_ARCANA_MAINTAINER = "ro.arcana.maintainer";
    private static final String PROP_ARCANA_DEVICE = "ro.arcana.device";

    public arcanaInfoPreferenceController(Context context) {
        super(context);
    }

    private String getDeviceName() {
        String device = SystemProperties.get(PROP_ARCANA_DEVICE, "");
        if (device.equals("")) {
            device = Build.MANUFACTURER + " " + Build.MODEL;
        }
        return device;
    }

    private String getarcanaVersion() {
        final String version = SystemProperties.get(PROP_ARCANA_VERSION,
                this.mContext.getString(R.string.device_info_default));
        final String versionCode = SystemProperties.get(PROP_ARCANA_VERSION_CODE,
                this.mContext.getString(R.string.device_info_default));

        return version + " | " + versionCode;
    }

    private String getarcanaReleaseType() {
        final String releaseType = SystemProperties.get(PROP_ARCANA_RELEASETYPE,
                this.mContext.getString(R.string.device_info_default));

        return releaseType.substring(0, 1).toUpperCase() +
                 releaseType.substring(1).toLowerCase();
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        final LayoutPreference arcanaInfoPreference = screen.findPreference(KEY_ARCANA_INFO);
        final TextView version = (TextView) arcanaInfoPreference.findViewById(R.id.version_message);
        final TextView device = (TextView) arcanaInfoPreference.findViewById(R.id.device_message);
        final TextView releaseType = (TextView) arcanaInfoPreference.findViewById(R.id.release_type_message);
        final TextView maintainer = (TextView) arcanaInfoPreference.findViewById(R.id.maintainer_message);
        final String arcanaVersion = getarcanaVersion();
        final String arcanaDevice = getDeviceName();
        final String arcanaReleaseType = getarcanaReleaseType();
        final String arcanaMaintainer = SystemProperties.get(PROP_ARCANA_MAINTAINER,
                this.mContext.getString(R.string.device_info_default));
        version.setText(arcanaVersion);
        device.setText(arcanaDevice);
        releaseType.setText(arcanaReleaseType);
        maintainer.setText(arcanaMaintainer);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return KEY_ARCANA_INFO;
    }
}
