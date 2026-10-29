package com.plarent.backend.plant

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class PlantService(
    private val plantRepository: PlantRepository,
    private val plantImageStorage: PlantImageStorage,
    @Value("\${plarent.images.max-bytes:5242880}")
    private val maxImageBytes: Long = PlantImageRules.DEFAULT_MAX_BYTES,
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

    fun updatePlant(id: UUID, request: UpdatePlantRequest): Plant {
        val plant = getPlant(id)
        request.name?.let { plant.name = it.trim() }
        request.species?.let { plant.species = it.trim() }
        request.location?.let { plant.location = it.trim().ifBlank { null } }
        request.acquiredDate?.let { plant.acquiredDate = it }
        request.notes?.let { plant.notes = it.trim().ifBlank { null } }
        plant.updatedAt = Instant.now()
        return plantRepository.save(plant)
    }

    fun saveImage(id: UUID, bytes: ByteArray, contentType: String?): Plant {
        val type = PlantImageRules.validatedContentType(bytes, contentType, maxImageBytes)
        val plant = getPlant(id)
        plantImageStorage.write(plant.id, bytes)
        plant.imageContentType = type
        plant.updatedAt = Instant.now()
        return plantRepository.save(plant)
    }

    fun getImage(id: UUID): StoredPlantImage {
        val plant = getPlant(id)
        val contentType = plant.imageContentType ?: throw PlantImageNotFoundException(id)
        val bytes = plantImageStorage.read(plant.id) ?: throw PlantImageNotFoundException(id)
        return StoredPlantImage(bytes, contentType)
    }

    fun deletePlant(id: UUID) {
        if (!plantRepository.existsById(id)) {
            throw PlantNotFoundException(id)
        }
        plantRepository.deleteById(id)
        plantImageStorage.delete(id)
    }
}

class PlantNotFoundException(id: UUID) : RuntimeException("Plant $id not found")
