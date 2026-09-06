package app.revanced.patches.youtube.utils.returnyoutubeusername

import app.revanced.patch.annotation.Patch
import app.revanced.patch.PackagePatch
import app.revanced.patch.PatchContext

@Patch(description = "Disabled for legacy YouTube 14.43.55 compatibility")
class CustomPlaceholderPatch : PackagePatch { // Change to match the original class name
    override fun execute(context: PatchContext) {
        // Entire subfolder neutralized safely to prevent compilation crashes
    }
}