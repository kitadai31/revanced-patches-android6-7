package app.revanced.patches.youtube.layout.general.snackbar.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.layout.general.snackbar.bytecode.fingerprints.HideSnackbarFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.util.integrations.Constants.GENERAL_LAYOUT

@Name("hide-snackbar-bytecode-patch")
@YouTubeCompatibility
class HideSnackbarBytecodePatch : BytecodePatch(
    listOf(
        HideSnackbarFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        HideSnackbarFingerprint.result?.mutableMethod?.let {
            it.addInstructionsWithLabels(
                0, """
                    invoke-static {}, $GENERAL_LAYOUT->hideSnackbar()Z
                    move-result v0
                    if-eqz v0, :default
                    return-void
                    """, ExternalLabel("default", it.getInstruction(0))
            )
        } ?: throw HideSnackbarFingerprint.exception
    }
}
