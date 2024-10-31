package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.api.register.RegisterApi
import com.webcompiler.app_backend.model.AppUserRole
import com.webcompiler.app_backend.model.User
import com.webcompiler.app_backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

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
            val passwordPart1 = encodedPassword.take(encodedPassword.length / 2) //TODO check
            val passwordPart2 = encodedPassword.drop(encodedPassword.length / 2)
            val user = User(
                name = username,
                email = email,
                passwordPart1 = passwordPart1,
                role = AppUserRole.ADMIN.role,
            )
            userRepository.save(user)
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
        val user = User(
            name = username,
            email = email,
            passwordPart1 = passwordPart1,
            role = AppUserRole.MODERATOR.role,
        )
        userRepository.save(user)
        vaultService.savePasswordPart2(username, passwordPart2)
    }

    @Transactional
    fun saveUser(
        username: String,
        email: String,
        password: String,
    ) {
        val encodedPassword = passwordEncoder.encode(password)
        val passwordPart1 = encodedPassword.take(password.length / 2)
        val passwordPart2 = encodedPassword.drop(password.length / 2)
        val user = User(
            name = username,
            email = email,
            passwordPart1 = passwordPart1,
            role = AppUserRole.USER.role,
        )
        userRepository.save(user)
        vaultService.savePasswordPart2(username, passwordPart2)
    }
}