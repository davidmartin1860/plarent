package com.plarent.backend.plant

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class PlantResponse(
    val id: UUID,
    val name: String,
    val species: String,
    val location: String?,
    val acquiredDate: LocalDate?,
    val notes: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun from(plant: Plant) = PlantResponse(
            id = plant.id,
            name = plant.name,
            species = plant.species,
            location = plant.location,
            acquiredDate = plant.acquiredDate,
            notes = plant.notes,
            createdAt = plant.createdAt,
            updatedAt = plant.updatedAt,
        )
    }
}

data class CreatePlantRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:NotBlank
    @field:Size(max = 150)
    val species: String,

    @field:Size(max = 150)
    val location: String? = null,

    val acquiredDate: LocalDate? = null,

    @field:Size(max = 2000)
    val notes: String? = null,
)
