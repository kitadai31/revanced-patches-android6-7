package app.revanced.patches.youtube.ads.general.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultError
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.ads.general.bytecode.fingerprints.ComponentContextParserFingerprint
import app.revanced.patches.youtube.ads.general.bytecode.fingerprints.EmptyComponentBuilderFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.patches.mapping.ResourceMappingPatch
import app.revanced.shared.util.bytecode.BytecodeHelper
import app.revanced.shared.util.integrations.Constants.ADS_PATH
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21s
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31i
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@DependsOn([ResourceMappingPatch::class])
@Name("hide-general-ads-bytecode-patch")
@YouTubeCompatibility
class GeneralAdsBytecodePatch : BytecodePatch(
    listOf(ComponentContextParserFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        ComponentContextParserFingerprint.result?.let { result ->
            val builderMethodIndex = EmptyComponentBuilderFingerprint
                .also { it.resolve(context, result.mutableMethod, result.mutableClass) }
                .let { it.result!!.scanResult.patternScanResult!!.startIndex }

            val emptyComponentFieldIndex = builderMethodIndex + 2

            with(result.mutableMethod) {
                val insertHookIndex = implementation!!.instructions.indexOfFirst {
                    it.opcode == Opcode.CONST_16 &&
                    (it as BuilderInstruction21s).narrowLiteral == 124
                } + 3

                val stringBuilderRegister = (getInstruction(insertHookIndex) as OneRegisterInstruction).registerA
                val clobberedRegister = (getInstruction(insertHookIndex - 3) as OneRegisterInstruction).registerA

                val bufferIndex = implementation!!.instructions.indexOfFirst {
                    it.opcode == Opcode.CONST &&
                    (it as Instruction31i).narrowLiteral == 183314536
                } - 1

                val bufferRegister = (getInstruction(bufferIndex) as OneRegisterInstruction).registerA

                val builderMethodDescriptor = getInstruction(builderMethodIndex).toDescriptor()
                val emptyComponentFieldDescriptor = getInstruction(emptyComponentFieldIndex).toDescriptor()

                addInstructionsWithLabels(
                    insertHookIndex, // right after setting the component.pathBuilder field,
                    """
                        invoke-static {v$stringBuilderRegister, v$bufferRegister}, $ADS_PATH/LithoFilterPatch;->filter(Ljava/lang/StringBuilder;Ljava/lang/String;)Z
                        move-result v$clobberedRegister
                        if-eqz v$clobberedRegister, :not_an_ad
                        move-object/from16 v$bufferRegister, p1
                        invoke-static {v$bufferRegister}, $builderMethodDescriptor
                        move-result-object v0
                        iget-object v0, v0, $emptyComponentFieldDescriptor
                        return-object v0
                    """,
                    ExternalLabel("not_an_ad", getInstruction(insertHookIndex))
                )
            }
        } ?: return PatchResultError("Could not find the method to hook.")

        BytecodeHelper.patchStatus(context, "GeneralAds")

        return PatchResultSuccess()
    }

    private companion object {
        fun Instruction.toDescriptor() = when (val reference = (this as? ReferenceInstruction)?.reference) {
            is MethodReference -> "${reference.definingClass}->${reference.name}(${
                reference.parameterTypes.joinToString(
                    ""
                ) { it }
            })${reference.returnType}"
            is FieldReference -> "${reference.definingClass}->${reference.name}:${reference.type}"
            else -> throw PatchResultError("Unsupported reference type")
        }
    }
}
