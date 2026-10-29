package com.plarent.backend.user

import java.util.UUID

// The principal stored in the SecurityContext for a request with a valid session token.
data class AuthenticatedUser(
    val id: UUID,
    val username: String,
)
