package app.revanced.patches.youtube.layout.seekbar.timeandseekbar.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.layout.seekbar.timeandseekbar.bytecode.fingerprints.TimeCounterFingerprint
import app.revanced.patches.youtube.layout.seekbar.timeandseekbar.bytecode.fingerprints.TimeCounterParentFingerprint
import app.revanced.patches.youtube.misc.resourceid.patch.SharedResourcdIdPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.patches.timebar.HookTimebarPatch
import app.revanced.shared.util.integrations.Constants.SEEKBAR_LAYOUT

@DependsOn([HookTimebarPatch::class, SharedResourcdIdPatch::class])
@Name("hide-time-and-seekbar-bytecode-patch")
@YouTubeCompatibility
class HideTimeAndSeekbarBytecodePatch : BytecodePatch(
    listOf(
        TimeCounterParentFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        TimeCounterParentFingerprint.result?.let { parentResult ->
            TimeCounterFingerprint.also { it.resolve(context, parentResult.classDef) }.result?.let { counterResult ->
                listOf(
                    HookTimebarPatch.SetTimbarFingerprintResult,
                    counterResult
                ).forEach {
                    val method = it.mutableMethod
                    method.addInstructionsWithLabels(
                        0, """
                            invoke-static {}, $SEEKBAR_LAYOUT->hideTimeAndSeekbar()Z
                            move-result v0
                            if-eqz v0, :hide_time_and_seekbar
                            return-void
                        """, ExternalLabel("hide_time_and_seekbar", method.getInstruction(0))
                    )
                }

            } ?: throw TimeCounterFingerprint.exception
        } ?: throw TimeCounterParentFingerprint.exception
    }
}
