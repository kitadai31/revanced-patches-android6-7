package app.revanced.shared.patches.theme.bytecode

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.fingerprints.LithoThemeFingerprint
import app.revanced.shared.patches.theme.resource.GeneralThemeResourcePatch
import app.revanced.shared.util.integrations.Constants.UTILS_PATH

@Name("general-theme")
@DependsOn([GeneralThemeResourcePatch::class])
@YouTubeCompatibility
class GeneralThemePatch : BytecodePatch(
    listOf(
        LithoThemeFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        val result = LithoThemeFingerprint.result!!
        val method = result.mutableMethod
        val patchIndex = result.scanResult.patternScanResult!!.endIndex - 1

        method.addInstructions(
            patchIndex, """
                invoke-static {p1}, $UTILS_PATH/LithoThemePatch;->applyLithoTheme(I)I
                move-result p1
            """
        )
    }
}
