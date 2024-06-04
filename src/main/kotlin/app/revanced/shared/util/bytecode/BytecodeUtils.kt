package app.revanced.shared.util.bytecode

import app.revanced.patcher.fingerprint.method.impl.MethodFingerprint
import app.revanced.shared.extensions.exception
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction21c
import com.android.tools.smali.dexlib2.iface.Method
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction
import com.android.tools.smali.dexlib2.iface.reference.Reference

fun Method.isNarrowLiteralExists(value: Int): Boolean {
    return getNarrowLiteralIndex(value) != -1
}

fun Method.isWideLiteralExists(value: Long): Boolean {
    return getWideLiteralIndex(value) != -1
}

fun Method.isWide32LiteralExists(value: Long): Boolean {
    return getWide32LiteralIndex(value) != -1
}

fun Method.getNarrowLiteralIndex(value: Int): Int {
    return implementation?.let {
        it.instructions.indexOfFirst { instruction ->
            instruction.opcode == Opcode.CONST
                    && (instruction as NarrowLiteralInstruction).narrowLiteral == value
        }
    } ?: -1
}

fun Method.getStringIndex(value: String): Int {
    return implementation?.let {
        it.instructions.indexOfFirst { instruction ->
            instruction.opcode == Opcode.CONST_STRING
                    && (instruction as BuilderInstruction21c).reference.toString() == value
        }
    } ?: -1
}

fun Method.getWideLiteralIndex(value: Long): Int {
    return implementation?.let {
        it.instructions.indexOfFirst { instruction ->
            instruction.opcode == Opcode.CONST
                    && (instruction as WideLiteralInstruction).wideLiteral == value
        }
    } ?: -1
}

fun Method.getWide32LiteralIndex(value: Long): Int {
    return implementation?.let {
        it.instructions.indexOfFirst { instruction ->
            instruction.opcode == Opcode.CONST_WIDE_32
                    && (instruction as WideLiteralInstruction).wideLiteral == value
        }
    } ?: -1
}

fun MethodFingerprint.resultOrThrow() = result ?: throw exception

/**
 * Get the [Reference] of an [Instruction] as [T].
 *
 * @param T The type of [Reference] to cast to.
 * @return The [Reference] as [T] or null
 * if the [Instruction] is not a [ReferenceInstruction] or the [Reference] is not of type [T].
 * @see ReferenceInstruction
 */
inline fun <reified T : Reference> Instruction.getReference() =
    (this as? ReferenceInstruction)?.reference as? T

/**
 * Get the index of the first [Instruction] that matches the predicate.
 *
 * @param predicate The predicate to match.
 * @return The index of the first [Instruction] that matches the predicate.
 */
fun Method.indexOfFirstInstruction(predicate: Instruction.() -> Boolean) =
    this.implementation!!.instructions.indexOfFirst(predicate)

fun Method.getStringInstructionIndex(value: String) = implementation?.let {
    it.instructions.indexOfFirst { instruction ->
        instruction.opcode == Opcode.CONST_STRING
                && (instruction as? ReferenceInstruction)?.reference.toString() == value
    }
} ?: -1