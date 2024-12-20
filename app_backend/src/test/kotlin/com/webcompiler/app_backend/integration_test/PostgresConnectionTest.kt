package com.webcompiler.app_backend.integration_test

import com.webcompiler.app_backend.service.VaultService
import com.webcompiler.app_backend.integration_test.config.TestContainersConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.jdbc.core.JdbcTemplate
import kotlin.test.assertEquals

class PostgresConnectionTest : TestContainersConfig() {

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @MockBean
    lateinit var vaultService: VaultService

    @Test
    fun `test connection to PostgreSQL`() {
        val result = jdbcTemplate.queryForObject("SELECT current_database();", String::class.java)
        assertEquals("test_db", result)
    }
}
