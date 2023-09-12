package app.revanced.patches.youtube.misc.pipnotification.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.misc.pipnotification.bytecode.fingerprints.*
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception

@Name("hide-pip-notification-bytecode-patch")
@YouTubeCompatibility
class PiPNotificationBytecodePatch : BytecodePatch(
    listOf(
        PrimaryPiPFingerprint,
        SecondaryPiPFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        arrayOf(
            PrimaryPiPFingerprint,
            SecondaryPiPFingerprint
        ).map {
            it.result ?: throw it.exception
        }.forEach {
            val index = it.scanResult.patternScanResult!!.startIndex + 1
            it.mutableMethod.addInstruction(index, "return-void")
        }
    }
}