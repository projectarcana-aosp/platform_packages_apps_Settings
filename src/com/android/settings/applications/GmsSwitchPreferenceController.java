/*
 * Copyright (C) 2022 Project Kaleidoscope
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.android.settings.applications;

import android.content.Context;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.preference.Preference;

import com.android.settings.core.TogglePreferenceController;
import com.android.settings.R;

public class GmsSwitchPreferenceController extends TogglePreferenceController {

    private Context mContext;
    private Preference mPreference;

    public GmsSwitchPreferenceController(Context context, String key) {
        super(context, key);
        mContext = context;
    }

    @Override
    public void updateState(Preference preference) {
        super.updateState(preference);
        mPreference = preference;
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    @Override
    public boolean isChecked() {
        return Settings.Secure.getInt(mContext.getContentResolver(),
                                        Settings.Secure.GMS_ENABLED, 0) == 1;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        if (!isChecked) {
            new AlertDialog.Builder(mContext)
                .setTitle(R.string.gms_disable_alert_title)
                .setMessage(R.string.gms_disable_alert_message)
                .setPositiveButton(R.string.gms_disable_alert_positive_btn,
                    (dialog, which) -> {
                        Settings.Secure.putInt(mContext.getContentResolver(),
                                Settings.Secure.GMS_ENABLED, 0);
                        updateState(mPreference);
                    })
                .setNegativeButton(R.string.gms_disable_alert_negative_btn, null)
                .create().show();
            return false;
        } else {
            Settings.Secure.putInt(mContext.getContentResolver(),
                                    Settings.Secure.GMS_ENABLED, 1);
            return true;
        }
    }
    
    @Override
    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_apps;
    }
}
