package app.revanced.patches.youtube.video.quality.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.youtube.misc.videoid.legacy.patch.LegacyVideoIdPatch
import app.revanced.patches.youtube.video.quality.bytecode.fingerprints.VideoQualityReferenceFingerprint
import app.revanced.patches.youtube.video.quality.bytecode.fingerprints.VideoQualitySetterFingerprint
import app.revanced.patches.youtube.video.quality.bytecode.fingerprints.VideoUserQualityChangeFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.util.integrations.Constants.VIDEO_PATH
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference

@Name("default-video-quality-bytecode-patch")
@DependsOn([LegacyVideoIdPatch::class])
@YouTubeCompatibility
class VideoQualityBytecodePatch : BytecodePatch(
    listOf(
        VideoQualitySetterFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        VideoQualitySetterFingerprint.result?.let { parentResult ->
            VideoQualityReferenceFingerprint.also { it.resolve(context, parentResult.classDef) }.result?.let { result ->
                val instructions = result.method.implementation!!.instructions
                val qualityFieldReference =
                    (instructions.elementAt(0) as ReferenceInstruction).reference as FieldReference

                val qIndexMethodName =
                    context.classes.single { it.type == qualityFieldReference.type }.methods.single { it.parameterTypes.first() == "I" }.name

                parentResult.mutableMethod.addInstructions(
                    0, """
                        iget-object v0, p0, ${result.classDef.type}->${qualityFieldReference.name}:${qualityFieldReference.type}
                        const-string v1, "$qIndexMethodName"
                        invoke-static {p1, p2, v0, v1}, $INTEGRATIONS_VIDEO_QUALITY_CLASS_DESCRIPTOR->setVideoQuality([Ljava/lang/Object;ILjava/lang/Object;Ljava/lang/String;)I
                        move-result p2
                    """,
                )
            } ?: throw VideoQualityReferenceFingerprint.exception

            VideoUserQualityChangeFingerprint.also { it.resolve(context, parentResult.classDef) }.result?.mutableMethod?.addInstruction(
                0,
                "invoke-static {p3}, $INTEGRATIONS_VIDEO_QUALITY_CLASS_DESCRIPTOR->userChangedQuality(I)V"
            ) ?: throw VideoUserQualityChangeFingerprint.exception

        } ?: throw VideoQualitySetterFingerprint.exception

        LegacyVideoIdPatch.injectCall("$INTEGRATIONS_VIDEO_QUALITY_CLASS_DESCRIPTOR->newVideoStarted(Ljava/lang/String;)V")
    }
    private companion object {
        const val INTEGRATIONS_VIDEO_QUALITY_CLASS_DESCRIPTOR =
            "$VIDEO_PATH/VideoQualityPatch;"
    }
}
