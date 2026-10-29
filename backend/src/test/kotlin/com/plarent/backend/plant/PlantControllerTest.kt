package com.plarent.backend.plant

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
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
            .andExpect(jsonPath("$[0].hasImage").value(false))
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
    fun `updates a plant`() {
        val id = UUID.randomUUID()
        val request = UpdatePlantRequest(name = "Monty", species = "Monstera deliciosa", location = "Kitchen")
        whenever(plantService.updatePlant(eq(id), any())).thenReturn(
            Plant(id = id, name = "Monty", species = "Monstera deliciosa", location = "Kitchen"),
        )

        mockMvc.perform(
            patch("/api/plants/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Monty"))
            .andExpect(jsonPath("$.location").value("Kitchen"))
    }

    @Test
    fun `returns 404 when updating unknown plant`() {
        val id = UUID.randomUUID()
        whenever(plantService.updatePlant(eq(id), any())).thenThrow(PlantNotFoundException(id))
        val request = UpdatePlantRequest(name = "Monty", species = "Monstera deliciosa")

        mockMvc.perform(
            patch("/api/plants/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `rejects a blank name on update`() {
        mockMvc.perform(
            patch("/api/plants/${UUID.randomUUID()}")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "  "}"""),
        )
            .andExpect(status().isBadRequest)
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

    @Test
    fun `uploads an image`() {
        val id = UUID.randomUUID()
        val bytes = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())
        whenever(plantService.saveImage(eq(id), any(), eq("image/jpeg"))).thenReturn(
            Plant(id = id, name = "Monstera", species = "Monstera deliciosa", imageContentType = "image/jpeg"),
        )

        mockMvc.perform(
            multipart("/api/plants/$id/image")
                .file(MockMultipartFile("image", "plant.jpg", "image/jpeg", bytes)),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.hasImage").value(true))
    }

    @Test
    fun `returns the stored image`() {
        val id = UUID.randomUUID()
        val bytes = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)
        whenever(plantService.getImage(id)).thenReturn(StoredPlantImage(bytes, "image/png"))

        mockMvc.perform(get("/api/plants/$id/image"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.IMAGE_PNG))
            .andExpect(content().bytes(bytes))
    }

    @Test
    fun `returns 404 when the plant has no image`() {
        val id = UUID.randomUUID()
        whenever(plantService.getImage(id)).thenThrow(PlantImageNotFoundException(id))

        mockMvc.perform(get("/api/plants/$id/image"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `rejects an invalid image`() {
        val id = UUID.randomUUID()
        whenever(plantService.saveImage(eq(id), any(), any())).thenThrow(
            InvalidPlantImageException("Only JPEG, PNG, WEBP and GIF images are allowed"),
        )

        mockMvc.perform(
            multipart("/api/plants/$id/image")
                .file(MockMultipartFile("image", "notes.txt", "text/plain", "hello".toByteArray())),
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Only JPEG, PNG, WEBP and GIF images are allowed"))
    }
}
