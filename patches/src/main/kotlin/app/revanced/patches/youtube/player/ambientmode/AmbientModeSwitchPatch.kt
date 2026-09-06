package app.revanced.patches.youtube.player.ambientmode

import app.revanced.patch.annotation.Patch
import app.revanced.patch.PackagePatch
import app.revanced.patch.PatchContext

@Patch(description = "Disabled for YouTube 14.43.55 compatibility")
class AmbientModePatch : PackagePatch {
    override fun execute(context: PatchContext) {
        // Do nothing. Ambient mode does not exist in v14.
    }
}