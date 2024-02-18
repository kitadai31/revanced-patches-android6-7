package app.revanced.patches.youtube.misc.updatescreen.bytecode.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

object AppBlockingCheckResultToStringFingerprint : MethodFingerprint(
    returnType = "Ljava/lang/String;",
    strings = listOf("AppBlockingCheckResult{intent=")
)