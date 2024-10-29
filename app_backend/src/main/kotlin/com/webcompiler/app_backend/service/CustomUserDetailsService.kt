package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.repository.UserRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

@Service
class CustomUserDetailsService(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val vaultService: VaultService,
    @Autowired private val userService: UserService,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByName(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        val passwordPart2 = vaultService.getPasswordPart2ByUsername(username)
        return User
            .withUsername(user.name)
            .password(user.passwordPart1 + passwordPart2)
            .roles(user.role)
            .build()
    }

    @PostConstruct
    fun initAdminUser() {
        val adminUsername = System.getenv("APP_ADMIN_LOGIN") ?: "admin"
        val adminEmail = System.getenv("APP_ADMIN_EMAIL") ?: "admin@admin.com"
        val adminPassword = System.getenv("APP_ADMIN_PASSWORD") ?: "admin"
        userService.saveAdmin(adminUsername, adminEmail, adminPassword)
    }
}
