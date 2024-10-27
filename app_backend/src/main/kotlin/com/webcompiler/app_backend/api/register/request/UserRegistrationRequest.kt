package com.webcompiler.app_backend.api.register.request

data class UserRegistrationRequest(
    val username: String,
    val email: String,
    val password: String
)
