package app.revanced.patches.youtube.misc.returnyoutubedislike.bytecode.fingerprints

import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import com.android.tools.smali.dexlib2.AccessFlags

object DislikeFingerprint : MethodFingerprint(
    returnType = "V",
    accessFlags = AccessFlags.PROTECTED or AccessFlags.CONSTRUCTOR,
    strings = listOf("like/dislike")
)