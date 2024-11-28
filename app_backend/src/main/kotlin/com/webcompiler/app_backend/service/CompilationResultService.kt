package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.api.user.request.CompilationResultSaveRequest
import com.webcompiler.app_backend.model.CompilationResult
import com.webcompiler.app_backend.repository.CompilationResultRepository
import com.webcompiler.app_backend.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CompilationResultService(
    @Autowired private val compilationResultRepository: CompilationResultRepository,
    @Autowired private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(CompilationResultService::class.java)

    fun saveCompilationResult(request: CompilationResultSaveRequest) {
        val user = findUserByName(request.username)
        val compilationResult = CompilationResult(
            code = request.code,
            output = request.output,
            appUser = user
        )
        compilationResultRepository.save(compilationResult)
        logger.info("Compilation result for user ${user.name} saved successfully.")
    }

    private fun findUserByName(username: String) =
        userRepository.findByName(username) ?: throw ResponseStatusException(
            HttpStatus.NOT_FOUND,
            "User $username not found"
        ).also { logger.error("User $username not found during compilation result save") }

    fun getCompilationHistoryByUsername(username: String): List<CompilationResult> {
        return userRepository.findByName(username)?.compilationResults.orEmpty()
    }

    fun deleteById(id: Long): Boolean {
        return if (compilationResultRepository.existsById(id)) {
            compilationResultRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
