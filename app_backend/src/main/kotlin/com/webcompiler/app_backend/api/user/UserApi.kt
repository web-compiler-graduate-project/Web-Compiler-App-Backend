package com.webcompiler.app_backend.api.user

import com.webcompiler.app_backend.api.register.RegisterApi
import com.webcompiler.app_backend.api.user.request.UserUpdateRequest
import com.webcompiler.app_backend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserApi(
    @Autowired private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(RegisterApi::class.java)

    @PutMapping("/update-user")
    fun updateUser(@RequestBody request: UserUpdateRequest): ResponseEntity<String> {
        val (currentUsername, newUsername, newEmail, currentPassword, newPassword) = request
        logger.info("Attempting to update user: $currentUsername with new email: $newEmail")
        return try {
            userService.updateUser(
                currentUsername,
                newUsername,
                newEmail,
                currentPassword,
                newPassword
            )
            logger.info("User updated successfully: $currentUsername")
            ResponseEntity("User updated successfully", HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Failed to update user: ${e.message}", e)
            ResponseEntity("Error updating user: ${e.message}", HttpStatus.BAD_REQUEST)
        }
    }
}
