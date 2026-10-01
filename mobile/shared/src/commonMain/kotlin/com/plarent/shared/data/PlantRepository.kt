package com.plarent.shared.data

import com.plarent.shared.model.CreatePlantRequest
import com.plarent.shared.model.Plant
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PlantRepository(private val baseUrl: String) {

    // No explicit engine: Ktor auto-discovers the platform engine dependency
    // (OkHttp on Android, Darwin on iOS) declared in each source set.
    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun listPlants(): List<Plant> =
        httpClient.get("$baseUrl/plants").body()

    suspend fun getPlant(id: String): Plant? {
        val response = httpClient.get("$baseUrl/plants/$id")
        if (response.status == HttpStatusCode.NotFound) {
            return null
        }
        return response.body()
    }

    suspend fun createPlant(request: CreatePlantRequest): Plant =
        httpClient.post("$baseUrl/plants") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}
