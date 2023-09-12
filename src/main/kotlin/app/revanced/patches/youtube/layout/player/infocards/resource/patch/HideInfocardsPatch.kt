package app.revanced.patches.youtube.layout.player.infocards.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.player.infocards.bytecode.patch.HideInfoCardsBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-info-cards")
@Description("Hides info-cards in videos.")
@DependsOn(
    [
        HideInfoCardsBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class HideInfoCardsPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: PLAYER",
            "SETTINGS: HIDE_INFO_CARDS"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-info-cards"
        )
    }
}