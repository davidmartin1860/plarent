package com.plarent.backend.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.UUID

@Repository
interface UserSessionRepository : JpaRepository<UserSession, UUID> {
    fun findByTokenHash(tokenHash: String): UserSession?

    fun deleteByTokenHash(tokenHash: String)

    fun deleteByUserIdAndExpiresAtBefore(userId: UUID, cutoff: Instant)
}
