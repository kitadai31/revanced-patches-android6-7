package app.revanced.patches.youtube.layout.general.startupshortsreset.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.layout.general.startupshortsreset.bytecode.fingerprints.UserWasInShortsFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.toErrorResult
import app.revanced.shared.util.integrations.Constants.GENERAL_LAYOUT
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Name("hide-startup-shorts-player-bytecode-patch")
@YouTubeCompatibility
class HideShortsOnStartupBytecodePatch : BytecodePatch(
    listOf(
        UserWasInShortsFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        UserWasInShortsFingerprint.result?.let {
            val insertIndex = it.scanResult.patternScanResult!!.endIndex + 1

            with (it.mutableMethod) {
                val register = (getInstruction(insertIndex - 1) as OneRegisterInstruction).registerA + 2
                addInstructionsWithLabels(
                    insertIndex, """
                        invoke-static { }, $GENERAL_LAYOUT->hideStartupShortsPlayer()Z
                        move-result v$register
                        if-eqz v$register, :show_startup_shorts_player
                        return-void
                    """, ExternalLabel("show_startup_shorts_player", getInstruction(insertIndex))
                )
            }
        } ?: return UserWasInShortsFingerprint.toErrorResult()

        return PatchResultSuccess()
    }
}
