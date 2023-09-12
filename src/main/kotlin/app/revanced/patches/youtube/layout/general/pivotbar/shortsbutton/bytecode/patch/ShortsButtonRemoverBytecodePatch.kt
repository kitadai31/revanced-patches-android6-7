package app.revanced.patches.youtube.layout.general.pivotbar.shortsbutton.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.layout.general.pivotbar.shortsbutton.bytecode.fingerprints.PivotBarEnumFingerprint
import app.revanced.patches.youtube.layout.general.pivotbar.shortsbutton.bytecode.fingerprints.PivotBarShortsButtonViewFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.fingerprints.PivotBarFingerprint
import app.revanced.shared.util.integrations.Constants.GENERAL_LAYOUT
import app.revanced.shared.util.pivotbar.InjectionUtils.REGISTER_TEMPLATE_REPLACEMENT
import app.revanced.shared.util.pivotbar.InjectionUtils.injectHook

@Name("hide-shorts-button")
@YouTubeCompatibility
class ShortsButtonRemoverBytecodePatch : BytecodePatch(
    listOf(PivotBarFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        /*
         * Resolve fingerprints
         */

        PivotBarFingerprint.result?.let { parentResult ->
            with (
                arrayOf(
                    PivotBarEnumFingerprint,
                    PivotBarShortsButtonViewFingerprint
                ).onEach {
                    it.resolve(
                        context,
                        parentResult.mutableMethod,
                        parentResult.mutableClass
                    )
                }.map {
                    it.result?.scanResult?.patternScanResult ?: throw it.exception
                }
            ) {
                val enumScanResult = this[0]
                val buttonViewResult = this[1]

                val enumHookInsertIndex = enumScanResult.startIndex + 2
                val buttonHookInsertIndex = buttonViewResult.endIndex

                mapOf(
                    buttonHook to buttonHookInsertIndex,
                    enumHook to enumHookInsertIndex
                ).forEach { (hook, insertIndex) ->
                    parentResult.mutableMethod.injectHook(hook, insertIndex)
                }
            }

        } ?: throw PivotBarFingerprint.exception
    }
    private companion object {
        const val enumHook =
            "sput-object v$REGISTER_TEMPLATE_REPLACEMENT, $GENERAL_LAYOUT" +
            "->" +
            "lastPivotTab:Ljava/lang/Enum;"

        const val buttonHook =
            "invoke-static { v$REGISTER_TEMPLATE_REPLACEMENT }, $GENERAL_LAYOUT" +
            "->" +
            "hideShortsButton(Landroid/view/View;)V"
    }
}