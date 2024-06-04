package app.revanced.shared.patches.settings

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.shared.util.resources.ResourceHelper
import app.revanced.shared.util.resources.ResourceUtils
import app.revanced.shared.util.resources.ResourceUtils.copyResources
import app.revanced.shared.util.resources.ResourceUtils.copyXmlNode

/**
 * Abstract settings resource patch
 *
 * @param sourceDirectory Source directory to copy the preference template from
 * @param sourcehostDirectory Source directory to copy the preference template from
 */
abstract class AbstractSettingsResourcePatch(
    private val sourceDirectory: String,
    private val sourcehostDirectory: String,
    private val isYouTube: Boolean,
) : ResourcePatch {
    override fun execute(context: ResourceContext) {
        /*
         * used for self-restart
         */
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            editor.file.getElementsByTagName("manifest").item(0).also {
                it.appendChild(it.ownerDocument.createElement("uses-permission").also { element ->
                    element.setAttribute("android:name", "android.permission.SCHEDULE_EXACT_ALARM")
                })
            }
        }

        /*
         * Copy strings
         */
        context.copyXmlNode(sourcehostDirectory, "values/strings.xml", "resources")
        context.copyXmlNode(sourcehostDirectory, "values/arrays.xml", "resources")

        /* initialize ReVanced Settings */
        if (isYouTube == true) {
            context.copyResources(sourceDirectory, ResourceUtils.ResourceGroup("xml", "revanced_prefs.xml"))
            ResourceHelper.initReVancedSettings(context)
        }
    }
}