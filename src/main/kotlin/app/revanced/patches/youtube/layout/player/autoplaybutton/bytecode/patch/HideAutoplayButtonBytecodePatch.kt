package app.revanced.patches.youtube.layout.player.autoplaybutton.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.toErrorResult
import app.revanced.shared.fingerprints.LayoutConstructorFingerprint
import app.revanced.shared.patches.mapping.ResourceMappingPatch
import app.revanced.shared.util.integrations.Constants.PLAYER_LAYOUT
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Name("hide-autoplay-button-bytecode-patch")
@DependsOn([ResourceMappingPatch::class])
@YouTubeCompatibility
class HideAutoplayButtonBytecodePatch : BytecodePatch(
    listOf(
            LayoutConstructorFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {
        // resolve the offsets such as ...
        val autoNavPreviewStubId = ResourceMappingPatch.resourceMappings.single {
            it.name == "autonav_preview_stub"
        }.id

        LayoutConstructorFingerprint.result?.mutableMethod?.let { method ->
            with (method.implementation!!.instructions) {
                // where to insert the branch instructions and ...
                val insertIndex = this.indexOfFirst {
                    (it as? WideLiteralInstruction)?.wideLiteral == autoNavPreviewStubId
                }
                // where to branch away
                val branchIndex = this.subList(insertIndex + 1, this.size - 1).indexOfFirst {
                    ((it as? ReferenceInstruction)?.reference as? MethodReference)?.name == "addOnLayoutChangeListener"
                } + 2

                val jumpInstruction = this[insertIndex + branchIndex] as Instruction

                method.addInstructionsWithLabels(
                    insertIndex, """
                        invoke-static {}, $PLAYER_LAYOUT->hideAutoPlayButton()Z
                        move-result v15
                        if-nez v15, :hidden
                    """, ExternalLabel("hidden", jumpInstruction)
                )
            }
        } ?: return LayoutConstructorFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}

