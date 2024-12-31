package app.revanced.patches.youtube.general.startpage

import app.revanced.util.fingerprint.legacyFingerprint
import com.android.tools.smali.dexlib2.Opcode

internal val browseIdFingerprint = legacyFingerprint(
    name = "browseIdFingerprint",
    returnType = "Lcom/google/android/apps/youtube/app/common/ui/navigation/PaneDescriptor;",
    parameters = emptyList(),
    opcodes = listOf(
        Opcode.INVOKE_VIRTUAL,
        Opcode.MOVE_RESULT_OBJECT,
        Opcode.RETURN_OBJECT,
    ),
    strings = listOf("FEwhat_to_watch"),
    customFingerprint = { methodDef, _ ->
        methodDef.name == "w"
    }
)

internal val intentActionFingerprint = legacyFingerprint(
    name = "intentActionFingerprint",
    // In 17.34.36, the target method was part of WatchWhileActivity.onPostCreate()
    parameters = listOf("Landroid/os/Bundle;"),
    strings = listOf("has_handled_intent"),
)
