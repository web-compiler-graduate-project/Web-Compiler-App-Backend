package com.webcompiler.app_backend

import com.webcompiler.app_backend.model.AppUser
import com.webcompiler.app_backend.model.Task
import com.webcompiler.app_backend.model.TaskSolution
import com.webcompiler.app_backend.repository.TaskRepository
import com.webcompiler.app_backend.repository.TaskSolutionRepository
import com.webcompiler.app_backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TaskService(
    @Autowired private val taskRepository: TaskRepository,
    @Autowired private val userRepository: UserRepository
) {

    @Transactional
    fun saveTask(
        title: String,
        taskDescription: String,
        moderatorUsername: String,
    ) {
        val user = userRepository.findByName(moderatorUsername) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Moderator with provided username $moderatorUsername not found."
        )
        val task = Task(
            title = title,
            description = taskDescription
        )
        task.users.add(user)
        user.tasks.add(task)
        taskRepository.save(task)
    }

    fun getAllAvailableTasks(username: String): List<Task> {
        val user =
            userRepository.findByName(username) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        return taskRepository.findByIsEnabledTrueAndUsersNotContaining(user)
    }

    fun getAllModeratorTasks(moderatorUsername: String): List<Task> {
        val moderator = userRepository.findByName(moderatorUsername) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Moderator with provided username $moderatorUsername not found."
        )
        return taskRepository.findByUsersContaining(moderator)
    }

    @Transactional
    fun deleteTask(taskId: Long) {
        val task = taskRepository.findById(taskId).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
        }
        taskRepository.delete(task)
    }

    @Transactional
    fun toggleTaskStatus(taskId: Long) {
        val task = taskRepository.findById(taskId).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
        }
        taskRepository.save(
            task.copy(
                isEnabled = !task.isEnabled
            )
        )
    }

    @Transactional
    fun registerUserInTask(taskId: Long, username: String) {
        val task = taskRepository.findById(taskId).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
        }
        val user =
            userRepository.findByName(username) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        task.users.add(user)
        user.tasks.add(task)
        taskRepository.save(task)
    }

    fun isTaskAssignedToUser(username: String): Boolean {
        val user =
            userRepository.findByName(username) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        return user.tasks.size > user.taskSolutions.size
    }

    fun getAssignedTaskDetails(username: String): Task {
        val user =
            userRepository.findByName(username) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        if (user.tasks.size > user.taskSolutions.size) {
            val assignedTask = user.tasks.filter { task ->
                !user.taskSolutions.any { it.task == task }
            }.minByOrNull { it.id }
            return assignedTask ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No assigned task found")
        }
        throw ResponseStatusException(HttpStatus.NOT_FOUND, "No task assigned to the user")
    }

    fun getAllTasks(): List<Task> =
        taskRepository.findAll()
}