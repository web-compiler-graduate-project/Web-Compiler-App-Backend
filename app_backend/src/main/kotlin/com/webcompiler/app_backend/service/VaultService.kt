package com.webcompiler.app_backend.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class VaultService(
    private val restTemplate: RestTemplate,
) {

    @Value("\${spring.cloud.vault.uri}")
    lateinit var vaultUrl: String

    @Value("\${spring.cloud.vault.token}")
    lateinit var vaultToken: String

    fun savePasswordPart2(username: String, passwordPart2: String) {
        val secret = mapOf("data" to mapOf("password_part2" to passwordPart2))
        val url = "$vaultUrl/v1/secret/data/web-compiler/$username"
        val headers = HttpHeaders().apply {
            set("X-Vault-Token", vaultToken)
            contentType = MediaType.APPLICATION_JSON
        }
        val request = HttpEntity(secret, headers)
        restTemplate.postForEntity(url, request, String::class.java)
    }

    fun getPasswordPart2ByUsername(username: String): String {
        val url = "$vaultUrl/v1/secret/data/web-compiler/$username"
        val headers = HttpHeaders().apply {
            set("X-Vault-Token", vaultToken)
            contentType = MediaType.APPLICATION_JSON
        }
        val request = HttpEntity(null, headers)
        val response = restTemplate.exchange(url, HttpMethod.GET, request, String::class.java)
        if (response.statusCode.is2xxSuccessful) {
            val responseBody = response.body ?: throw RuntimeException("Empty response from Vault")
            val jsonNode = ObjectMapper().readTree(responseBody)
            return jsonNode.path("data").path("data").path("password_part2").asText()
        } else {
            throw RuntimeException("Failed to fetch password from Vault: ${response.statusCode}")
        }
    }

    fun updatePasswordPart2ByUsername(username: String, newPasswordPart2: String) {
        val secret = mapOf("data" to mapOf("password_part2" to newPasswordPart2))
        val url = "$vaultUrl/v1/secret/data/web-compiler/$username"
        val headers = HttpHeaders().apply {
            set("X-Vault-Token", vaultToken)
            contentType = MediaType.APPLICATION_JSON
        }
        val request = HttpEntity(secret, headers)
        try {
            val response = restTemplate.postForEntity(url, request, String::class.java)
            if (!response.statusCode.is2xxSuccessful) {
                throw RuntimeException("Failed to update password in Vault: ${response.statusCode}")
            }
        } catch (e: Exception) {
            throw RuntimeException("Error occurred while updating password: ${e.message}", e)
        }
    }

    fun deletePasswordByUsername(username: String) {
        val url = "$vaultUrl/v1/secret/data/web-compiler/$username"
        val headers = HttpHeaders().apply {
            set("X-Vault-Token", vaultToken)
            contentType = MediaType.APPLICATION_JSON
        }
        val request = HttpEntity(null, headers)
        try {
            val response = restTemplate.exchange(url, HttpMethod.DELETE, request, String::class.java)
            if (!response.statusCode.is2xxSuccessful) {
                throw RuntimeException("Failed to delete password in Vault: ${response.statusCode}")
            }
        } catch (e: Exception) {
            throw RuntimeException("Error occurred while deleting password: ${e.message}", e)
        }
    }

}
