package app.revanced.patches.youtube.layout.general.pivotbar.switchbutton.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.layout.general.pivotbar.switchbutton.fingerprints.AutoMotiveFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.util.integrations.Constants.GENERAL_LAYOUT
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Name("switch-create-notification-bytecode-patch")
@YouTubeCompatibility
class SwitchCreateButtonBytecodePatch : BytecodePatch(
    listOf(AutoMotiveFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        AutoMotiveFingerprint.result?.let {
            with(it.mutableMethod) {
                val insertIndex = it.scanResult.patternScanResult!!.endIndex
                val register = (getInstruction(insertIndex) as OneRegisterInstruction).registerA

                addInstructions(
                    insertIndex, """
                        invoke-static {v$register}, $GENERAL_LAYOUT->switchCreateNotification(Z)Z
                        move-result v$register
                        """
                )
            }
        } ?: throw AutoMotiveFingerprint.exception
    }
}