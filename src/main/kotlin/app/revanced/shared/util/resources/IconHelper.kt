package app.revanced.shared.util.resources

import app.revanced.patcher.data.ResourceContext
import app.revanced.shared.util.FileCopyCompat
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import org.w3c.dom.Element

internal object IconHelper {

    fun customIcon(
        context: ResourceContext,
        iconName: String
    ) {
        val launchIcon = arrayOf(
            "adaptiveproduct_youtube_background_color_108",
            "adaptiveproduct_youtube_foreground_color_108",
            "ic_launcher",
            "ic_launcher_round"
        )

        val splashIcon = arrayOf(
            "product_logo_youtube_color_24",
            "product_logo_youtube_color_36",
            "product_logo_youtube_color_144",
            "product_logo_youtube_color_192"
        )

        copyResources(
            context,
            "youtube",
            iconName,
            "launchericon",
            "mipmap",
            launchIcon
        )

        copyResources(
            context,
            "youtube",
            iconName,
            "splashicon",
            "drawable",
            splashIcon
        )

        monochromeIcon(
            context,
            "youtube",
            "adaptive_monochrome_ic_youtube_launcher",
            iconName
        )

        context.xmlEditor["res/values-v31/styles.xml"].use { editor ->
            with(editor.file) {
                val childNodes = getElementsByTagName("resources").item(0).childNodes
                val length = childNodes.length

                for (i in 0 until length) {
                    val node = childNodes.item(i) as? Element ?: continue

                    if (node.getAttribute("name") == "Base.Theme.YouTube.Launcher") {
                        node.removeChild(node.childNodes.item(0))
                    }
                }
            }
        }
    }

    private fun copyResources(
        context: ResourceContext,
        appName: String,
        iconName: String,
        iconPath: String,
        directory: String,
        iconArray: Array<String>
    ){
        arrayOf(
            "xxxhdpi",
            "xxhdpi",
            "xhdpi",
            "hdpi",
            "mdpi"
        ).forEach { size ->
            iconArray.forEach iconLoop@{ name ->
                try {
                    Files.copy(
                        this.javaClass.classLoader.getResourceAsStream("$appName/branding/$iconName/$iconPath/$size/$name.png")!!,
                        context["res"].resolve("$directory-$size").resolve("$name.png").toPath(),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                } catch (e: NoSuchMethodError) {
                    FileCopyCompat.copy(
                        this.javaClass.classLoader.getResourceAsStream("$appName/branding/$iconName/$iconPath/$size/$name.png")!!,
                        context["res"].resolve("$directory-$size").resolve("$name.png")
                    )
                }
            }
        }
    }

    private fun monochromeIcon(
        context: ResourceContext,
        appName: String,
        monochromeIconName: String,
        iconName: String
    ){
        try {
            val relativePath = "drawable/$monochromeIconName.xml"
            try {
                Files.copy(
                    this.javaClass.classLoader.getResourceAsStream("$appName/branding/$iconName/monochromeicon/$relativePath")!!,
                    context["res"].resolve(relativePath).toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                )
            } catch (e: NoSuchMethodError) {
                FileCopyCompat.copy(
                    this.javaClass.classLoader.getResourceAsStream("$appName/branding/$iconName/monochromeicon/$relativePath")!!,
                    context["res"].resolve(relativePath)
                )
            }
        } catch (_: Exception) {}
    }
}