package app.revanced.patches.youtube.misc.spoofclient.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal object CreatePlaybackSpeedMenuItemFingerprint : MethodFingerprint(
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "V",
    parameters = listOf("[L"),
    opcodes = listOf(
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.IGET_OBJECT,
        Opcode.CONST_4,
        Opcode.IF_EQZ,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT, // Return value controls the creation of the playback speed menu item.
        Opcode.IF_EQZ, // If the return value is false, the playback speed menu item is not created.
    ),
)