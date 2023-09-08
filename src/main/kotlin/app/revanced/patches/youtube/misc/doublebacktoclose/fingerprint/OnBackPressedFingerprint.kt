package app.revanced.patches.youtube.misc.doublebacktoclose.fingerprint

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.Opcode

object OnBackPressedFingerprint : MethodFingerprint(
    opcodes = listOf(
        Opcode.RETURN_VOID
    ),
    customFingerprint = { methodDef, _ ->
        methodDef.definingClass.endsWith("WatchWhileActivity;")
                && methodDef.name == "onBackPressed"
    }
)
