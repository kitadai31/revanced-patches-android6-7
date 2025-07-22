package app.revanced.extension.youtube.settings;

import static app.revanced.extension.shared.utils.StringRef.str;
import static app.revanced.extension.youtube.utils.ThemeUtils.setNavigationBarColor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;

import app.revanced.extension.shared.utils.Logger;
import app.revanced.extension.shared.utils.ResourceUtils;
import app.revanced.extension.shared.utils.Utils;
import app.revanced.extension.youtube.settings.preference.ReVancedPreferenceFragment;
import app.revanced.extension.youtube.utils.ThemeUtils;

/**
 * Note that the superclass is overwritten to the superclass of the LicenseMenuActivity at patch time.
 */
@SuppressWarnings("deprecation")
public final class ReVancedSettingsHostActivity extends Activity {
    private static ViewGroup.LayoutParams toolbarLayoutParams;
    private boolean isInitialized = false;

    @SuppressLint("StaticFieldLeak")
    private SearchViewController searchViewController;

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
            // Check sanity first
            String dataString = getIntent().getDataString();
            if (!"revanced_extended_settings_intent".equals(dataString)) {
                // User did not open RVX Settings
                // (For example, the user opened the Open source licenses menu)
                isInitialized = false;
                Logger.printDebug(() -> "onCreate ignored");
                return;
            }

            // Set fragment theme
            setTheme(ThemeUtils.getThemeId());

            // Set Navigation bar color
            setNavigationBarColor(getWindow());

            // Set content
            setContentView(ResourceUtils.getLayoutIdentifier("revanced_settings_with_toolbar"));

            PreferenceFragment fragment = new ReVancedPreferenceFragment();
            createToolbar(fragment);

            getFragmentManager()
                    .beginTransaction()
                    .replace(ResourceUtils.getIdIdentifier("revanced_settings_fragments"), fragment)
                    .commit();

            isInitialized = true;
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
            toolbarTextView.setTextColor(ThemeUtils.getAppForegroundColor());
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) toolbarTextView.getLayoutParams();
                lp.setMarginStart(margin);
                lp.setMarginEnd(margin);
                toolbarTextView.setLayoutParams(lp);
            }
        }
        setToolbarLayoutParams(toolbar);

        // Add Search bar.
        if (fragment instanceof ReVancedPreferenceFragment rvxPreferenceFragment) {
            searchViewController = SearchViewController.addSearchViewComponents(this, toolbar, rvxPreferenceFragment);
        }

        toolBarParent.addView(toolbar, 0);
    }

    @Override
    public void onBackPressed() {
        if (searchViewController != null && searchViewController.isSearchActive()) {
            searchViewController.closeSearch();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (searchViewController != null) {
            searchViewController.handleOrientationChange(configuration.orientation);
        }
    }

    @Override
    protected void onDestroy() {
        isInitialized = false;
        super.onDestroy();
    }

    /**
     * Injection point.
     */
    public boolean isInitialized() {
        return isInitialized;
    }
}
