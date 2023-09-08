package app.revanced.patches.youtube.misc.protobufspoof.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.data.toMethodWalker
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.playertype.patch.PlayerTypeHookPatch
import app.revanced.patches.youtube.misc.protobufpoof.fingerprints.ProtobufParameterBuilderFingerprint
import app.revanced.patches.youtube.misc.protobufspoof.fingerprints.ScrubbedPreviewLayoutFingerprint
import app.revanced.patches.youtube.misc.protobufspoof.fingerprints.StoryboardThumbnailFingerprint
import app.revanced.patches.youtube.misc.protobufspoof.fingerprints.StoryboardThumbnailParentFingerprint
import app.revanced.patches.youtube.misc.protobufspoof.fingerprints.SubtitleWindowFingerprint
import app.revanced.patches.youtube.misc.videoid.mainstream.patch.MainstreamVideoIdPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.toErrorResult
import app.revanced.shared.util.integrations.Constants.MISC_PATH
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction

@Name("spoof-player-parameters-bytecode-patch")
@DependsOn([
    PlayerTypeHookPatch::class,
    MainstreamVideoIdPatch::class,
    SpoofPlayerParameterResourcePatch::class
])
@YouTubeCompatibility
class SpoofPlayerParameterBytecodePatch : BytecodePatch(
    listOf(
        ProtobufParameterBuilderFingerprint,
        SubtitleWindowFingerprint,
        ScrubbedPreviewLayoutFingerprint,
        StoryboardThumbnailParentFingerprint
    )
) {
    override fun execute(context: BytecodeContext): PatchResult {

        // hook parameter
        ProtobufParameterBuilderFingerprint.result?.let {
            with (context
                .toMethodWalker(it.method)
                .nextMethod(it.scanResult.patternScanResult!!.startIndex, true)
                .getMethod() as MutableMethod
            ) {
                val protobufParam = 3

                addInstructions(
                    0,
                    """
                        invoke-static {p$protobufParam}, $MISC_PATH/SpoofPlayerParameterPatch;->overridePlayerParameter(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object p$protobufParam
                    """
                )
            }
        } ?: return ProtobufParameterBuilderFingerprint.toErrorResult()


        // ============================================================
        //             ↓ workarounds for side effects ↓
        // ============================================================

        // When the player parameter is spoofed in incognito mode, this value will always be false
        // If this value is true, the timestamp and chapter are shown when tapping the seekbar.
        StoryboardThumbnailParentFingerprint.result?.classDef?.let { classDef ->
            StoryboardThumbnailFingerprint.also {
                it.resolve(
                    context,
                    classDef
                )
            }.result?.let {
                it.mutableMethod.apply {
                    val targetIndex = it.scanResult.patternScanResult!!.endIndex
                    val targetRegister =
                        getInstruction<OneRegisterInstruction>(targetIndex).registerA

                    // Since this is end of the method must replace one line then add the rest.
                    addInstructions(
                        targetIndex + 1,
                        """
                            invoke-static {}, $MISC_PATH/SpoofPlayerParameterPatch;->getSeekbarThumbnailOverrideValue()Z
                            move-result v$targetRegister
                            return v$targetRegister
                            """
                    )
                    removeInstruction(targetIndex)
                }
            } ?: return StoryboardThumbnailFingerprint.toErrorResult()
        } ?: return StoryboardThumbnailParentFingerprint.toErrorResult()

        // Seekbar thumbnail now show up but are always a blank image.
        // Additional changes are needed to force the client to generate the thumbnails (assuming it's possible),
        // but for now hide the empty thumbnail.
        ScrubbedPreviewLayoutFingerprint.result?.let {
            it.mutableMethod.apply {
                val endIndex = it.scanResult.patternScanResult!!.endIndex
                val imageViewFieldName = getInstruction<ReferenceInstruction>(endIndex).reference

                addInstructions(
                    implementation!!.instructions.lastIndex,
                    """
                        iget-object v0, p0, $imageViewFieldName   # copy imageview field to a register
                        invoke-static {v0}, $MISC_PATH/SpoofPlayerParameterPatch;->seekbarImageViewCreated(Landroid/widget/ImageView;)V
                        """
                )
            }
        } ?: return ScrubbedPreviewLayoutFingerprint.toErrorResult()

        // fix subtitle position issue (when spoof to shorts)
        SubtitleWindowFingerprint.result?.mutableMethod?.addInstructions(
            0,
            """
                invoke-static {p1, p2, p3, p4, p5}, $MISC_PATH/SpoofPlayerParameterPatch;->getSubtitleWindowSettingsOverride(IIIZZ)[I
                move-result-object v0
                const/4 v1, 0x0
                aget p1, v0, v1     # ap, anchor configuration
                const/4 v1, 0x1
                aget p2, v0, v1     # ah, horizontal anchor
                const/4 v1, 0x2
                aget p3, v0, v1     # av, vertical anchor
            """
        ) ?: return SubtitleWindowFingerprint.toErrorResult()

        // Hook video id, required for subtitle fix.
        MainstreamVideoIdPatch.injectCall("$MISC_PATH/SpoofPlayerParameterPatch;->setCurrentVideoId(Ljava/lang/String;)V")

        return PatchResultSuccess()
    }
}