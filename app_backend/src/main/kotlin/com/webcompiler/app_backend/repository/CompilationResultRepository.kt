package com.webcompiler.app_backend.repository

import com.webcompiler.app_backend.model.AppUser
import com.webcompiler.app_backend.model.CompilationResult
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CompilationResultRepository : JpaRepository<CompilationResult, Long>