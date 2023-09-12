package app.revanced.patches.youtube.layout.general.pivotbar.createbutton.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.MethodFingerprintExtensions.name
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patches.youtube.layout.general.pivotbar.createbutton.bytecode.fingerprints.PivotBarCreateButtonViewFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.fingerprints.PivotBarFingerprint
import app.revanced.shared.util.integrations.Constants.GENERAL_LAYOUT
import app.revanced.shared.util.pivotbar.InjectionUtils.REGISTER_TEMPLATE_REPLACEMENT
import app.revanced.shared.util.pivotbar.InjectionUtils.injectHook

@Name("hide-create-button-bytecode-patch")
@YouTubeCompatibility
class CreateButtonRemoverBytecodePatch : BytecodePatch(
    listOf(PivotBarFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        /*
         * Resolve fingerprints
         */

        val pivotBarResult = PivotBarFingerprint.result ?: throw PatchException("PivotBarFingerprint failed")

        if (!PivotBarCreateButtonViewFingerprint.resolve(context, pivotBarResult.mutableMethod, pivotBarResult.mutableClass))
            throw PatchException("${PivotBarCreateButtonViewFingerprint.name} failed")

        val createButtonResult = PivotBarCreateButtonViewFingerprint.result!!
        val insertIndex = createButtonResult.scanResult.patternScanResult!!.endIndex

        /*
         * Inject hooks
         */

        val hook =
            "invoke-static { v$REGISTER_TEMPLATE_REPLACEMENT }, $GENERAL_LAYOUT->hideCreateButton(Landroid/view/View;)V"

        createButtonResult.mutableMethod.injectHook(hook, insertIndex)
    }
}
