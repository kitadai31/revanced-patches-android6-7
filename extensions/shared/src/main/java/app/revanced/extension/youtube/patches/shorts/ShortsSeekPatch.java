package app.revanced.extension.youtube.patches.shorts;

import static app.revanced.extension.shared.utils.Utils.getChildView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import app.revanced.extension.shared.utils.ResourceUtils;
import app.revanced.extension.shared.utils.Utils;
import app.revanced.extension.youtube.settings.Settings;
import app.revanced.extension.youtube.shared.VideoInformation;

@SuppressWarnings("unused")
public class ShortsSeekPatch {

    /**
     * Whether replaced one of camera button or 3-dots menu button.
     * Reset to false every time when replaced the search button.
     */
    private static boolean replacedCameraOrMenuButton = false;

    public static void replaceToolbarButton(String enumString, View parentView) {
        if (!Settings.ENABLE_SHORTS_SEEK.get()) return;

        boolean isSearchButton;

        if (enumString.equals("SEARCH_FILLED")) {
            isSearchButton = true;
        } else if (enumString.equals("SHORTS_HEADER_CAMERA") || enumString.equals("MORE_VERT")) {
            if (replacedCameraOrMenuButton) return;
            isSearchButton = false;
        } else {
            return;
        }

        ImageView imageView = getChildView((ViewGroup) parentView, view -> view instanceof ImageView);
        if (imageView == null) return;

        // Overriding is possible only after OnClickListener is assigned to the button.
        Utils.runOnMainThreadDelayed(() -> {
            final boolean useLongSeek = Settings.SHORTS_SEEK_AMOUNT.get();
            if (isSearchButton) {
                imageView.setImageResource(
                        ResourceUtils.getDrawableIdentifier(useLongSeek ? "ic_seek_back_10_fill_36px" : "ic_seek_back_5_fill_36px")
                );
                imageView.setOnClickListener(v-> VideoInformation.seekToRelative(useLongSeek ? -10000 : -5000));
            } else {
                imageView.setImageResource(
                        ResourceUtils.getDrawableIdentifier(Settings.SHORTS_SEEK_AMOUNT.get() ? "ic_seek_forward_10_fill_36px" : "ic_seek_forward_5_fill_36px")
                );
                imageView.setOnClickListener(v -> VideoInformation.seekToRelative(useLongSeek ? 10000 : 5000));
            }
        }, 0);

        replacedCameraOrMenuButton = !isSearchButton;
    }

}
