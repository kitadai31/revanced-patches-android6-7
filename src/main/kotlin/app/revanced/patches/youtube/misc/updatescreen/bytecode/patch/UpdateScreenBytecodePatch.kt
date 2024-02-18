package app.revanced.patches.youtube.misc.updatescreen.bytecode.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.misc.updatescreen.bytecode.fingerprints.AppBlockingCheckResultToStringFingerprint
import app.revanced.shared.extensions.exception
import com.android.tools.smali.dexlib2.util.MethodUtil

class UpdateScreenBytecodePatch : BytecodePatch(
    listOf(AppBlockingCheckResultToStringFingerprint)
) {
    override fun execute(context: BytecodeContext) {
        AppBlockingCheckResultToStringFingerprint.result?.mutableClass?.methods?.first { method ->
            MethodUtil.isConstructor(method)
                    && method.parameters == listOf("Landroid/content/Intent;", "Z")
        }?.addInstruction(
            1, "const/4 p1, 0x0"
        ) ?: throw AppBlockingCheckResultToStringFingerprint.exception
    }
}