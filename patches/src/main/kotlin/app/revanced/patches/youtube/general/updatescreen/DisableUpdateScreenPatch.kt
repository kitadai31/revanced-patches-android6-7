package app.revanced.patches.youtube.general.updatescreen

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.youtube.general.components.appBlockingCheckResultToStringFingerprint
import app.revanced.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.patch.PatchList.DISABLE_UPDATE_SCREEN
import app.revanced.util.fingerprint.mutableClassOrThrow
import com.android.tools.smali.dexlib2.util.MethodUtil

val disableUpdateScreen = bytecodePatch(
    DISABLE_UPDATE_SCREEN.title,
    DISABLE_UPDATE_SCREEN.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    execute {
        appBlockingCheckResultToStringFingerprint.mutableClassOrThrow().methods.first { method ->
            MethodUtil.isConstructor(method) &&
                    method.parameters == listOf("Landroid/content/Intent;", "Z")
        }.addInstructions(
            1,
            "const/4 p1, 0x0"
        )
    }
}