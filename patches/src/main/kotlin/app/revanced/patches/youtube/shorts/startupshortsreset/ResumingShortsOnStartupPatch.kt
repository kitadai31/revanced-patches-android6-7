package app.revanced.patches.youtube.shorts.startupshortsreset

import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.extension.Constants.SHORTS_CLASS_DESCRIPTOR
import app.revanced.patches.youtube.utils.patch.PatchList.DISABLE_RESUMING_SHORTS_ON_STARTUP
import app.revanced.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.patches.youtube.utils.settings.settingsPatch
import app.revanced.util.fingerprint.methodOrThrow
import app.revanced.util.getReference
import app.revanced.util.getWalkerMethod
import app.revanced.util.indexOfFirstInstructionOrThrow
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Suppress("unused")
val resumingShortsOnStartupPatch = bytecodePatch(
    DISABLE_RESUMING_SHORTS_ON_STARTUP.title,
    DISABLE_RESUMING_SHORTS_ON_STARTUP.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(settingsPatch)

    execute {

        userWasInShortsFingerprint.methodOrThrow().apply {
            val listenableInstructionIndex = indexOfFirstInstructionOrThrow {
                opcode == Opcode.INVOKE_INTERFACE &&
                        getReference<MethodReference>()?.definingClass == "Lcom/google/common/util/concurrent/ListenableFuture;" &&
                        getReference<MethodReference>()?.name == "isDone"
            }
            val originalInstructionRegister =
                getInstruction<FiveRegisterInstruction>(listenableInstructionIndex).registerC
            val freeRegister =
                getInstruction<OneRegisterInstruction>(listenableInstructionIndex + 1).registerA

            addInstructionsWithLabels(
                listenableInstructionIndex + 1,
                """
                    invoke-static {}, $SHORTS_CLASS_DESCRIPTOR->disableResumingStartupShortsPlayer()Z
                    move-result v$freeRegister
                    if-eqz v$freeRegister, :show
                    return-void
                    :show
                    invoke-interface {v$originalInstructionRegister}, Lcom/google/common/util/concurrent/ListenableFuture;->isDone()Z
                    """
            )
            removeInstruction(listenableInstructionIndex)
        }

        // region add settings

        addPreference(
            arrayOf(
                "PREFERENCE_SCREEN: SHORTS",
                "SETTINGS: DISABLE_RESUMING_SHORTS_PLAYER"
            ),
            DISABLE_RESUMING_SHORTS_ON_STARTUP
        )

        // endregion

    }
}
