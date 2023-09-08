package app.revanced.patches.youtube.video.customspeed.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object SpeedArrayGeneratorFingerprint : MethodFingerprint(
    returnType = "[L",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.STATIC,
    strings = listOf("0.0#")
)
