package com.webcompiler.app_backend.api.user.request

data class CompilationResultSaveRequest(
    val code: String,
    val output: String,
    val username: String
)