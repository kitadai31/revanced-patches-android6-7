package app.revanced.patches.youtube.layout.general.tabletminiplayer.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.layout.general.tabletminiplayer.bytecode.fingerprints.*
import app.revanced.patches.youtube.misc.resourceid.patch.SharedResourcdIdPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.util.integrations.Constants.GENERAL_LAYOUT
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction

@Name("enable-tablet-miniplayer-bytecode-patch")
@DependsOn([SharedResourcdIdPatch::class])
@YouTubeCompatibility
class TabletMiniPlayerBytecodePatch : BytecodePatch(
    listOf(
        MiniPlayerDimensionsCalculatorFingerprint,
        MiniPlayerResponseModelSizeCheckFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        MiniPlayerDimensionsCalculatorFingerprint.result?.let { parentResult ->
            // first resolve the fingerprints via the parent fingerprint
            val miniPlayerClass = parentResult.classDef

            arrayOf(
                MiniPlayerOverrideNoContextFingerprint,
                MiniPlayerOverrideFingerprint,
                MiniPlayerResponseModelSizeCheckFingerprint
            ).map {
                it to (it.also { it.resolve(context, miniPlayerClass) }.result ?: throw it.exception)
            }.forEach { (fingerprint, result) ->
                if (fingerprint == MiniPlayerOverrideNoContextFingerprint) {
                    val (method, _, parameterRegister) = result.addProxyCall()
                    method.insertOverride(method.implementation!!.instructions.size - 1, parameterRegister)
                } else {
                    val (_, _, _) = result.addProxyCall()
                }
            }
        } ?: throw MiniPlayerDimensionsCalculatorFingerprint.exception
    }

    // helper methods
    private companion object {
        fun MethodFingerprintResult.addProxyCall(): Triple<MutableMethod, Int, Int> {
            val (method, scanIndex, parameterRegister) = this.unwrap()
            method.insertOverride(scanIndex, parameterRegister)

            return Triple(method, scanIndex, parameterRegister)
        }

        fun MutableMethod.insertOverride(index: Int, overrideRegister: Int) {
            this.addInstructions(
                index,
                """
                    invoke-static {v$overrideRegister}, $GENERAL_LAYOUT->enableTabletMiniPlayer(Z)Z
                    move-result v$overrideRegister
                    """
            )
        }

        fun MethodFingerprintResult.unwrap(): Triple<MutableMethod, Int, Int> {
            val scanIndex = this.scanResult.patternScanResult!!.endIndex
            val method = this.mutableMethod
            val instructions = method.implementation!!.instructions
            val parameterRegister = (instructions[scanIndex] as OneRegisterInstruction).registerA

            return Triple(method, scanIndex, parameterRegister)
        }
    }
}
