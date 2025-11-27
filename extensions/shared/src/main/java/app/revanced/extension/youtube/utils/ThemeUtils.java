package app.revanced.extension.youtube.utils;

import static app.revanced.extension.shared.utils.ResourceUtils.getColor;
import static app.revanced.extension.shared.utils.ResourceUtils.getDrawable;
import static app.revanced.extension.shared.utils.ResourceUtils.getStyleIdentifier;
import static app.revanced.extension.shared.utils.Utils.clamp;
import static app.revanced.extension.shared.utils.Utils.getResources;
import static app.revanced.extension.shared.utils.Utils.isSDKAbove;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Window;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import app.revanced.extension.shared.utils.BaseThemeUtils;
import app.revanced.extension.shared.utils.Logger;

@SuppressWarnings({"unused", "SameParameterValue"})
public class ThemeUtils extends BaseThemeUtils {

    public static int getThemeId() {
        final String themeName = isDarkTheme()
                ? "Theme.YouTube.Settings.Dark"
                : "Theme.YouTube.Settings";

        return getStyleIdentifier(themeName);
    }

    public static Drawable getBackButtonDrawable() {
        final String drawableName = isDarkTheme()
                ? "yt_outline_arrow_left_white_24"
                : "yt_outline_arrow_left_black_24";

        return getDrawable(drawableName);
    }

    public static Drawable getTrashButtonDrawable() {
        final String drawableName = isDarkTheme()
                ? "yt_outline_trash_can_white_24"
                : "yt_outline_trash_can_black_24";

        return getDrawable(drawableName);
    }

    public static int getDialogBackgroundColor() {
        final String colorName = isDarkTheme()
                ? "yt_black1"
                : "yt_white1";

        return getColor(colorName);
    }

    /**
     * Adjusts the background color based on the current theme.
     *
     * @param isHandleBar If true, applies a stronger darkening factor (0.9) for the handle bar in light theme;
     *                    if false, applies a standard darkening factor (0.95) for other elements in light theme.
     * @return A modified background color, lightened by 20% for dark themes or darkened by 5% (or 10% for handle bar)
     *         for light themes to ensure visual contrast.
     */
    public static int getAdjustedBackgroundColor(boolean isHandleBar) {
        final int baseColor = getDialogBackgroundColor();
        float darkThemeFactor = isHandleBar ? 1.25f : 1.115f; // 1.25f for handleBar, 1.115f for others in dark theme.
        float lightThemeFactor = isHandleBar ? 0.9f : 0.95f; // 0.9f for handleBar, 0.95f for others in light theme.
        return isDarkTheme()
                ? adjustColorBrightness(baseColor, darkThemeFactor)  // Lighten for dark theme.
                : adjustColorBrightness(baseColor, lightThemeFactor); // Darken for light theme.
    }

    /**
     * Since {@link android.widget.Toolbar} is used instead of {@link android.support.v7.widget.Toolbar},
     * We have to manually specify the toolbar background.
     *
     * @return toolbar background color.
     */
    public static int getToolbarBackgroundColor() {
        final String colorName = isDarkTheme()
                ? "yt_black3"   // Color names used in the light theme
                : "yt_white1";  // Color names used in the dark theme

        return getColor(colorName);
    }

    public static int getPressedElementColor() {
        String colorHex = isDarkTheme()
                ? lightenColor(getBackgroundColorHexString(), 15)
                : darkenColor(getBackgroundColorHexString(), 15);
        return Color.parseColor(colorHex);
    }

    public static GradientDrawable getSearchViewShape() {
        GradientDrawable shape = new GradientDrawable();

        String currentHex = getBackgroundColorHexString();
        String defaultHex = isDarkTheme() ? "#1A1A1A" : "#E5E5E5";

        String finalHex;
        if (currentThemeColorIsBlackOrWhite()) {
            shape.setColor(Color.parseColor(defaultHex)); // stock black/white color
            finalHex = defaultHex;
        } else {
            // custom color theme
            String adjustedColor = isDarkTheme()
                    ? lightenColor(currentHex, 15)
                    : darkenColor(currentHex, 15);
            shape.setColor(Color.parseColor(adjustedColor));
            finalHex = adjustedColor;
        }
        Logger.printDebug(() -> "searchbar color: " + finalHex);

        shape.setCornerRadius(30 * getResources().getDisplayMetrics().density);

        return shape;
    }

    /**More actions
     * Sets the system navigation bar color for the activity.
     * Applies the background color obtained from {@link #getBackgroundColor()} to the navigation bar.
     * For Android 10 (API 29) and above, enforces navigation bar contrast to ensure visibility.
     */
    public static void setNavigationBarColor(@Nullable Window window) {
        if (window == null) {
            Logger.printDebug(() -> "Cannot set navigation bar color, window is null");
            return;
        }

        window.setNavigationBarColor(getBackgroundColor());
        if (isSDKAbove(29)) {
            window.setNavigationBarContrastEnforced(true);
        }
    }

    /**
     * Adjusts the brightness of a color by lightening or darkening it based on the given factor.
     * <p>
     * If the factor is greater than 1, the color is lightened by interpolating toward white (#FFFFFF).
     * If the factor is less than or equal to 1, the color is darkened by scaling its RGB components toward black (#000000).
     * The alpha channel remains unchanged.
     *
     * @param color  The input color to adjust, in ARGB format.
     * @param factor The adjustment factor. Use values > 1.0f to lighten (e.g., 1.11f for slight lightening)
     *               or values <= 1.0f to darken (e.g., 0.95f for slight darkening).
     * @return The adjusted color in ARGB format.
     */
    @ColorInt
    public static int adjustColorBrightness(@ColorInt int color, float factor) {
        final int alpha = Color.alpha(color);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        if (factor > 1.0f) {
            // Lighten: Interpolate toward white (255).
            final float t = 1.0f - (1.0f / factor); // Interpolation parameter.
            red = Math.round(red + (255 - red) * t);
            green = Math.round(green + (255 - green) * t);
            blue = Math.round(blue + (255 - blue) * t);
        } else {
            // Darken or no change: Scale toward black.
            red = (int) (red * factor);
            green = (int) (green * factor);
            blue = (int) (blue * factor);
        }

        // Ensure values are within [0, 255].
        red = clamp(red, 0, 255);
        green = clamp(green, 0, 255);
        blue = clamp(blue, 0, 255);

        return Color.argb(alpha, red, green, blue);
    }

    private static boolean currentThemeColorIsBlackOrWhite() {
        final int color = isDarkTheme()
                ? getDarkColor()
                : getLightColor();

        return getBackgroundColor() == color;
    }

    // Convert HEX to RGB
    private static int[] hexToRgb(String hex) {
        int r = Integer.valueOf(hex.substring(1, 3), 16);
        int g = Integer.valueOf(hex.substring(3, 5), 16);
        int b = Integer.valueOf(hex.substring(5, 7), 16);
        return new int[]{r, g, b};
    }

    // Convert RGB to HEX
    private static String rgbToHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x", r, g, b);
    }

    // Darken color by percentage
    private static String darkenColor(String hex, double percentage) {
        int[] rgb = hexToRgb(hex);
        int r = (int) (rgb[0] * (1 - percentage / 100));
        int g = (int) (rgb[1] * (1 - percentage / 100));
        int b = (int) (rgb[2] * (1 - percentage / 100));
        return rgbToHex(r, g, b);
    }

    // Lighten color by percentage
    private static String lightenColor(String hex, double percentage) {
        int[] rgb = hexToRgb(hex);
        int r = (int) (rgb[0] + (255 - rgb[0]) * (percentage / 100));
        int g = (int) (rgb[1] + (255 - rgb[1]) * (percentage / 100));
        int b = (int) (rgb[2] + (255 - rgb[2]) * (percentage / 100));
        return rgbToHex(r, g, b);
    }
}
