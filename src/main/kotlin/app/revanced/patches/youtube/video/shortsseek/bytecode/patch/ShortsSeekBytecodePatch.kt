package app.revanced.patches.youtube.video.shortsseek.bytecode.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.misc.videoid.mainstream.patch.MainstreamVideoIdPatch
import app.revanced.patches.youtube.video.shortsseek.bytecode.fingerprints.ToolBarButtonFingerprint
import app.revanced.shared.extensions.exception
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

@DependsOn([MainstreamVideoIdPatch::class])
class ShortsSeekBytecodePatch : BytecodePatch(
    setOf(
        ToolBarButtonFingerprint,
    )
) {
    override fun execute(context: BytecodeContext) {

        // reference: https://github.com/inotia00/revanced-patches/blob/revanced-extended/src/main/kotlin/app/revanced/patches/youtube/utils/toolbar/ToolBarHookPatch.kt

        ToolBarButtonFingerprint.result?.let {
            it.mutableMethod.apply {
                val replaceIndex = it.scanResult.patternScanResult!!.startIndex
                val freeIndex = it.scanResult.patternScanResult!!.endIndex - 1

                val replaceReference = getInstruction<ReferenceInstruction>(replaceIndex).reference
                val replaceRegister =
                    getInstruction<FiveRegisterInstruction>(replaceIndex).registerC
                val enumRegister = getInstruction<FiveRegisterInstruction>(replaceIndex).registerD
                val freeRegister = getInstruction<TwoRegisterInstruction>(freeIndex).registerA

                val imageViewIndex = replaceIndex + 2
                val imageViewReference =
                    getInstruction<ReferenceInstruction>(imageViewIndex).reference

                val afterSetOnClickListenerInstruction = implementation!!.instructions.first {
                    it.opcode == Opcode.IGET_OBJECT && it is ReferenceInstruction && (it.reference as FieldReference).type == "Landroid/view/View\$OnLongClickListener;"
                }

                addInstructionsWithLabels(
                    replaceIndex + 1, """
                        iget-object v$freeRegister, p0, $imageViewReference
                        invoke-static {v$enumRegister, v$freeRegister}, Lapp/revanced/integrations/patches/video/ShortsSeekPatch;->hookToolBar(Ljava/lang/Enum;Landroid/widget/ImageView;)Z
                        move-result v$freeRegister
                        if-nez v$freeRegister, :hook_succeed  # skip setImageDrawable & setOnClickListener
                        invoke-interface {v$replaceRegister, v$enumRegister}, $replaceReference
                        """,
                    ExternalLabel("hook_succeed", afterSetOnClickListenerInstruction)
                )
                removeInstruction(replaceIndex)
            }
        } ?: throw ToolBarButtonFingerprint.exception
    }
}