package app.revanced.patches.youtube.layout.general.pivotbar.createbutton.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.general.pivotbar.createbutton.bytecode.patch.CreateButtonRemoverBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-create-button")
@Description("Hides the create button in the navigation bar.")
@DependsOn(
    [
        CreateButtonRemoverBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class CreateButtonRemoverPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: GENERAL",
            "SETTINGS: HIDE_CREATE_BUTTON"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-create-button"
        )
    }
}