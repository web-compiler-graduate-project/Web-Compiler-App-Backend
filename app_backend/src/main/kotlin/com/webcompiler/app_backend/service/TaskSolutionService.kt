package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.model.TaskSolution
import com.webcompiler.app_backend.repository.TaskSolutionRepository
import com.webcompiler.app_backend.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class TaskSolutionService(
    @Autowired private val userRepository: UserRepository,
    @Autowired private val taskSolutionRepository: TaskSolutionRepository
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
}