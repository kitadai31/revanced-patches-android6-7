package app.revanced.patches.youtube.misc.trackingurlhook

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.trackingurlhook.fingerprints.TrackingUrlModelFingerprint
import app.revanced.shared.util.bytecode.getTargetIndexWithMethodReferenceNameOrThrow
import app.revanced.shared.util.bytecode.resultOrThrow
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

class TrackingUrlHookPatch : BytecodePatch(
    setOf(TrackingUrlModelFingerprint)
) {

    override fun execute(context: BytecodeContext) {
        trackingUrlMethod = TrackingUrlModelFingerprint.resultOrThrow().mutableMethod
    }

    companion object {
        private lateinit var trackingUrlMethod: MutableMethod

        internal fun hookTrackingUrl(
            descriptor: String
        ) = trackingUrlMethod.apply {
            val targetIndex = getTargetIndexWithMethodReferenceNameOrThrow("parse") + 1
            val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

            var smaliInstruction = "invoke-static {v$targetRegister}, $descriptor"

            if (!descriptor.endsWith("V")) {
                smaliInstruction += """
                move-result-object v$targetRegister
                
                """.trimIndent()
            }

            addInstructions(
                targetIndex + 1,
                smaliInstruction
            )
        }
    }
}