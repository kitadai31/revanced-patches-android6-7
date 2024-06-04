package app.revanced.patches.youtube.misc.spoofclient.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.shared.util.bytecode.getReference
import app.revanced.shared.util.bytecode.indexOfFirstInstruction
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal object PlayerGestureConfigSyntheticFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("Ljava/lang/Object;"),
    opcodes = listOf(
        Opcode.SGET_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.IF_EQZ,
        Opcode.IF_EQZ,
        // Opcode.IGET_OBJECT,      // doesn't exist in 17.34.36
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,      // playerGestureConfig.downAndOutLandscapeAllowed
        Opcode.MOVE_RESULT,
        Opcode.CHECK_CAST,
        Opcode.IPUT_BOOLEAN,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,      // playerGestureConfig.downAndOutPortraitAllowed
        Opcode.MOVE_RESULT,
        Opcode.IPUT_BOOLEAN,
        Opcode.RETURN_VOID,
    ),
    customFingerprint = { methodDef, classDef ->
        fun indexOfDownAndOutAllowedInstruction(methodDef: Method) =
            methodDef.indexOfFirstInstruction {
                val reference = getReference<MethodReference>()
                reference?.definingClass == "Lcom/google/android/libraries/youtube/innertube/model/media/PlayerConfigModel;" &&
                        reference.parameterTypes.isEmpty() &&
                        reference.returnType == "Z"
            }

        // This method is always called "a" because this kind of class always has a single method.
        methodDef.name == "a" && classDef.methods.count() == 19 && // 18 constructor + 1 method
                indexOfDownAndOutAllowedInstruction(methodDef) > 0
    },
)