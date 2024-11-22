package com.webcompiler.app_backend.api.moderator.response

data class TaskSolutionResponse (
    val id: Long,
    val comments: String? = null,
    val grade: Int? = null,
    val code: String? = null,
    val output: String? = null,
    val username: String? = null,
    val userEmail: String? = null
)