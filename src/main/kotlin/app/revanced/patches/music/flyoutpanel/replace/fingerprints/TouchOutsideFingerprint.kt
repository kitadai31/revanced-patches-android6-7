package app.revanced.patches.music.flyoutpanel.replace.fingerprints

import app.revanced.patches.music.utils.resourceid.SharedResourceIdPatch.TouchOutside
import app.revanced.util.fingerprint.LiteralValueFingerprint

object TouchOutsideFingerprint : LiteralValueFingerprint(
    returnType = "Landroid/view/View;",
    literalSupplier = { TouchOutside }
)