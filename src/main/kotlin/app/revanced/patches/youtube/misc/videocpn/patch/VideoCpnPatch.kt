package app.revanced.patches.youtube.misc.videocpn.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.videocpn.fingerprint.OrganicPlaybackContextModelFingerprint
import app.revanced.shared.extensions.exception

class VideoCpnPatch : BytecodePatch(
    listOf(OrganicPlaybackContextModelFingerprint)
) {
    override fun execute(context: BytecodeContext) {

        insertMethod = OrganicPlaybackContextModelFingerprint.result?.mutableMethod
            ?: throw OrganicPlaybackContextModelFingerprint.exception

    }

    companion object {
        private lateinit var insertMethod: MutableMethod

        fun injectCall(
            methodDescriptor: String
        ) {
            insertMethod.addInstructions(
                2,
                "invoke-static {p1,p2}, $methodDescriptor"
            )
        }
    }
}