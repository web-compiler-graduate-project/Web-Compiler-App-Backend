package com.webcompiler.app_backend.api.moderator.request

data class ModeratorUpdateRequest (
    val currentUsername: String,
    val newUsername: String? = null,
    val newEmail: String? = null,
    val currentPassword: String? = null,
    val newPassword: String? = null
)