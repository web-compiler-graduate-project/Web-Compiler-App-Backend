package com.webcompiler.app_backend.api.user

import com.webcompiler.app_backend.api.register.RegisterApi
import com.webcompiler.app_backend.api.user.request.CompilationResultSaveRequest
import com.webcompiler.app_backend.api.user.request.UserUpdateRequest
import com.webcompiler.app_backend.api.user.response.UserCompilationHistoryResponse
import com.webcompiler.app_backend.config.CustomUserDetails
import com.webcompiler.app_backend.service.CompilationResultService
import com.webcompiler.app_backend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/user")
class UserApi(
    @Autowired private val userService: UserService,
    @Autowired private val compilationResultService: CompilationResultService
) {

    private val logger = LoggerFactory.getLogger(RegisterApi::class.java)

    @PutMapping("/update-account")
    fun updateUser(
        @RequestBody request: UserUpdateRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<String> {
        val (currentUsername, newUsername, newEmail, currentPassword, newPassword) = request
        if (userDetails.username != currentUsername) {
            logger.warn("Username mismatch: session user is ${userDetails.username}, but the request attempted to update $currentUsername")
            return ResponseEntity("Unauthorized to update this account", HttpStatus.FORBIDDEN)
        }
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

    @PostMapping("/save-compilation-result")
    fun saveCompilationResult(@RequestBody request: CompilationResultSaveRequest): ResponseEntity<String> {
        logger.info("Request to save compilation result for user: ${request.username}")
        return try {
            compilationResultService.saveCompilationResult(request)
            logger.info("Compilation result saved for user: ${request.username}")
            ResponseEntity("Compilation result saved successfully", HttpStatus.CREATED)
        } catch (ex: ResponseStatusException) {
            logger.error("Error saving compilation result for ${request.username}: ${ex.message}", ex)
            ResponseEntity("Error: ${ex.reason}", ex.statusCode)
        } catch (ex: Exception) {
            logger.error("Unexpected error saving compilation result for ${request.username}: ${ex.message}", ex)
            ResponseEntity("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @GetMapping("/user-compilation-history")
    fun getUserCompilationHistory(@AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<List<UserCompilationHistoryResponse>> {
        val username = userDetails.username
        logger.info("Fetching compilation history for user: $username")
        return try {
            val history = compilationResultService.getCompilationHistoryByUsername(username)

            if (history.isEmpty()) {
                logger.info("No compilation history found for user: $username")
                ResponseEntity(emptyList(), HttpStatus.NOT_FOUND)
            } else {
                val response = history.map { result ->
                    UserCompilationHistoryResponse(
                        code = result.code ?: "empty",
                        output = result.output ?: "empty",
                        id = result.id.toString()
                    )
                }
                logger.info("Compilation history retrieved for user: $username")
                ResponseEntity(response, HttpStatus.OK)
            }
        } catch (ex: Exception) {
            logger.error("Error retrieving compilation history for user: $username", ex)
            ResponseEntity(emptyList(), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @DeleteMapping("/user-compilation-history/{id}")
    fun deleteCompilationResult(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<String> {
        val username = userDetails.username
        logger.info("Request to delete compilation result with ID $id for user: $username")
        return try {
            val deleted = compilationResultService.deleteById(id)
            if (deleted) {
                logger.info("Compilation result with ID $id deleted for user: $username")
                ResponseEntity("Compilation result deleted successfully", HttpStatus.OK)
            } else {
                logger.warn("Compilation result with ID $id not found for user: $username")
                ResponseEntity("Compilation result not found", HttpStatus.NOT_FOUND)
            }
        } catch (ex: Exception) {
            logger.error("Error deleting compilation result with ID $id for user: $username", ex)
            ResponseEntity("Error deleting compilation result", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}
