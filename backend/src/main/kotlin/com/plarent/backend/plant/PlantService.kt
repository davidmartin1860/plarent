package com.plarent.backend.plant

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class PlantService(
    private val plantRepository: PlantRepository,
) {
    fun listPlants(): List<Plant> =
        plantRepository.findAll(org.springframework.data.domain.Sort.by("createdAt").descending())

    fun getPlant(id: UUID): Plant =
        plantRepository.findById(id).orElseThrow { PlantNotFoundException(id) }

    fun createPlant(request: CreatePlantRequest): Plant {
        val plant = Plant(
            name = request.name.trim(),
            species = request.species.trim(),
            location = request.location?.trim()?.ifBlank { null },
            acquiredDate = request.acquiredDate,
            notes = request.notes?.trim()?.ifBlank { null },
        )
        return plantRepository.save(plant)
    }

    fun deletePlant(id: UUID) {
        if (!plantRepository.existsById(id)) {
            throw PlantNotFoundException(id)
        }
        plantRepository.deleteById(id)
    }
}

class PlantNotFoundException(id: UUID) : RuntimeException("Plant $id not found")
