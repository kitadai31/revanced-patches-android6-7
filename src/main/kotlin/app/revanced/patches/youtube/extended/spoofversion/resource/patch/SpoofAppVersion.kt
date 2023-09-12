package app.revanced.patches.youtube.extended.spoofversion.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.extended.spoofversion.bytecode.patch.SpoofAppVersionBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("spoof-app-version")
@Description("Spoof the YouTube client version to enable the new layout or restore old layout.")
@DependsOn(
    [
        SpoofAppVersionBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class SpoofAppVersion : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings(
            context,
            "PREFERENCE_CATEGORY: REVANCED_EXTENDED_SETTINGS",
            "PREFERENCE: MISC_SETTINGS",
            "SETTINGS: SPOOF_APP_VERSION"
        )

        ResourceHelper.patchSuccess(
            context,
            "spoof-app-version"
        )
    }
}