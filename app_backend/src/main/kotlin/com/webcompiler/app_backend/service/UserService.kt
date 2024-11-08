package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.api.register.RegisterApi
import com.webcompiler.app_backend.model.AppUserRole
import com.webcompiler.app_backend.model.AppUser
import com.webcompiler.app_backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val vaultService: VaultService,
) {

    private val logger = LoggerFactory.getLogger(RegisterApi::class.java)

    @Transactional
    fun saveAdmin(
        username: String,
        email: String,
        password: String,
    ) {
        if (userRepository.existsByName(username)) {
            logger.info("Admin account with provided credentials already exists. Skipping admin initialization...")
        } else {
            val encodedPassword = passwordEncoder.encode(password)
            val passwordPart1 = encodedPassword.take(encodedPassword.length / 2)
            val passwordPart2 = encodedPassword.drop(encodedPassword.length / 2)
            val appUser = AppUser(
                name = username,
                email = email,
                passwordPart1 = passwordPart1,
                role = AppUserRole.ADMIN.role,
            )
            userRepository.save(appUser)
            vaultService.savePasswordPart2(username, passwordPart2)
            logger.info("Admin user with login $username initialized correctly.")
        }
    }


    @Transactional
    fun saveModerator(
        username: String,
        email: String,
        password: String,
    ) {
        val encodedPassword = passwordEncoder.encode(password)
        val passwordPart1 = encodedPassword.take(encodedPassword.length / 2)
        val passwordPart2 = encodedPassword.drop(encodedPassword.length / 2)
        val appUser = AppUser(
            name = username,
            email = email,
            passwordPart1 = passwordPart1,
            role = AppUserRole.MODERATOR.role,
        )
        userRepository.save(appUser)
        vaultService.savePasswordPart2(username, passwordPart2)
    }

    @Transactional
    fun saveUser(
        username: String,
        email: String,
        password: String,
    ) {
        val encodedPassword = passwordEncoder.encode(password)
        val passwordPart1 = encodedPassword.take(encodedPassword.length / 2)
        val passwordPart2 = encodedPassword.drop(encodedPassword.length / 2)
        val appUser = AppUser(
            name = username,
            email = email,
            passwordPart1 = passwordPart1,
            role = AppUserRole.USER.role,
        )
        userRepository.save(appUser)
        vaultService.savePasswordPart2(username, passwordPart2)
    }

    @Transactional
    fun updateUser(
        currentUsername: String,
        newUsername: String?,
        newEmail: String?,
        currentPassword: String?,
        newPassword: String?
    ) {
        val user = userRepository.findByName(currentUsername) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "User not found"
        )
        val newPasswordPart1 = handlePasswordUpdate(user, currentPassword, newPassword, newUsername)
        updateUserDetails(user, newUsername, newEmail, newPasswordPart1)
    }

    private fun handlePasswordUpdate(
        user: AppUser,
        currentPassword: String?,
        newPassword: String?,
        newUsername: String?
    ): String? {
        if (currentPassword == null || newPassword == null || newPassword.length <= 7) {
            return null
        }
        val fullPassword = user.passwordPart1 + vaultService.getPasswordPart2ByUsername(user.name!!)
        if (currentPassword != fullPassword) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Provided password does not match our records. Please check your password and try again."
            )
        }
        val encodedPassword = passwordEncoder.encode(newPassword)
        vaultService.savePasswordPart2(newUsername ?: user.name, encodedPassword.drop(encodedPassword.length / 2))
        return encodedPassword.take(encodedPassword.length / 2)
    }

    private fun updateUserDetails(user: AppUser, newUsername: String?, newEmail: String?, newPasswordPart1: String?) {
        newUsername?.let {
            val passwordPart2 = vaultService.getPasswordPart2ByUsername(user.name!!)
            vaultService.deletePasswordByUsername(user.name)
            vaultService.savePasswordPart2(newUsername, passwordPart2)
        }
        userRepository.save(
            user.copy(
                name = newUsername ?: user.name,
                email = newEmail ?: user.email,
                passwordPart1 = newPasswordPart1 ?: user.passwordPart1
            )
        )
    }

    @Transactional
    fun deleteUser(username: String) {
        userRepository.deleteByName(username)
        vaultService.deletePasswordByUsername(username)
    }
}