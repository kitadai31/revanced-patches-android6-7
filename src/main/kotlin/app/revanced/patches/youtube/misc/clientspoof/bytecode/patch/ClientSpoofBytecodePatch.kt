package app.revanced.patches.youtube.misc.clientspoof.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.misc.clientspoof.bytecode.fingerprints.UserAgentHeaderBuilderFingerprint
import app.revanced.patches.youtube.misc.microg.shared.Constants.PACKAGE_NAME
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction

@Name("client-spoof-bytecode-patch")
@YouTubeCompatibility
class ClientSpoofBytecodePatch : BytecodePatch(
    listOf(UserAgentHeaderBuilderFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        UserAgentHeaderBuilderFingerprint.result?.let {
            val insertIndex = it.scanResult.patternScanResult!!.endIndex

            with (it.mutableMethod) {
                val packageNameRegister = (getInstruction(insertIndex) as FiveRegisterInstruction).registerD
                addInstruction(insertIndex, "const-string v$packageNameRegister, \"$PACKAGE_NAME\"")
            }
        } ?: throw UserAgentHeaderBuilderFingerprint.exception
    }
}
