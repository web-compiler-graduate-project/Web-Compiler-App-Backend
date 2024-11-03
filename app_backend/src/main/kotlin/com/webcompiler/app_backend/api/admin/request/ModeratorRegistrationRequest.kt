package com.webcompiler.app_backend.api.admin.request

data class ModeratorRegistrationRequest(
    val username: String,
    val email: String,
    val password: String
)
