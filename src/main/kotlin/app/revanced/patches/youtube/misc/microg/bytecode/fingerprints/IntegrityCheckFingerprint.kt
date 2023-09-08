package app.revanced.patches.youtube.misc.microg.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object IntegrityCheckFingerprint : MethodFingerprint(
    returnType = "L",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    parameters = listOf("L", "L"),
    strings = listOf("This should never happen.", "GooglePlayServicesUtil", "Google Play Store signature invalid.")
)