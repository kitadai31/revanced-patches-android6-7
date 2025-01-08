package app.revanced.extension.youtube.patches.utils;

import static app.revanced.extension.shared.settings.preference.AbstractPreferenceFragment.showRestartDialog;
import static app.revanced.extension.shared.utils.StringRef.str;
import static app.revanced.extension.shared.utils.Utils.runOnMainThreadDelayed;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import app.revanced.extension.shared.settings.BaseSettings;
import app.revanced.extension.shared.settings.BooleanSetting;
import app.revanced.extension.youtube.settings.Settings;
import app.revanced.extension.youtube.utils.ExtendedUtils;

@SuppressWarnings("unused")
public class InitializationPatch {
    private static final BooleanSetting SETTINGS_INITIALIZED = BaseSettings.SETTINGS_INITIALIZED;

    /**
     * Some layouts that depend on litho do not load when the app is first installed.
     * (Also reproduced on unPatched YouTube)
     * <p>
     * To fix this, show the restart dialog when the app is installed for the first time.
     */
    public static void onCreate(@NonNull Activity mActivity) {
        if (SETTINGS_INITIALIZED.get()) {
            return;
        }
        // First run with new RVX
        // migrate SponsorBlock private user id if updated from from v2.160.x
        SharedPreferences sbPrefs = mActivity.getSharedPreferences("sponsor-block", Context.MODE_PRIVATE);
        String sbuuid = sbPrefs.getString("uuid", null);
        if (sbuuid != null) {
            Settings.SB_PRIVATE_USER_ID.save(sbuuid);
            sbPrefs.edit().clear().apply();  // clear all old sponsor-block prefs
        }
        // migrate RYD user id if updated from v2.160.x
        SharedPreferences rydPrefs = mActivity.getSharedPreferences("ryd", Context.MODE_PRIVATE);
        String rydId = rydPrefs.getString("ryd_userId", null);
        if (rydId != null) {
            Settings.RYD_USER_ID.save(rydId);
            rydPrefs.edit().clear().apply();  // clear all old ryd prefs
        }
        runOnMainThreadDelayed(() -> showRestartDialog(mActivity, str("revanced_extended_restart_first_run"), 3500), 500);
        runOnMainThreadDelayed(() -> SETTINGS_INITIALIZED.save(true), 1000);
    }

    public static void setExtendedUtils(@NonNull Activity mActivity) {
        ExtendedUtils.setPlayerFlyoutMenuAdditionalSettings();
    }
}