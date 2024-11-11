package com.webcompiler.app_backend.api.admin

import com.webcompiler.app_backend.api.admin.request.AdminUpdateRequest
import com.webcompiler.app_backend.api.admin.request.ModeratorRegistrationRequest
import com.webcompiler.app_backend.api.moderator.request.ModeratorUpdateRequest
import com.webcompiler.app_backend.api.register.RegisterApi
import com.webcompiler.app_backend.api.user.request.UserUpdateRequest
import com.webcompiler.app_backend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class AdminApi(
    @Autowired private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(RegisterApi::class.java)

    @PostMapping("/register-moderator")
    fun registerModerator(@RequestBody request: ModeratorRegistrationRequest): ResponseEntity<String> {
        val (username, email, password) = request
        logger.info("Attempting to register moderator: $username with email: $email")
        userService.saveModerator(
            username,
            email,
            password
        )
        logger.info("User registered successfully: $username")
        return ResponseEntity("Moderator created successfully", HttpStatus.CREATED)
    }

    @DeleteMapping("/delete-moderator")
    fun deleteModerator(@RequestParam userName: String): ResponseEntity<String> {
        logger.info("Attempting to delete moderator: $userName")
        return try {
            userService.deleteUser(userName)
            logger.info("Moderator deleted successfully: $userName")
            ResponseEntity("Moderator deleted successfully", HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Failed to delete moderator: ${e.message}", e)
            ResponseEntity("Error deleting moderator: ${e.message}", HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/update-account")
    fun updateUser(@RequestBody request: AdminUpdateRequest): ResponseEntity<String> {
        val (currentUsername, newUsername, newEmail, currentPassword, newPassword) = request
        logger.info("Attempting to update admin account: $currentUsername")
        return try {
            userService.updateUser(
                currentUsername,
                newUsername,
                newEmail,
                currentPassword,
                newPassword
            )
            logger.info("Admin account updated successfully: $currentUsername")
            ResponseEntity("Admin account updated successfully", HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Failed to update admin account: ${e.message}", e)
            ResponseEntity("Error updating admin account: ${e.message}", HttpStatus.BAD_REQUEST)
        }
    }
}