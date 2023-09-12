package app.revanced.patches.youtube.video.speed.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.patches.youtube.video.speed.bytecode.patch.VideoSpeedBytecodePatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("default-video-speed")
@Description("Adds ability to set default video speed settings.")
@DependsOn(
    [
        VideoSpeedBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class VideoSpeedPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings(
            context,
            "PREFERENCE_CATEGORY: REVANCED_EXTENDED_SETTINGS",
            "PREFERENCE: VIDEO_SETTINGS",
            "SETTINGS: DEFAULT_VIDEO_SPEED"
        )

        ResourceHelper.patchSuccess(
            context,
            "default-video-speed"
        )
    }
}