package com.plarent.shared.model

import kotlinx.serialization.Serializable

/**
 * Mirrors the backend's PlantResponse DTO. Date/instant fields are kept as
 * plain Strings (ISO-8601) rather than parsed types, matching the raw JSON
 * the backend returns. UI layers can format/parse them as needed.
 */
@Serializable
data class Plant(
    val id: String,
    val name: String,
    val species: String,
    val location: String?,
    val acquiredDate: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String,
)

@Serializable
data class CreatePlantRequest(
    val name: String,
    val species: String,
    val location: String? = null,
    val acquiredDate: String? = null,
    val notes: String? = null,
)
