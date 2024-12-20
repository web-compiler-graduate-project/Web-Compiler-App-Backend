package com.webcompiler.app_backend.api.admin

import com.webcompiler.app_backend.api.admin.request.AdminUpdateRequest
import com.webcompiler.app_backend.api.admin.request.ModeratorRegistrationRequest
import com.webcompiler.app_backend.api.admin.response.TaskResponse
import com.webcompiler.app_backend.api.admin.response.UserResponse
import com.webcompiler.app_backend.api.register.RegisterApi
import com.webcompiler.app_backend.model.AppUserRole
import com.webcompiler.app_backend.service.TaskService
import com.webcompiler.app_backend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/admin")
class AdminApi(
    @Autowired private val userService: UserService,
    @Autowired private val taskService: TaskService
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

    @GetMapping("/moderators")
    fun getModerators(): ResponseEntity<List<UserResponse>> {
        logger.info("Request to fetch moderators received")
        return try {
            val moderators = userService.getModerators().map { user ->
                UserResponse(
                    username = user.name,
                    email = user.email,
                    isEnabled = user.isEnabled,
                    id = user.id.toString()
                )
            }
            logger.info("Successfully fetched ${moderators.size} moderators")
            ResponseEntity.ok(moderators)
        } catch (ex: DataAccessException) {
            logger.error("Database error while fetching moderators", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList())
        } catch (ex: Exception) {
            logger.error("Unexpected error while fetching moderators", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList())
        }
    }

    @DeleteMapping("/moderators/{id}")
    fun deleteModerator(@PathVariable id: Long): ResponseEntity<Void> {
        logger.info("Request to delete moderator with ID: $id")
        return try {
            userService.deleteModeratorById(id)
            logger.info("Successfully deleted moderator with ID: $id")
            ResponseEntity.noContent().build()
        } catch (ex: DataAccessException) {
            logger.error("Database error while deleting moderator with ID: $id", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        } catch (ex: Exception) {
            logger.error("Unexpected error while deleting moderator with ID: $id", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PutMapping("/moderators/{id}/block")
    fun updateModeratorStatus(@PathVariable id: Long, @RequestParam isEnabled: Boolean): ResponseEntity<Void> {
        logger.info("Request to update 'isEnabled' status for moderator with ID: $id to $isEnabled")
        return try {
            userService.updateAccountStatus(id, isEnabled)
            logger.info("Successfully updated 'isEnabled' status for moderator with ID: $id")
            ResponseEntity.noContent().build()
        } catch (ex: DataAccessException) {
            logger.error("Database error while updating status for moderator with ID: $id", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        } catch (ex: Exception) {
            logger.error("Unexpected error while updating status for moderator with ID: $id", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/users")
    fun getUsers(): ResponseEntity<List<UserResponse>> {
        logger.info("Request to fetch users received")
        return try {
            val users = userService.getUsers().map { user ->
                UserResponse(
                    username = user.name,
                    email = user.email,
                    isEnabled = user.isEnabled,
                    id = user.id.toString()
                )
            }
            logger.info("Successfully fetched ${users.size} users")
            ResponseEntity.ok(users)
        } catch (ex: DataAccessException) {
            logger.error("Database error while fetching users", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList())
        } catch (ex: Exception) {
            logger.error("Unexpected error while fetching users", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(emptyList())
        }
    }

    @DeleteMapping("/users/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        logger.info("Request to delete user with ID: $id")
        return try {
            userService.deleteUserById(id)
            logger.info("Successfully deleted user with ID: $id")
            ResponseEntity.noContent().build()
        } catch (ex: DataAccessException) {
            logger.error("Database error while deleting user with ID: $id", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        } catch (ex: Exception) {
            logger.error("Unexpected error while deleting user with ID: $id", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PutMapping("/users/{id}/block")
    fun updateUserStatus(@PathVariable id: Long, @RequestParam isEnabled: Boolean): ResponseEntity<Void> {
        logger.info("Request to update 'isEnabled' status for user with ID: $id to $isEnabled")
        return try {
            userService.updateAccountStatus(id, isEnabled)
            logger.info("Successfully updated 'isEnabled' status for user with ID: $id")
            ResponseEntity.noContent().build()
        } catch (ex: DataAccessException) {
            logger.error("Database error while updating status for user with ID: $id", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        } catch (ex: Exception) {
            logger.error("Unexpected error while updating status for user with ID: $id", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @GetMapping("/tasks")
    fun getAllTasks(): ResponseEntity<List<TaskResponse>> {
        logger.info("Request to fetch all tasks.")
        return try {
            ResponseEntity.ok(
                taskService.getAllTasks().map { task ->
                    val users = task.users
                    .filter { user ->
                        user.role == AppUserRole.USER.toString()
                    }
                    val moderator = task.getModeratorTaskOwner()
                    TaskResponse(
                        title = task.title,
                        description = task.description,
                        id = task.id,
                        assignedUsersCount = users.size,
                        solutionCount = task.taskSolutions.size,
                        isEnabled = task.isEnabled,
                        moderatorUserName = moderator.name,
                        moderatorEmail = moderator.email
                    )
                }
            )
        } catch (ex: Exception) {
            logger.error("Unexpected error while fetching tasks for admin panel: ", ex)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @DeleteMapping("/tasks/{taskId}")
    fun deleteTask(@PathVariable taskId: Long): ResponseEntity<String> {
        logger.info("Attempting to delete task with ID: $taskId")
        return try {
            taskService.deleteTask(taskId)
            logger.info("Task deleted successfully with ID: $taskId")
            ResponseEntity("Task deleted successfully", HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Failed to delete task: ${e.message}", e)
            ResponseEntity("Error deleting task: ${e.message}", HttpStatus.BAD_REQUEST)
        }
    }

    @PutMapping("/tasks/{taskId}/block")
    fun toggleTaskStatus(@PathVariable taskId: Long): ResponseEntity<String> {
        logger.info("Attempting to toggle block status for task with ID: $taskId")
        return try {
            taskService.toggleTaskStatus(taskId)
            logger.info("Task status toggled successfully for ID: $taskId")
            ResponseEntity("Task status updated successfully", HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Failed to toggle task status: ${e.message}", e)
            ResponseEntity("Error toggling task status: ${e.message}", HttpStatus.BAD_REQUEST)
        }
    }
}