package app.revanced.patches.shared.textcomponent

import app.revanced.util.fingerprint.legacyFingerprint
import app.revanced.util.or
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.Opcode

internal val textComponentConstructorFingerprint = legacyFingerprint(
    name = "textComponentConstructorFingerprint",
    returnType = "V",
    accessFlags = AccessFlags.PRIVATE or AccessFlags.CONSTRUCTOR,
    strings = listOf("TextComponent")
)

internal val textComponentContextFingerprint = legacyFingerprint(
    name = "textComponentContextFingerprint",
    // In 17.34.36, this method is separated to 2 methods: oay.c() calls obd.e()
    customFingerprint = { method, classDef ->
        classDef.type == "Lobd;" && method.name == "e"
    }
)
