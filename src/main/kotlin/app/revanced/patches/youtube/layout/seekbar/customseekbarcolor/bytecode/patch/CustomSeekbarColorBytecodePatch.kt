package app.revanced.patches.youtube.layout.seekbar.customseekbarcolor.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.youtube.layout.seekbar.customseekbarcolor.bytecode.fingerprints.ControlsOverlayStyleFingerprint
import app.revanced.patches.youtube.misc.drawable.DrawableColorPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.bytecode.getWalkerMethod
import app.revanced.shared.util.bytecode.resultOrThrow
import app.revanced.shared.util.integrations.Constants.SEEKBAR_LAYOUT
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction

@Name("custom-seekbar-color-bytecode-patch")
@DependsOn([DrawableColorPatch::class])
@YouTubeCompatibility
class CustomSeekbarColorBytecodePatch : BytecodePatch(
    setOf(
        ControlsOverlayStyleFingerprint,
    )
) {
    override fun execute(context: BytecodeContext) {
        ControlsOverlayStyleFingerprint.resultOrThrow().let {
            val walkerMethod =
                it.getWalkerMethod(context, it.scanResult.patternScanResult!!.startIndex + 1)
            walkerMethod.apply {
                val colorRegister = getInstruction<TwoRegisterInstruction>(0).registerA

                addInstructions(
                    0, """
                    invoke-static {v$colorRegister}, $SEEKBAR_LAYOUT->overrideSeekbarColor(I)I
                    move-result v$colorRegister
                    """
                )
            }
        }

        // Change color or hide thumbnails seekbar
        DrawableColorPatch.injectCall("$SEEKBAR_LAYOUT->getColor(I)I")
    }
}
