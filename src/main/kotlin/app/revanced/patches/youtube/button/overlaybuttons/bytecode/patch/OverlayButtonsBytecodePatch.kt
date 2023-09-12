package app.revanced.patches.youtube.button.overlaybuttons.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.youtube.misc.playercontrols.bytecode.patch.PlayerControlsBytecodePatch
import app.revanced.patches.youtube.misc.videoid.mainstream.patch.MainstreamVideoIdPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.integrations.Constants.BUTTON_PATH

@Name("overlay-buttons-bytecode-patch")
@DependsOn(
    dependencies = [
        PlayerControlsBytecodePatch::class,
        MainstreamVideoIdPatch::class
    ]
)
@YouTubeCompatibility
class OverlayButtonsBytecodePatch : BytecodePatch() {
    override fun execute(context: BytecodeContext) {
        val AutoRepeat = "$BUTTON_PATH/AutoRepeat;"
        val Copy = "$BUTTON_PATH/Copy;"
        val CopyWithTimeStamp = "$BUTTON_PATH/CopyWithTimeStamp;"
        val Download = "$BUTTON_PATH/Download;"

        arrayOf(
            Download,
            AutoRepeat,
            CopyWithTimeStamp,
            Copy,
        ).forEach { descriptor ->
            PlayerControlsBytecodePatch.initializeControl(descriptor)
            PlayerControlsBytecodePatch.injectVisibility(descriptor)            
        }
    }
}