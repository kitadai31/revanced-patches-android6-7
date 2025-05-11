package app.revanced.extension.youtube.patches.utils;

import app.revanced.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class FreezeLayoutUpdatesPatch {

    private static final boolean enabled = Settings.FREEZE_LAYOUT_UPDATES.get();
    private static final boolean disableLayoutUpdates = Settings.DISABLE_LAYOUT_UPDATES.get();
    //public static boolean freezeTimestamp = Settings.FREEZE_LAYOUT_UPDATES_TIMESTAMP.get();


    public static String getHotConfigGroup(String original) {
        if (enabled) {
            String savedValue = Settings.FROZEN_HOT_CONFIG_GROUP.get();
            // Default value of config groups are null, but ReVanced's StringSetting doesn't support saving null.
            if (disableLayoutUpdates || savedValue.isEmpty()) {
                return null;
            }
            return savedValue;
        }
        return original;
    }

    public static String getHotHashData(String original) {
        if (enabled) {
            if (disableLayoutUpdates) {
                return "";
            }
            return Settings.FROZEN_HOT_HASH_DATA.get();
        }
        return original;
    }

    public static String getColdConfigGroup(String original) {
        if (enabled) {
            String savedValue = Settings.FROZEN_COLD_CONFIG_GROUP.get();
            // Default value of config groups are null, but ReVanced's StringSetting doesn't support saving null.
            if (disableLayoutUpdates || savedValue.isEmpty()) {
                return null;
            }
            return savedValue;
        }
        return original;
    }

    public static String getColdHashData(String original) {
        if (enabled) {
            if (disableLayoutUpdates) {
                return "";
            }
            return Settings.FROZEN_COLD_HASH_DATA.get();
        }
        return original;
    }

    /*
    public static long getHotStoredTimestamp(long original) {
        if (enabled && disableLayoutUpdates) {
            return -1;
        }
        if (enabled && freezeTimestamp) {
            return Settings.FROZEN_HOT_STORED_TIMESTAMP.get();
        }
        return original;
    }

    public static long getColdStoredTimestamp(long original) {
        if (enabled && disableLayoutUpdates) {
            return -1;
        }
        if (enabled && freezeTimestamp) {
            return Settings.FROZEN_COLD_STORED_TIMESTAMP.get();
        }
        return original;
    }
    */
}
