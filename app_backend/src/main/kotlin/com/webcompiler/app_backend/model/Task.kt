package com.webcompiler.app_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "task")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = true)
    val title: String? = null,

    @Column(columnDefinition = "TEXT", nullable = true)
    val description: String? = null,

    val isEnabled: Boolean = true,

    @OneToMany(mappedBy = "task", cascade = [CascadeType.ALL], orphanRemoval = true)
    val taskSolutions: MutableList<TaskSolution> = mutableListOf(),

    @ManyToMany(mappedBy = "tasks", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    val users: MutableList<AppUser> = mutableListOf()
)


