package com.webcompiler.app_backend.api.user

import com.webcompiler.app_backend.api.register.RegisterApi
import com.webcompiler.app_backend.api.user.request.UserUpdateRequest
import com.webcompiler.app_backend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserApi(
    @Autowired private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(RegisterApi::class.java)

    @PostMapping("/update-account")
    fun updateUser(@RequestBody request: UserUpdateRequest): ResponseEntity<String> {
        val (currentUsername, newUsername, newEmail, currentPassword, newPassword) = request
        logger.info("Attempting to update user account: $currentUsername")
        return try {
            userService.updateUser(
                currentUsername,
                newUsername,
                newEmail,
                currentPassword,
                newPassword
            )
            logger.info("User account updated successfully: $currentUsername")
            ResponseEntity("User account updated successfully", HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Failed to update user account: ${e.message}", e)
            ResponseEntity("Error updating user account: ${e.message}", HttpStatus.BAD_REQUEST)
        }
    }
}
