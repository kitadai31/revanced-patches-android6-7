package app.revanced.patches.youtube.layout.seekbar.customseekbarcolor.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.seekbar.customseekbarcolor.bytecode.patch.CustomSeekbarColorBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("custom-seekbar-color")
@Description("Change seekbar color in dark mode.")
@DependsOn(
    [
        CustomSeekbarColorBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class CustomSeekbarColorPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: SEEKBAR",
            "SETTINGS: CUSTOM_SEEKBAR_COLOR"
        )

        ResourceHelper.patchSuccess(
            context,
            "custom-seekbar-color"
        )
    }
}