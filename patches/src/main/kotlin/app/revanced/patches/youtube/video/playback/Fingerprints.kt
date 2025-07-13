package app.revanced.patches.youtube.video.playback

import app.revanced.util.fingerprint.legacyFingerprint
import app.revanced.util.getReference
import app.revanced.util.indexOfFirstInstruction
import app.revanced.util.indexOfFirstInstructionReversed
import app.revanced.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

internal val deviceDimensionsModelToStringFingerprint = legacyFingerprint(
    name = "deviceDimensionsModelToStringFingerprint",
    returnType = "L",
    strings = listOf("minh.", ";maxh.")
)

internal val hdrCapabilityFingerprint = legacyFingerprint(
    name = "hdrCapabilityFingerprint",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf(
        "av1_profile_main_10_hdr_10_plus_supported",
        "video/av01"
    )
)

internal val playbackSpeedChangedFromRecyclerViewFingerprint = legacyFingerprint(
    name = "playbackSpeedChangedFromRecyclerViewFingerprint",
    returnType = "L",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IGET,
        Opcode.INVOKE_VIRTUAL
    ),
    customFingerprint = { method, _ ->
        method.indexOfFirstInstruction {
            opcode == Opcode.IGET &&
                    getReference<FieldReference>()?.type == "F"
        } >= 0
    }
)

internal val loadVideoParamsFingerprint = legacyFingerprint(
    name = "loadVideoParamsFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.CONSTRUCTOR,
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT,
        Opcode.IPUT,
        Opcode.INVOKE_INTERFACE,
    )
)

internal val loadVideoParamsParentFingerprint = legacyFingerprint(
    name = "loadVideoParamsParentFingerprint",
    returnType = "Z",
    parameters = listOf("J"),
    strings = listOf("LoadVideoParams.playerListener = null")
)

internal val qualityChangedFromRecyclerViewFingerprint = legacyFingerprint(
    name = "qualityChangedFromRecyclerViewFingerprint",
    returnType = "L",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.IGET,  // Video resolution (human readable).
        Opcode.IGET_OBJECT,
        Opcode.IGET_BOOLEAN,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_STATIC,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_DIRECT,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.INVOKE_VIRTUAL,
        Opcode.GOTO,
        Opcode.CONST_4,
        Opcode.IF_NE,
        Opcode.IGET_OBJECT,
        Opcode.INVOKE_INTERFACE,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.IGET,
    )
)

internal val qualityMenuViewInflateOnItemClickFingerprint = legacyFingerprint(
    name = "qualityMenuViewInflateOnItemClickFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    customFingerprint = { method, _ ->
        method.name == "onItemClick" &&
                indexOfContextInstruction(method) >= 0
    }
)

internal fun indexOfContextInstruction(method: Method) =
    method.indexOfFirstInstructionReversed {
        opcode == Opcode.IGET_OBJECT &&
                getReference<FieldReference>()?.type == "Landroid/content/Context;"
    }

internal val qualitySetterFingerprint = legacyFingerprint(
    name = "qualitySetterFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L"),
    strings = listOf("VIDEO_QUALITIES_MENU_BOTTOM_SHEET_FRAGMENT")
)

internal val vp9CapabilityFingerprint = legacyFingerprint(
    name = "vp9CapabilityFingerprint",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    returnType = "Z",
    strings = listOf(
        "vp9_supported",
        "video/x-vnd.on2.vp9"
    )
)

internal val playbackSpeedInitializeFingerprint = legacyFingerprint(
    name = "playbackSpeedInitializeFingerprint",
    returnType = "F",
    accessFlags = AccessFlags.PRIVATE or AccessFlags.STATIC,
    parameters = listOf("L"),
    opcodes = listOf(
        Opcode.IGET,
        Opcode.RETURN
    )
)
