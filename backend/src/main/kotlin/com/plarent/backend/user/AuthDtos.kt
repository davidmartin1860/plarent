package com.plarent.backend.user

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.UUID

// bcrypt only looks at the first 72 bytes of a password (and Spring rejects longer ones).
private const val MAX_PASSWORD_BYTES = 72

data class RegisterRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 32)
    @field:Pattern(regexp = "[a-zA-Z0-9_.-]+", message = "may only contain letters, digits, '_', '.' and '-'")
    val username: String,

    @field:Size(min = 8)
    val password: String,
) {
    @get:JsonIgnore
    @get:AssertTrue(message = "password must be at most $MAX_PASSWORD_BYTES bytes")
    val isPasswordWithinLimit: Boolean
        get() = password.toByteArray().size <= MAX_PASSWORD_BYTES
}

data class LoginRequest(
    @field:NotBlank
    @field:Size(max = 32)
    val username: String,

    @field:NotBlank
    val password: String,
) {
    @get:JsonIgnore
    @get:AssertTrue(message = "password must be at most $MAX_PASSWORD_BYTES bytes")
    val isPasswordWithinLimit: Boolean
        get() = password.toByteArray().size <= MAX_PASSWORD_BYTES
}

data class UserResponse(
    val id: UUID,
    val username: String,
    val createdAt: Instant,
) {
    companion object {
        fun from(user: User) = UserResponse(
            id = user.id,
            username = user.username,
            createdAt = user.createdAt,
        )
    }
}

data class AuthResponse(
    val token: String,
    val user: UserResponse,
)
