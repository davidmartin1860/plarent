package com.plarent.backend.user

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import java.time.Instant
import java.util.Optional

class AuthServiceTest {

    private val userRepository: UserRepository = mock()
    private val userSessionRepository: UserSessionRepository = mock()
    private val passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
    private val limiter = LoginAttemptLimiter()
    private val authService = AuthService(userRepository, userSessionRepository, passwordEncoder, limiter)

    private val user = User(username = "mutti", passwordHash = passwordEncoder.encode("correct horse"))

    @Test
    fun `register stores a lowercased username and a hashed password`() {
        whenever(userRepository.saveAndFlush(any<User>())).thenAnswer { it.arguments[0] }

        val response = authService.register(RegisterRequest(username = " Mutti ", password = "correct horse"))

        val saved = argumentCaptor<User>().also { verify(userRepository).saveAndFlush(it.capture()) }.firstValue
        assertEquals("mutti", saved.username)
        assertNotEquals("correct horse", saved.passwordHash)
        assertEquals("mutti", response.user.username)
    }

    @Test
    fun `register rejects a taken username`() {
        whenever(userRepository.existsByUsername("mutti")).thenReturn(true)

        assertThrows<UsernameTakenException> {
            authService.register(RegisterRequest(username = "Mutti", password = "correct horse"))
        }
    }

    @Test
    fun `session stores only the token hash`() {
        whenever(userRepository.findByUsername("mutti")).thenReturn(user)

        val response = authService.login(LoginRequest(username = "mutti", password = "correct horse"))

        val session = argumentCaptor<UserSession>().also { verify(userSessionRepository).save(it.capture()) }.firstValue
        assertNotEquals(response.token, session.tokenHash)
        assertEquals(AuthService.hashToken(response.token), session.tokenHash)
    }

    @Test
    fun `login rejects a wrong password and an unknown user the same way`() {
        whenever(userRepository.findByUsername("mutti")).thenReturn(user)

        assertThrows<InvalidCredentialsException> { authService.login(LoginRequest("mutti", "wrong password")) }
        assertThrows<InvalidCredentialsException> { authService.login(LoginRequest("nobody", "correct horse")) }
        verify(userSessionRepository, never()).save(any())
    }

    @Test
    fun `login is blocked after too many failures`() {
        whenever(userRepository.findByUsername("mutti")).thenReturn(user)
        repeat(LoginAttemptLimiter.MAX_FAILURES) {
            assertThrows<InvalidCredentialsException> { authService.login(LoginRequest("mutti", "wrong password")) }
        }

        assertThrows<TooManyLoginAttemptsException> { authService.login(LoginRequest("mutti", "correct horse")) }
    }

    @Test
    fun `authenticate resolves a valid token`() {
        val session = UserSession(userId = user.id, tokenHash = AuthService.hashToken("token"), expiresAt = Instant.now().plusSeconds(60))
        whenever(userSessionRepository.findByTokenHash(session.tokenHash)).thenReturn(session)
        whenever(userRepository.findById(user.id)).thenReturn(Optional.of(user))

        assertEquals(AuthenticatedUser(user.id, "mutti"), authService.authenticate("token"))
    }

    @Test
    fun `authenticate rejects and deletes an expired session`() {
        val session = UserSession(userId = user.id, tokenHash = AuthService.hashToken("token"), expiresAt = Instant.now().minusSeconds(1))
        whenever(userSessionRepository.findByTokenHash(session.tokenHash)).thenReturn(session)

        assertNull(authService.authenticate("token"))
        verify(userSessionRepository).delete(session)
    }
}
