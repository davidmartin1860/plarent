package com.plarent.backend.plant

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.UUID

class PlantImageNotFoundException(id: UUID) : RuntimeException("Plant $id has no image")

class InvalidPlantImageException(message: String) : RuntimeException(message)

data class StoredPlantImage(
    val bytes: ByteArray,
    val contentType: String,
)

object PlantImageRules {
    const val DEFAULT_MAX_BYTES: Long = 5L * 1024 * 1024

    fun validatedContentType(bytes: ByteArray, contentType: String?, maxBytes: Long): String {
        if (bytes.isEmpty()) {
            throw InvalidPlantImageException("Image file is empty")
        }
        if (bytes.size > maxBytes) {
            throw InvalidPlantImageException("Image is larger than 5 MB")
        }

        val type = contentType?.substringBefore(';')?.trim()?.lowercase()
            ?: throw InvalidPlantImageException("Image content type is missing")
        val normalized = if (type == "image/jpg") "image/jpeg" else type
        if (!matches(normalized, bytes)) {
            throw InvalidPlantImageException("Only JPEG, PNG, WEBP and GIF images are allowed")
        }
        return normalized
    }

    private fun matches(contentType: String, bytes: ByteArray): Boolean = when (contentType) {
        "image/jpeg" -> startsWith(bytes, JPEG)
        "image/png" -> startsWith(bytes, PNG)
        "image/gif" -> startsWith(bytes, GIF87A) || startsWith(bytes, GIF89A)
        "image/webp" -> startsWith(bytes, RIFF) && bytes.size >= 12 && bytes.copyOfRange(8, 12).contentEquals(WEBP)
        else -> false
    }

    private fun startsWith(bytes: ByteArray, prefix: ByteArray): Boolean =
        bytes.size >= prefix.size && bytes.copyOfRange(0, prefix.size).contentEquals(prefix)

    private val JPEG = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())
    private val PNG = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)
    private val GIF87A = "GIF87a".encodeToByteArray()
    private val GIF89A = "GIF89a".encodeToByteArray()
    private val RIFF = "RIFF".encodeToByteArray()
    private val WEBP = "WEBP".encodeToByteArray()
}

class PlantImageStorage(private val directory: Path) {
    fun write(id: UUID, bytes: ByteArray) {
        Files.createDirectories(directory)
        Files.write(
            directory.resolve(id.toString()),
            bytes,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING,
            StandardOpenOption.WRITE,
        )
    }

    fun read(id: UUID): ByteArray? {
        val path = directory.resolve(id.toString())
        if (!Files.isRegularFile(path)) {
            return null
        }
        return Files.readAllBytes(path)
    }

    fun delete(id: UUID) {
        Files.deleteIfExists(directory.resolve(id.toString()))
    }
}

@Configuration
class PlantImageConfiguration {
    @Bean
    fun plantImageStorage(@Value("\${plarent.images.directory}") directory: String): PlantImageStorage =
        PlantImageStorage(Path.of(directory))
}
