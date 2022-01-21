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

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.app.settings.SettingsEnums;
import android.os.Bundle;
import android.util.FeatureFlagUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.settings.R;
import com.android.settings.core.CategoryMixin;
import com.android.settings.core.FeatureFlags;
import com.android.settings.homepage.contextualcards.ContextualCardsFragment;
import com.android.settings.overlay.FeatureFactory;
import com.android.settingslib.core.lifecycle.HideNonSystemOverlayMixin;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import java.util.ArrayList;

/** Settings homepage activity */
public class SettingsHomepageActivity extends FragmentActivity implements
        CategoryMixin.CategoryHandler {

    private static final String TAG = "SettingsHomepageActivity";

    private static final long HOMEPAGE_LOADING_TIMEOUT_MS = 300;

    private View mHomepageView;
    private CategoryMixin mCategoryMixin;
    CollapsingToolbarLayout collapsing_toolbar;

    static ArrayList<String> text=new ArrayList<>();
    static {
        text.add("Arise, Young One.");
	text.add("Welcome Stranger!");
	text.add("One's soul shreds uniqueness.");
        text.add("Calm down young one, catch your breath.");
        text.add("Roquelaire, would you like a cracker?");
        text.add("Ah my little friend, always busy-busy.");
        text.add("Ho ho! You found me!");
        text.add("Greetings, my friend.");
        text.add("Welcome to the Secret Shop!");
        text.add("Ah, some tea while you wait?");
        text.add("Those go together nicely.");
        text.add("Plucked from the Arcanery.");
        text.add("Your ignorance ensured your demise.");
        text.add("Give it not a second thought.");
        text.add("Whosoever stands against me, stands briefly.");
        text.add("Remember me, for I will remember you!");
        text.add("Be mindful of your purpose.");
        text.add("Your foes will fear you now.");
        text.add("My favorite customer!");
        text.add("Business is brisk.");
        text.add("Ahh, hows your journey little one?");
        text.add("Mistakes are always part of one's life, youngster.");
        text.add("Have a lucky day sire!");
        text.add("You can do it Stranger!");
	text.add("It was never wrong to try, young one.");
	text.add("The learned one strikes.");
	text.add("They will never know what hit them.");
	text.add("Turn the tables!");
	text.add("The enemy will be destroyed, no matter the cost!");
	text.add("A good strategist always keeps something in reserve.");
	text.add("Never Settle?");
	text.add("Gratitude unlocks the fullness of life, Milord.");
	text.add("A joker is a little fool who is different from everyone else.");
	text.add("Failure is not Fatal, Customer.");
	text.add("Taking a rest is not a sin young man.");
	text.add("What is truth, but a survivor's story?");
	text.add("In a world without love, death means nothing.");
	text.add("Always appreciate your own endeavors, Milord.");
	text.add("Fear is the first of many foes.");
	text.add("The climb may be long, but the view is worth it.");
	text.add("The waves will drag you down, unless you fight to shore.");
	text.add("The darker the night, the brighter the stars.");
	text.add("Fight and be remembered, or die and be forgotten.");
	text.add("In case no one asked, are you doing fine youngster?");
	text.add("Nothing bears fruit from hatred, but disaster my friend.");
	text.add("Ahh Another day to become a legend.");
	text.add("In case no one told you this, you are awesome!");
	text.add("My dear friend always busy, want some cookies?");
	text.add("Uhmm Never Forget?");
	text.add("Show em what you got stranger!");
    }

    static ArrayList<String> welcome=new ArrayList<>();
    static {
        welcome.add("Settings");
        welcome.add("Hello.");
        welcome.add("Hola.");
        welcome.add("Halo.");
        welcome.add("Ciao.");
        welcome.add("Welcome.");
    }
    
    @Override
    public CategoryMixin getCategoryMixin() {
        return mCategoryMixin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_homepage_container);


        final View root = findViewById(R.id.settings_homepage_container);
	LinearLayout commonCon = root.findViewById(R.id.common_con);
        final Toolbar toolbar = root.findViewById(R.id.search_action_bar);
	collapsing_toolbar =  root.findViewById(R.id.collapsing_toolbar);
        TextView greeter = root.findViewById(R.id.greeter);
	greeter.setText(text.get(randomNum(0, text.size()-1)));

        FeatureFactory.getFactory(this).getSearchFeatureProvider()
                .initSearchToolbar(this /* activity */, toolbar, SettingsEnums.SETTINGS_HOMEPAGE);

	AppBarLayout appBarLayout = root.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, i) -> {

            float abs = ((float) Math.abs(i)) / ((float) appBarLayout1.getTotalScrollRange());
            float f2 = 1.0f - abs;
            //greeter text
            if (f2 == 1.0)
                ObjectAnimator.ofFloat(greeter, View.ALPHA, 1f).setDuration(500).start();
            else
                greeter.setAlpha(0f);

        });

        getLifecycle().addObserver(new HideNonSystemOverlayMixin(this));
	collapsing_toolbar.setTitle(welcome.get(randomNum(0, welcome.size()-1)));
        mCategoryMixin = new CategoryMixin(this);
        getLifecycle().addObserver(mCategoryMixin);

        showFragment(new TopLevelSettings(), R.id.main_content);
        ((FrameLayout) findViewById(R.id.main_content))
                .getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
    }

    private void showFragment(Fragment fragment, int id) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final Fragment showFragment = fragmentManager.findFragmentById(id);

        if (showFragment == null) {
            fragmentTransaction.add(id, fragment);
        } else {
            fragmentTransaction.show(showFragment);
        }
        fragmentTransaction.commit();
    }

    private void initHomepageContainer() {
        final View view = findViewById(R.id.homepage_container);
        // Prevent inner RecyclerView gets focus and invokes scrolling.
        view.setFocusableInTouchMode(true);
        view.requestFocus();
    }

    private int getSearchBoxHeight() {
        final int searchBarHeight = getResources().getDimensionPixelSize(R.dimen.search_bar_height);
        final int searchBarMargin = getResources().getDimensionPixelSize(R.dimen.search_bar_margin);
        return searchBarHeight + searchBarMargin * 2;
    }

    private int randomNum(int min , int max) {
	int r = (max - min) + 1;
	return (int)(Math.random() * r) + min;
    }
}
