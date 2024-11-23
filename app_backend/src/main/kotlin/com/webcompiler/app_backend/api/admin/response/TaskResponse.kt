package com.webcompiler.app_backend.api.admin.response

data class TaskResponse(
    val title: String? = null,
    val description: String? = null,
    val id: Long? = null,
    val assignedUsersCount: Int = 0,
    val solutionCount: Int = 0,
    val isEnabled : Boolean? = null,
    val moderatorUserName: String? = null,
    val moderatorEmail: String? = null
)
