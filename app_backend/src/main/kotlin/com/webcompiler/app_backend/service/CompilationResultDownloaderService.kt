package com.webcompiler.app_backend.service

import com.webcompiler.app_backend.repository.CompilationResultRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
class CompilationResultDownloaderService(
    @Autowired private val compilationResultRepository: CompilationResultRepository
) {

    fun downloadCompilationResultById(id: Long): ResponseEntity<ByteArray> {
        val compilationResult = compilationResultRepository.findById(id)
            .orElseThrow { RuntimeException("Compilation result not found for ID: $id") }
        val byteArrayOutputStream = ByteArrayOutputStream()
        ZipOutputStream(byteArrayOutputStream).use { zipOut ->
            val codeFileName = "code.cpp"
            val codeFileContent = compilationResult.code ?: "No code available"
            zipOut.putNextEntry(ZipEntry(codeFileName))
            zipOut.write(codeFileContent.toByteArray())
            zipOut.closeEntry()
            val outputFileName = "output.txt"
            val outputFileContent = compilationResult.output ?: "No output available"
            zipOut.putNextEntry(ZipEntry(outputFileName))
            zipOut.write(outputFileContent.toByteArray())
            zipOut.closeEntry()
        }
        val zipBytes = byteArrayOutputStream.toByteArray()
        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=compilation_result_$id.zip")
        headers.add(HttpHeaders.CONTENT_TYPE, "application/zip")
        return ResponseEntity(zipBytes, headers, HttpStatus.OK)
    }
}