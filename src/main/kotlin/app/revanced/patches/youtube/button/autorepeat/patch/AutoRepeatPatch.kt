package app.revanced.patches.youtube.button.autorepeat.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.button.autorepeat.fingerprints.*
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.util.integrations.Constants.UTILS_PATH
import app.revanced.shared.util.integrations.Constants.VIDEO_PATH
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Name("always-autorepeat")
@YouTubeCompatibility
class AutoRepeatPatch : BytecodePatch(
    listOf(
        AutoRepeatParentFingerprint,
        AutoNavInformerFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        AutoRepeatParentFingerprint.result?.classDef?.let { classDef ->
            AutoRepeatFingerprint.also {
                it.resolve(context, classDef)
            }.result?.mutableMethod?.let {
                it.addInstructionsWithLabels(
                    0, """
                    invoke-static {}, $VIDEO_PATH/VideoInformation;->videoEnded()Z
                    move-result v0
                    if-eqz v0, :noautorepeat
                    return-void
                """, ExternalLabel("noautorepeat", it.getInstruction(0))
                )
            } ?: throw AutoRepeatFingerprint.exception
        } ?: throw AutoRepeatParentFingerprint.exception

        AutoNavInformerFingerprint.result?.mutableMethod?.let {
            with (it.implementation!!.instructions) {
                val index = this.size - 1 - 1
                val register = (this[index] as OneRegisterInstruction).registerA
                it.addInstructions(
                    index + 1, """
                    invoke-static {v$register}, $UTILS_PATH/EnableAutoRepeatPatch;->enableAutoRepeat(Z)Z
                    move-result v0
                """
                )

            }
        } ?: throw AutoNavInformerFingerprint.exception
    }
}
