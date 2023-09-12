package app.revanced.shared.patches.videoads

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.shared.extensions.exception
import app.revanced.shared.fingerprints.LegacyVideoAdsFingerprint
import app.revanced.shared.fingerprints.MainstreamVideoAdsFingerprint
import app.revanced.shared.fingerprints.MainstreamVideoAdsParentFingerprint

@Name("general-video-ads-patch")
class GeneralVideoAdsPatch : BytecodePatch(
    listOf(
        LegacyVideoAdsFingerprint,
        MainstreamVideoAdsParentFingerprint,
    )
) {
    override fun execute(context: BytecodeContext) {
        val LegacyVideoAdsResult = LegacyVideoAdsFingerprint.result ?: throw LegacyVideoAdsFingerprint.exception

        LegacyVideoAdsMethod =
            context.toMethodWalker(LegacyVideoAdsResult.method)
                .nextMethod(13, true)
                .getMethod() as MutableMethod

        MainstreamVideoAdsFingerprint.resolve(context, MainstreamVideoAdsParentFingerprint.result!!.classDef)

        val MainstreamAdsResult = MainstreamVideoAdsFingerprint.result ?: throw MainstreamVideoAdsFingerprint.exception

        MainstreamVideoAdsMethod = MainstreamAdsResult.mutableMethod

        InsertIndex = MainstreamAdsResult.scanResult.patternScanResult!!.endIndex
    }

    internal companion object {
        var InsertIndex: Int = 0

        lateinit var LegacyVideoAdsMethod: MutableMethod
        lateinit var MainstreamVideoAdsMethod: MutableMethod

        fun injectLegacyAds(
            descriptor: String
        ) {
            LegacyVideoAdsMethod.addInstructions(
                0, """
                    invoke-static {}, $descriptor
                    move-result v1
            """
            )
        }

        fun injectMainstreamAds(
            descriptor: String
        ) {
            MainstreamVideoAdsMethod.addInstructionsWithLabels(
                InsertIndex, """
                    invoke-static {}, $descriptor
                    move-result v1
                    if-nez v1, :show_video_ads
                    return-void
            """, ExternalLabel("show_video_ads", MainstreamVideoAdsMethod.getInstruction(InsertIndex))
            )
        }

    }
}