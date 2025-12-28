package app.revanced.patches.youtube.utils.fix.litho

import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patches.shared.extension.Constants.PATCHES_PATH
import app.revanced.patches.youtube.utils.patch.PatchList.LITHO_BUFFER_FIX
import app.revanced.util.fingerprint.methodOrThrow

private const val EXTENSION_LITHO_FIX_CLASS_DESCRIPTOR =
    "$PATCHES_PATH/litho/LithoBufferFix;"

val lithoBufferFixPatch = bytecodePatch(
    LITHO_BUFFER_FIX.title,
    LITHO_BUFFER_FIX.summary
) {
    execute {
        flatBufferInitFingerprint.methodOrThrow().apply {
            addInstructions(
                0,
                """
                invoke-static {p1, p2}, $EXTENSION_LITHO_FIX_CLASS_DESCRIPTOR->validateBuffer(ILjava/nio/ByteBuffer;)Ljava/nio/ByteBuffer;
                move-result-object p2
                """
            )
        }
    }
}
