package app.revanced.patches.youtube.layout.fullscreen.fullscreenpanels.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.fullscreen.fullscreenpanels.bytecode.patch.FullscreenButtonContainerBytecodePatch
import app.revanced.patches.youtube.layout.fullscreen.fullscreenpanels.bytecode.patch.HideFullscreenPanelsBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-fullscreen-panels")
@Description("Hides video description and comments panel in fullscreen view.")
@DependsOn(
    [
        FullscreenButtonContainerBytecodePatch::class,
        HideFullscreenPanelsBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class HideFullscreenPanelsPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: FULLSCREEN",
            "SETTINGS: HIDE_FULLSCREEN_PANELS"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-fullscreen-panels"
        )
    }
}