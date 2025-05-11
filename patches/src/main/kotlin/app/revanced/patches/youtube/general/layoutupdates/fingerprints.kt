package app.revanced.patches.youtube.general.layoutupdates

import app.revanced.util.fingerprint.legacyFingerprint
import app.revanced.util.or
import com.android.tools.smali.dexlib2.AccessFlags

internal val hotConfigPreferenceFingerprint = legacyFingerprint(
    name = "hotConfigPreferenceFingerprint",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = emptyList(),
    returnType = "V",
    strings = listOf("com.google.android.libraries.youtube.innertube.hot_stored_timestamp"),
    customFingerprint = { method, classDef ->
        method.name == "run" && classDef.type == "Lusi;"
    }
)

internal val coldConfigPreferenceFingerprint = legacyFingerprint(
    name = "hotConfigPreferenceFingerprint",
    accessFlags = AccessFlags.PUBLIC or AccessFlags.FINAL,
    parameters = emptyList(),
    returnType = "V",
    strings = listOf(
        "com.google.android.libraries.youtube.innertube.cold_config_group",
        "com.google.android.libraries.youtube.innertube.cold_stored_timestamp",
        "com.google.android.libraries.youtube.innertube.cold_hash_data",
    ),
    customFingerprint = { method, classDef ->
        method.name == "o" && classDef.type == "Lutm;"
    }
)