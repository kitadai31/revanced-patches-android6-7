package app.revanced.patches.youtube.misc.spoofclient.patch

import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.extensions.InstructionExtensions.getInstructions
import app.revanced.patcher.extensions.or
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.misc.protobufpoof.fingerprints.PlayerParameterBuilderFingerprint
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.PlayerResponseModelBackgroundAudioPlaybackFingerprint
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.BuildInitPlaybackRequestFingerprint
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.BuildPlaybackStatsRequestURIFingerprint
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.BuildPlayerRequestURIFingerprint
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.CreatePlaybackSpeedMenuItemFingerprint
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.CreatePlayerRequestBodyFingerprint
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.CreatePlayerRequestBodyWithModelFingerprint
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.CreatePlayerRequestBodyWithModelFingerprint.indexOfModelInstruction
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.CreatePlayerRequestBodyWithVersionReleaseFingerprint
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.CreatePlayerRequestBodyWithVersionReleaseFingerprint.indexOfBuildInstruction
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.NerdsStatsVideoFormatBuilderFingerprint
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.PlayerGestureConfigSyntheticFingerprint
import app.revanced.patches.youtube.misc.spoofclient.fingerprints.SetPlayerRequestClientTypeFingerprint
import app.revanced.patches.youtube.misc.trackingurlhook.TrackingUrlHookPatch
import app.revanced.patches.youtube.misc.useragent.fingerprints.UserAgentHeaderBuilderFingerprint
import app.revanced.patches.youtube.misc.videocpn.patch.VideoCpnPatch
import app.revanced.shared.util.bytecode.getReference
import app.revanced.shared.util.bytecode.getStringInstructionIndex
import app.revanced.shared.util.bytecode.resultOrThrow
import app.revanced.shared.util.integrations.Constants.MISC_PATH
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter

class SpoofClientBytecodePatch : BytecodePatch(
    setOf(
        // Client type spoof.
        BuildInitPlaybackRequestFingerprint,
        BuildPlayerRequestURIFingerprint,
        SetPlayerRequestClientTypeFingerprint,
        CreatePlayerRequestBodyFingerprint,
        CreatePlayerRequestBodyWithModelFingerprint,
        PlayerParameterBuilderFingerprint,
        CreatePlayerRequestBodyWithVersionReleaseFingerprint,
        UserAgentHeaderBuilderFingerprint,

        // Background playback in live stream.
        PlayerResponseModelBackgroundAudioPlaybackFingerprint,

        // Watch history.
        BuildPlaybackStatsRequestURIFingerprint,

        // Player gesture config.
        PlayerGestureConfigSyntheticFingerprint,

        // Player speed menu item.
        CreatePlaybackSpeedMenuItemFingerprint,

        // Nerds stats video format.
        NerdsStatsVideoFormatBuilderFingerprint,
    )
) {

    override fun execute(context: BytecodeContext) {

        val INTEGRATIONS_CLASS_DESCRIPTOR = "$MISC_PATH/SpoofClientPatch;"
//        val CLIENT_INFO_CLASS_DESCRIPTOR = "Lcom/google/protos/youtube/api/innertube/InnertubeContext\$ClientInfo;"
        val CLIENT_INFO_CLASS_DESCRIPTOR = "Lajnp;"  // 17.34.36 only

        // region Block /initplayback requests to fall back to /get_watch requests.

        BuildInitPlaybackRequestFingerprint.resultOrThrow().let {
            it.mutableMethod.apply {
                val moveUriStringIndex = it.scanResult.patternScanResult!!.startIndex
                val targetRegister = getInstruction<OneRegisterInstruction>(moveUriStringIndex).registerA

                addInstructions(
                    moveUriStringIndex + 1, """
                        invoke-static { v$targetRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->blockInitPlaybackRequest(Ljava/lang/String;)Ljava/lang/String;
                        move-result-object v$targetRegister
                        """,
                )
            }
        }

        // endregion

        // region Block /get_watch requests to fall back to /player requests.

        BuildPlayerRequestURIFingerprint.resultOrThrow().let {
            it.mutableMethod.apply {
                val invokeToStringIndex = it.scanResult.patternScanResult!!.startIndex
                val uriRegister = getInstruction<FiveRegisterInstruction>(invokeToStringIndex).registerC

                addInstructions(
                    invokeToStringIndex, """
                        invoke-static { v$uriRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->blockGetWatchRequest(Landroid/net/Uri;)Landroid/net/Uri;
                        move-result-object v$uriRegister
                        """,
                )
            }
        }

        // endregion

        // region Get field references to be used below.

        val (clientInfoField, clientInfoClientTypeField, clientInfoClientVersionField) =
            SetPlayerRequestClientTypeFingerprint.resultOrThrow().let { result ->
                with (result.mutableMethod) {
                    // Field in the player request object that holds the client info object.
                    val clientInfoField = getInstructions().find { instruction ->
                        // requestMessage.clientInfo = clientInfoBuilder.build();
                        instruction.opcode == Opcode.IPUT_OBJECT &&
                                instruction.getReference<FieldReference>()?.type == CLIENT_INFO_CLASS_DESCRIPTOR
                    }?.getReference<FieldReference>() ?: throw PatchException("Could not find clientInfoField")

                    // Client info object's client type field.
                    val clientInfoClientTypeField = getInstruction(result.scanResult.patternScanResult!!.endIndex)
                        .getReference<FieldReference>() ?: throw PatchException("Could not find clientInfoClientTypeField")

                    val clientInfoVersionIndex = getStringInstructionIndex("10.29")
                    val clientInfoVersionRegister = getInstruction<OneRegisterInstruction>(clientInfoVersionIndex).registerA
                    val clientInfoClientVersionFieldIndex = implementation!!.instructions.let {
                        clientInfoVersionIndex + it.subList(clientInfoVersionIndex, it.size - 1).indexOfFirst { instruction ->
                            instruction.opcode == Opcode.IPUT_OBJECT
                                    && (instruction as TwoRegisterInstruction).registerA == clientInfoVersionRegister
                        }
                    }

                    // Client info object's client version field.
                    val clientInfoClientVersionField = getInstruction(clientInfoClientVersionFieldIndex)
                        .getReference<FieldReference>() ?: throw PatchException("Could not find clientInfoClientVersionField")

                    Triple(clientInfoField, clientInfoClientTypeField, clientInfoClientVersionField)
                }
            }

        val clientInfoClientModelField = CreatePlayerRequestBodyWithModelFingerprint.resultOrThrow().mutableMethod.let {
            val instructions = it.getInstructions()
            val getClientModelIndex = indexOfModelInstruction(it)

            // The next IPUT_OBJECT instruction after getting the client model is setting the client model field.
            instructions.subList(
                getClientModelIndex,
                instructions.size,
            ).find { instruction ->
                val reference = instruction.getReference<FieldReference>()
                instruction.opcode == Opcode.IPUT_OBJECT
                        && reference?.definingClass == CLIENT_INFO_CLASS_DESCRIPTOR
                        && reference.type == "Ljava/lang/String;"
            }?.getReference<FieldReference>() ?: throw PatchException("Could not find clientInfoClientModelField")
        }

        val clientInfoOsVersionField =
            CreatePlayerRequestBodyWithVersionReleaseFingerprint.resultOrThrow().mutableMethod.let {
                val buildIndex = indexOfBuildInstruction(it)
                val instructions = it.getInstructions()

                instructions.subList(
                    buildIndex - 5,
                    buildIndex,
                ).find { instruction ->
                    val reference = instruction.getReference<FieldReference>()
                    instruction.opcode == Opcode.IPUT_OBJECT
                            && reference?.definingClass == CLIENT_INFO_CLASS_DESCRIPTOR
                            && reference.type == "Ljava/lang/String;"
                }?.getReference<FieldReference>()
                    ?: throw PatchException("Could not find clientInfoOsVersionField")
            }

        // endregion

        // region Spoof client type for /player requests.

        CreatePlayerRequestBodyFingerprint.resultOrThrow().let {
            it.mutableMethod.apply {
                val setClientInfoMethodName = "setClientInfo"
                val checkCastIndex = it.scanResult.patternScanResult!!.startIndex

                val checkCastInstruction = getInstruction<OneRegisterInstruction>(checkCastIndex)
                val requestMessageInstanceRegister = checkCastInstruction.registerA
                val clientInfoContainerClassName = checkCastInstruction.getReference<TypeReference>()!!.type

                addInstruction(
                    checkCastIndex + 1,
                    "invoke-static { v$requestMessageInstanceRegister }," +
                            " $definingClass->$setClientInfoMethodName($clientInfoContainerClassName)V",
                )

                // Change client info to use the spoofed values.
                // Do this in a helper method, to remove the need of picking out multiple free registers from the hooked code.
                it.mutableClass.methods.add(
                    ImmutableMethod(
                        definingClass,
                        setClientInfoMethodName,
                        listOf(ImmutableMethodParameter(clientInfoContainerClassName, annotations, "clientInfoContainer")),
                        "V",
                        AccessFlags.PRIVATE or AccessFlags.STATIC,
                        annotations,
                        null,
                        MutableMethodImplementation(3),
                    ).toMutable().apply {
                        addInstructions(
                            """
                                invoke-static { }, $INTEGRATIONS_CLASS_DESCRIPTOR->isClientSpoofingEnabled()Z
                                move-result v0
                                if-eqz v0, :disabled
                            
                                iget-object v0, p0, $clientInfoField
                            
                                # Set client type to the spoofed value.
                                iget v1, v0, $clientInfoClientTypeField
                                invoke-static { v1 }, $INTEGRATIONS_CLASS_DESCRIPTOR->getClientTypeId(I)I
                                move-result v1
                                iput v1, v0, $clientInfoClientTypeField
                                
                                # Set client model to the spoofed value.
                                iget-object v1, v0, $clientInfoClientModelField
                                invoke-static { v1 }, $INTEGRATIONS_CLASS_DESCRIPTOR->getClientModel(Ljava/lang/String;)Ljava/lang/String;
                                move-result-object v1
                                iput-object v1, v0, $clientInfoClientModelField
                            
                                # Set client version to the spoofed value.
                                iget-object v1, v0, $clientInfoClientVersionField
                                invoke-static { v1 }, $INTEGRATIONS_CLASS_DESCRIPTOR->getClientVersion(Ljava/lang/String;)Ljava/lang/String;
                                move-result-object v1
                                iput-object v1, v0, $clientInfoClientVersionField
                                
                                # Set client os version to the spoofed value.
                                iget-object v1, v0, $clientInfoOsVersionField
                                invoke-static { v1 }, $INTEGRATIONS_CLASS_DESCRIPTOR->getOsVersion(Ljava/lang/String;)Ljava/lang/String;
                                move-result-object v1
                                iput-object v1, v0, $clientInfoOsVersionField

                                :disabled
                                return-void
                                """,
                        )
                    },
                )
            }
        }

        // endregion

        // region Spoof user-agent

        UserAgentHeaderBuilderFingerprint.resultOrThrow().mutableMethod.apply {
            val insertIndex = implementation!!.instructions.lastIndex
            val insertRegister = getInstruction<OneRegisterInstruction>(insertIndex).registerA

            addInstructions(
                insertIndex, """
                    invoke-static { v$insertRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->getUserAgent(Ljava/lang/String;)Ljava/lang/String;
                    move-result-object v$insertRegister
                    """
            )
        }

        // endregion

        // region check whether video is Shorts or Clips.

        PlayerParameterBuilderFingerprint.resultOrThrow().mutableMethod.apply {
                val videoIdRegister = 1
                val playerParameterRegister = 3

                addInstructions(
                    0, """
                        invoke-static {p$videoIdRegister, p$playerParameterRegister}, $INTEGRATIONS_CLASS_DESCRIPTOR->setPlayerResponseVideoId(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
                        move-result-object p$playerParameterRegister
                        """
                )
        }

        // endregion

        // region fix player gesture.

        PlayerGestureConfigSyntheticFingerprint.resultOrThrow().let {
            val endIndex = it.scanResult.patternScanResult!!.endIndex
            val downAndOutLandscapeAllowedIndex = endIndex - 3
            val downAndOutPortraitAllowedIndex = endIndex - 9

            arrayOf(
                downAndOutLandscapeAllowedIndex,
                downAndOutPortraitAllowedIndex,
            ).forEach { index ->
                val gestureAllowedMethod = context.toMethodWalker(it.mutableMethod)
                    .nextMethod(index, true)
                    .getMethod() as MutableMethod

                gestureAllowedMethod.apply {
                    val isAllowedIndex = getInstructions().lastIndex
                    val isAllowed = getInstruction<OneRegisterInstruction>(isAllowedIndex).registerA

                    addInstructions(
                        isAllowedIndex,
                        """
                            invoke-static { v$isAllowed }, $INTEGRATIONS_CLASS_DESCRIPTOR->enablePlayerGesture(Z)Z
                            move-result v$isAllowed
                        """,
                    )
                }
            }
        }

        // endregion

        // Fix playback speed menu item if spoofing to iOS.

        CreatePlaybackSpeedMenuItemFingerprint.resultOrThrow().let {
            val shouldCreateMenuIndex = it.scanResult.patternScanResult!!.endIndex

            it.mutableMethod.apply {
                val shouldCreateMenuRegister = getInstruction<OneRegisterInstruction>(shouldCreateMenuIndex).registerA

                addInstructions(
                    shouldCreateMenuIndex,
                    """
                        invoke-static { v$shouldCreateMenuRegister }, $INTEGRATIONS_CLASS_DESCRIPTOR->forceCreatePlaybackSpeedMenu(Z)Z
                        move-result v$shouldCreateMenuRegister
                    """,
                )
            }
        }

        // endregion

        // region fix background playback in live stream, if spoofing to iOS

        // This force enables audio background playback.
        PlayerResponseModelBackgroundAudioPlaybackFingerprint.resultOrThrow().mutableMethod.apply {
            addInstructionsWithLabels(
                0, """
                    invoke-static { }, $INTEGRATIONS_CLASS_DESCRIPTOR->overrideBackgroundAudioPlayback()Z
                    move-result v0
                    if-eqz v0, :disabled
                    return v0
                    """, ExternalLabel("disabled", getInstruction(0))
            )
        }

        // endregion

        // region watch history if spoofing to iOS

        BuildPlaybackStatsRequestURIFingerprint.resultOrThrow().let {
            val walkerMethod = context.toMethodWalker(it.method)
                .nextMethod(it.scanResult.patternScanResult!!.startIndex, true)
                .getMethod() as MutableMethod

            walkerMethod.addInstructions(
                0, """
                    invoke-static {p0}, $INTEGRATIONS_CLASS_DESCRIPTOR->overrideTrackingUrl(Landroid/net/Uri;)Landroid/net/Uri;
                    move-result-object p0
                    """
            )
        }

        VideoCpnPatch.injectCall("$INTEGRATIONS_CLASS_DESCRIPTOR->setCpn(Ljava/lang/String;Z)V")

        TrackingUrlHookPatch.hookTrackingUrl("$INTEGRATIONS_CLASS_DESCRIPTOR->setTrackingUriParameter(Landroid/net/Uri;)V")

        // endregion

        // region append spoof info

        NerdsStatsVideoFormatBuilderFingerprint.resultOrThrow().mutableMethod.apply {
            for (index in implementation!!.instructions.size - 1 downTo 0) {
                val instruction = getInstruction(index)
                if (instruction.opcode != Opcode.RETURN_OBJECT)
                    continue

                val register = (instruction as OneRegisterInstruction).registerA

                addInstructions(
                    index, """
                            invoke-static {v$register}, $INTEGRATIONS_CLASS_DESCRIPTOR->appendSpoofedClient(Ljava/lang/String;)Ljava/lang/String;
                            move-result-object v$register
                            """
                )
            }
        }

        // endregion
    }
}