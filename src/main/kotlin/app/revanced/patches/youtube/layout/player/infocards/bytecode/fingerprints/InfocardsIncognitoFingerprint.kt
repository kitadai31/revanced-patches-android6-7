package app.revanced.patches.youtube.layout.player.infocards.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import com.android.tools.smali.dexlib2.AccessFlags

@Name("infocards-incognito-fingerprint")
@YouTubeCompatibility
object InfocardsIncognitoFingerprint : MethodFingerprint(
    returnType = "Ljava/lang/Boolean;",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf("vibrator")
)