package app.revanced.patches.youtube.misc.drawable.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

object DrawableFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PROTECTED or AccessFlags.FINAL,
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.IF_NEZ,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_VIRTUAL, // Paint.setColor: inject point
        Opcode.RETURN_VOID
    ),
    customFingerprint = { methodDef, classDef ->
        methodDef.name == "onBoundsChange"
                && classDef.superclass == "Landroid/graphics/drawable/Drawable;"
    }
)