package com.webcompiler.app_backend.api.admin.response

data class UserResponse(
    val username: String? = null,
    val email: String? = null,
    val isEnabled: Boolean? = null,
    val id: String? = null
)
