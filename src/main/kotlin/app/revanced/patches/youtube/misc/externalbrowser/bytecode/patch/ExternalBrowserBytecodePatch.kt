package app.revanced.patches.youtube.misc.externalbrowser.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.misc.externalbrowser.bytecode.fingerprints.*
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.util.integrations.Constants.MISC_PATH
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21c

@Name("enable-external-browser-bytecode-patch")
@YouTubeCompatibility
class ExternalBrowserBytecodePatch : BytecodePatch(
    listOf(
        ExternalBrowserPrimaryFingerprint,
        ExternalBrowserSecondaryFingerprint,
        ExternalBrowserTertiaryFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        arrayOf(
            ExternalBrowserPrimaryFingerprint,
            ExternalBrowserSecondaryFingerprint,
            ExternalBrowserTertiaryFingerprint
        ).forEach {
            val result = it.result?: throw it.exception
            val endIndex = result.scanResult.patternScanResult!!.endIndex
            with (result.mutableMethod) {
                val register = (implementation!!.instructions[endIndex] as Instruction21c).registerA
                addInstructions(
                    endIndex + 1, """
                        invoke-static {v$register}, $MISC_PATH/ExternalBrowserPatch;->enableExternalBrowser(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$register
                    """
                )
            }
        }
    }
}