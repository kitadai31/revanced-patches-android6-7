package app.revanced.patches.youtube.layout.fullscreen.flimstripoverlay.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.fullscreen.flimstripoverlay.bytecode.patch.HideFilmstripOverlayBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-filmstrip-overlay")
@Description("Hide flimstrip overlay on swipe controls.")
@DependsOn(
    [
        HideFilmstripOverlayBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class HideFilmstripOverlayPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: FULLSCREEN",
            "SETTINGS: HIDE_FILMSTRIP_OVERLAY"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-filmstrip-overlay"
        )
    }
}
