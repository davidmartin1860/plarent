package com.plarent.backend.common

import com.plarent.backend.user.AuthService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

const val BEARER_PREFIX = "Bearer "

// Resolves an `Authorization: Bearer <token>` header to the session's user. Requests
// without a valid token pass through unauthenticated and are rejected by authorization.
class SessionTokenAuthenticationFilter(
    private val authService: AuthService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            authService.authenticate(header.removePrefix(BEARER_PREFIX))?.let { user ->
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(user, null, emptyList())
            }
        }
        chain.doFilter(request, response)
    }
}
