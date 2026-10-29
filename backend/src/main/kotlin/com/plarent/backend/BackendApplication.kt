package com.plarent.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ImportRuntimeHints

// Authentication is handled by session tokens (see SecurityConfig), so skip the
// in-memory default user Spring Boot would otherwise generate.
@SpringBootApplication(exclude = [UserDetailsServiceAutoConfiguration::class])
@ImportRuntimeHints(NativeHints::class)
class BackendApplication

fun main(args: Array<String>) {
    runApplication<BackendApplication>(*args)
}
