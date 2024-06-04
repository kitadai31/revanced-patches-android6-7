package app.revanced.patches.youtube.misc.spoofclient.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object BuildPlayerRequestURIFingerprint : MethodFingerprint(
    returnType = "Ljava/lang/String;",
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL, // Register holds player request URI.
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IPUT_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.RETURN_OBJECT,
    ),
    strings = listOf(
        "key",
        "asig",
    ),
)