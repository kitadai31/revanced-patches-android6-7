package app.revanced.patches.youtube.misc.settings.resource.patch

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.youtube.misc.integrations.patch.IntegrationsPatch
import app.revanced.patches.youtube.misc.resourceid.patch.SharedResourcdIdPatch
import app.revanced.patches.youtube.misc.settings.bytecode.patch.SettingsBytecodePatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.patches.options.PatchOptions
import app.revanced.shared.patches.settings.AbstractSettingsResourcePatch
import app.revanced.shared.util.resources.ResourceHelper
import app.revanced.shared.util.resources.ResourceUtils
import app.revanced.shared.util.resources.ResourceUtils.copyResources
import org.w3c.dom.Element

@Patch
@Name("settings")
@Description("Applies mandatory patches to implement ReVanced settings into the application.")
@DependsOn(
    [
        IntegrationsPatch::class,
        PatchOptions::class,
        SharedResourcdIdPatch::class,
        SettingsBytecodePatch::class
    ]
)
@YouTubeCompatibility
class SettingsPatch : AbstractSettingsResourcePatch(
    "youtube/settings",
    "youtube/settings/host",
    true
) {
    override fun execute(context: ResourceContext) {
        super.execute(context)

        /*
         * create directory for the untranslated language resources
         */
        context["res/values-v21"].mkdirs()

        arrayOf(
            ResourceUtils.ResourceGroup(
                "layout",
                "revanced_settings_toolbar.xml",
                "revanced_settings_with_toolbar.xml",
                "revanced_settings_with_toolbar_layout.xml"
            ),
            ResourceUtils.ResourceGroup(
                "values-v21",
                "strings.xml"
            )
        ).forEach { resourceGroup ->
            context.copyResources("youtube/settings", resourceGroup)
        }

        /*
         * remove revanced settings divider
         */
        arrayOf("Theme.YouTube.Settings", "Theme.YouTube.Settings.Dark").forEach { themeName ->
            context.xmlEditor["res/values/styles.xml"].use { editor ->
                with(editor.file) {
                    val childNodes = getElementsByTagName("resources").item(0).childNodes
                    val length = childNodes.length

                    val newElement: Element = createElement("item")
                    newElement.setAttribute("name", "android:listDivider")
                    newElement.appendChild(createTextNode("@null"))

                    for (i in 0 until length) {
                        val node = childNodes.item(i) as? Element ?: continue

                        if (node.getAttribute("name") == themeName) {
                            node.appendChild(newElement)
                        }
                    }
                }
            }
        }

        /*
         add settings
         */
        ResourceHelper.addSettings(
            context,
            "PREFERENCE_CATEGORY: REVANCED_EXTENDED_SETTINGS",
            "PREFERENCE: MISC_SETTINGS",
            "SETTINGS: ABOUT"
        )
    }
}