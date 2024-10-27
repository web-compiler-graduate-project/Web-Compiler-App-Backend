package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.model.User
import com.webcompiler.app_backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class PasswordService(
    private val userRepository: UserRepository,
    private val restTemplate: RestTemplate
) {

    @Value("\${spring.cloud.vault.uri}")
    lateinit var vaultUrl: String

    fun saveUser(
        username: String,
        email: String,
        passwordPart1: String,
        passwordPart2: String,
    ) {
        val user = User(
            name = username,
            email = email,
            password = passwordPart2,
        )
        userRepository.save(user)
        val secret = mapOf("password_part1" to passwordPart1)
        val url = "$vaultUrl/v1/secret/myapp/$username"
        restTemplate.put(url, secret)
    }
}