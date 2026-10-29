package com.plarent.backend.plant

import com.plarent.backend.user.AuthenticatedUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
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
    fun listPlants(@AuthenticationPrincipal user: AuthenticatedUser): List<PlantResponse> =
        plantService.listPlants(user.id).map { PlantResponse.from(it) }

    @GetMapping("/{id}")
    fun getPlant(@AuthenticationPrincipal user: AuthenticatedUser, @PathVariable id: UUID): PlantResponse =
        PlantResponse.from(plantService.getPlant(id, user.id))

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createPlant(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @Valid @RequestBody request: CreatePlantRequest,
    ): PlantResponse =
        PlantResponse.from(plantService.createPlant(user.id, request))

    @PatchMapping("/{id}")
    fun updatePlant(
        @AuthenticationPrincipal user: AuthenticatedUser,
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdatePlantRequest,
    ): PlantResponse =
        PlantResponse.from(plantService.updatePlant(id, user.id, request))

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletePlant(@AuthenticationPrincipal user: AuthenticatedUser, @PathVariable id: UUID) {
        plantService.deletePlant(id, user.id)
    }
}
