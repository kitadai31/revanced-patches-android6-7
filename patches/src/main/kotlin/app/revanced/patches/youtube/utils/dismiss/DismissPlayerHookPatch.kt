package app.revanced.patches.youtube.utils.dismiss

import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.utils.extension.Constants.EXTENSION_PATH
import app.revanced.patches.youtube.utils.extension.sharedExtensionPatch
import app.revanced.util.addStaticFieldToExtension
import app.revanced.util.findMethodOrThrow
import app.revanced.util.fingerprint.methodOrThrow
import app.revanced.util.getReference
import app.revanced.util.getWalkerMethod
import app.revanced.util.indexOfFirstInstructionOrThrow
import app.revanced.util.indexOfFirstInstructionReversedOrThrow
import app.revanced.util.indexOfFirstLiteralInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val EXTENSION_VIDEO_UTILS_CLASS_DESCRIPTOR =
    "$EXTENSION_PATH/utils/VideoUtils;"

private lateinit var dismissMethod: MutableMethod

val dismissPlayerHookPatch = bytecodePatch(
    description = "dismissPlayerHookPatch"
) {
    dependsOn(sharedExtensionPatch)

    execute {
        dismissPlayerOnClickListenerFingerprint.methodOrThrow().apply {
            val literalIndex =
                indexOfFirstLiteralInstructionOrThrow(DISMISS_PLAYER_LITERAL)
            val dismissPlayerIndex = indexOfFirstInstructionOrThrow(literalIndex) {
                val reference = getReference<MethodReference>()
                opcode == Opcode.INVOKE_INTERFACE &&
                        reference?.returnType == "V" &&
                        reference.parameterTypes.isEmpty()
            }

            // In 17.34.36, these methods were interfaces, so specify it directly.
            dismissMethod = findMethodOrThrow("Lkxs;") { name == "t" }

            // Target
            // invoke-interface {v0}, Lkkg;->f()V
            // kkg > esn > lad

            // Field
            // iget-object v0, p0, Lkhf;->a:Ljava/lang/Object;

            addInstructions(
                dismissPlayerIndex + 1,
                """
                    check-cast v0, Llad;
                    sput-object v0, $EXTENSION_VIDEO_UTILS_CLASS_DESCRIPTOR->dismissPlayerClass:Llad;
                """
            )

            val smaliInstructions =
                """
                        if-eqz v0, :ignore
                        invoke-virtual {v0}, Llad;->f()V
                        :ignore
                        return-void
                        """

            addStaticFieldToExtension(
                EXTENSION_VIDEO_UTILS_CLASS_DESCRIPTOR,
                "dismissPlayer",
                "dismissPlayerClass",
                "Llad;",
                smaliInstructions,
                false
            )
        }
    }
}

/**
 * This method is called when the video is closed.
 */
internal fun hookDismissObserver(descriptor: String) =
    dismissMethod.addInstruction(
        0,
        "invoke-static {}, $descriptor"
    )