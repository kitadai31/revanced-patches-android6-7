package app.revanced.shared.util

import java.io.File
import java.io.InputStream

/**
 * Provides alternative file copy functions that does not use java.nio.file.
 *
 * This is needed for ReVanced Manager running on Android 6.0-7.1
 * because before Android 8.0 does not support java.nio.file.
 *
 * To use, catch NoClassDefFoundError or catch NoSuchMethodError caused by java.io.File#toPath()
 */
object FileCopyCompat {
    /**
     * Alternative implementation of
     * java.nio.file.Files#copy(Path source, Path target, CopyOption... options)
     *
     * If the target file already exists, it will be replaced.
     */
    fun copy(source: File, target: File) {
        source.copyTo(target, overwrite = true)
    }

    /**
     * Alternative implementation of
     * java.nio.file.Files#copy(InputStream in, Path target, CopyOption... options)
     *
     * If the target file already exists, it will be replaced.
     */
    fun copy(source: InputStream, target: File) {
        source.use { inputStream ->
            target.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
    }
}