package com.webcompiler.app_backend.api.moderator

import com.webcompiler.app_backend.api.moderator.request.ModeratorUpdateRequest
import com.webcompiler.app_backend.api.register.RegisterApi
import com.webcompiler.app_backend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/moderator/")
class ModeratorApi(
    @Autowired private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(RegisterApi::class.java)

    @PutMapping("/update-account")
    fun updateModerator(@RequestBody request: ModeratorUpdateRequest): ResponseEntity<String> {
        val (currentUsername, newUsername, newEmail, currentPassword, newPassword) = request
        logger.info("Attempting to update moderator: $currentUsername with new email: $newEmail")
        return try {
            userService.updateUser(
                currentUsername,
                newUsername,
                newEmail,
                currentPassword,
                newPassword
            )
            logger.info("Moderator updated successfully: $currentUsername")
            ResponseEntity("Moderator updated successfully", HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Failed to update moderator: ${e.message}", e)
            ResponseEntity("Error updating moderator: ${e.message}", HttpStatus.BAD_REQUEST)
        }
    }
}