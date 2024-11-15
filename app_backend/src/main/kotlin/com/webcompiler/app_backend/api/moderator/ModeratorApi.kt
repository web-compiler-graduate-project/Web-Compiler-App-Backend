package com.webcompiler.app_backend.api.moderator

import com.webcompiler.app_backend.api.moderator.request.AddTaskRequest
import com.webcompiler.app_backend.api.moderator.request.ModeratorUpdateRequest
import com.webcompiler.app_backend.api.moderator.response.TaskResponse
import com.webcompiler.app_backend.api.register.RegisterApi
import com.webcompiler.app_backend.config.CustomUserDetails
import com.webcompiler.app_backend.model.AppUserRole
import com.webcompiler.app_backend.service.TaskService
import com.webcompiler.app_backend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/moderator/")
class ModeratorApi(
    @Autowired private val userService: UserService,
    @Autowired private val taskService: TaskService
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

    @PostMapping("/add-task")
    fun saveTask(
        @RequestBody request: AddTaskRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails
    ): ResponseEntity<String> {
        val (title, taskDescription) = request
        logger.info("Attempting to save new task with title: $title")
        return try {
            taskService.saveTask(title, taskDescription, userDetails.username)
            logger.info("Task saved successfully with title: $title")
            ResponseEntity("Task added successfully", HttpStatus.CREATED)
        } catch (e: Exception) {
            logger.error("Failed to save task: ${e.message}", e)
            ResponseEntity("Error saving task: ${e.message}", HttpStatus.BAD_REQUEST)
        }
    }

    @GetMapping("/tasks")
    fun getModeratorTasks(@AuthenticationPrincipal userDetails: CustomUserDetails): ResponseEntity<List<TaskResponse>> {
        logger.info("Attempting to retrieve tasks for moderator: ${userDetails.username}")
        return try {
            val tasks = taskService.getAllModeratorTasks(userDetails.username).map { task ->
                val users = task.users
                    .filter { user ->
                        user.role == AppUserRole.USER.toString()
                    }
                    .map { user ->
                        user.name ?: throw ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Requested user account has not assigned username. Username cannot be null."
                        )
                    }
                TaskResponse( //TODO add solutions
                    title = task.title,
                    description = task.description,
                    id = task.id,
                    assignedUsersCount = users.size,
                    isEnabled = task.isEnabled,
                    assignedUsers = users,
                    solutionCount = task.taskSolutions.size
                )
            }
            logger.info("Successfully retrieved tasks for moderator: ${userDetails.username}")
            ResponseEntity(tasks, HttpStatus.OK)
        } catch (e: Exception) {
            logger.error("Failed to retrieve tasks for moderator: ${e.message}", e)
            ResponseEntity(HttpStatus.BAD_REQUEST)
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