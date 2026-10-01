package com.plarent.backend.plant

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Testcontainers
@SpringBootTest
class PlantRepositoryIntegrationTest {

    @Autowired
    lateinit var plantRepository: PlantRepository

    @Test
    fun `saves and reloads a plant`() {
        val saved = plantRepository.save(Plant(name = "Monstera", species = "Monstera deliciosa"))

        val found = plantRepository.findById(saved.id)

        assertTrue(found.isPresent)
        assertEquals("Monstera", found.get().name)
    }

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine").apply {
            withDatabaseName("plarent")
            withUsername("plarent")
            withPassword("plarent")
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}
