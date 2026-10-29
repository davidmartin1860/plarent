package com.plarent.backend.user

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class LoginAttemptLimiterTest {

    private val limiter = LoginAttemptLimiter()
    private val now = Instant.parse("2026-10-29T12:00:00Z")

    private fun fail(times: Int, at: Instant = now) = repeat(times) { limiter.recordFailure("mutti", at) }

    @Test
    fun `blocks after the maximum number of failures`() {
        fail(LoginAttemptLimiter.MAX_FAILURES - 1)
        assertDoesNotThrow { limiter.checkAllowed("mutti", now) }

        fail(1)
        assertThrows<TooManyLoginAttemptsException> { limiter.checkAllowed("mutti", now) }
        assertDoesNotThrow { limiter.checkAllowed("someone-else", now) }
    }

    @Test
    fun `unblocks once the window has passed`() {
        fail(LoginAttemptLimiter.MAX_FAILURES)

        assertDoesNotThrow { limiter.checkAllowed("mutti", now.plus(LoginAttemptLimiter.WINDOW).plusSeconds(1)) }
    }

    @Test
    fun `reset clears failures`() {
        fail(LoginAttemptLimiter.MAX_FAILURES)
        limiter.reset("mutti")

        assertDoesNotThrow { limiter.checkAllowed("mutti", now) }
    }
}
