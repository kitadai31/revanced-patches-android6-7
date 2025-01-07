package app.revanced.util

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * Provides java.nio.file.Files compatible functions.
 *
 * This is needed for ReVanced Manager running on Android 6.0-7.1
 * because before Android 8.0 does not support java.nio.file.
 */
internal object FilesCompat {
    private val useCompat = try {
        Class.forName("java.nio.file.Files")
        false
    } catch (_ : ClassNotFoundException) {
        // Under Android 8.0
        true
    }

    fun copy(source: File, target: File) {
        if (useCompat) {
            source.copyTo(target, overwrite = true)
        } else {
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    fun copy(source: InputStream, target: File) {
        if (useCompat) {
            source.use { inputStream ->
                target.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        } else {
            Files.copy(source, target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    fun createDirectories(file: File) {
        if (useCompat) {
            if (!file.mkdirs()) {
                throw IOException()
            }
        } else {
            Files.createDirectories(file.toPath())
        }
    }
}