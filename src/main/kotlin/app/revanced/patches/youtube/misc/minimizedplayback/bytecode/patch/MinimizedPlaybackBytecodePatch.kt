package app.revanced.patches.youtube.misc.minimizedplayback.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.util.proxy.mutableTypes.MutableMethod
import app.revanced.patches.youtube.misc.minimizedplayback.bytecode.fingerprints.*
import app.revanced.patches.youtube.misc.resourceid.patch.SharedResourcdIdPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

@Name("enable-minimized-playback-bytecode-patch")
@DependsOn([SharedResourcdIdPatch::class])
@YouTubeCompatibility
class MinimizedPlaybackBytecodePatch : BytecodePatch(
    listOf(
        KidsMinimizedPlaybackPolicyControllerFingerprint,
        MinimizedPlaybackManagerFingerprint,
        MinimizedPlaybackSettingsFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        val methods = arrayOf(
            KidsMinimizedPlaybackPolicyControllerFingerprint,
            MinimizedPlaybackManagerFingerprint,
            MinimizedPlaybackSettingsFingerprint
        ).map {
            it.result?.mutableMethod?: throw it.exception
        }

        methods[0].hookPlaybackController()
        methods[1].hookPlayback()
        methods[2].walkMutable(context)
    }

    private companion object {
        fun MutableMethod.walkMutable(
            context: BytecodeContext) {
            val booleanCalls = implementation!!.instructions.withIndex()
                .filter { ((it.value as? ReferenceInstruction)?.reference as? MethodReference)?.returnType == "Z" }

            val booleanIndex = booleanCalls.elementAt(1).index
            val booleanMethod =
                context.toMethodWalker(this)
                .nextMethod(booleanIndex, true)
                .getMethod() as MutableMethod

            booleanMethod.hookPlayback()
        }

        fun MutableMethod.hookPlayback() {
            addInstructions(
                0, """
                    const/4 v0, 0x1
                    return v0
                """
            )
        }

        fun MutableMethod.hookPlaybackController() {
            addInstruction(0, "return-void")
        }
    }
}
