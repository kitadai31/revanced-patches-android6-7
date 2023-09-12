package app.revanced.patches.youtube.misc.osversion

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch(false)
@Name("os-version-check")
@Description("Check the Android version and show a warning if the device is Android 8.0 or higher.")
@DependsOn([CheckOsVersionBytecodePatch::class])
@YouTubeCompatibility
class CheckOsVersionPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        ResourceHelper.patchSuccess(
            context,
            "os-version-check"
        )
    }
}