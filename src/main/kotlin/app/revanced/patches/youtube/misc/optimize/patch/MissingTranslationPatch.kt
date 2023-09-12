package app.revanced.patches.youtube.misc.optimize.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.util.resources.ResourceUtils.copyXmlNode

@Name("add-missing-translation-patch")
@Description("Adds missing translation files from YouTube.")
@YouTubeCompatibility
class MissingTranslationPatch : ResourcePatch {
    override fun execute(context: ResourceContext) {

        LANGUAGE_LIST.forEach { name ->
            context.copyXmlNode("youtube/resource/host", "values-$name/strings.xml", "resources")
        }
    }

    private companion object {
        val LANGUAGE_LIST = arrayOf(
            "af",
            "am",
            "ar",
            "az",
            "b+sr+Latn",
            "be",
            "bg",
            "bn",
            "bs",
            "ca",
            "cs",
            "da",
            "de",
            "el",
            "en-rGB",
            "en-rIN",
            "es",
            "es-rUS",
            "et",
            "eu",
            "fa",
            "fi",
            "fr",
            "fr-rCA",
            "gl",
            "gu",
            "hi",
            "hr",
            "hu",
            "hy",
            "in",
            "is",
            "it",
            "iw",
            "ja",
            "ka",
            "kk",
            "km",
            "kn",
            "ko",
            "ky",
            "lo",
            "lt",
            "lv",
            "mk",
            "ml",
            "mn",
            "mr",
            "ms",
            "my",
            "nb",
            "ne",
            "nl",
            "pa",
            "pl",
            "pt-rBR",
            "pt-rPT",
            "ro",
            "ru",
            "si",
            "sk",
            "sl",
            "sq",
            "sr",
            "sv",
            "sw",
            "ta",
            "te",
            "th",
            "tl",
            "tr",
            "uk",
            "ur",
            "uz",
            "vi",
            "zh-rCN",
            "zh-rHK",
            "zh-rTW",
            "zu"
        )
    }
}
