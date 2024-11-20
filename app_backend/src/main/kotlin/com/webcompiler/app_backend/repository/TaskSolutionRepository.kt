package com.webcompiler.app_backend.repository

import com.webcompiler.app_backend.model.TaskSolution
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TaskSolutionRepository: JpaRepository<TaskSolution, Long>