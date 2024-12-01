package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.CompilationResultService
import com.webcompiler.app_backend.api.user.request.CompilationResultSaveRequest
import com.webcompiler.app_backend.model.AppUser
import com.webcompiler.app_backend.model.CompilationResult
import com.webcompiler.app_backend.repository.CompilationResultRepository
import com.webcompiler.app_backend.repository.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompilationResultServiceTest {

    private lateinit var compilationResultRepository: CompilationResultRepository
    private lateinit var userRepository: UserRepository
    private lateinit var target: CompilationResultService

    @BeforeEach
    fun setUp() {
        compilationResultRepository = mock(CompilationResultRepository::class.java)
        userRepository = mock(UserRepository::class.java)
        target = CompilationResultService(compilationResultRepository, userRepository)
    }

    @Test
    fun `should save compilation result successfully`() {
        val username = "testUser"
        val code = "int main() { return 0; }"
        val output = "Compilation successful"
        val user = AppUser(id = 1L, name = username, compilationResults = mutableListOf())
        val request = CompilationResultSaveRequest(username = username, code = code, output = output)

        `when`(userRepository.findByName(username)).thenReturn(user)
        `when`(compilationResultRepository.save(any(CompilationResult::class.java))).thenReturn(
            CompilationResult(
                code = code,
                output = output,
                appUser = user
            )
        )

        target.saveCompilationResult(request)

        verify(compilationResultRepository, times(1)).save(any(CompilationResult::class.java))
        verify(userRepository, times(1)).findByName(username)
    }

    @Test
    fun `should throw exception when user not found during compilation result save`() {
        val username = "nonExistentUser"
        val request = CompilationResultSaveRequest(username = username, code = "code", output = "output")

        `when`(userRepository.findByName(username)).thenReturn(null)

        val exception = assertFailsWith<ResponseStatusException> {
            target.saveCompilationResult(request)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
        assertEquals("User $username not found", exception.reason)

        verify(userRepository, times(1)).findByName(username)
        verify(compilationResultRepository, times(0)).save(any(CompilationResult::class.java))
    }

    @Test
    fun `should return compilation history for a user`() {
        val username = "testUser"
        val user = AppUser(
            id = 1L,
            name = username,
            compilationResults = mutableListOf(
                CompilationResult(
                    id = 1L,
                    code = "code",
                    output = "output",
                    appUser = mock()
                )
            )
        )

        `when`(userRepository.findByName(username)).thenReturn(user)
        val history = target.getCompilationHistoryByUsername(username)

        assertEquals(1, history.size)
        assertEquals("code", history[0].code)
        assertEquals("output", history[0].output)
    }

    @Test
    fun `should return empty list when user has no compilation history`() {
        val username = "testUser"
        val user = AppUser(id = 1L, name = username, compilationResults = mutableListOf())
        `when`(userRepository.findByName(username)).thenReturn(user)

        val history = target.getCompilationHistoryByUsername(username)

        assertTrue(history.isEmpty())
    }

    @Test
    fun `should delete compilation result by ID successfully`() {
        val id = 1L
        `when`(compilationResultRepository.existsById(id)).thenReturn(true)

        val result = target.deleteById(id)

        assertTrue(result)
        verify(compilationResultRepository, times(1)).deleteById(id)
    }

    @Test
    fun `should return false when trying to delete non-existent compilation result`() {
        val id = 1L
        `when`(compilationResultRepository.existsById(id)).thenReturn(false)

        val result = target.deleteById(id)

        assertFalse(result)
        verify(compilationResultRepository, times(0)).deleteById(id)
    }
}
