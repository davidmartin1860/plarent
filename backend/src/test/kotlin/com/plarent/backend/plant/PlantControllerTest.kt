package com.plarent.backend.plant

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@WebMvcTest(PlantController::class)
class PlantControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var plantService: PlantService

    @Test
    fun `lists plants`() {
        val plant = Plant(name = "Monstera", species = "Monstera deliciosa")
        whenever(plantService.listPlants()).thenReturn(listOf(plant))

        mockMvc.perform(get("/api/plants"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("Monstera"))
    }

    @Test
    fun `returns 404 for unknown plant`() {
        val id = UUID.randomUUID()
        whenever(plantService.getPlant(id)).thenThrow(PlantNotFoundException(id))

        mockMvc.perform(get("/api/plants/$id"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `creates a plant`() {
        val request = CreatePlantRequest(name = "Monstera", species = "Monstera deliciosa")
        whenever(plantService.createPlant(any())).thenReturn(
            Plant(name = request.name, species = request.species),
        )

        mockMvc.perform(
            post("/api/plants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.name").value("Monstera"))
    }

    @Test
    fun `deletes a plant`() {
        val id = UUID.randomUUID()

        mockMvc.perform(delete("/api/plants/$id"))
            .andExpect(status().isNoContent)

        verify(plantService).deletePlant(id)
    }

    @Test
    fun `returns 404 when deleting unknown plant`() {
        val id = UUID.randomUUID()
        whenever(plantService.deletePlant(id)).thenThrow(PlantNotFoundException(id))

        mockMvc.perform(delete("/api/plants/$id"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `rejects blank name`() {
        val request = CreatePlantRequest(name = "", species = "Monstera deliciosa")

        mockMvc.perform(
            post("/api/plants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `rejects blank species`() {
        val request = CreatePlantRequest(name = "Monstera", species = " ")

        mockMvc.perform(
            post("/api/plants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }
}
