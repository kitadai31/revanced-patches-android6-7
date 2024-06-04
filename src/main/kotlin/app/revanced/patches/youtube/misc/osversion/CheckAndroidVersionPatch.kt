package app.revanced.patches.youtube.misc.osversion

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
@Name("check-android-version")
@Description("If the device is Android 8.0 or higher, show a warning dialog.")
@DependsOn(
    [
        CheckAndroidVersionBytecodePatch::class,
        SettingsPatch::class,
    ]
)
@YouTubeCompatibility
class CheckAndroidVersionPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        ResourceHelper.patchSuccess(
            context,
            "check-android-version"
        )
    }
}