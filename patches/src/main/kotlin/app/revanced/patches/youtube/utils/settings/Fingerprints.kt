package app.revanced.patches.youtube.utils.settings

import app.revanced.patches.youtube.utils.resourceid.appearance
import app.revanced.util.fingerprint.legacyFingerprint

internal val themeSetterSystemFingerprint = legacyFingerprint(
    name = "themeSetterSystemFingerprint",
    returnType = "L",
    literals = listOf(appearance),
)

internal val settingsHostActivityOnCreateFingerprint = legacyFingerprint(
    name = "settingsHostActivityOnCreateFingerprint",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;"),
    customFingerprint = { method, classDef ->
        classDef.endsWith("/ReVancedSettingsHostActivity;") && method.name == "onCreate"
    }
)

internal val licenseMenuActivityOnCreateFingerprint = legacyFingerprint(
    name = "licenseMenuActivityOnCreateFingerprint",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;"),
    customFingerprint = { method, classDef ->
        classDef.endsWith("/LicenseMenuActivity;") && method.name == "onCreate"
    }
)

internal val proxyBillingActivityV2OnCreateFingerprint = legacyFingerprint(
    name = "proxyBillingActivityV2OnCreateFingerprint",
    returnType = "V",
    parameters = listOf("Landroid/os/Bundle;"),
    customFingerprint = { method, classDef ->
        classDef.endsWith("/ProxyBillingActivityV2;") && method.name == "onCreate"
    }
)
