package app.revanced.patches.youtube.video.customspeed.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.video.customspeed.bytecode.fingerprints.*
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.patches.options.PatchOptions
import app.revanced.shared.util.integrations.Constants.VIDEO_PATH
import com.android.tools.smali.dexlib2.builder.instruction.BuilderArrayPayload
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Name("custom-speed-bytecode-patch")
@DependsOn([PatchOptions::class])
@YouTubeCompatibility
class CustomVideoSpeedBytecodePatch : BytecodePatch(
    listOf(
        SpeedArrayGeneratorFingerprint,
        SpeedLimiterFingerprint,
        VideoSpeedEntriesFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        val speed = PatchOptions.CustomSpeedArrays
        val splits = speed!!.replace(" ","").split(",")
        if (splits.isEmpty()) throw IllegalArgumentException("Invalid speed elements")
        val videoSpeedsArray = splits.map { it.toFloat().toRawBits() }

        SpeedArrayGeneratorFingerprint.result?.let { result ->
            with (result.mutableMethod) {
                val sizeCallIndex = implementation!!.instructions
                    .indexOfFirst { ((it as? ReferenceInstruction)?.reference as? MethodReference)?.name == "size" }

                if (sizeCallIndex == -1) throw PatchException("Couldn't find call to size()")

                val sizeCallResultRegister =
                    (implementation!!.instructions.elementAt(sizeCallIndex + 1) as OneRegisterInstruction).registerA

                addInstructionsWithLabels(
                    sizeCallIndex + 2,
                    """
                        invoke-static {}, $INTEGRATIONS_VIDEO_SPEED_CLASS_DESCRIPTOR->isCustomVideoSpeedEnabled()Z
                        move-result v9
                        if-eqz v9, :defaultspeed
                        const/4 v$sizeCallResultRegister, 0x0
                    """, ExternalLabel("defaultspeed", getInstruction(sizeCallIndex + 2))
                )

                val (arrayLengthConstIndex, arrayLengthConst) = implementation!!.instructions.withIndex()
                    .first { (it.value as? NarrowLiteralInstruction)?.narrowLiteral == 7 }

                val arrayLengthConstDestination = (arrayLengthConst as OneRegisterInstruction).registerA

                val videoSpeedsArrayType = "$INTEGRATIONS_VIDEO_SPEED_ENTRIES_CLASS_DESCRIPTOR->videoSpeed:[F"

                addInstructionsWithLabels(
                    arrayLengthConstIndex + 1,
                    """
                        if-eqz v9, :defaultspeed
                        sget-object v$arrayLengthConstDestination, $videoSpeedsArrayType
                        array-length v$arrayLengthConstDestination, v$arrayLengthConstDestination
                    """, ExternalLabel("defaultspeed", getInstruction(arrayLengthConstIndex + 1))
                )

                val (originalArrayFetchIndex, originalArrayFetch) = implementation!!.instructions.withIndex()
                    .first {
                        val reference = ((it.value as? ReferenceInstruction)?.reference as? FieldReference)
                        reference?.definingClass?.contains("PlayerConfigModel") ?: false &&
                                reference?.type == "[F"
                    }

                val originalArrayFetchDestination = (originalArrayFetch as OneRegisterInstruction).registerA

                addInstructionsWithLabels(
                    originalArrayFetchIndex + 1,
                    """
                        if-eqz v9, :defaultspeed
                        sget-object v$originalArrayFetchDestination, $videoSpeedsArrayType
                    """, ExternalLabel("defaultspeed", getInstruction(originalArrayFetchIndex + 1))
                )
            }
        } ?: throw SpeedArrayGeneratorFingerprint.exception

        SpeedLimiterFingerprint.result?.let { result ->
            with (result.mutableMethod) {
                val (limiterMinConstIndex, limiterMinConst) = implementation!!.instructions.withIndex()
                    .first { (it.value as? NarrowLiteralInstruction)?.narrowLiteral == 0.25f.toRawBits() }
                val (limiterMaxConstIndex, limiterMaxConst) = implementation!!.instructions.withIndex()
                    .first { (it.value as? NarrowLiteralInstruction)?.narrowLiteral == 2.0f.toRawBits() }

                val limiterMinConstDestination = (limiterMinConst as OneRegisterInstruction).registerA
                val limiterMaxConstDestination = (limiterMaxConst as OneRegisterInstruction).registerA

                fun hexFloat(float: Float): String = "0x%08x".format(float.toRawBits())

                replaceInstruction(
                    limiterMinConstIndex,
                    "const/high16 v$limiterMinConstDestination, ${hexFloat(speedLimitMin)}"
                )
                replaceInstruction(
                    limiterMaxConstIndex,
                    "const/high16 v$limiterMaxConstDestination, ${hexFloat(speedLimitMax)}"
                )
            }
        } ?: throw SpeedLimiterFingerprint.exception

        VideoSpeedEntriesFingerprint.result?.let {
            with (it.mutableMethod) {
                val arrayPayloadIndex = implementation!!.instructions.size - 1

                replaceInstruction(
                    0,
                    "const/16 v0, ${videoSpeedsArray.size}"
                )

                implementation!!.replaceInstruction(
                    arrayPayloadIndex,
                    BuilderArrayPayload(
                        4,
                        videoSpeedsArray
                    )
                )
            }
        } ?: throw VideoSpeedEntriesFingerprint.exception
    }
    companion object {
        const val INTEGRATIONS_VIDEO_SPEED_CLASS_DESCRIPTOR =
            "$VIDEO_PATH/VideoSpeedPatch;"

        const val INTEGRATIONS_VIDEO_SPEED_ENTRIES_CLASS_DESCRIPTOR =
            "$VIDEO_PATH/VideoSpeedEntries;"

        const val speedLimitMin = 0.0f
        const val speedLimitMax = 100f
    }
}
