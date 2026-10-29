package com.plarent.backend.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.plarent.backend.user.AuthService
import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.DispatcherTypeRequestMatcher

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity, authService: AuthService, objectMapper: ObjectMapper): SecurityFilterChain {
        http {
            // Stateless bearer-token API: no cookies, so no CSRF exposure and no HTTP session.
            csrf { disable() }
            formLogin { disable() }
            httpBasic { disable() }
            logout { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            authorizeHttpRequests {
                // Error dispatches (e.g. validation 400s) must keep their status instead of turning into 401s.
                authorize(DispatcherTypeRequestMatcher(DispatcherType.ERROR), permitAll)
                authorize("/error", permitAll)
                authorize("/api/auth/register", permitAll)
                authorize("/api/auth/login", permitAll)
                authorize(anyRequest, authenticated)
            }
            exceptionHandling { authenticationEntryPoint = unauthorizedEntryPoint(objectMapper) }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(SessionTokenAuthenticationFilter(authService))
        }
        return http.build()
    }

    // Delegating encoder: bcrypt today, and stored hashes carry an {id} prefix so the
    // algorithm can be upgraded later without invalidating existing passwords.
    @Bean
    fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    private fun unauthorizedEntryPoint(objectMapper: ObjectMapper) = AuthenticationEntryPoint { _, response, _ ->
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        objectMapper.writeValue(
            response.outputStream,
            ErrorResponse(status = HttpStatus.UNAUTHORIZED.value(), message = "Authentication required"),
        )
    }
}
