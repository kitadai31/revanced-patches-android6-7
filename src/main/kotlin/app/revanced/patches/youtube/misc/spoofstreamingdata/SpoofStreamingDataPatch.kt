package app.revanced.patches.youtube.misc.spoofstreamingdata

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("spoof-streaming-data")
@Description("Adds options to spoof the streaming data to allow video playback.")
@DependsOn(
    [
        SpoofStreamingDataBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class SpoofStreamingDataPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /**
         * Add settings
         */
        ResourceHelper.addSettings(
            context,
            "PREFERENCE_CATEGORY: REVANCED_EXTENDED_SETTINGS",
            "PREFERENCE: MISC_SETTINGS",
            "SETTINGS: SPOOF_STREAMING_DATA"
        )

        ResourceHelper.patchSuccess(
            context,
            "spoof-streaming-data"
        )
    }
}