package app.revanced.patches.youtube.layout.general.crowdfundingbox.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.general.crowdfundingbox.bytecode.patch.CrowdfundingBoxBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-crowdfunding-box")
@Description("Hides the crowdfunding box between the player and video description.")
@DependsOn(
    [
        CrowdfundingBoxBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class CrowdfundingBoxPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: GENERAL",
            "SETTINGS: HIDE_CROWDFUNDING_BOX"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-crowdfunding-box"
        )
    }
}