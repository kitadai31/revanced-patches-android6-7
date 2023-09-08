package app.revanced.patches.youtube.misc.litho.filter.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.misc.litho.filter.fingerprints.LithoFingerprint
import app.revanced.patches.youtube.misc.swiperefresh.patch.SwipeRefreshPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.integrations.Constants.ADS_PATH
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.Opcode

@DependsOn(
    [
        //DoubleBackToClosePatch::class,
        SwipeRefreshPatch::class
    ]
)
@YouTubeCompatibility
class LithoFilterPatch : BytecodePatch(
    listOf(
        LithoFingerprint
    )
)
{
    override fun execute(context: BytecodeContext): PatchResult {

        //Litho
        val lithoMethod = LithoFingerprint.result!!.mutableMethod
        val lithoMethodThirdParam = lithoMethod.parameters[2]
        val lithoRegister1 = lithoMethod.implementation!!.registerCount - lithoMethod.parameters.size + 1
        val lithoRegister2 = lithoRegister1 + 1
        val lithoInstructions = lithoMethod.implementation!!.instructions

        val lithoIndex = lithoInstructions.indexOfFirst {
            it.opcode == Opcode.CONST_STRING &&
            (it as BuilderInstruction21c).reference.toString() == "Element missing type extension"
        } + 2

        val FirstReference =
            lithoMethod.let { method ->
                (method.implementation!!.instructions.elementAt(lithoIndex) as ReferenceInstruction).reference as MethodReference
            }

        val SecondReference =
            lithoMethod.let { method ->
                (method.implementation!!.instructions.elementAt(lithoIndex + 2) as ReferenceInstruction).reference as FieldReference
            }

        lithoMethod.addInstructionsWithLabels(
            0, """
                move-object/from16 v1, v$lithoRegister1
                invoke-virtual {v1}, Ljava/lang/Object;->toString()Ljava/lang/String;
                move-result-object v1
                move-object/from16 v2, v$lithoRegister2
                iget-object v2, v2, $lithoMethodThirdParam->b:Ljava/nio/ByteBuffer;
                invoke-static {v1, v2}, $ADS_PATH/ExtendedLithoFilterPatch;->InflatedLithoView(Ljava/lang/String;Ljava/nio/ByteBuffer;)Z
                move-result v3
                if-eqz v3, :do_not_block
                move-object/from16 v0, p1
                invoke-static {v0}, $FirstReference
                move-result-object v0
                iget-object v0, v0, ${SecondReference.definingClass}->${SecondReference.name}:${SecondReference.type}
                return-object v0
            """, ExternalLabel("do_not_block", lithoMethod.getInstruction(0))
        )

        return PatchResultSuccess()
    }
}
