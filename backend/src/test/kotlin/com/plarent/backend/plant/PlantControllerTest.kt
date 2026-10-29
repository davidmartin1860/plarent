package com.plarent.backend.plant

import com.fasterxml.jackson.databind.ObjectMapper
import com.plarent.backend.common.SecurityConfig
import com.plarent.backend.user.AuthService
import com.plarent.backend.user.AuthenticatedUser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.UUID

@WebMvcTest(PlantController::class)
@Import(SecurityConfig::class)
class PlantControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var plantService: PlantService

    @MockBean
    lateinit var authService: AuthService

    private val user = AuthenticatedUser(id = UUID.randomUUID(), username = "mutti")

    @BeforeEach
    fun authenticate() {
        whenever(authService.authenticate(TOKEN)).thenReturn(user)
    }

    private fun MockHttpServletRequestBuilder.authed() = header("Authorization", "Bearer $TOKEN")

    private fun plant(id: UUID = UUID.randomUUID(), name: String = "Monstera", location: String? = null) =
        Plant(id = id, ownerId = user.id, name = name, species = "Monstera deliciosa", location = location)

    @Test
    fun `rejects requests without a token`() {
        mockMvc.perform(get("/api/plants"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.status").value(401))
    }

    @Test
    fun `rejects requests with an unknown token`() {
        mockMvc.perform(get("/api/plants").header("Authorization", "Bearer nope"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `lists plants`() {
        whenever(plantService.listPlants(user.id)).thenReturn(listOf(plant()))

        mockMvc.perform(get("/api/plants").authed())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].name").value("Monstera"))
    }

    @Test
    fun `returns 404 for unknown plant`() {
        val id = UUID.randomUUID()
        whenever(plantService.getPlant(id, user.id)).thenThrow(PlantNotFoundException(id))

        mockMvc.perform(get("/api/plants/$id").authed())
            .andExpect(status().isNotFound)
    }

    @Test
    fun `creates a plant`() {
        val request = CreatePlantRequest(name = "Monstera", species = "Monstera deliciosa")
        whenever(plantService.createPlant(eq(user.id), any())).thenReturn(plant())

        mockMvc.perform(
            post("/api/plants")
                .authed()
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
        whenever(plantService.updatePlant(eq(id), eq(user.id), any())).thenReturn(
            plant(id = id, name = "Monty", location = "Kitchen"),
        )

        mockMvc.perform(
            patch("/api/plants/$id")
                .authed()
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
        whenever(plantService.updatePlant(eq(id), eq(user.id), any())).thenThrow(PlantNotFoundException(id))
        val request = UpdatePlantRequest(name = "Monty", species = "Monstera deliciosa")

        mockMvc.perform(
            patch("/api/plants/$id")
                .authed()
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun `rejects a blank name on update`() {
        mockMvc.perform(
            patch("/api/plants/${UUID.randomUUID()}")
                .authed()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"name": "  "}"""),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `deletes a plant`() {
        val id = UUID.randomUUID()

        mockMvc.perform(delete("/api/plants/$id").authed())
            .andExpect(status().isNoContent)

        verify(plantService).deletePlant(id, user.id)
    }

    @Test
    fun `returns 404 when deleting unknown plant`() {
        val id = UUID.randomUUID()
        whenever(plantService.deletePlant(id, user.id)).thenThrow(PlantNotFoundException(id))

        mockMvc.perform(delete("/api/plants/$id").authed())
            .andExpect(status().isNotFound)
    }

    @Test
    fun `rejects blank name`() {
        val request = CreatePlantRequest(name = "", species = "Monstera deliciosa")

        mockMvc.perform(
            post("/api/plants")
                .authed()
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
                .authed()
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)),
        )
            .andExpect(status().isBadRequest)
    }

    companion object {
        private const val TOKEN = "test-token"
    }
}
