package app.revanced.patches.youtube.misc.protobufspoof.fingerprints

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint

internal object StatsQueryParameterFingerprint : MethodFingerprint(
    strings = listOf("adunit"),
)