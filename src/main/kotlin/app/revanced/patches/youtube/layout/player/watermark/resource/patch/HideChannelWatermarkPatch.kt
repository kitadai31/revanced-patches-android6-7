package app.revanced.patches.youtube.layout.player.watermark.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.player.watermark.bytecode.patch.HideChannelWatermarkBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-channel-watermark")
@Description("Hides creator's watermarks on videos.")
@DependsOn(
    [
        HideChannelWatermarkBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class  HideChannelWatermarkPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: PLAYER",
            "SETTINGS: HIDE_CHANNEL_WATERMARK"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-channel-watermark"
        )
    }
}