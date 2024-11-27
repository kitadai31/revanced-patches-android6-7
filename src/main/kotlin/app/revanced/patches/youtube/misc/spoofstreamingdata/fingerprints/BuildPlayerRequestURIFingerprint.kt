package app.revanced.patches.youtube.utils.fix.streamingdata.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patches.youtube.utils.fix.streamingdata.fingerprints.BuildPlayerRequestURIFingerprint.indexOfToStringInstruction
import app.revanced.shared.util.bytecode.getReference
import app.revanced.shared.util.bytecode.indexOfFirstInstruction
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal object BuildPlayerRequestURIFingerprint : MethodFingerprint(
    returnType = "Ljava/lang/String;",
    strings = listOf(
        "key",
        "asig",
    ),
    customFingerprint = { methodDef, _ ->
        indexOfToStringInstruction(methodDef) >= 0
    },
) {
    fun indexOfToStringInstruction(methodDef: Method) =
        methodDef.indexOfFirstInstruction {
            opcode == Opcode.INVOKE_VIRTUAL &&
                    getReference<MethodReference>().toString() == "Landroid/net/Uri;->toString()Ljava/lang/String;"
        }
}