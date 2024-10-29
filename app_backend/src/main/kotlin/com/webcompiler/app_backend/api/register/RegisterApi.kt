package com.webcompiler.app_backend.api.register

import com.webcompiler.app_backend.api.register.request.UserRegistrationRequest
import com.webcompiler.app_backend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/register")
class RegisterApi(
    @Autowired private val userService: UserService,
) {

    private val logger = LoggerFactory.getLogger(RegisterApi::class.java)

    @PostMapping
    fun createUser(@RequestBody request: UserRegistrationRequest): ResponseEntity<String> {
        val (username, email, password) = request

        logger.info("Attempting to register user: $username with email: $email")

        userService.saveUser(
            username,
            email,
            password
        )

        logger.info("User registered successfully: $username")
        return ResponseEntity("User created successfully", HttpStatus.CREATED)
    }
}
