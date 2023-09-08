package app.revanced.patches.youtube.misc.returnyoutubedislike.bytecode.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object TextComponentSpecFingerprint : MethodFingerprint(
    returnType = "L",
    accessFlags = AccessFlags.STATIC.getValue(),
    opcodes = listOf(Opcode.CMPL_FLOAT),
    customFingerprint = { methodDef, _ ->
        methodDef.implementation!!.instructions.any {
            ((it as? NarrowLiteralInstruction)?.narrowLiteral == 16842907)
        }
    }
)