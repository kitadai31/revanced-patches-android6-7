package app.revanced.patches.youtube.misc.forceheader.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.PatchException
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.settings.resource.patch.SettingsPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.FileCopyCompat
import app.revanced.shared.util.resources.ResourceHelper
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@Patch(false)
@Name("force-premium-heading")
@Description("Forces premium heading on the home screen.")
@DependsOn([SettingsPatch::class])
@YouTubeCompatibility
class PremiumHeadingPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        val resDirectory = context["res"]
        if (!resDirectory.isDirectory) throw PatchException("The res folder can not be found.")

        val (original, replacement) = "yt_premium_wordmark_header" to "yt_wordmark_header"
        val modes = arrayOf("light", "dark")

        arrayOf("xxxhdpi", "xxhdpi", "xhdpi", "hdpi", "mdpi").forEach { size ->
            val headingDirectory = resDirectory.resolve("drawable-$size")
            modes.forEach { mode ->
                val fromFile = headingDirectory.resolve("${original}_$mode.png")
                val toFile = headingDirectory.resolve("${replacement}_$mode.png")

                if (!fromFile.exists())
                    throw PatchException("The file $fromFile does not exist in the resources. Therefore, this patch can not succeed.")
                try {
                    Files.copy(
                        fromFile.toPath(),
                        toFile.toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                } catch (e: NoSuchMethodError) {
                    FileCopyCompat.copy(
                        fromFile,
                        toFile
                    )
                }
            }
        }

        val revancedprefs = context["res/xml/revanced_prefs.xml"]
        revancedprefs.writeText(
            revancedprefs.readText()
                .replace(
                    "<SwitchPreference android:title=\"@string/revanced_override_premium_header_title\" android:key=\"revanced_override_premium_header\" android:defaultValue=\"false\" android:summaryOn=\"@string/revanced_override_premium_header_summary_on\" android:summaryOff=\"@string/revanced_override_premium_header_summary_off\" />",
                    ""
                ).replace(
                    "header-switch",
                    "force-premium-heading"
                )
        )

        ResourceHelper.patchSuccess(
            context,
            "force-premium-heading"
        )
    }
}
