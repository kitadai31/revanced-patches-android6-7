package app.revanced.patches.youtube.swipe.swipebrightnessinhdr.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.swipe.swipebrightnessinhdr.bytecode.fingerprints.HDRVideoFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.util.integrations.Constants.SWIPE_PATH

@Name("enable-swipe-gesture-brightness-in-hdr-patch")
@YouTubeCompatibility
class SwipeGestureBrightnessInHDRPatch : BytecodePatch(
    listOf(
        HDRVideoFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        HDRVideoFingerprint.result?.mutableMethod?.let {
            it.addInstructionsWithLabels(
                0, """
                    invoke-static {}, $SWIPE_PATH/EnableSwipeGestureBrightnessInHDRPatch;->enableSwipeGestureBrightnessInHDR()Z
                    move-result v0
                    if-eqz v0, :default
                    return-void
                """, ExternalLabel("default", it.getInstruction(0))
            )
        } ?: throw HDRVideoFingerprint.exception
    }
}