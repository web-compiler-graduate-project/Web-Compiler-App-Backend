package com.webcompiler.app_backend.integration_test.config

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.vault.VaultContainer

@SpringBootTest
@ExtendWith(SpringExtension::class)
class TestContainersConfig {

    companion object {
        private lateinit var postgresContainer: PostgreSQLContainer<*>
        private lateinit var vaultContainer: VaultContainer<*>

        @BeforeAll
        @JvmStatic
        fun setup() {
            postgresContainer = PostgreSQLContainer("postgres:13")
                .apply {
                    withDatabaseName("test_db")
                    withUsername("test_user")
                    withPassword("test_password")
                    start()
                }

            System.setProperty("DB_NAME", "test_db")
            System.setProperty("DB_PORT", postgresContainer.getMappedPort(5432).toString())
            System.setProperty("DB_USER", postgresContainer.username)
            System.setProperty("DB_PASSWORD", postgresContainer.password)
            System.setProperty("DB_HOST", postgresContainer.host)

            vaultContainer = VaultContainer("vault:1.13.0")
                .apply {
                    withVaultToken("root")
                    start()
                }
            System.setProperty(
                "VAULT_URL",
                "http://" + vaultContainer.host + ":" + vaultContainer.getMappedPort(8200).toString()
            )
            System.setProperty("VAULT_TOKEN_ID", "root")
        }

        @AfterAll
        @JvmStatic
        fun teardown() {
            postgresContainer.stop()
        }
    }
}
