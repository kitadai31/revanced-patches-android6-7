package app.revanced.patches.youtube.layout.fullscreen.endscreenoverlay.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.fullscreen.endscreenoverlay.bytecode.patch.HideEndscreenOverlayBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-endscreen-overlay")
@Description("Hide endscreen overlay on swipe controls.")
@DependsOn(
    [
        HideEndscreenOverlayBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class HideFilmStripPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: FULLSCREEN",
            "SETTINGS: HIDE_ENDSCREEN_OVERLAY"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-endscreen-overlay"
        )
    }
}
