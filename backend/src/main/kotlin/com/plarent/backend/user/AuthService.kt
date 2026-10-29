package com.plarent.backend.user

import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant
import java.util.Base64
import java.util.HexFormat
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val userSessionRepository: UserSessionRepository,
    private val passwordEncoder: PasswordEncoder,
    private val loginAttemptLimiter: LoginAttemptLimiter,
) {
    private val secureRandom = SecureRandom()

    // Checked against when the username doesn't exist, so a login takes the same
    // time whether or not the account exists.
    private val dummyPasswordHash: String = passwordEncoder.encode(UUID.randomUUID().toString())

    @Transactional
    fun register(request: RegisterRequest): AuthResponse {
        val username = normalize(request.username)
        if (userRepository.existsByUsername(username)) {
            throw UsernameTakenException()
        }
        val user = try {
            userRepository.saveAndFlush(User(username = username, passwordHash = passwordEncoder.encode(request.password)))
        } catch (e: DataIntegrityViolationException) {
            // Lost a race with a concurrent registration of the same name.
            throw UsernameTakenException()
        }
        return AuthResponse(token = createSession(user.id), user = UserResponse.from(user))
    }

    @Transactional
    fun login(request: LoginRequest): AuthResponse {
        val username = normalize(request.username)
        loginAttemptLimiter.checkAllowed(username)

        val user = userRepository.findByUsername(username)
        val passwordMatches = passwordEncoder.matches(request.password, user?.passwordHash ?: dummyPasswordHash)
        if (user == null || !passwordMatches) {
            loginAttemptLimiter.recordFailure(username)
            throw InvalidCredentialsException()
        }
        loginAttemptLimiter.reset(username)

        userSessionRepository.deleteByUserIdAndExpiresAtBefore(user.id, Instant.now())
        return AuthResponse(token = createSession(user.id), user = UserResponse.from(user))
    }

    @Transactional
    fun logout(token: String) {
        userSessionRepository.deleteByTokenHash(hashToken(token))
    }

    @Transactional
    fun authenticate(token: String): AuthenticatedUser? {
        val session = userSessionRepository.findByTokenHash(hashToken(token)) ?: return null
        if (session.expiresAt.isBefore(Instant.now())) {
            userSessionRepository.delete(session)
            return null
        }
        val user = userRepository.findById(session.userId).orElse(null) ?: return null
        return AuthenticatedUser(id = user.id, username = user.username)
    }

    fun getUser(id: UUID): User =
        userRepository.findById(id).orElseThrow { IllegalStateException("Authenticated user $id no longer exists") }

    private fun createSession(userId: UUID): String {
        val bytes = ByteArray(TOKEN_BYTES).also(secureRandom::nextBytes)
        val token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
        userSessionRepository.save(
            UserSession(userId = userId, tokenHash = hashToken(token), expiresAt = Instant.now().plus(SESSION_LIFETIME)),
        )
        return token
    }

    private fun normalize(username: String) = username.trim().lowercase()

    companion object {
        private const val TOKEN_BYTES = 32
        val SESSION_LIFETIME: Duration = Duration.ofDays(30)

        fun hashToken(token: String): String =
            HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(token.toByteArray()))
    }
}
