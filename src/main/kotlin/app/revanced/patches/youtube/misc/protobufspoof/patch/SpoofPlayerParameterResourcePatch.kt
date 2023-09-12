package app.revanced.patches.youtube.misc.protobufspoof.patch

import app.revanced.patcher.data.ResourceContext
import app.revanced.patcher.patch.ResourcePatch
import app.revanced.shared.patches.mapping.ResourceMappingPatch

class SpoofPlayerParameterResourcePatch : ResourcePatch {
    override fun execute(context: ResourceContext) {
        // used in ScrubbedPreviewLayoutFingerprint
        scrubbedPreviewThumbnailResourceId = ResourceMappingPatch.resourceMappings.single {
            it.type == "id" && it.name == "thumbnail"
        }.id
    }

    companion object {
        var scrubbedPreviewThumbnailResourceId: Long = -1
    }
}