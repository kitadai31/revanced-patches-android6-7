package app.revanced.extension.youtube.patches.spoof.ui;

import static app.revanced.extension.shared.utils.Utils.getChildView;

import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.util.Objects;

import app.revanced.extension.shared.patches.spoof.requests.StreamingDataRequest;
import app.revanced.extension.shared.utils.Logger;
import app.revanced.extension.youtube.patches.overlaybutton.BottomControlButton;
import app.revanced.extension.youtube.patches.spoof.ReloadVideoPatch;
import app.revanced.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class ReloadVideoButtonController {
    private static final boolean SPOOF_STREAMING_DATA_TV_RELOAD_VIDEO_BUTTON =
            Settings.SPOOF_STREAMING_DATA.get() &&
                    Settings.SPOOF_STREAMING_DATA_USE_TV.get() &&
                    Settings.SPOOF_STREAMING_DATA_TV_RELOAD_VIDEO_BUTTON.get();
    private static WeakReference<ImageView> buttonReference = new WeakReference<>(null);
    private static boolean isVisible;


    /**
     * injection point
     */
    public static void initialize(View youtubeControlsLayout) {
        try {
            ImageView imageView = Objects.requireNonNull(getChildView(youtubeControlsLayout, "revanced_reload_video_button"));
            imageView.setVisibility(View.GONE);
            imageView.setOnClickListener(v -> ReloadVideoPatch.reloadVideo());
            buttonReference = new WeakReference<>(imageView);
        } catch (Exception ex) {
            Logger.printException(() -> "Unable to set RelativeLayout", ex);
        }
    }

    public static void changeVisibility(boolean visible, boolean animation) {
        ImageView imageView = buttonReference.get();
        if (imageView == null || isVisible == visible) return;
        isVisible = visible;

        if (visible) {
            imageView.clearAnimation();
            if (!shouldBeShown()) {
                return;
            }
            if (animation) {
                Animation fadeIn = BottomControlButton.getButtonFadeIn();
                if (fadeIn != null) {
                    imageView.startAnimation(fadeIn);
                }
            }
            imageView.setVisibility(View.VISIBLE);
            return;
        }
        if (imageView.getVisibility() == View.VISIBLE) {
            imageView.clearAnimation();
            if (animation) {
                Animation fadeOut = BottomControlButton.getButtonFadeOut();
                if (fadeOut != null) {
                    imageView.startAnimation(fadeOut);
                }
            }
            imageView.setVisibility(View.GONE);
        }
    }

    public static void changeVisibilityNegatedImmediate() {
        ImageView imageView = buttonReference.get();
        if (imageView == null) return;
        if (!shouldBeShown()) return;


        imageView.clearAnimation();
        Animation fadeOutImmediate = BottomControlButton.getButtonFadeOutImmediate();
        if (fadeOutImmediate != null) {
            imageView.startAnimation(fadeOutImmediate);
        }
        imageView.setVisibility(View.GONE);
    }

    private static boolean shouldBeShown() {
        return SPOOF_STREAMING_DATA_TV_RELOAD_VIDEO_BUTTON &&
                ReloadVideoPatch.isProgressBarVisible();
    }
}
