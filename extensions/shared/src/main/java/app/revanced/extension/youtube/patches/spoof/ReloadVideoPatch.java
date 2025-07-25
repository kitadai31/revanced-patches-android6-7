package app.revanced.extension.youtube.patches.spoof;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

import app.revanced.extension.shared.patches.spoof.requests.StreamingDataRequest;
import app.revanced.extension.shared.utils.Logger;
import app.revanced.extension.shared.utils.Utils;
import app.revanced.extension.youtube.settings.Settings;
import app.revanced.extension.youtube.shared.VideoInformation;
import app.revanced.extension.youtube.utils.VideoUtils;

@SuppressWarnings("unused")
public class ReloadVideoPatch {
    private static final boolean SPOOF_STREAMING_DATA_TV_RELOAD_VIDEO_BUTTON =
            Settings.SPOOF_STREAMING_DATA.get() &&
                    Settings.SPOOF_STREAMING_DATA_USE_TV.get() &&
                    Settings.SPOOF_STREAMING_DATA_TV_RELOAD_VIDEO_BUTTON.get();

    @NonNull
    private static String playlistId = "";
    @NonNull
    private static String videoId = "";
    private static volatile int progressBarVisibility = 0;

    /**
     * Injection point.
     */
    @Nullable
    public static String newPlayerResponseParameter(@NonNull String newlyLoadedVideoId, @Nullable String playerParameter,
                                                    @Nullable String newlyLoadedPlaylistId, boolean isShortAndOpeningOrPlaying) {
        if (SPOOF_STREAMING_DATA_TV_RELOAD_VIDEO_BUTTON && !VideoInformation.playerParametersAreShort(playerParameter)) {
            if (newlyLoadedPlaylistId == null || newlyLoadedPlaylistId.isEmpty()) {
                playlistId = "";
            } else if (!Objects.equals(playlistId, newlyLoadedPlaylistId)) {
                playlistId = newlyLoadedPlaylistId;
                Logger.printDebug(() -> "newVideoStarted, videoId: " + newlyLoadedVideoId + ", playlistId: " + newlyLoadedPlaylistId);
            }
        }

        return playerParameter; // Return the original value since we are observing and not modifying.
    }

    /**
     * Injection point.
     * Called after {@link StreamingDataRequest}.
     */
    public static void newVideoStarted(@NonNull String newlyLoadedChannelId, @NonNull String newlyLoadedChannelName,
                                       @NonNull String newlyLoadedVideoId, @NonNull String newlyLoadedVideoTitle,
                                       final long newlyLoadedVideoLength, boolean newlyLoadedLiveStreamValue) {
        if (!SPOOF_STREAMING_DATA_TV_RELOAD_VIDEO_BUTTON) {
            return;
        }
        if (Objects.equals(videoId, newlyLoadedVideoId)) {
            return;
        }
        if (!Utils.isNetworkConnected()) {
            Logger.printDebug(() -> "Network not connected, ignoring video");
            return;
        }
        videoId = newlyLoadedVideoId;
        Logger.printDebug(() -> "newVideoStarted: " + newlyLoadedVideoId);
    }

    /**
     * Injection point.
     * Hooks the visibility of the loading circle (progress bar) that appears when buffering occurs.
     */
    public static void setProgressBarVisibility(int visibility) {
        if (SPOOF_STREAMING_DATA_TV_RELOAD_VIDEO_BUTTON) {
            progressBarVisibility = visibility;
        }
    }

    public static boolean isProgressBarVisible() {
        return progressBarVisibility == 0;
    }

    public static void reloadVideo() {
        VideoUtils.dismissPlayer();

        // Open the video.
        if (playlistId.isEmpty()) {
            VideoUtils.openVideo(videoId);
        } else { // If the video is playing from a playlist, the url must include the playlistId.
            VideoUtils.openPlaylist(playlistId, videoId, true);
        }
    }
}
