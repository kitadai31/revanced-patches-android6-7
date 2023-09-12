package app.revanced.patches.youtube.layout.fullscreen.endscreenoverlay.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.findMutableMethodOf
import app.revanced.shared.extensions.toResult
import app.revanced.shared.patches.mapping.ResourceMappingPatch
import app.revanced.shared.util.integrations.Constants.FULLSCREEN_LAYOUT
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31i

@Name("hide-endscreen-overlay-bytecode-patch")
@DependsOn([ResourceMappingPatch::class])
@YouTubeCompatibility
class HideEndscreenOverlayBytecodePatch : BytecodePatch() {
    // list of resource names to get the id of
    private val resourceIds = arrayOf(
        "app_related_endscreen_results"
    ).map { name ->
        ResourceMappingPatch.resourceMappings.single { it.name == name }.id
    }
    private var patchSuccessArray = Array(resourceIds.size) {false}

    override fun execute(context: BytecodeContext) {
        context.classes.forEach { classDef ->
            classDef.methods.forEach { method ->
                with(method.implementation) {
                    this?.instructions?.forEachIndexed { index, instruction ->
                        when (instruction.opcode) {
                            Opcode.CONST -> {
                                when ((instruction as Instruction31i).wideLiteral) {
                                    resourceIds[0] -> { // end screen result
                                        val insertIndex = index - 13
                                        val invokeInstruction = instructions.elementAt(insertIndex)
                                        if (invokeInstruction.opcode != Opcode.IF_NEZ) return@forEachIndexed

                                        val mutableMethod = context.proxy(classDef).mutableClass.findMutableMethodOf(method)

                                        val dummyRegister = (instructions.elementAt(index) as Instruction31i).registerA
                                        mutableMethod.addInstructionsWithLabels(
                                            insertIndex, """
                                                invoke-static {}, $FULLSCREEN_LAYOUT->hideEndscreenOverlay()Z
                                                move-result v$dummyRegister
                                                if-eqz v$dummyRegister, :on
                                                return-void
                                            """, ExternalLabel("on", mutableMethod.getInstruction(insertIndex))
                                        )

                                        patchSuccessArray[0] = true;
                                    }
                                }
                            }
                            else -> return@forEachIndexed
                        }
                    }
                }
            }
        }
        return toResult(patchSuccessArray.indexOf(false))
    }
}
