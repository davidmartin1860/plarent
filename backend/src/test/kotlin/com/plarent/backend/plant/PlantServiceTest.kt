package com.plarent.backend.plant

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.time.LocalDate
import java.util.UUID

class PlantServiceTest {

    private val plantRepository: PlantRepository = mock()
    private val plantService = PlantService(plantRepository)
    private val ownerId = UUID.randomUUID()

    @Test
    fun `update only changes the fields that are sent`() {
        val plant = Plant(
            ownerId = ownerId,
            name = "Monstera",
            species = "Monstera deliciosa",
            location = "Kitchen",
            acquiredDate = LocalDate.of(2025, 3, 1),
            notes = "Likes indirect light",
        )
        whenever(plantRepository.findByIdAndOwnerId(plant.id, ownerId)).thenReturn(plant)
        whenever(plantRepository.save(any<Plant>())).thenAnswer { it.arguments[0] }

        val updated = plantService.updatePlant(plant.id, ownerId, UpdatePlantRequest(name = " Monty ", location = ""))

        assertEquals("Monty", updated.name)
        assertEquals("Monstera deliciosa", updated.species)
        assertNull(updated.location)
        assertEquals(LocalDate.of(2025, 3, 1), updated.acquiredDate)
        assertEquals("Likes indirect light", updated.notes)
    }

    @Test
    fun `another user's plant is not found`() {
        val id = UUID.randomUUID()

        assertThrows<PlantNotFoundException> { plantService.getPlant(id, ownerId) }
    }
}
