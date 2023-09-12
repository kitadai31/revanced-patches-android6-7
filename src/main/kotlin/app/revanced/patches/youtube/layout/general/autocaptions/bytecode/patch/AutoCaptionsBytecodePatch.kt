package app.revanced.patches.youtube.layout.general.autocaptions.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.layout.general.autocaptions.bytecode.fingerprints.SubtitleTrackFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.fingerprints.StartVideoInformerFingerprint
import app.revanced.shared.fingerprints.SubtitleButtonControllerFingerprint
import app.revanced.shared.util.integrations.Constants.GENERAL_LAYOUT

@Name("hide-auto-captions-bytecode-patch")
@YouTubeCompatibility
class AutoCaptionsBytecodePatch : BytecodePatch(
    listOf(
        StartVideoInformerFingerprint,
        SubtitleButtonControllerFingerprint,
        SubtitleTrackFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        listOf(
            StartVideoInformerFingerprint.toPatch(Status.DISABLED),
            SubtitleButtonControllerFingerprint.toPatch(Status.ENABLED)
        ).forEach { (fingerprint, status) ->
            with(fingerprint.result?.mutableMethod ?: throw fingerprint.exception) {
                addInstructions(
                    0,
                    """
                    const/4 v0, ${status.value}
                    sput-boolean v0, $GENERAL_LAYOUT->captionsButtonStatus:Z
                    """
                )
            }
        }

        SubtitleTrackFingerprint.result?.mutableMethod?.let {
            it.addInstructionsWithLabels(
                0, """
                    invoke-static {}, $GENERAL_LAYOUT->hideAutoCaptions()Z
                    move-result v0
                    if-eqz v0, :auto_captions_shown
                    sget-boolean v0, $GENERAL_LAYOUT->captionsButtonStatus:Z
                    if-nez v0, :auto_captions_shown
                    const/4 v0, 0x1
                    return v0
                """, ExternalLabel("auto_captions_shown", it.getInstruction(0))
            )
        } ?: throw SubtitleTrackFingerprint.exception
    }

    private fun MethodFingerprint.toPatch(visibility: Status) = SetStatus(this, visibility)

    private data class SetStatus(val fingerprint: MethodFingerprint, val visibility: Status)

    private enum class Status(val value: Int) {
        ENABLED(1),
        DISABLED(0)
    }
}