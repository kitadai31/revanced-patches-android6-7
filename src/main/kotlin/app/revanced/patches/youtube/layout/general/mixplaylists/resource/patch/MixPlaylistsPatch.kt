package app.revanced.patches.youtube.layout.general.mixplaylists.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.layout.general.mixplaylists.bytecode.patch.MixPlaylistsBytecodePatch
import app.revanced.patches.youtube.misc.litho.filter.patch.LithoFilterPatch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceHelper

@Patch
@Name("hide-mix-playlists")
@Description("Removes mix playlists from home feed and video player.")
@DependsOn(
    [
        LithoFilterPatch::class,
        MixPlaylistsBytecodePatch::class,
        SettingsPatch::class
    ]
)
@YouTubeCompatibility
class MixPlaylistsPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        /*
         add settings
         */
        ResourceHelper.addSettings2(
            context,
            "PREFERENCE_CATEGORY: REVANCED_SETTINGS",
            "PREFERENCE: LAYOUT_SETTINGS",
            "PREFERENCE_HEADER: GENERAL",
            "SETTINGS: HIDE_MIX_PLAYLISTS"
        )

        ResourceHelper.patchSuccess(
            context,
            "hide-mix-playlists"
        )
    }
}