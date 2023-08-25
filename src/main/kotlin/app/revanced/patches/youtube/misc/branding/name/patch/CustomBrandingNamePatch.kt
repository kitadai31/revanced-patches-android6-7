package app.revanced.patches.youtube.misc.branding.name.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.patches.options.PatchOptions
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("custom-branding-name")
@DependsOn(
    [
        PatchOptions::class,
        SettingsPatch::class
    ]
)
@Description("Changes the YouTube launcher name to your choice (defaults to ReVanced Extended).")
@YouTubeCompatibility
class CustomBrandingNamePatch : ResourcePatch {
    override fun execute(context: ResourceContext): PatchResult {

        // App name
        val appName = PatchOptions.YouTube_AppName

        val manifest = context["AndroidManifest.xml"]
        manifest.writeText(
            manifest.readText()
                .replace(
                    "android:label=\"@string/application_name",
                    "android:label=\"$appName"
                )
        )

        ResourceHelper.labelPatchSuccess(
            context,
            "$appName"
        )

        return PatchResultSuccess()
    }
}
