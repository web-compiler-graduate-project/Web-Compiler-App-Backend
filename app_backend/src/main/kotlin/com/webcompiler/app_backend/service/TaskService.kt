package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.model.Task
import com.webcompiler.app_backend.repository.TaskRepository
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

    fun getAllAvailableTasks(isEnabled: Boolean): List<Task> =
        taskRepository.findAllByIsEnabled(isEnabled)

    fun getAllModeratorTasks(moderatorUsername: String): List<Task> {
        val moderator = userRepository.findByName(moderatorUsername) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Moderator with provided username $moderatorUsername not found."
        )
        return taskRepository.findByUsersContaining(moderator)
    }

    fun deleteTask(taskId: Long) {
        val task = taskRepository.findById(taskId).orElseThrow {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found")
        }
        taskRepository.delete(task)
    }

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
}