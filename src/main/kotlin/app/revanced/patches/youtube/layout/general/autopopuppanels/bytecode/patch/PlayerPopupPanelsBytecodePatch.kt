package app.revanced.patches.youtube.layout.general.autopopuppanels.bytecode.patch

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.layout.general.autopopuppanels.bytecode.fingerprints.EngagementPanelControllerFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.util.integrations.Constants.GENERAL_LAYOUT

@Name("hide-auto-player-popup-panels-bytecode-patch")
@YouTubeCompatibility
class PlayerPopupPanelsBytecodePatch : BytecodePatch(
    listOf(
        EngagementPanelControllerFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {

        EngagementPanelControllerFingerprint.result?.mutableMethod?.let {
            it.addInstructionsWithLabels(
                0, """
                    invoke-static {}, $GENERAL_LAYOUT->hideAutoPlayerPopupPanels()Z
                    move-result v0
                    if-eqz v0, :player_popup_panels_shown
                    if-eqz p4, :player_popup_panels_shown
                    const/4 v0, 0x0
                    return-object v0
                """, ExternalLabel("player_popup_panels_shown", it.getInstruction(0))
            )
        } ?: throw EngagementPanelControllerFingerprint.exception
    }
}
