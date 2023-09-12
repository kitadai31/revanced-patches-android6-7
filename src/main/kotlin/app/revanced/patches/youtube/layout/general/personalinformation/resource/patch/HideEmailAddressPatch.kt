package app.revanced.patches.youtube.layout.general.personalinformation.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.general.personalinformation.bytecode.patch.HideEmailAddressBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-email-address")
@Description("Hides the email address in the account switcher.")
@DependsOn(
    [
        HideEmailAddressBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class HideEmailAddressPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: GENERAL",
            "SETTINGS: HIDE_EMAIL_ADDRESS"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-email-address"
        )
    }
}