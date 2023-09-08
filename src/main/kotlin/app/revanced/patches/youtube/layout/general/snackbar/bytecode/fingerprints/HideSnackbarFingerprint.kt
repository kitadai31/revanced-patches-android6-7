package app.revanced.patches.youtube.layout.general.snackbar.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object HideSnackbarFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = listOf("L", "L"),
    customFingerprint = { methodDef, _ -> methodDef.definingClass == "Lcom/google/android/apps/youtube/app/common/ui/bottomui/BottomUiContainer;" }
)