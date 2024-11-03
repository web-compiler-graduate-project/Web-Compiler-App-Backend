package com.webcompiler.app_backend.api.user.request

data class UserUpdateRequest(
    val currentUsername: String,
    val newUsername: String? = null,
    val newEmail: String? = null,
    val currentPassword: String? = null,
    val newPassword: String? = null
)
