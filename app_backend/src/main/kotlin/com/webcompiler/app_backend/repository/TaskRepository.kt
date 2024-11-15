package com.webcompiler.app_backend.repository

import com.webcompiler.app_backend.model.AppUser
import com.webcompiler.app_backend.model.Task
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskRepository: JpaRepository<Task, Long> {

    fun findAllByIsEnabled(isEnabled: Boolean): List<Task>

    fun findByUsersContaining(user: AppUser): List<Task>
}