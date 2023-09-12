package app.revanced.patches.youtube.misc.openlinksdirectly.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.openlinksdirectly.bytecode.patch.OpenLinksDirectlyBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("enable-open-links-directly")
@Description("Bypass URL redirects (youtube.com/redirect) when opening links in video descriptions.")
@DependsOn(
    [
        OpenLinksDirectlyBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class OpenLinksDirectlyPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings(
            context,
            "PREFERENCE_CATEGORY: REVANCED_EXTENDED_SETTINGS",
            "PREFERENCE: MISC_SETTINGS",
            "SETTINGS: ENABLE_OPEN_LINKS_DIRECTLY"
        )

        ResourceHelper.patchSuccess(
            context,
            "enable-open-links-directly"
        )
    }
}