package app.revanced.patches.youtube.misc.spoofclient.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

// 19.20.34 -> agob.J(anch)
//17.34.36 -> aatn.c()
object CreatePlayerRequestBodyFingerprint : MethodFingerprint(
    returnType = "V",
//    parameters = listOf("L"),
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.CHECK_CAST,
        Opcode.IGET,
        Opcode.AND_INT_LIT16,
    ),
    strings = listOf("ms"),
)