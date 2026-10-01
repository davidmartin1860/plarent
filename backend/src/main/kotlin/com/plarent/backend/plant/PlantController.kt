package com.plarent.backend.plant

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/plants")
class PlantController(
    private val plantService: PlantService,
) {
    @GetMapping
    fun listPlants(): List<PlantResponse> =
        plantService.listPlants().map { PlantResponse.from(it) }

    @GetMapping("/{id}")
    fun getPlant(@PathVariable id: UUID): PlantResponse =
        PlantResponse.from(plantService.getPlant(id))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPlant(@Valid @RequestBody request: CreatePlantRequest): PlantResponse =
        PlantResponse.from(plantService.createPlant(request))
}
