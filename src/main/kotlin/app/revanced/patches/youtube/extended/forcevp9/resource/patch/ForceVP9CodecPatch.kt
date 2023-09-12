package app.revanced.patches.youtube.extended.forcevp9.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.extended.forcevp9.bytecode.patch.ForceVP9CodecBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("force-vp9-codec")
@Description("Forces the VP9 codec for videos.")
@DependsOn(
    [
        ForceVP9CodecBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class ForceVP9CodecPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings(
            context,
            "PREFERENCE_CATEGORY: REVANCED_EXTENDED_SETTINGS",
            "PREFERENCE: MISC_SETTINGS",
            "SETTINGS: ENABLE_VP9_CODEC"
        )

        ResourceHelper.patchSuccess(
            context,
            "force-vp9-codec"
        )
    }
}