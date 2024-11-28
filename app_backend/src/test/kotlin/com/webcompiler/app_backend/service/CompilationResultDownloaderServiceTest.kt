package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.model.CompilationResult
import com.webcompiler.app_backend.repository.CompilationResultRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import java.util.*
import kotlin.test.Test

class CompilationResultDownloaderServiceTest {

    private val compilationResultRepository: CompilationResultRepository = mock(CompilationResultRepository::class.java)
    private val target = CompilationResultDownloaderService(compilationResultRepository)

    @Test
    fun `should download compilation result and return zip file`() {
        val id = 1L
        val code = "int main() { return 0; }"
        val output = "Compilation successful"
        val compilationResult = CompilationResult(id = id, code = code, output = output)

        `when`(compilationResultRepository.findById(id)).thenReturn(Optional.of(compilationResult))
        val response = target.downloadCompilationResultById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertTrue(response.body!!.isNotEmpty())

        val headers = response.headers
        assertEquals("attachment; filename=compilation_result_1.zip", headers[HttpHeaders.CONTENT_DISPOSITION]?.get(0))
        assertEquals("application/zip", headers[HttpHeaders.CONTENT_TYPE]?.get(0))
        val zipContent = response.body
        assertNotNull(zipContent)
    }

    @Test
    fun `should throw exception when compilation result not found`() {
        val id = 1L

        `when`(compilationResultRepository.findById(id)).thenReturn(Optional.empty())

        val exception = assertThrows<RuntimeException> {
            target.downloadCompilationResultById(id)
        }
        assertEquals("Compilation result not found for ID: 1", exception.message)
    }

    @Test
    fun `should handle empty code and output gracefully`() {
        val id = 1L
        val compilationResult = CompilationResult(id = id, code = null, output = null)

        `when`(compilationResultRepository.findById(id)).thenReturn(Optional.of(compilationResult))
        val response = target.downloadCompilationResultById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        val zipContent = response.body
        assertNotNull(zipContent)
    }
}