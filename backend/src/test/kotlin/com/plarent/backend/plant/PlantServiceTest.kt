package com.plarent.backend.plant

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.nio.file.Path
import java.time.LocalDate
import java.util.Optional
import kotlin.test.assertFailsWith

class PlantServiceTest {

    @TempDir
    lateinit var imageDirectory: Path

    private val plantRepository: PlantRepository = mock()
    private lateinit var plantService: PlantService

    @BeforeEach
    fun setUp() {
        plantService = PlantService(plantRepository, PlantImageStorage(imageDirectory))
    }

    @Test
    fun `update only changes the fields that are sent`() {
        val plant = Plant(
            name = "Monstera",
            species = "Monstera deliciosa",
            location = "Kitchen",
            acquiredDate = LocalDate.of(2025, 3, 1),
            notes = "Likes indirect light",
            imageContentType = "image/png",
        )
        whenever(plantRepository.findById(plant.id)).thenReturn(Optional.of(plant))
        whenever(plantRepository.save(any<Plant>())).thenAnswer { it.arguments[0] }

        val updated = plantService.updatePlant(plant.id, UpdatePlantRequest(name = " Monty ", location = ""))

        assertEquals("Monty", updated.name)
        assertEquals("Monstera deliciosa", updated.species)
        assertNull(updated.location)
        assertEquals(LocalDate.of(2025, 3, 1), updated.acquiredDate)
        assertEquals("Likes indirect light", updated.notes)
        assertEquals("image/png", updated.imageContentType)
    }

    @Test
    fun `saves a jpeg and serves it back`() {
        val plant = Plant(name = "Monstera", species = "Monstera deliciosa")
        whenever(plantRepository.findById(plant.id)).thenReturn(Optional.of(plant))
        whenever(plantRepository.save(any<Plant>())).thenAnswer { it.arguments[0] }

        val saved = plantService.saveImage(plant.id, JPEG, "image/jpeg")
        val image = plantService.getImage(plant.id)

        assertEquals("image/jpeg", saved.imageContentType)
        assertEquals("image/jpeg", image.contentType)
        kotlin.test.assertContentEquals(JPEG, image.bytes)
    }

    @Test
    fun `replacing an image overwrites the stored file`() {
        val plant = Plant(name = "Monstera", species = "Monstera deliciosa", imageContentType = "image/jpeg")
        whenever(plantRepository.findById(plant.id)).thenReturn(Optional.of(plant))
        whenever(plantRepository.save(any<Plant>())).thenAnswer { it.arguments[0] }

        plantService.saveImage(plant.id, JPEG, "image/jpeg")
        plantService.saveImage(plant.id, PNG, "image/png")

        val image = plantService.getImage(plant.id)
        assertEquals("image/png", image.contentType)
        kotlin.test.assertContentEquals(PNG, image.bytes)
    }

    @Test
    fun `rejects a file that is not an image`() {
        val plant = Plant(name = "Monstera", species = "Monstera deliciosa")
        whenever(plantRepository.findById(plant.id)).thenReturn(Optional.of(plant))

        assertFailsWith<InvalidPlantImageException> {
            plantService.saveImage(plant.id, "hello".encodeToByteArray(), "text/plain")
        }
    }

    @Test
    fun `rejects an image larger than the configured limit`() {
        val plant = Plant(name = "Monstera", species = "Monstera deliciosa")
        whenever(plantRepository.findById(plant.id)).thenReturn(Optional.of(plant))
        val limited = PlantService(plantRepository, PlantImageStorage(imageDirectory), maxImageBytes = JPEG.size.toLong())

        assertFailsWith<InvalidPlantImageException> {
            limited.saveImage(plant.id, JPEG + byteArrayOf(0), "image/jpeg")
        }
    }

    @Test
    fun `deleting a plant removes its image file`() {
        val plant = Plant(name = "Monstera", species = "Monstera deliciosa")
        whenever(plantRepository.findById(plant.id)).thenReturn(Optional.of(plant))
        whenever(plantRepository.save(any<Plant>())).thenAnswer { it.arguments[0] }
        whenever(plantRepository.existsById(plant.id)).thenReturn(true)

        plantService.saveImage(plant.id, JPEG, "image/jpeg")
        plantService.deletePlant(plant.id)

        assertNull(PlantImageStorage(imageDirectory).read(plant.id))
        verify(plantRepository).deleteById(plant.id)
    }

    private companion object {
        val JPEG = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xD9.toByte())
        val PNG = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
    }
}
