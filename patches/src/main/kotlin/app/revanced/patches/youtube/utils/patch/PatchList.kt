package app.revanced.patches.youtube.utils.patch

internal enum class PatchList(
    val title: String,
    val summary: String,
    var included: Boolean? = false
) { 
    DISABLE_UPDATE_SCREEN(
        "Disable update screen",
        "Disable the force update screen (\"Switch to YouTube.com\" or \"Update your app\")"
    ),
    GMSCORE_SUPPORT(
        "GmsCore support",
        "Allows patched Google apps to run without root and under a different package name by using GmsCore instead of Google Play Services."
    ),
    SETTINGS_FOR_YOUTUBE(
        "Settings for YouTube",
        "Applies mandatory patches to implement ReVanced Extended settings into the application."
    ),
    SPOOF_APP_VERSION(
        "Spoof app version",
        "Adds options to spoof the YouTube client version. This can be used to restore old UI elements and features."
    ),
    VIDEO_PLAYBACK(
        "Video playback",
        "Adds options to customize settings related to video playback, such as default video quality and playback speed."
    ),
    FREEZE_LAYOUT_UPDATES(
        "Freeze layout updates",
        "Freeze the current server-side layout. Mandatory for 14.43.55."
    ),
    ADD_MISSING_RESOURCES(
        "Add missing resources",
        "Add missing resources to prevent crashes in 14.43.55. Currently, only a few resources are supported. Also, replace missing drawables with a transparent image to prevent crashes."
    )
}