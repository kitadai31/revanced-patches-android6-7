package app.revanced.patches.youtube.misc.videoid.mainstream.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.removeInstruction
import app.revanced.patcher.extensions.InstructionExtensions.replaceInstruction
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableAnnotation
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod.Companion.toMutable
import app.revanced.patches.youtube.misc.playertype.patch.PlayerTypeHookPatch
import app.revanced.patches.youtube.misc.videoid.mainstream.fingerprint.*
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.patches.timebar.HookTimebarPatch
import app.revanced.shared.util.integrations.Constants.VIDEO_PATH
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.MutableMethodImplementation
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21c
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter
import com.android.tools.smali.dexlib2.util.MethodUtil

@Name("video-id-hook-mainstream")
@Description("Hook to detect when the video id changes (mainstream)")
@YouTubeCompatibility
@DependsOn(
    [
        HookTimebarPatch::class,
        PlayerTypeHookPatch::class
    ]
)
class MainstreamVideoIdPatch : BytecodePatch(
    listOf(
        MainstreamVideoIdFingerprint,
        PlayerControllerFingerprint,
        PlayerControllerSetTimeReferenceFingerprint,
        PlayerInitFingerprint,
        RepeatListenerFingerprint,
        SeekFingerprint,
        VideoTimeFingerprint,
        VideoTimeParentFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        RepeatListenerFingerprint.result?.let {
            val removeIndex = it.scanResult.patternScanResult!!.startIndex
                with (it.mutableMethod) {
                    // removeInstruction(removeIndex)
                    removeInstruction(removeIndex - 1)
                }
        } ?: throw RepeatListenerFingerprint.exception


        PlayerInitFingerprint.result?.let { parentResult ->
            playerInitMethod = parentResult.mutableClass.methods.first { MethodUtil.isConstructor(it) }

            SeekFingerprint.also { it.resolve(context, parentResult.classDef) }.result?.let {
                val resultMethod = it.method

                // Required because build fails without it.
                val nullMutableSet : MutableSet<MutableAnnotation>? = null

                with (it.mutableMethod) {
                    val seekHelperMethod = ImmutableMethod(
                        resultMethod.definingClass,
                        "seekTo",
                        listOf(ImmutableMethodParameter("J", nullMutableSet, "time")),
                        "Z",
                        AccessFlags.PUBLIC or AccessFlags.FINAL,
                        null, null,
                        MutableMethodImplementation(4)
                    ).toMutable()

                    val seekSourceEnumType = resultMethod.parameterTypes[1].toString()

                    seekHelperMethod.addInstructions(
                        0,
                        """
                            sget-object v0, $seekSourceEnumType->a:$seekSourceEnumType
                            invoke-virtual {p0, p1, p2, v0}, ${resultMethod.definingClass}->${resultMethod.name}(J$seekSourceEnumType)Z
                            move-result p1
                            return p1
                            """
                    )

                    parentResult.mutableClass.methods.add(seekHelperMethod)
                }
            } ?: throw SeekFingerprint.exception
        } ?: throw PlayerInitFingerprint.exception


        VideoTimeParentFingerprint.result?.let { parentResult ->
            VideoTimeFingerprint.also { it.resolve(context, parentResult.classDef) }.result?.mutableMethod?.addInstruction(
                0,
                "invoke-static {p1, p2}, $VideoInformation->setCurrentVideoTimeHighPrecision(J)V"
            ) ?: throw VideoTimeFingerprint.exception
        } ?: throw VideoTimeParentFingerprint.exception


        /*
        Set current video time
        */
        PlayerControllerSetTimeReferenceFingerprint.result?.let {
            with(context
                .toMethodWalker(it.method)
                .nextMethod(it.scanResult.patternScanResult!!.startIndex, true)
                .getMethod() as MutableMethod
            ) {
                addInstruction(
                    2,
                    "invoke-static {p1, p2}, $VideoInformation->setCurrentVideoTime(J)V"
                )
            }
        } ?: throw PlayerControllerSetTimeReferenceFingerprint.exception


        with (HookTimebarPatch.EmptyColorFingerprintResult.mutableMethod) {
            val methodReference =
                HookTimebarPatch.TimbarFingerprintResult.method.let {
                    (it.implementation!!.instructions.elementAt(2) as ReferenceInstruction).reference as MethodReference
                }

            val instructions = implementation!!.instructions

            reactReference =
                ((instructions.elementAt(instructions.count() - 3) as ReferenceInstruction).reference as FieldReference).name


            for ((index, instruction) in instructions.withIndex()) {
                if (instruction.opcode != Opcode.CHECK_CAST) continue
                val primaryRegister = (instruction as Instruction21c).registerA + 1
                val secondaryRegister = primaryRegister + 1
                addInstructions(
                    index, """
                        invoke-virtual {p0}, $methodReference
                        move-result-wide v$primaryRegister
                        invoke-static {v$primaryRegister, v$secondaryRegister}, $VideoInformation->setCurrentVideoLength(J)V
                    """
                )
                break
            }
        }


        PlayerControllerFingerprint.result?.mutableMethod?.let {
            val instructions = it.implementation!!.instructions

            for ((index, instruction) in instructions.withIndex()) {
                if (instruction.opcode != Opcode.CONST_STRING) continue
                val register = (instruction as OneRegisterInstruction).registerA
                it.replaceInstruction(
                    index,
                    "const-string v$register, \"$reactReference\""
                )
                break
            }

        } ?: throw PlayerControllerFingerprint.exception


        MainstreamVideoIdFingerprint.result?.let {
            insertIndex = it.scanResult.patternScanResult!!.endIndex

            with (it.mutableMethod) {
                insertMethod = this
                videoIdRegister = (implementation!!.instructions[insertIndex] as OneRegisterInstruction).registerA
            }
            offset++ // offset so setCurrentVideoId is called before any injected call
        } ?: throw MainstreamVideoIdFingerprint.exception

        
        injectCall("$VideoInformation->setCurrentVideoId(Ljava/lang/String;)V")
        injectCallonCreate(VideoInformation, "onCreate")
    }

    companion object {
        const val VideoInformation = "$VIDEO_PATH/VideoInformation;"
        private var offset = 0

        private var insertIndex: Int = 0
        private var reactReference: String? = null
        private var videoIdRegister: Int = 0
        private lateinit var insertMethod: MutableMethod
        private lateinit var playerInitMethod: MutableMethod

        /**
         * Adds an invoke-static instruction, called with the new id when the video changes
         * @param methodDescriptor which method to call. Params have to be `Ljava/lang/String;`
         */
        fun injectCall(
            methodDescriptor: String
        ) {
            insertMethod.addInstructions(
                insertIndex + offset, // move-result-object offset
                "invoke-static {v$videoIdRegister}, $methodDescriptor"
            )
        }

        fun injectCallonCreate(MethodClass: String, MethodName: String) =
            playerInitMethod.addInstruction(
                4,
                "invoke-static {v0}, $MethodClass->$MethodName(Ljava/lang/Object;)V"
            )
    }
}

