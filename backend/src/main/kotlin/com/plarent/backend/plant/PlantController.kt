package com.plarent.backend.plant

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
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

    @PatchMapping("/{id}")
    fun updatePlant(@PathVariable id: UUID, @Valid @RequestBody request: UpdatePlantRequest): PlantResponse =
        PlantResponse.from(plantService.updatePlant(id, request))

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePlant(@PathVariable id: UUID) {
        plantService.deletePlant(id)
    }

    @GetMapping("/{id}/image")
    fun getImage(@PathVariable id: UUID): ResponseEntity<ByteArray> {
        val image = plantService.getImage(id)
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(image.contentType))
            .header("X-Content-Type-Options", "nosniff")
            .body(image.bytes)
    }

    @PostMapping("/{id}/image", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(@PathVariable id: UUID, @RequestParam("image") file: MultipartFile): PlantResponse =
        PlantResponse.from(plantService.saveImage(id, file.bytes, file.contentType))
}
