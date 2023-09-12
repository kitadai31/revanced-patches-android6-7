package app.revanced.patches.youtube.layout.general.headerswitch.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.general.headerswitch.bytecode.patch.HeaderSwitchBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("header-switch")
@Description("Add switch to change header.")
@DependsOn(
    [
        HeaderSwitchBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class HeaderSwitchPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: GENERAL",
            "SETTINGS: HEADER_SWITCH"
        )

        ResourceHelper.patchSuccess(
            context,
            "header-switch"
        )
    }
}