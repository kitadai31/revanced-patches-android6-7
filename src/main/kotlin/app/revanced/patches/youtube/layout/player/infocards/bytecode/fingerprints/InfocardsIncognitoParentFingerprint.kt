package app.revanced.patches.youtube.layout.player.infocards.bytecode.fingerprints

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.extensions.or
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.shared.annotation.YouTubeCompatibility
import com.android.tools.smali.dexlib2.AccessFlags

@Name("infocards-incognito-parent-fingerprint")
@YouTubeCompatibility
object InfocardsIncognitoParentFingerprint : MethodFingerprint(
    returnType = "Ljava/lang/String;",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    strings = listOf("player_overlay_info_card_teaser"),
)