package app.revanced.patches.youtube.misc.microg.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.youtube.layout.player.castbutton.resource.patch.HideCastButtonPatch
import app.revanced.patches.youtube.misc.useragent.SpoofUserAgentPatch
import app.revanced.patches.youtube.misc.microg.bytecode.fingerprints.*
import app.revanced.patches.youtube.misc.microg.shared.Constants.PACKAGE_NAME
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.patches.options.PatchOptions
import app.revanced.shared.util.bytecode.BytecodeHelper
import app.revanced.shared.util.microg.MicroGBytecodeHelper

@Name("microg-support-bytecode-patch")
@DependsOn(
    [
        SpoofUserAgentPatch::class,
        HideCastButtonPatch::class,
        PatchOptions::class
    ]
)
@YouTubeCompatibility
class MicroGBytecodePatch : BytecodePatch(
    listOf(
        CastContextFetchFingerprint,
        CastDynamiteModuleFingerprint,
        CastDynamiteModuleV2Fingerprint,
        GooglePlayUtilityFingerprint,
        IntegrityCheckFingerprint,
        PrimeFingerprint,
        ServiceCheckFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        val packageName = PatchOptions.YouTube_PackageName

        // apply common microG patch
        MicroGBytecodeHelper.patchBytecode(
            context, arrayOf(
                MicroGBytecodeHelper.packageNameTransform(
                    PACKAGE_NAME,
                    "$packageName"
                )
            ),
            MicroGBytecodeHelper.PrimeMethodTransformationData(
                PrimeFingerprint,
                PACKAGE_NAME,
                "$packageName"
            ),
            listOf(
                IntegrityCheckFingerprint,
                ServiceCheckFingerprint,
                GooglePlayUtilityFingerprint,
                CastDynamiteModuleFingerprint,
                CastDynamiteModuleV2Fingerprint,
                CastContextFetchFingerprint
            )
        )

        BytecodeHelper.injectInit(context, "MicroGPatch", "checkAvailability")
    }
}
