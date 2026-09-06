package app.revanced.patches.youtube.shorts.seek

import app.revanced.patch.annotation.Patch
import app.revanced.patch.PackagePatch
import app.revanced.patch.PatchContext

@Patch(description = "Disabled for YouTube 14.43.55 compatibility")
class ShortsSeekPatch : PackagePatch {
    override fun execute(context: PatchContext) {
        // Do nothing. This patch is disabled for version 14.
    }
}