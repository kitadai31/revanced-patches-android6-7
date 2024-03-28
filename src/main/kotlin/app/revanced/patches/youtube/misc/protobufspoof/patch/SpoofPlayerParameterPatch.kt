package app.revanced.patches.youtube.misc.protobufspoof.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

// Remove for now
// @Patch
@Name("spoof-player-parameters")
@Description("Spoofs player parameters to prevent the endless buffering issue.")
@DependsOn(
    [
        SpoofPlayerParameterBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class SpoofPlayerParameterPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings(
            context,
            "PREFERENCE_CATEGORY: REVANCED_EXTENDED_SETTINGS",
            "PREFERENCE: MISC_SETTINGS",
            "SETTINGS: ENABLE_PROTOBUF_SPOOF"
        )

        ResourceHelper.patchSuccess(
            context,
            "spoof-player-parameters"
        )
    }
}