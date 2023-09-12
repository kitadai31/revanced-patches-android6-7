package app.revanced.patches.youtube.layout.general.widesearchbar.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.layout.general.widesearchbar.bytecode.fingerprints.*
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.util.integrations.Constants.GENERAL_LAYOUT

@Name("enable-wide-searchbar-bytecode-patch")
@YouTubeCompatibility
class WideSearchbarBytecodePatch : BytecodePatch(
    listOf(
        WideSearchbarOneParentFingerprint,
        WideSearchbarTwoParentFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        arrayOf(
            WideSearchbarOneParentFingerprint to WideSearchbarOneFingerprint,
            WideSearchbarTwoParentFingerprint to WideSearchbarTwoFingerprint
        ).map { (parentFingerprint, fingerprint) ->
            parentFingerprint.result?.classDef?.let { classDef ->
                fingerprint.also { it.resolve(context, classDef) }.result?.let {
                    val index = if (fingerprint == WideSearchbarOneFingerprint) it.scanResult.patternScanResult!!.endIndex
                    else it.scanResult.patternScanResult!!.startIndex

                    val targetMethod =
                        context
                        .toMethodWalker(it.method)
                        .nextMethod(index, true)
                        .getMethod() as MutableMethod

                    injectSearchBarHook(targetMethod)
                } ?: throw fingerprint.exception
            } ?: throw parentFingerprint.exception
        }
    }

    private fun injectSearchBarHook(method: MutableMethod) {
        val index = method.implementation!!.instructions.size - 1
        method.addInstructions(
            index, """
            invoke-static {}, $GENERAL_LAYOUT->enableWideSearchbar()Z
            move-result p0
        """
        )
    }
}
