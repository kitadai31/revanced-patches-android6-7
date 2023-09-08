package app.revanced.patches.youtube.misc.openlinksdirectly.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patches.youtube.misc.openlinksdirectly.bytecode.fingerprints.*
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.toErrorResult
import app.revanced.shared.util.integrations.Constants.MISC_PATH
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c

@Name("enable-open-links-directly-bytecode-patch")
@YouTubeCompatibility
class OpenLinksDirectlyBytecodePatch : BytecodePatch(
    listOf(
        OpenLinksDirectlyFingerprintPrimary,
        OpenLinksDirectlyFingerprintSecondary
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        arrayOf(
            OpenLinksDirectlyFingerprintPrimary,
            OpenLinksDirectlyFingerprintSecondary
        ).forEach {
            val result = it.result?: return it.toErrorResult()
            val insertIndex = result.scanResult.patternScanResult!!.startIndex
            with (result.mutableMethod) {
                val register = (implementation!!.instructions[insertIndex] as Instruction35c).registerC
                replaceInstruction(
                    insertIndex,
                    "invoke-static {v$register}, $MISC_PATH/OpenLinksDirectlyPatch;->enableBypassRedirect(Ljava/lang/String;)Landroid/net/Uri;"
                )
            }
        }

        return PatchResultSuccess()
    }
}