package app.revanced.shared.patches.timebar

import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint.Companion.resolve
import app.revanced.patcher.fingerprint.method.impl.MethodFingerprintResult
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.annotations.DependsOn
import app.revanced.patches.youtube.misc.resourceid.patch.SharedResourcdIdPatch
import app.revanced.shared.annotation.YouTubeCompatibility
import app.revanced.shared.extensions.exception
import app.revanced.shared.fingerprints.EmptyColorFingerprint
import app.revanced.shared.fingerprints.OnDrawFingerprint
import app.revanced.shared.fingerprints.TimebarFingerprint

@Name("hook-timebar-patch")
@DependsOn([SharedResourcdIdPatch::class])
@YouTubeCompatibility
class HookTimebarPatch : BytecodePatch(
    listOf(
        EmptyColorFingerprint,
        TimebarFingerprint
    )
) {
    override fun execute(context: BytecodeContext) {
        EmptyColorFingerprintResult = EmptyColorFingerprint.result ?: throw EmptyColorFingerprint.exception

        OnDrawFingerprint.resolve(context, EmptyColorFingerprintResult.classDef)
        SetTimbarFingerprintResult = OnDrawFingerprint.result ?: throw OnDrawFingerprint.exception

        TimbarFingerprintResult = TimebarFingerprint.result ?: throw TimebarFingerprint.exception
    }

    internal companion object {
        lateinit var EmptyColorFingerprintResult: MethodFingerprintResult
        lateinit var SetTimbarFingerprintResult: MethodFingerprintResult
        lateinit var TimbarFingerprintResult: MethodFingerprintResult
    }
}