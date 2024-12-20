package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.model.AppUser
import com.webcompiler.app_backend.model.AppUserRole
import com.webcompiler.app_backend.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.server.ResponseStatusException
import java.util.*

class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    @Mock
    private lateinit var vaultService: VaultService

    @InjectMocks
    private lateinit var target: UserService

    init {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `should save admin successfully when user does not exist`() {
        val username = "admin"
        val email = "admin@example.com"
        val password = "password"
        val encodedPassword = "encodedPassword123"

        `when`(userRepository.existsByName(username)).thenReturn(false)
        `when`(passwordEncoder.encode(password)).thenReturn(encodedPassword)

        target.saveAdmin(username, email, password)

        verify(userRepository).save(
            AppUser(
                name = username,
                email = email,
                passwordPart1 = encodedPassword.take(encodedPassword.length / 2),
                role = AppUserRole.ADMIN.role
            )
        )
        verify(vaultService).savePasswordPart2(username, encodedPassword.drop(encodedPassword.length / 2))
    }

    @Test
    fun `should skip saving admin if already exists`() {
        val username = "admin"

        `when`(userRepository.existsByName(username)).thenReturn(true)

        target.saveAdmin(username, "admin@example.com", "password")

        verify(userRepository, never()).save(any())
        verify(vaultService, never()).savePasswordPart2(any(), any())
    }

    @Test
    fun `should save moderator successfully`() {
        val username = "moderator"
        val email = "moderator@example.com"
        val password = "password"
        val encodedPassword = "encodedPassword123"

        `when`(passwordEncoder.encode(password)).thenReturn(encodedPassword)

        target.saveModerator(username, email, password)

        verify(userRepository).save(
            AppUser(
                name = username,
                email = email,
                passwordPart1 = encodedPassword.take(encodedPassword.length / 2),
                role = AppUserRole.MODERATOR.role
            )
        )
        verify(vaultService).savePasswordPart2(username, encodedPassword.drop(encodedPassword.length / 2))
    }

    @Test
    fun `should update user successfully`() {
        val currentUsername = "user"
        val newUsername = "newUser"
        val newEmail = "newuser@example.com"
        val currentPassword = "currentPassword"
        val newPassword = "newPassword"
        val user = AppUser(name = currentUsername, email = "user@example.com", passwordPart1 = "part1")
        val encodedPassword = "newEncodedPassword123"

        `when`(userRepository.findByName(currentUsername)).thenReturn(user)
        `when`(vaultService.getPasswordPart2ByUsername(currentUsername)).thenReturn("part2")
        `when`(passwordEncoder.matches(currentPassword, "part1part2")).thenReturn(true)
        `when`(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword)

        target.updateUser(currentUsername, newUsername, newEmail, currentPassword, newPassword)

        verify(vaultService).savePasswordPart2(currentUsername, encodedPassword.drop(encodedPassword.length / 2))
        verify(userRepository).save(
            user.copy(
                name = newUsername,
                email = newEmail,
                passwordPart1 = encodedPassword.take(encodedPassword.length / 2)
            )
        )
    }

    @Test
    fun `should throw exception if current password is incorrect`() {
        val currentUsername = "user"
        val currentPassword = "currentPassword"
        val newPassword = "newPassword"
        val user = AppUser(name = currentUsername, email = "user@example.com", passwordPart1 = "part1")

        `when`(userRepository.findByName(currentUsername)).thenReturn(user)
        `when`(vaultService.getPasswordPart2ByUsername(currentUsername)).thenReturn("part2")
        `when`(passwordEncoder.matches(currentPassword, "part1part2")).thenReturn(false)

        val exception = assertThrows<ResponseStatusException> {
            target.updateUser(currentUsername, null, null, currentPassword, newPassword)
        }

        assertEquals(HttpStatus.BAD_REQUEST, exception.statusCode)
        assertEquals("Provided password does not match our records. Please check your password and try again.", exception.reason)
    }

    @Test
    fun `should delete user successfully`() {
        val username = "user"

        target.deleteUser(username)

        verify(userRepository).deleteByName(username)
        verify(vaultService).deletePasswordByUsername(username)
    }

    @Test
    fun `should update account status successfully`() {
        val id = 1L
        val isEnabled = true
        val user = AppUser(id = id, name = "user", role = AppUserRole.USER.role)

        `when`(userRepository.findById(id)).thenReturn(Optional.of(user))

        target.updateAccountStatus(id, isEnabled)

        verify(userRepository).save(user.copy(isEnabled = isEnabled))
    }

    @Test
    fun `should throw exception if trying to change admin status`() {
        val id = 1L
        val isEnabled = false
        val user = AppUser(id = id, name = "admin", role = AppUserRole.ADMIN.role)

        `when`(userRepository.findById(id)).thenReturn(Optional.of(user))

        val exception = assertThrows<ResponseStatusException> {
            target.updateAccountStatus(id, isEnabled)
        }

        assertEquals(HttpStatus.FORBIDDEN, exception.statusCode)
        assertEquals("Admin account status cannot be changed.", exception.reason)
    }
}
