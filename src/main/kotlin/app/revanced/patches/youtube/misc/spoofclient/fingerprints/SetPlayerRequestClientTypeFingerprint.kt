package app.revanced.patches.youtube.misc.spoofclient.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object SetPlayerRequestClientTypeFingerprint : MethodFingerprint(
    strings = listOf("10.29"),
    opcodes = listOf(
        Opcode.IGET,
        Opcode.IPUT, // Sets ClientInfo.clientId.
    ),
)