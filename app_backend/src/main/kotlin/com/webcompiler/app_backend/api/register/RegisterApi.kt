package com.webcompiler.app_backend.api.register

import com.webcompiler.app_backend.api.register.request.UserRegistrationRequest
import com.webcompiler.app_backend.service.PasswordService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/register")
class RegisterApi(
    @Autowired private val passwordService: PasswordService,
) {

    private val logger = LoggerFactory.getLogger(RegisterApi::class.java)

    @PostMapping
    fun createUser(@RequestBody request: UserRegistrationRequest): ResponseEntity<String> {
        val (username, email, password) = request

        logger.info("Attempting to register user: $username with email: $email")

        val passwordPart1 = password.take(password.length / 2)
        val passwordPart2 = password.drop(password.length / 2)

        passwordService.saveUser(
            username,
            email,
            passwordPart1,
            passwordPart2,
        )

        logger.info("User registered successfully: $username")
        return ResponseEntity("User created successfully", HttpStatus.CREATED)
    }

    //TODO username/email verifier
}
