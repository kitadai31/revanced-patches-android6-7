package app.revanced.patches.youtube.misc.optimize.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("optimize-resource")
@DependsOn(
    [
        RedundantResourcePatch::class,
        MissingTranslationPatch::class,
        SettingsPatch::class
    ]
)
@Description("Removes duplicate resources and adds missing translation files from YouTube.")
@YouTubeCompatibility
class OptimizeResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        ResourceHelper.patchSuccess(
            context,
            "optimize-resource"
        )
    }
}
