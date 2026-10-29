package com.plarent.backend.common

import com.plarent.backend.plant.InvalidPlantImageException
import com.plarent.backend.plant.PlantImageNotFoundException
import com.plarent.backend.plant.PlantNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.multipart.MaxUploadSizeExceededException
import java.time.Instant

data class ErrorResponse(
    val status: Int,
    val message: String,
    val timestamp: Instant = Instant.now(),
)

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(PlantNotFoundException::class, PlantImageNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(ex: RuntimeException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(status = HttpStatus.NOT_FOUND.value(), message = ex.message ?: "Not found"))

    @ExceptionHandler(InvalidPlantImageException::class, MaxUploadSizeExceededException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleInvalidImage(ex: RuntimeException): ResponseEntity<ErrorResponse> {
        val message = if (ex is InvalidPlantImageException) {
            ex.message ?: "Invalid image"
        } else {
            "Image is larger than 5 MB"
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(status = HttpStatus.BAD_REQUEST.value(), message = message))
    }
}
