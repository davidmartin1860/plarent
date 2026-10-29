package com.plarent.backend.plant

import com.plarent.backend.PostgresIntegrationTest
import com.plarent.backend.user.User
import com.plarent.backend.user.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class PlantRepositoryIntegrationTest : PostgresIntegrationTest() {

    @Autowired
    lateinit var plantRepository: PlantRepository

    @Autowired
    lateinit var userRepository: UserRepository

    private fun newUser() = userRepository.save(User(username = "user-${UUID.randomUUID().toString().take(8)}", passwordHash = "x"))

    @Test
    fun `saves and reloads a plant for its owner only`() {
        val owner = newUser()
        val other = newUser()
        val saved = plantRepository.save(Plant(ownerId = owner.id, name = "Monstera", species = "Monstera deliciosa"))

        val found = plantRepository.findByIdAndOwnerId(saved.id, owner.id)

        assertNotNull(found)
        assertEquals("Monstera", found.name)
        assertNull(plantRepository.findByIdAndOwnerId(saved.id, other.id))
        assertEquals(listOf(saved.id), plantRepository.findAllByOwnerIdOrderByCreatedAtDesc(owner.id).map { it.id })
    }
}
