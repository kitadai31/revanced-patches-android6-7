package app.revanced.patches.youtube.swipe.controls

import app.revanced.patch.annotation.Patch
import app.revanced.patch.PackagePatch
import app.revanced.patch.PatchContext

@Patch(description = "Disabled for YouTube 14.43.55 compatibility")
class SwipeControlsPatch : PackagePatch {
    override fun execute(context: PatchContext) {
        // Do nothing. This patch is skipped for version 14.
    }
}