package app.revanced.patches.youtube.video.shortsseek.resource

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.patches.youtube.video.shortsseek.bytecode.patch.ShortsSeekBytecodePatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("shorts-seek")
@Description("Replace search and menu button in Shorts player with skip/rewind button.")
@DependsOn(
    [
        ShortsSeekBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class ShortsSeekPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */

        ResourceHelper.addSettings(
            context,
            "PREFERENCE_CATEGORY: REVANCED_EXTENDED_SETTINGS",
            "PREFERENCE: VIDEO_SETTINGS",
            "SETTINGS: SHORTS_SEEK"
        )

        ResourceHelper.patchSuccess(
            context,
            "shorts-seek"
        )
    }
}