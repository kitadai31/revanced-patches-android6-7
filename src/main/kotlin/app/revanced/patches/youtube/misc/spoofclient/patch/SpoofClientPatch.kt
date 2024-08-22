package app.revanced.patches.youtube.misc.spoofclient.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.minimizedplayback.resource.patch.MinimizedPlaybackPatch
import app.revanced.patches.youtube.misc.playertype.patch.PlayerTypeHookPatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("spoof-client")
@Description("Adds options to spoofs the client to allow video playback.")
@DependsOn(
    [
        // Required to fix background playback issue of live stream on iOS client.
        MinimizedPlaybackPatch::class,
        PlayerTypeHookPatch::class,

        SpoofClientBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class SpoofClientPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /**
         * Add settings
         */
        ResourceHelper.addSettings(
            context,
            "PREFERENCE_CATEGORY: REVANCED_EXTENDED_SETTINGS",
            "PREFERENCE: MISC_SETTINGS",
            "SETTINGS: SPOOF_CLIENT"
        )

        ResourceHelper.patchSuccess(
            context,
            "spoof-client"
        )
    }
}