package com.webcompiler.app_backend

import com.webcompiler.app_backend.config.CustomUserDetails
import com.webcompiler.app_backend.repository.UserRepository
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val vaultService: VaultService,
    @Autowired private val userService: UserService,
) : UserDetailsService {

    override fun loadUserByUsername(username: String): CustomUserDetails {
        val user = userRepository.findByName(username)
            ?: throw UsernameNotFoundException("User not found with username: $username")
        val passwordPart2 = vaultService.getPasswordPart2ByUsername(username)
        return CustomUserDetails(user, passwordPart2)
    }

    @PostConstruct
    fun initAdminUser() {
        val adminUsername = System.getenv("APP_ADMIN_LOGIN") ?: "admin"
        val adminEmail = System.getenv("APP_ADMIN_EMAIL") ?: "admin@admin.com"
        val adminPassword = System.getenv("APP_ADMIN_PASSWORD") ?: "admin"
        userService.saveAdmin(adminUsername, adminEmail, adminPassword)
    }
}
