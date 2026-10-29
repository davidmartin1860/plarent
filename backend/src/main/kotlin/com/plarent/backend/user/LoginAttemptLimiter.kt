package com.plarent.backend.user

import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

// Throttles password guessing per username. Keyed by username rather than IP because
// all web traffic reaches the API from the Next.js server. In-memory, so it resets on
// restart and isn't shared between instances - good enough for a single backend.
@Component
class LoginAttemptLimiter {
    private data class Window(val start: Instant, val failures: Int)

    private val windows = ConcurrentHashMap<String, Window>()

    fun checkAllowed(username: String, now: Instant = Instant.now()) {
        val window = windows[username] ?: return
        if (!window.isExpired(now) && window.failures >= MAX_FAILURES) {
            throw TooManyLoginAttemptsException()
        }
    }

    fun recordFailure(username: String, now: Instant = Instant.now()) {
        windows.compute(username) { _, window ->
            if (window == null || window.isExpired(now)) Window(now, 1) else window.copy(failures = window.failures + 1)
        }
        if (windows.size > PRUNE_THRESHOLD) {
            windows.entries.removeIf { it.value.isExpired(now) }
        }
    }

    fun reset(username: String) {
        windows.remove(username)
    }

    private fun Window.isExpired(now: Instant) = start.plus(WINDOW).isBefore(now)

    companion object {
        const val MAX_FAILURES = 10
        val WINDOW: Duration = Duration.ofMinutes(15)
        private const val PRUNE_THRESHOLD = 10_000
    }
}
