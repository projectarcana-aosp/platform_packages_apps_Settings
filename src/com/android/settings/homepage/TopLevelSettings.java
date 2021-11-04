/*
 * Copyright (C) 2018 The Android Open Source Project
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

package com.android.settings.homepage;

import static com.android.settings.search.actionbar.SearchMenuController.NEED_SEARCH_ICON_IN_ACTION_BAR;
import static com.android.settingslib.search.SearchIndexable.MOBILE;

import android.app.Activity;
import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.Utils;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.support.SupportPreferenceController;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.search.SearchIndexable;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settings.widget.EntityHeaderController;

@SearchIndexable(forTarget = MOBILE)
public class TopLevelSettings extends DashboardFragment implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TAG = "TopLevelSettings";
    private static final String KEY_USER_CARD = "top_level_usercard";

    public TopLevelSettings() {
        final Bundle args = new Bundle();
        // Disable the search icon because this page uses a full search view in actionbar.
        args.putBoolean(NEED_SEARCH_ICON_IN_ACTION_BAR, false);
        setArguments(args);
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.top_level_settings;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public void onStart() {
        super.onStart();
        onUserCard();
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.DASHBOARD_SUMMARY;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        use(SupportPreferenceController.class).setActivity(getActivity());
    }

    @Override
    public int getHelpResource() {
        // Disable the help icon because this page uses a full search view in actionbar.
        return 0;
    }

    @Override
    public Fragment getCallbackFragment() {
        return this;
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        new SubSettingLauncher(getActivity())
                .setDestination(pref.getFragment())
                .setArguments(pref.getExtras())
                .setSourceMetricsCategory(caller instanceof Instrumentable
                        ? ((Instrumentable) caller).getMetricsCategory()
                        : Instrumentable.METRICS_CATEGORY_UNKNOWN)
                .setTitleRes(-1)
                .launch();
        return true;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        super.onCreatePreferences(savedInstanceState, rootKey);
        final PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            return;
        }
        // Tint the homepage icons
        final int tintColor = Utils.getHomepageIconColor(getContext());
        final int count = screen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            final Preference preference = screen.getPreference(i);
            if (preference == null) {
                break;
            }
            final Drawable icon = preference.getIcon();
            if (icon != null) {
                icon.setTint(tintColor);
            }

	    onSetPrefCard();

        }
    }

    private void onSetPrefCard() {
	final PreferenceScreen screen = getPreferenceScreen();
        final int count = screen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            final Preference preference = screen.getPreference(i);

 	    String key = preference.getKey();

	    if (key.equals("top_level_network")){
	        preference.setLayoutResource(R.layout.card_view_pref_top);
	    }
            if (key.equals("top_level_connected_devices")){
                preference.setLayoutResource(R.layout.card_view_pref_middle);
            }
            if (key.equals("top_level_accounts")){
                preference.setLayoutResource(R.layout.card_view_pref_bottom);
            }
            if (key.equals("top_level_octavi_lab")){
                preference.setLayoutResource(R.layout.card_view_pref_top);
            }
            if (key.equals("top_level_wallpaper")){
                preference.setLayoutResource(R.layout.card_view_pref_bottom);
            }
            if (key.equals("top_level_battery")){
                preference.setLayoutResource(R.layout.card_view_pref_top);
            }
            if (key.equals("top_level_display")){
                preference.setLayoutResource(R.layout.card_view_pref_middle);
            }
            if (key.equals("top_level_sound")){
                preference.setLayoutResource(R.layout.card_view_pref_middle);
            }
            if (key.equals("top_level_apps")){
                preference.setLayoutResource(R.layout.card_view_pref_bottom);
            }
            if (key.equals("top_level_storage")){
                preference.setLayoutResource(R.layout.card_view_pref_top);
            }
            if (key.equals("top_level_notification")){
                preference.setLayoutResource(R.layout.card_view_pref_middle);
            }
            if (key.equals("top_level_location")){
                preference.setLayoutResource(R.layout.card_view_pref_middle);
            }
            if (key.equals("top_level_accessibility")){
                preference.setLayoutResource(R.layout.card_view_pref_bottom);
            }
            if (key.equals("top_level_security")){
                preference.setLayoutResource(R.layout.card_view_pref_top);
            }
            if (key.equals("top_level_privacy")){
                preference.setLayoutResource(R.layout.card_view_pref_middle);
            }
            if (key.equals("top_level_emergency")){
                preference.setLayoutResource(R.layout.card_view_pref_bottom);
            }
            if (key.equals("top_level_system")){
                preference.setLayoutResource(R.layout.card_view_pref_top);
            }
            if (key.equals("top_level_about_device")){
                preference.setLayoutResource(R.layout.card_view_pref_bottom);
            }
            if (key.equals("top_level_usercard")){
                preference.setLayoutResource(R.layout.usercard);
            }
            if (key.equals("dashboard_tile_pref_com.google.android.apps.wellbeing.settings.TopLevelSettingsActivity")){
                preference.setLayoutResource(R.layout.card_view_pref_top);
            }
            if (key.equals("dashboard_tile_pref_com.google.android.gms.app.settings.GoogleSettingsIALink")){
                preference.setLayoutResource(R.layout.card_view_pref_bottom);
            }
	}
    }

    private void onUserCard() {
        final LayoutPreference headerPreference =
                (LayoutPreference) getPreferenceScreen().findPreference(KEY_USER_CARD);
        final View userCard = headerPreference.findViewById(R.id.entity_header);
        final TextView textview = headerPreference.findViewById(R.id.summary);
        final Activity context = getActivity();
        final Bundle bundle = getArguments();
        final EntityHeaderController controller = EntityHeaderController
                .newInstance(context, this, userCard)
                .setRecyclerView(getListView(), getSettingsLifecycle())
                .setButtonActions(EntityHeaderController.ActionType.ACTION_NONE,
                        EntityHeaderController.ActionType.ACTION_NONE);

        userCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$UserSettingsActivity"));
                startActivity(intent);
            }
        });

        final int iconId = bundle.getInt("icon_id", 0);
        if (iconId == 0) {
            final UserManager userManager = (UserManager) getActivity().getSystemService(
                    Context.USER_SERVICE);
            final UserInfo info = Utils.getExistingUser(userManager,
                    android.os.Process.myUserHandle());
            controller.setLabel(info.name);
            controller.setIcon(
                    com.android.settingslib.Utils.getUserIcon(getActivity(), userManager, info));
        }

        controller.done(context, true /* rebindActions */);
    }

    @Override
    protected boolean shouldForceRoundedIcon() {
        return getContext().getResources()
                .getBoolean(R.bool.config_force_rounded_icon_TopLevelSettings);
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.top_level_settings) {

                @Override
                protected boolean isPageSearchEnabled(Context context) {
                    // Never searchable, all entries in this page are already indexed elsewhere.
                    return false;
                }
            };
}
