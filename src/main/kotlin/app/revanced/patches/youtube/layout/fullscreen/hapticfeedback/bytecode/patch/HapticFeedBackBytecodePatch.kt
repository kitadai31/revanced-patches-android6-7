package app.revanced.patches.youtube.layout.fullscreen.hapticfeedback.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.layout.fullscreen.hapticfeedback.bytecode.fingerprints.*
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.toErrorResult
import app.revanced.shared.util.integrations.Constants.FULLSCREEN_LAYOUT
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Name("disable-haptic-feedback-bytecode-patch")
@YouTubeCompatibility
class HapticFeedBackBytecodePatch : BytecodePatch(
    listOf(
        MarkerHapticsFingerprint,
        SeekHapticsFingerprint,
        ScrubbingHapticsFingerprint,
        //ZoomHapticsFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        arrayOf(
            SeekHapticsFingerprint to "disableSeekVibrate",
            ScrubbingHapticsFingerprint to "disableScrubbingVibrate",
            MarkerHapticsFingerprint to "disableChapterVibrate",
            //ZoomHapticsFingerprint to "disableZoomVibrate"
        ).map { (fingerprint, name) ->
            fingerprint.result?.let {
                if (fingerprint == SeekHapticsFingerprint)
                    it.disableHaptics(name)
                else
                    it.voidHaptics(name)
            } ?: return fingerprint.toErrorResult()
        }

        return PatchResultSuccess()
    }

    private companion object {
        fun MethodFingerprintResult.disableHaptics(targetMethodName: String) {
            val startIndex = scanResult.patternScanResult!!.startIndex
            val endIndex = scanResult.patternScanResult!!.endIndex
            val insertIndex = endIndex + 4
            val targetRegister = (method.implementation!!.instructions.elementAt(insertIndex) as OneRegisterInstruction).registerA
            val dummyRegister = targetRegister + 1

            with (mutableMethod) {
                removeInstruction(insertIndex)

                addInstructionsWithLabels(
                    insertIndex, """
                     invoke-static {}, $FULLSCREEN_LAYOUT->$targetMethodName()Z
                     move-result v$dummyRegister
                     if-eqz v$dummyRegister, :vibrate
                     const-wide/16 v$targetRegister, 0x0
                     goto :exit
                     :vibrate
                     const-wide/16 v$targetRegister, 0x19
                """, ExternalLabel("exit", mutableMethod.getInstruction(insertIndex))
                )

                addInstructionsWithLabels(
                    startIndex, """
                     invoke-static {}, $FULLSCREEN_LAYOUT->$targetMethodName()Z
                     move-result v$dummyRegister
                     if-eqz v$dummyRegister, :vibrate
                     return-void
                """, ExternalLabel("vibrate", mutableMethod.getInstruction(startIndex))
                )
            }
        }

        fun MethodFingerprintResult.voidHaptics(targetMethodName: String) {
             mutableMethod.addInstructionsWithLabels(
                 0, """
                     invoke-static {}, $FULLSCREEN_LAYOUT->$targetMethodName()Z
                     move-result v0
                     if-eqz v0, :vibrate
                     return-void
                 """, ExternalLabel("vibrate", mutableMethod.getInstruction(0))
             )
        }
    }
}

