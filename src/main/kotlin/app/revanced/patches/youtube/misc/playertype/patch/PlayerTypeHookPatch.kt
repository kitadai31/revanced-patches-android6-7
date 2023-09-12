package app.revanced.patches.youtube.misc.playertype.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patches.youtube.misc.playertype.fingerprint.UpdatePlayerTypeFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.util.integrations.Constants.UTILS_PATH

@Name("player-type-hook")
@Description("Hook to get the current player type of WatchWhileActivity")
@YouTubeCompatibility
class PlayerTypeHookPatch : BytecodePatch(
    listOf(
        UpdatePlayerTypeFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        // hook YouTubePlayerOverlaysLayout.updatePlayerLayout()
        UpdatePlayerTypeFingerprint.result?.mutableMethod?.addInstruction(
            0,
            "invoke-static { p1 }, $UTILS_PATH/PlayerTypeHookPatch;->YouTubePlayerOverlaysLayout_updatePlayerTypeHookEX(Ljava/lang/Object;)V"
        ) ?: throw UpdatePlayerTypeFingerprint.exception
    }
}
