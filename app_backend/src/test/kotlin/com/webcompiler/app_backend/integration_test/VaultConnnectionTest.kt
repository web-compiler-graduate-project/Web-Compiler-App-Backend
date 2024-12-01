package com.webcompiler.app_backend.integration_test

import com.webcompiler.app_backend.VaultService
import com.webcompiler.app_backend.integration_test.config.TestContainersConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.vault.core.VaultTemplate
import org.springframework.vault.support.VaultResponse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class VaultConnectionTest : TestContainersConfig() {

    @Autowired
    lateinit var vaultTemplate: VaultTemplate

    @MockBean
    lateinit var vaultService: VaultService

    @Test
    fun `test connection to Vault by writing and reading a secret`() {
        val secretData = mapOf("data" to mapOf("mykey" to "myvalue"))

        try {
            val writeResponse = vaultTemplate.write("secret/data/mysecret", secretData)
            assertNotNull(writeResponse, "The secret should be written.")
            println("Secret written: $secretData")

            val secret: VaultResponse = vaultTemplate.read("secret/data/mysecret")
            assertNotNull(secret, "The secret should be read.")
            println("Secret read: ${secret.data}")

            val secretDataFromVault = secret.data?.get("data") as? Map<*, *> ?: error("Data is missing")
            assertTrue(secretDataFromVault.containsKey("mykey"), "The secret should contain the key 'mykey'")
            println("The secret contains 'mykey': ${secretDataFromVault["mykey"]}")
        } catch (ex: Exception) {
            println("Error occurred while writing or reading the secret: ${ex.message}")
            throw AssertionError("Vault connection or secret retrieval failed", ex)
        }
    }
}
