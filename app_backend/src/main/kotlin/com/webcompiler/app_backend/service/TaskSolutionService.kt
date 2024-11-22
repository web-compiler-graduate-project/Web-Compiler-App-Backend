package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.model.TaskSolution
import com.webcompiler.app_backend.repository.TaskRepository
import com.webcompiler.app_backend.repository.TaskSolutionRepository
import com.webcompiler.app_backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TaskSolutionService(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val taskSolutionRepository: TaskSolutionRepository,
    @Autowired private val taskRepository: TaskRepository
) {

    @Transactional
    fun submitSolution(username: String) {
        val user =
            userRepository.findByName(username) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        val solution = TaskSolution(
            task = user.tasks.maxByOrNull { it.id } ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No assigned task found for given user."
            ),
            compilationResult = user.compilationResults.maxByOrNull { it.id } ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "No assigned compilation result found for given user registered in task."
            )
        )
        user.taskSolutions.add(solution)
        solution.users.add(user)
        taskSolutionRepository.save(solution)
    }

    fun getSolutionsByTaskId(taskId: Long): List<TaskSolution> =
        taskRepository.findByIdOrNull(taskId)?.taskSolutions ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Task with id $taskId not found."
        )

    fun gradeSolution(solutionId: Long, grade: Int, comments: String) {
        val solution = taskSolutionRepository.findByIdOrNull(solutionId) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "Task solution with id $solutionId not found."
        )
        taskSolutionRepository.save(
            solution.copy(
                comments = comments,
                grade = grade
            )
        )
    }
}