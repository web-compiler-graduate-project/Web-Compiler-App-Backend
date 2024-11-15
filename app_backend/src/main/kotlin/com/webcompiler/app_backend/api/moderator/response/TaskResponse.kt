package com.webcompiler.app_backend.api.moderator.response

data class TaskResponse(
    val title: String? = null,
    val description: String? = null,
    val id: Long? = null,
    val assignedUsersCount: Int = 0,
    val assignedUsers: List<String> = emptyList(),
    val solutionCount: Int = 0,
    val isEnabled : Boolean? = null,
    val solutions: List<TaskSolutionResponse> = emptyList()
)
