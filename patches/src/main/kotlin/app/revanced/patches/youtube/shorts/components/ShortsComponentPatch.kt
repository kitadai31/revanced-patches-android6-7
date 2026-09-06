package app.revanced.patches.youtube.shorts.components

import app.revanced.patcher.Fingerprint
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstructions
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.shared.litho.addLithoFilter
import app.revanced.patches.shared.litho.lithoFilterPatch
import app.revanced.patches.shared.mainactivity.injectOnCreateMethodCall
import app.revanced.patches.shared.textcomponent.hookSpannableString
import app.revanced.patches.shared.textcomponent.textComponentPatch
import app.revanced.patches.youtube.utils.bottomSheetMenuItemBuilderFingerprint
import app.revanced.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.extension.Constants.COMPONENTS_PATH
import app.revanced.patches.youtube.utils.extension.Constants.SHORTS_CLASS_DESCRIPTOR
import app.revanced.patches.youtube.utils.extension.Constants.SHORTS_PATH
import app.revanced.patches.youtube.utils.extension.Constants.UTILS_PATH
import app.revanced.patches.youtube.utils.indexOfSpannedCharSequenceInstruction
import app.revanced.patches.youtube.utils.lottie.LOTTIE_ANIMATION_VIEW_CLASS_DESCRIPTOR
import app.revanced.patches.youtube.utils.lottie.lottieAnimationViewHookPatch
import app.revanced.patches.youtube.utils.mainactivity.mainActivityResolvePatch
import app.revanced.patches.youtube.utils.navigation.addBottomBarContainerHook
import app.revanced.patches.youtube.utils.navigation.navigationBarHookPatch
import app.revanced.patches.youtube.utils.patch.PatchList.HIDE_FEED_FLYOUT_MENU
import app.revanced.patches.youtube.utils.patch.PatchList.SHORTS_COMPONENTS
import app.revanced.patches.youtube.utils.playertype.playerTypeHookPatch
import app.revanced.patches.youtube.utils.playservice.is_18_31_or_greater
import app.revanced.patches.youtube.utils.playservice.is_18_34_or_greater
import app.revanced.patches.youtube.utils.playservice.is_18_49_or_greater
import app.revanced.patches.youtube.utils.playservice.is_19_02_or_greater
import app.revanced.patches.youtube.utils.playservice.is_19_11_or_greater
import app.revanced.patches.youtube.utils.playservice.is_19_25_or_greater
import app.revanced.patches.youtube.utils.playservice.is_19_28_or_greater
import app.revanced.patches.youtube.utils.playservice.is_19_34_or_greater
import app.revanced.patches.youtube.utils.playservice.is_20_09_or_greater
import app.revanced.patches.youtube.utils.playservice.versionCheckPatch
import app.revanced.patches.youtube.utils.recyclerview.recyclerViewTreeObserverHook
import app.revanced.patches.youtube.utils.recyclerview.recyclerViewTreeObserverPatch
import app.revanced.patches.youtube.utils.resourceid.bottomBarContainer
import app.revanced.patches.youtube.utils.resourceid.metaPanel
import app.revanced.patches.youtube.utils.resourceid.reelDynRemix
import app.revanced.patches.youtube.utils.resourceid.reelDynShare
import app.revanced.patches.youtube.utils.resourceid.reelFeedbackLike
import app.revanced.patches.youtube.utils.resourceid.reelFeedbackPause
import app.revanced.patches.youtube.utils.resourceid.reelFeedbackPlay
import app.revanced.patches.youtube.utils.resourceid.reelForcedMuteButton
import app.revanced.patches.youtube.utils.resourceid.reelPlayerFooter
import app.revanced.patches.youtube.utils.resourceid.reelPlayerRightPivotV2Size
import app.revanced.patches.youtube.utils.resourceid.reelRightDislikeIcon
import app.revanced.patches.youtube.utils.resourceid.reelRightLikeIcon
import app.revanced.patches.youtube.utils.resourceid.reelVodTimeStampsContainer
import app.revanced.patches.youtube.utils.resourceid.rightComment
import app.revanced.patches.youtube.utils.resourceid.sharedResourceIdPatch
import app.revanced.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.patches.youtube.utils.settings.ResourceUtils.getContext
import app.revanced.patches.youtube.utils.settings.settingsPatch
import app.revanced.patches.youtube.utils.toolbar.hookToolBar
import app.revanced.patches.youtube.utils.toolbar.toolBarHookPatch
import app.revanced.patches.youtube.utils.videoIdFingerprintShorts
import app.revanced.patches.youtube.video.information.hookShortsVideoInformation
import app.revanced.patches.youtube.video.information.videoInformationPatch
import app.revanced.patches.youtube.video.playbackstart.PLAYBACK_START_DESCRIPTOR_CLASS_DESCRIPTOR
import app.revanced.patches.youtube.video.playbackstart.playbackStartDescriptorPatch
import app.revanced.patches.youtube.video.playbackstart.playbackStartVideoIdReference
import app.revanced.patches.youtube.video.playbackstart.shortsPlaybackStartIntentFingerprint
import app.revanced.patches.youtube.video.playbackstart.shortsPlaybackStartIntentLegacyFingerprint
import app.revanced.patches.youtube.video.videoid.hookPlayerResponseVideoId
import app.revanced.patches.youtube.video.videoid.videoIdPatch
import app.revanced.util.REGISTER_TEMPLATE_REPLACEMENT
import app.revanced.util.ResourceGroup
import app.revanced.util.cloneMutable
import app.revanced.util.copyResources
import app.revanced.util.findMethodOrThrow
import app.revanced.util.findMutableMethodOf
import app.revanced.util.fingerprint.definingClassOrThrow
import app.revanced.util.fingerprint.injectLiteralInstructionBooleanCall
import app.revanced.util.fingerprint.matchOrThrow
import app.revanced.util.fingerprint.methodOrThrow
import app.revanced.util.fingerprint.resolvable
import app.revanced.util.getReference
import app.revanced.util.getWalkerMethod
import app.revanced.util.indexOfFirstInstruction
import app.revanced.util.indexOfFirstInstructionOrThrow
import app.revanced.util.indexOfFirstInstructionReversedOrThrow
import app.revanced.util.indexOfFirstLiteralInstruction
import app.revanced.util.indexOfFirstLiteralInstructionOrThrow
import app.revanced.util.indexOfFirstStringInstruction
import app.revanced.util.indexOfFirstStringInstructionOrThrow
import app.revanced.util.injectLiteralInstructionViewCall
import app.revanced.util.or
import app.revanced.util.replaceLiteralInstructionCall
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.util.MethodUtil

private const val EXTENSION_ANIMATION_FEEDBACK_CLASS_DESCRIPTOR =
    "$SHORTS_PATH/AnimationFeedbackPatch;"

private val shortsAnimationPatch = bytecodePatch(
    description = "shortsAnimationPatch",
    dependsOn = listOf(
        LottieAnimationViewHookPatch,
        settingsPatch
    )
) {
    execute {


        // region patch for open Shorts in regular player

        fun extensionInstructions(playbackStartRegister: Int, freeRegister: Int) =
            """
                invoke-virtual { v$playbackStartRegister }, $playbackStartVideoIdReference
                move-result-object v$freeRegister
                invoke-static { v$freeRegister }, $SHORTS_CLASS_DESCRIPTOR->openShortInRegularPlayer(Ljava/lang/String;)Z
                move-result v$freeRegister
                if-eqz v$freeRegister, :disabled
                return-void
                :disabled
                nop
            """

                        if (is_19_25_or_greater) {
            shortsPlayBackStartIntentFingerprint.methodOrThrow().addInstructionsWithLabels(
                0,
                """
                move-object/from16 v0, p1
                ${extensionInstructions(0, 1)}
                """
            )
        } else {
        // Target fallback class mapping for YouTube v14.43.55
        val customFingerprint = MethodFingerprint {
            custom { methodDef, classDef ->
                classDef.type == "Lnxa;"
                }
            }
            
            // This line tells ReVanced to resolve the fingerprint and intercept it
            customFingerprint.methodOrThrow().addInstructionsWithLabels(
                0,
                """
                move-object/from16 v0, p1
                ${extensionInstructions(0, 1)}
                """
            )
        } // Closes else block
    } // Closes execute block
} // Closes shortsAnimationPatch

