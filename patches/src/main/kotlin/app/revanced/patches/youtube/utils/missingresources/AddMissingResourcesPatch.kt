package app.revanced.patches.youtube.utils.missingresources

import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.extensions.InstructionExtensions.getInstruction
import app.revanced.patcher.patch.bytecodePatch
import app.revanced.patcher.patch.resourcePatch
import app.revanced.patcher.util.smali.ExternalLabel
import app.revanced.patches.youtube.utils.compatibility.Constants.COMPATIBLE_PACKAGE
import app.revanced.patches.youtube.utils.patch.PatchList.ADD_MISSING_RESOURCES
import app.revanced.patches.youtube.utils.settings.ResourceUtils.addPreference
import app.revanced.patches.youtube.utils.settings.settingsPatch
import app.revanced.util.ResourceGroup
import app.revanced.util.copyResources
import app.revanced.util.fingerprint.methodOrThrow

private val addMissingResourcesBytecodePatch = bytecodePatch {
    execute {
        // Hook navigation bar getDrawable to fix a crash when a resource is not found
        // When resource id is 0x0, replace it with a transparent image
        navigationBarGetDrawableFingerprint.methodOrThrow().apply {
            addInstructionsWithLabels(
                0,
                """
                if-nez p1, :original
                const p1, 0x7f080144 # @drawable/button_color_transparent_background
                """,
                ExternalLabel("original", getInstruction(0))
            )
        }
    }
}

@Suppress("unused")
val addMissingResourcesPatch = resourcePatch(
    ADD_MISSING_RESOURCES.title,
    ADD_MISSING_RESOURCES.summary,
) {
    compatibleWith(COMPATIBLE_PACKAGE)

    dependsOn(
        settingsPatch,
        addMissingResourcesBytecodePatch,
    )

    execute {

        // region set some aliases
        document("res/values/drawables.xml").use { document ->
            val rootNode = document.documentElement

            mapOf(
                // Shorts player
                "ic_right_like_off_shadowed" to "@drawable/ic_right_like_off_32c",
                "ic_right_like_on_shadowed" to "@drawable/ic_right_like_on_32c",
                "ic_right_dislike_off_shadowed" to "@drawable/ic_right_dislike_off_32c",
                "ic_right_dislike_on_shadowed" to "@drawable/ic_right_dislike_on_32c",
                "ic_right_comment_shadowed" to "@drawable/ic_right_comment_32c",
                "ic_right_share_shadowed" to "@drawable/ic_right_share_32c",
                "ic_remix_filled_white_shadowed" to "@drawable/ic_remix_filled_white_24",

                // Comments
                "yt_outline_thumb_up_black_18" to "@drawable/yt_outline_thumb_up_black_24",
                "yt_outline_thumb_down_black_18" to "@drawable/yt_outline_thumb_down_black_24",
                "yt_fill_thumb_up_black_18" to "@drawable/yt_fill_thumb_up_black_24",
                "yt_fill_thumb_down_black_18" to "@drawable/yt_fill_thumb_down_black_24",
                "yt_fill_spark_black_24" to "@drawable/yt_fill_sparkle_white_24",
            ).forEach { (key, value) ->
                val newElement = document.createElement("drawable")
                newElement.setAttribute("name", key)
                newElement.textContent = value
                rootNode.appendChild(newElement)
            }
        }

        // endregion

        // region add resources

        copyResources(
            "addmissingresources",
            ResourceGroup("drawable", "ic_waveform_elements.xml")
        )

        // endregion

        addPreference(ADD_MISSING_RESOURCES)

    }
}