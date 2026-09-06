package app.revanced.patches.youtube.player.miniplayer.general

import app.revanced.patch.annotation.Patch
import app.revanced.patch.PackagePatch
import app.revanced.patch.PatchContext

@Patch(description = "Disabled for YouTube 14.43.55 compatibility")
class MiniplayerGeneralPatch : PackagePatch {
    override fun execute(context: PatchContext) {
        // Do nothing. Handled natively by freeze layout updates patch.
    }
}
