package app.revanced.patches.youtube.utils.missingresources

import app.revanced.util.fingerprint.legacyFingerprint
import app.revanced.util.or
import com.android.tools.smali.dexlib2.AccessFlags

internal val navigationBarGetDrawableFingerprint = legacyFingerprint(
    name = "getDrawableFingerprint",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    returnType = "Landroid/graphics/drawable/Drawable;",
    parameters = listOf("Landroid/content/Context;", "I"),
    customFingerprint = { methodDef, classDef ->
        methodDef.name == "a"
        // && classDef.type == "Lzv;"
    }
)