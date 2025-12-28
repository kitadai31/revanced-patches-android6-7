package app.revanced.patches.youtube.utils.fix.litho

import app.revanced.util.fingerprint.legacyFingerprint
import app.revanced.util.getReference
import app.revanced.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

internal val flatBufferInitFingerprint = legacyFingerprint(
    name = "flatBufferInitFingerprint",
    returnType = "V",
    parameters = listOf("I", "Ljava/nio/ByteBuffer;"),
    customFingerprint = { method, _ ->
        method.implementation?.instructions?.any { instruction ->
            instruction.opcode == Opcode.INVOKE_VIRTUAL &&
                    (instruction.getReference() as? MethodReference)?.let { ref ->
                        ref.name == "getInt" &&
                                ref.definingClass == "Ljava/nio/ByteBuffer;"
                    } == true
        } == true
    }
)

internal val scrollPositionFingerprint = legacyFingerprint(
    name = "scrollPositionFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PROTECTED or AccessFlags.FINAL,
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.IF_NEZ,
        Opcode.INVOKE_DIRECT,
        Opcode.RETURN_VOID
    ),
    strings = listOf("scroll_position")
)

internal val scrollTopFingerprint = legacyFingerprint(
    name = "scrollTopFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.CHECK_CAST,
        Opcode.CONST_4,
        Opcode.INVOKE_VIRTUAL,
        Opcode.GOTO,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_INTERFACE
    )
)

internal val swipeRefreshLayoutFingerprint = legacyFingerprint(
    name = "swipeRefreshLayoutFingerprint",
    returnType = "Z",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.RETURN,
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT,
        Opcode.RETURN
    ),
    customFingerprint = { method, _ -> method.definingClass.endsWith("/SwipeRefreshLayout;") }
)
