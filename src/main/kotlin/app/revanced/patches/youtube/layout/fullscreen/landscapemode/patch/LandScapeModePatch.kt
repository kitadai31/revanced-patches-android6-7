package app.revanced.patches.youtube.layout.fullscreen.landscapemode.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch(false)
@Name("disable-landscape-mode")
@Description("Disable landscape mode when entering fullscreen.")
@DependsOn(
    [
        LandScapeModeBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class LandScapeModePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {

        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: FULLSCREEN",
            "SETTINGS: DISABLE_LANDSCAPE_MODE"
        )

        ResourceHelper.patchSuccess(
            context,
            "disable-landscape-mode"
        )

        return PatchResultSuccess()
    }
}