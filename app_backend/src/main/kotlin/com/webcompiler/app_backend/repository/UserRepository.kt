package com.webcompiler.app_backend.repository

import com.webcompiler.app_backend.model.AppUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<AppUser, Long> {

    fun findByName(name: String): AppUser?

    fun existsByName(name: String): Boolean

    fun deleteByName(name: String)
}