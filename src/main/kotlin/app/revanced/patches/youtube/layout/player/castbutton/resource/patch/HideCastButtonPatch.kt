package app.revanced.patches.youtube.layout.player.castbutton.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.player.castbutton.bytecode.patch.HideCastButtonBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-cast-button")
@Description("Hides the cast button in the video player.")
@DependsOn(
    [
        HideCastButtonBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class HideCastButtonPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: PLAYER",
            "SETTINGS: HIDE_CAST_BUTTON"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-cast-button"
        )
    }
}