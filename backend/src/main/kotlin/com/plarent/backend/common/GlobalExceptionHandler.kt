package com.plarent.backend.common

import com.plarent.backend.plant.PlantNotFoundException
import com.plarent.backend.user.InvalidCredentialsException
import com.plarent.backend.user.TooManyLoginAttemptsException
import com.plarent.backend.user.UsernameTakenException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import java.time.Instant

data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: Instant = Instant.now(),
)

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(PlantNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: PlantNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(status = HttpStatus.NOT_FOUND.value(), message = ex.message ?: "Not found"))

    @ExceptionHandler(UsernameTakenException::class)
    fun handleUsernameTaken(ex: UsernameTakenException): ResponseEntity<ErrorResponse> =
        error(HttpStatus.CONFLICT, ex)

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(ex: InvalidCredentialsException): ResponseEntity<ErrorResponse> =
        error(HttpStatus.UNAUTHORIZED, ex)

    @ExceptionHandler(TooManyLoginAttemptsException::class)
    fun handleTooManyLoginAttempts(ex: TooManyLoginAttemptsException): ResponseEntity<ErrorResponse> =
        error(HttpStatus.TOO_MANY_REQUESTS, ex)

    private fun error(status: HttpStatus, ex: Exception): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(status)
            .body(ErrorResponse(status = status.value(), message = ex.message ?: status.reasonPhrase))
}
