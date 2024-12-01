package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.CustomUserDetailsService
import com.webcompiler.app_backend.UserService
import com.webcompiler.app_backend.VaultService
import com.webcompiler.app_backend.model.AppUser
import com.webcompiler.app_backend.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.security.core.userdetails.UsernameNotFoundException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class CustomUserDetailsServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var vaultService: VaultService
    private lateinit var userService: UserService
    private lateinit var target: CustomUserDetailsService

    @BeforeEach
    fun setUp() {
        userRepository = mock(UserRepository::class.java)
        vaultService = mock(VaultService::class.java)
        userService = mock(UserService::class.java)
        target = CustomUserDetailsService(userRepository, vaultService, userService)
    }

    @Test
    fun `should load user by username successfully`() {
        val username = "testUser"
        val user = AppUser(id = 1L, name = username, email = "test@user.com", compilationResults = mutableListOf())
        val passwordPart2 = "password_part2"

        `when`(userRepository.findByName(username)).thenReturn(user)
        `when`(vaultService.getPasswordPart2ByUsername(username)).thenReturn(passwordPart2)

        val userDetails = target.loadUserByUsername(username)

        assertNotNull(userDetails)
        assertEquals(userDetails.username, username)
        assertEquals(userDetails.password, passwordPart2)
    }

    @Test
    fun `should throw exception when user not found`() {
        val username = "nonExistentUser"
        `when`(userRepository.findByName(username)).thenReturn(null)

        val exception = assertFailsWith<UsernameNotFoundException> {
            target.loadUserByUsername(username)
        }

        assertTrue(exception.message!!.contains("User not found with username: $username"))
    }
}
