package com.webcompiler.app_backend.repository

import com.webcompiler.app_backend.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun findByName(name: String): User?

    fun existsByName(name: String): Boolean
}