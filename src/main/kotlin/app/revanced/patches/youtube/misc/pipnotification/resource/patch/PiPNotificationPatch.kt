package app.revanced.patches.youtube.misc.pipnotification.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.pipnotification.bytecode.patch.PiPNotificationBytecodePatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-firsttime-background-notification")  // renamed from "hide-pip-notification"
@Description("Disable notification when you launch background play for the first time.")
@DependsOn(
    [
        PiPNotificationBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class PiPNotificationPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        ResourceHelper.patchSuccess(
            context,
            "hide-firsttime-background-notification"
        )
    }
}