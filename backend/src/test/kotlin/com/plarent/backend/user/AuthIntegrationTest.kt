package com.plarent.backend.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.plarent.backend.PostgresIntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.UUID

@AutoConfigureMockMvc
class AuthIntegrationTest : PostgresIntegrationTest() {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private fun uniqueName() = "user_${UUID.randomUUID().toString().take(8)}"

    private fun MockHttpServletRequestBuilder.json(body: String) =
        contentType(MediaType.APPLICATION_JSON).content(body)

    private fun MockHttpServletRequestBuilder.bearer(token: String) = header("Authorization", "Bearer $token")

    private fun register(username: String, password: String = "correct horse"): String {
        val body = mockMvc.perform(post("/api/auth/register").json("""{"username":"$username","password":"$password"}"""))
            .andExpect(status().isCreated)
            .andReturn().response.contentAsString
        return objectMapper.readTree(body)["token"].asText()
    }

    @Test
    fun `registers, logs in and logs out`() {
        val username = uniqueName()
        register(username)

        mockMvc.perform(post("/api/auth/register").json("""{"username":"${username.uppercase()}","password":"another one"}"""))
            .andExpect(status().isConflict)
        mockMvc.perform(post("/api/auth/login").json("""{"username":"$username","password":"wrong password"}"""))
            .andExpect(status().isUnauthorized)

        val body = mockMvc.perform(post("/api/auth/login").json("""{"username":"${username.uppercase()}","password":"correct horse"}"""))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.user.username").value(username))
            .andReturn().response.contentAsString
        val token = objectMapper.readTree(body)["token"].asText()

        mockMvc.perform(get("/api/me").bearer(token))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value(username))
        mockMvc.perform(post("/api/auth/logout").bearer(token))
            .andExpect(status().isNoContent)
        mockMvc.perform(get("/api/me").bearer(token))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `rejects invalid registrations with 400`() {
        mockMvc.perform(post("/api/auth/register").json("""{"username":"a b","password":"correct horse"}"""))
            .andExpect(status().isBadRequest)
        mockMvc.perform(post("/api/auth/register").json("""{"username":"${uniqueName()}","password":"short"}"""))
            .andExpect(status().isBadRequest)
        mockMvc.perform(post("/api/auth/register").json("""{"username":"${uniqueName()}","password":"${"ü".repeat(40)}"}"""))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `users only see their own plants`() {
        val alice = register(uniqueName())
        val bob = register(uniqueName())

        val body = mockMvc.perform(post("/api/plants").bearer(alice).json("""{"name":"Monstera","species":"Monstera deliciosa"}"""))
            .andExpect(status().isCreated)
            .andReturn().response.contentAsString
        val plantId = objectMapper.readTree(body)["id"].asText()

        mockMvc.perform(get("/api/plants").bearer(alice))
            .andExpect(jsonPath("$.length()").value(1))
        mockMvc.perform(get("/api/plants").bearer(bob))
            .andExpect(jsonPath("$.length()").value(0))
        mockMvc.perform(get("/api/plants/$plantId").bearer(bob))
            .andExpect(status().isNotFound)
        mockMvc.perform(patch("/api/plants/$plantId").bearer(bob).json("""{"name":"Mine now"}"""))
            .andExpect(status().isNotFound)
        mockMvc.perform(delete("/api/plants/$plantId").bearer(bob))
            .andExpect(status().isNotFound)
        mockMvc.perform(get("/api/plants/$plantId").bearer(alice))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Monstera"))
    }

    @Test
    fun `rejects plant requests without a token`() {
        mockMvc.perform(get("/api/plants"))
            .andExpect(status().isUnauthorized)
    }
}
