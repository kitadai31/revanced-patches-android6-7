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

@Patch
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

        /*
          add guide text to miscellaneous category
          (because this setting is in the different category from original RVX)
         */
        ResourceHelper.addSettings(
            context,
            "PREFERENCE_CATEGORY: REVANCED_EXTENDED_SETTINGS",
            "PREFERENCE: MISC_SETTINGS",
            "SETTINGS: SPOOF_PLAYER_PARAMETER_GUIDANCE"
        )

        ResourceHelper.patchSuccess(
            context,
            "spoof-player-parameters"
        )
    }
}