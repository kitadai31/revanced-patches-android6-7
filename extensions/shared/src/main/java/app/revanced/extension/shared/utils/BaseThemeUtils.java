package app.revanced.extension.shared.utils;

import static app.revanced.extension.shared.utils.ResourceUtils.getColor;
import static app.revanced.extension.shared.utils.ResourceUtils.getColorIdentifier;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;

import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public class BaseThemeUtils {
    // Must initially be a non-valid enum ordinal value.
    private static int currentThemeValueOrdinal = -1;

    @Nullable
    private static Boolean isDarkModeEnabled;

    /**
     * Injection point.
     * <p>
     * Updates dark/light mode since YT settings can force light/dark mode
     * which can differ from the global device settings.
     */
    public static void updateLightDarkModeStatus(Enum<?> value) {
        final int newOrdinalValue = value.ordinal();
        if (currentThemeValueOrdinal != newOrdinalValue) {
            currentThemeValueOrdinal = newOrdinalValue;
            isDarkModeEnabled = newOrdinalValue == 1;
            Logger.printDebug(() -> "Dark mode status: " + isDarkModeEnabled);
        }
    }

    /**
     * @return The current dark mode as set by any patch.
     *         Or if none is set, then the system dark mode status is returned.
     */
    public static boolean isDarkModeEnabled() {
        Boolean isDarkMode = isDarkModeEnabled;
        if (isDarkMode != null) {
            return isDarkMode;
        }

        Configuration config = Resources.getSystem().getConfiguration();
        final int currentNightMode = config.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    public static String getColorHexString(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }

    /**
     * Subclasses can override this and provide a themed color.
     */
    public static int getLightColor() {
        return Color.WHITE;
    }

    /**
     * Subclasses can override this and provide a themed color.
     */
    public static int getDarkColor() {
        return Color.BLACK;
    }

    public static String getBackgroundColorHexString() {
        return getColorHexString(getBackgroundColor());
    }

    public static String getForegroundColorHexString() {
        return getColorHexString(getForegroundColor());
    }

    public static int getBackgroundColor() {
        final String colorName = isDarkModeEnabled() ? "yt_black1" : "yt_white1";
        final int colorIdentifier = getColorIdentifier(colorName);
        if (colorIdentifier != 0) {
            return getColor(colorName);
        } else {
            return isDarkModeEnabled() ? getDarkColor() : getLightColor();
        }
    }

    public static int getForegroundColor() {
        final String colorName = isDarkModeEnabled() ? "yt_white1" : "yt_black1";
        final int colorIdentifier = getColorIdentifier(colorName);
        if (colorIdentifier != 0) {
            return getColor(colorName);
        } else {
            return isDarkModeEnabled() ? getLightColor() : getDarkColor();
        }
    }

}
