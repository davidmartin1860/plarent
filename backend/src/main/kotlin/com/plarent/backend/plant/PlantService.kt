package com.plarent.backend.plant

import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class PlantService(
    private val plantRepository: PlantRepository,
) {
    // Plants are only ever looked up by (id, owner): another user's plant is reported
    // as not found rather than forbidden, so ids don't leak which plants exist.
    fun listPlants(ownerId: UUID): List<Plant> =
        plantRepository.findAllByOwnerIdOrderByCreatedAtDesc(ownerId)

    fun getPlant(id: UUID, ownerId: UUID): Plant =
        plantRepository.findByIdAndOwnerId(id, ownerId) ?: throw PlantNotFoundException(id)

    fun createPlant(ownerId: UUID, request: CreatePlantRequest): Plant {
        val plant = Plant(
            ownerId = ownerId,
            name = request.name.trim(),
            species = request.species.trim(),
            location = request.location?.trim()?.ifBlank { null },
            acquiredDate = request.acquiredDate,
            notes = request.notes?.trim()?.ifBlank { null },
        )
        return plantRepository.save(plant)
    }

    fun updatePlant(id: UUID, ownerId: UUID, request: UpdatePlantRequest): Plant {
        val plant = getPlant(id, ownerId)
        request.name?.let { plant.name = it.trim() }
        request.species?.let { plant.species = it.trim() }
        request.location?.let { plant.location = it.trim().ifBlank { null } }
        request.acquiredDate?.let { plant.acquiredDate = it }
        request.notes?.let { plant.notes = it.trim().ifBlank { null } }
        plant.updatedAt = Instant.now()
        return plantRepository.save(plant)
    }

    fun deletePlant(id: UUID, ownerId: UUID) {
        plantRepository.delete(getPlant(id, ownerId))
    }
}

class PlantNotFoundException(id: UUID) : RuntimeException("Plant $id not found")
