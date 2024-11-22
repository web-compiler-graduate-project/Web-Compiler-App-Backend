package com.webcompiler.app_backend.api.user.response

data class TaskSolutionResponse (
    val id: Long? = null,
    val comments: String? = null,
    val grade: Int? = null,
    val code: String? = null,
    val output: String? = null,
    val taskName: String? = null,
    val taskDescription: String? = null
)