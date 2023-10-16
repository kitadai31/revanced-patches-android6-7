package app.revanced.patches.youtube.misc.protobufspoof.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object StoryboardRendererSpecRecommendedLevelFingerprint : MethodFingerprint(
    strings = listOf("#-1#"),
    opcodes = listOf(
        Opcode.IPUT_BOOLEAN,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_INTERFACE_RANGE,
        Opcode.MOVE_RESULT
    )
)