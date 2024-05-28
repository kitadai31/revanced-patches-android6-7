package app.revanced.patches.youtube.misc.useragent

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.misc.microg.shared.Constants
import app.revanced.patches.youtube.misc.useragent.fingerprints.UserAgentHeaderBuilderFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction

@Name("spoof-user-agent")
@Description("Spoofs the YouTube client to prevent playback issues.")
@YouTubeCompatibility
class SpoofUserAgentPatch : BytecodePatch(
    listOf(UserAgentHeaderBuilderFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        UserAgentHeaderBuilderFingerprint.result?.let {
            val insertIndex = it.scanResult.patternScanResult!!.endIndex

            with (it.mutableMethod) {
                val packageNameRegister = (getInstruction(insertIndex) as FiveRegisterInstruction).registerD
                addInstruction(insertIndex, "const-string v$packageNameRegister, \"${Constants.PACKAGE_NAME}\"")
            }
        } ?: throw UserAgentHeaderBuilderFingerprint.exception
    }
}