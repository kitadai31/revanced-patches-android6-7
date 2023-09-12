package app.revanced.patches.youtube.layout.general.autopopuppanels.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.general.autopopuppanels.bytecode.patch.PlayerPopupPanelsBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-auto-player-popup-panels")
@Description("Hide automatic popup panels (playlist or live chat) on video player.")
@DependsOn(
    [
        PlayerPopupPanelsBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class PlayerPopupPanelsPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: GENERAL",
            "SETTINGS: HIDE_AUTO_PLAYER_POPUP_PANELS"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-auto-player-popup-panels"
        )
    }
}