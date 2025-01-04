package app.revanced.patches.youtube.utils.sponsorblock

import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.patch.booleanOption
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.patch.resourcePatch
import app.revanced.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.extension.Constants.EXTENSION_PATH
import app.revanced.patches.youtube.utils.extension.Constants.PATCH_STATUS_CLASS_DESCRIPTOR
import app.revanced.patches.youtube.utils.patch.PatchList.SPONSORBLOCK
import app.revanced.patches.youtube.utils.playercontrols.addTopControl
import app.revanced.patches.youtube.utils.playercontrols.hookTopControlButton
import app.revanced.patches.youtube.utils.playercontrols.playerControlsPatch
import app.revanced.patches.youtube.utils.resourceid.insetOverlayViewLayout
import app.revanced.patches.youtube.utils.resourceid.playerOverlays
import app.revanced.patches.youtube.utils.resourceid.sharedResourceIdPatch
import app.revanced.patches.youtube.utils.seekbarFingerprint
import app.revanced.patches.youtube.utils.seekbarOnDrawFingerprint
import app.revanced.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.patches.youtube.utils.settings.settingsPatch
import app.revanced.patches.youtube.utils.totalTimeFingerprint
import app.revanced.patches.youtube.utils.youtubeControlsOverlayFingerprint
import app.revanced.patches.youtube.video.information.hookVideoInformation
import app.revanced.patches.youtube.video.information.onCreateHook
import app.revanced.patches.youtube.video.information.videoEndMethod
import app.revanced.patches.youtube.video.information.videoInformationPatch
import app.revanced.patches.youtube.video.information.videoTimeHook
import app.revanced.util.ResourceGroup
import app.revanced.util.copyResources
import app.revanced.util.fingerprint.matchOrThrow
import app.revanced.util.fingerprint.methodOrThrow
import app.revanced.util.getReference
import app.revanced.util.indexOfFirstInstructionOrThrow
import app.revanced.util.indexOfFirstInstructionReversedOrThrow
import app.revanced.util.indexOfFirstLiteralInstructionOrThrow
import app.revanced.util.updatePatchStatus
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

private const val EXTENSION_SPONSOR_BLOCK_PATH =
    "$EXTENSION_PATH/sponsorblock"

private const val EXTENSION_SPONSOR_BLOCK_UI_PATH =
    "$EXTENSION_SPONSOR_BLOCK_PATH/ui"

private const val EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS_DESCRIPTOR =
    "$EXTENSION_SPONSOR_BLOCK_PATH/SegmentPlaybackController;"

private const val EXTENSION_SPONSOR_BLOCK_VIEW_CONTROLLER_CLASS_DESCRIPTOR =
    "$EXTENSION_SPONSOR_BLOCK_UI_PATH/SponsorBlockViewController;"

val sponsorBlockBytecodePatch = bytecodePatch(
    description = "sponsorBlockBytecodePatch"
) {
    dependsOn(
        sharedResourceIdPatch,
        videoInformationPatch,
    )

    execute {
        // Hook the video time method
        videoTimeHook(
            EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS_DESCRIPTOR,
            "setVideoTime"
        )
        // Initialize the player controller
        onCreateHook(
            EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS_DESCRIPTOR,
            "initialize"
        )


        seekbarOnDrawFingerprint.methodOrThrow(seekbarFingerprint).apply {
            // Get left and right of seekbar rectangle
            val moveObjectIndex = indexOfFirstInstructionOrThrow(Opcode.MOVE_OBJECT_FROM16)

            addInstruction(
                moveObjectIndex + 1,
                "invoke-static/range {p0 .. p0}, " +
                        "$EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS_DESCRIPTOR->setSponsorBarRect(Ljava/lang/Object;)V"
            )

            // Set seekbar thickness
            val roundIndex = indexOfFirstInstructionOrThrow {
                getReference<MethodReference>()?.name == "round"
            } + 1
            val roundRegister = getInstruction<OneRegisterInstruction>(roundIndex).registerA

            addInstruction(
                roundIndex + 1,
                "invoke-static {v$roundRegister}, " +
                        "$EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS_DESCRIPTOR->setSponsorBarThickness(I)V"
            )

            // Draw segment
            val drawCircleIndex = indexOfFirstInstructionReversedOrThrow {
                getReference<MethodReference>()?.name == "drawCircle"
            }
            val drawCircleInstruction = getInstruction<FiveRegisterInstruction>(drawCircleIndex)
            addInstruction(
                drawCircleIndex,
                "invoke-static {v${drawCircleInstruction.registerC}, v${drawCircleInstruction.registerE}}, " +
                        "$EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS_DESCRIPTOR->drawSponsorTimeBars(Landroid/graphics/Canvas;F)V"
            )
        }

        // Voting & Shield button
        setOf("CreateSegmentButtonController;", "VotingButtonController;").forEach { className ->
            hookTopControlButton("$EXTENSION_SPONSOR_BLOCK_UI_PATH/$className")
        }

        // Append timestamp
        totalTimeFingerprint.methodOrThrow().apply {
            val targetIndex = indexOfFirstInstructionOrThrow {
                getReference<MethodReference>()?.name == "getString"
            } + 1
            val targetRegister = getInstruction<OneRegisterInstruction>(targetIndex).registerA

            addInstructions(
                targetIndex + 1, """
                        invoke-static {v$targetRegister}, $EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS_DESCRIPTOR->appendTimeWithoutSegments(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$targetRegister
                        """
            )
        }

        // Initialize the SponsorBlock view
        playerOverlaysLayoutInitFingerprint.matchOrThrow().let {
            it.method.apply {
                val targetIndex =
                    indexOfFirstLiteralInstructionOrThrow(playerOverlays)
                val checkCastIndex = indexOfFirstInstructionOrThrow(targetIndex, Opcode.CHECK_CAST)
                val targetRegister =
                    getInstruction<OneRegisterInstruction>(checkCastIndex).registerA

                addInstruction(
                    checkCastIndex + 1,
                    "invoke-static {v$targetRegister}, $EXTENSION_SPONSOR_BLOCK_VIEW_CONTROLLER_CLASS_DESCRIPTOR->initialize(Ljava/lang/Object;)V"
                )
            }
        }

        // Replace strings
        rectangleFieldInvalidatorFingerprint.methodOrThrow(seekbarFingerprint).apply {
            val invalidateIndex = indexOfInvalidateInstruction(this)
            val rectangleIndex = indexOfFirstInstructionReversedOrThrow(invalidateIndex + 1) {
                getReference<FieldReference>()?.type == "Landroid/graphics/Rect;"
            }
            val rectangleFieldName =
                (getInstruction<ReferenceInstruction>(rectangleIndex).reference as FieldReference).name

            segmentPlaybackControllerFingerprint.matchOrThrow().let {
                it.method.apply {
                    val replaceIndex = it.patternMatch!!.startIndex
                    val replaceRegister =
                        getInstruction<OneRegisterInstruction>(replaceIndex).registerA

                    replaceInstruction(
                        replaceIndex,
                        "const-string v$replaceRegister, \"$rectangleFieldName\""
                    )
                }
            }
        }

        // The vote and create segment buttons automatically change their visibility when appropriate,
        // but if buttons are showing when the end of the video is reached then they will not automatically hide.
        // Add a hook to forcefully hide when the end of the video is reached.
        videoEndMethod.addInstruction(
            0,
            "invoke-static {}, $EXTENSION_SPONSOR_BLOCK_VIEW_CONTROLLER_CLASS_DESCRIPTOR->endOfVideoReached()V"
        )

        // Set current video id
        hookVideoInformation("$EXTENSION_SEGMENT_PLAYBACK_CONTROLLER_CLASS_DESCRIPTOR->newVideoStarted(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;JZ)V")

        updatePatchStatus(PATCH_STATUS_CLASS_DESCRIPTOR, "SponsorBlock")
    }
}

@Suppress("unused")
val sponsorBlockPatch = resourcePatch(
    SPONSORBLOCK.title,
    SPONSORBLOCK.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        playerControlsPatch,
        sponsorBlockBytecodePatch,
        settingsPatch
    )

    val outlineIcon by booleanOption(
        key = "outlineIcon",
        default = false,
        title = "Outline icons",
        description = "Apply the outline icon.",
        required = true
    )

    execute {
        /**
         * merge SponsorBlock drawables to main drawables
         */
        arrayOf(
            ResourceGroup(
                "layout",
                "revanced_sb_inline_sponsor_overlay.xml",
                "revanced_sb_skip_sponsor_button.xml"
            ),
            ResourceGroup(
                "drawable",
                "revanced_sb_new_segment_background.xml",
                "revanced_sb_skip_sponsor_button_background.xml"
            )
        ).forEach { resourceGroup ->
            copyResources("youtube/sponsorblock/shared", resourceGroup)
        }

        if (outlineIcon == true) {
            arrayOf(
                ResourceGroup(
                    "layout",
                    "revanced_sb_new_segment.xml"
                ),
                ResourceGroup(
                    "drawable",
                    "revanced_sb_adjust.xml",
                    "revanced_sb_backward.xml",
                    "revanced_sb_compare.xml",
                    "revanced_sb_edit.xml",
                    "revanced_sb_forward.xml",
                    "revanced_sb_logo.xml",
                    "revanced_sb_publish.xml",
                    "revanced_sb_voting.xml"
                )
            ).forEach { resourceGroup ->
                copyResources("youtube/sponsorblock/outline", resourceGroup)
            }
        } else {
            arrayOf(
                ResourceGroup(
                    "layout",
                    "revanced_sb_new_segment.xml"
                ),
                ResourceGroup(
                    "drawable",
                    "revanced_sb_adjust.xml",
                    "revanced_sb_compare.xml",
                    "revanced_sb_edit.xml",
                    "revanced_sb_logo.xml",
                    "revanced_sb_publish.xml",
                    "revanced_sb_voting.xml"
                )
            ).forEach { resourceGroup ->
                copyResources("youtube/sponsorblock/default", resourceGroup)
            }
        }

        /**
         * merge xml nodes from the host to their real xml files
         */
        addTopControl("youtube/sponsorblock")

        /**
         * Add settings
         */
        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: SPONSOR_BLOCK"
            ),
            SPONSORBLOCK
        )
    }
}
