package com.webcompiler.app_backend.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.*
import org.springframework.web.client.RestTemplate

class VaultServiceTest {

    private val restTemplate: RestTemplate = mock(RestTemplate::class.java)
    private val target = VaultService(restTemplate)

    private val vaultUrl = "http://localhost:8200"
    private val vaultToken = "dummy-vault-token"

    init {
        target.vaultUrl = vaultUrl
        target.vaultToken = vaultToken
    }

    @Test
    fun `test savePasswordPart2`() {
        val username = "user1"
        val passwordPart2 = "password123"
        val secret = mapOf("data" to mapOf("password_part2" to passwordPart2))
        val url = "$vaultUrl/v1/secret/data/web-compiler/$username"
        val headers = HttpHeaders()
        headers.set("X-Vault-Token", vaultToken)
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(secret, headers)

        val response = ResponseEntity("OK", HttpStatus.OK)
        `when`(restTemplate.postForEntity(url, request, String::class.java)).thenReturn(response)

        target.savePasswordPart2(username, passwordPart2)

        verify(restTemplate).postForEntity(url, request, String::class.java)
    }

    @Test
    fun `test getPasswordPart2ByUsername`() {
        val username = "user1"
        val mockResponse = """
            {
                "data": {
                    "data": {
                        "password_part2": "password123"
                    }
                }
            }
        """
        val url = "$vaultUrl/v1/secret/data/web-compiler/$username"
        val headers = HttpHeaders()
        headers.set("X-Vault-Token", vaultToken)
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(null, headers)

        val response = ResponseEntity(mockResponse, HttpStatus.OK)
        `when`(restTemplate.exchange(url, HttpMethod.GET, request, String::class.java)).thenReturn(response)

        val password = target.getPasswordPart2ByUsername(username)

        assertEquals("password123", password)
    }

    @Test
    fun `test updatePasswordPart2ByUsername`() {
        val username = "user1"
        val newPasswordPart2 = "newpassword123"
        val secret = mapOf("data" to mapOf("password_part2" to newPasswordPart2))
        val url = "$vaultUrl/v1/secret/metadata/web-compiler/$username"
        val headers = HttpHeaders()
        headers.set("X-Vault-Token", vaultToken)
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(secret, headers)

        val response = ResponseEntity("OK", HttpStatus.OK)
        `when`(restTemplate.postForEntity(url, request, String::class.java)).thenReturn(response)

        target.updatePasswordPart2ByUsername(username, newPasswordPart2)

        verify(restTemplate).postForEntity(url, request, String::class.java)
    }

    @Test
    fun `test deletePasswordByUsername`() {
        val username = "user1"
        val url = "$vaultUrl/v1/secret/metadata/web-compiler/$username"
        val headers = HttpHeaders()
        headers.set("X-Vault-Token", vaultToken)
        headers.contentType = MediaType.APPLICATION_JSON
        val request = HttpEntity(null, headers)

        val response = ResponseEntity("OK", HttpStatus.OK)
        `when`(restTemplate.exchange(url, HttpMethod.DELETE, request, String::class.java)).thenReturn(response)

        target.deletePasswordByUsername(username)

        verify(restTemplate).exchange(url, HttpMethod.DELETE, request, String::class.java)
    }
}
