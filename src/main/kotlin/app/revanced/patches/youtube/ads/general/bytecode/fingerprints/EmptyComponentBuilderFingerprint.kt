package app.revanced.patches.youtube.ads.general.bytecode.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object EmptyComponentBuilderFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.INVOKE_STATIC_RANGE
    ),
)