package com.google.android.apps.youtube.app.settings.videoquality;

import static app.revanced.extension.shared.utils.StringRef.str;
import static app.revanced.extension.youtube.utils.ThemeUtils.setNavigationBarColor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import app.revanced.extension.shared.utils.Logger;
import app.revanced.extension.shared.utils.ResourceUtils;
import app.revanced.extension.shared.utils.Utils;
import app.revanced.extension.youtube.settings.SearchViewController;
import app.revanced.extension.youtube.settings.preference.ReVancedPreferenceFragment;
import app.revanced.extension.youtube.utils.ThemeUtils;

@SuppressWarnings("deprecation")
public class VideoQualitySettingsActivity extends Activity {
    private static ViewGroup.LayoutParams toolbarLayoutParams;

    @SuppressLint("StaticFieldLeak")
    private static SearchViewController searchViewController;

    public static void setToolbarLayoutParams(Toolbar toolbar) {
        if (toolbarLayoutParams != null) {
            toolbar.setLayoutParams(toolbarLayoutParams);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(Utils.getLocalizedContext(base));
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        try {
            // Set fragment theme
            setTheme(ThemeUtils.getThemeId());

            // Set Navigation bar color
            setNavigationBarColor(getWindow());

            // Set content
            setContentView(ResourceUtils.getLayoutIdentifier("revanced_settings_with_toolbar"));

            // Sanity check
            String dataString = getIntent().getDataString();
            if (!"revanced_extended_settings_intent".equals(dataString)) {
                Logger.printException(() -> "Unknown intent: " + dataString);
                return;
            }

            PreferenceFragment fragment = new ReVancedPreferenceFragment();
            createToolbar(fragment);

            getFragmentManager()
                    .beginTransaction()
                    .replace(ResourceUtils.getIdIdentifier("revanced_settings_fragments"), fragment)
                    .commit();
        } catch (Exception ex) {
            Logger.printException(() -> "onCreate failure", ex);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void createToolbar(PreferenceFragment fragment) {
        // Replace dummy placeholder toolbar.
        // This is required to fix submenu title alignment issue with Android ASOP 15+
        ViewGroup toolBarParent =
                findViewById(ResourceUtils.getIdIdentifier("revanced_toolbar_parent"));
        ViewGroup dummyToolbar =
                toolBarParent.findViewById(ResourceUtils.getIdIdentifier("revanced_toolbar"));
        toolbarLayoutParams = dummyToolbar.getLayoutParams();
        toolBarParent.removeView(dummyToolbar);

        Toolbar toolbar = new Toolbar(toolBarParent.getContext());
        toolbar.setBackgroundColor(ThemeUtils.getToolbarBackgroundColor());
        toolbar.setNavigationIcon(ThemeUtils.getBackButtonDrawable());
        toolbar.setTitle(str("revanced_extended_settings_title"));

        final int margin = Utils.dipToPixels(16);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            toolbar.setTitleMarginStart(margin);
            toolbar.setTitleMarginEnd(margin);
        }
        TextView toolbarTextView = Utils.getChildView(toolbar, false,
                view -> view instanceof TextView);
        if (toolbarTextView != null) {
            toolbarTextView.setTextColor(ThemeUtils.getForegroundColor());
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) toolbarTextView.getLayoutParams();
                lp.setMarginStart(margin);
                lp.setMarginEnd(margin);
                toolbarTextView.setLayoutParams(lp);
            }
        }
        setToolbarLayoutParams(toolbar);

        // Add Search Icon and EditText for ReVancedPreferenceFragment only.
        if (fragment instanceof ReVancedPreferenceFragment rvxPreferenceFragment) {
            searchViewController = SearchViewController.addSearchViewComponents(this, toolbar, rvxPreferenceFragment);
        }

        toolBarParent.addView(toolbar, 0);
    }

    @Override
    public void onBackPressed() {
        if (searchViewController != null && searchViewController.isSearchActive) {
            searchViewController.closeSearch();
            return;
        }
        super.onBackPressed();
    }
}
