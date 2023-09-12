package app.revanced.shared.patches.gestures

import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch

@Name("predictive-back-gesture")
@Description("Enables the predictive back gesture introduced on Android 13.")
class PredictiveBackGesturePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        context.xmlEditor["AndroidManifest.xml"].use { editor ->
            val document = editor.file

            with(document.getElementsByTagName("application").item(0)) {
                if (attributes.getNamedItem(FLAG) != null) return@with

                document.createAttribute(FLAG)
                    .apply { value = "false" }
                    .let(attributes::setNamedItem)

            }
        }
    }

    private companion object {
        const val FLAG = "android:enableOnBackInvokedCallback"
    }
}