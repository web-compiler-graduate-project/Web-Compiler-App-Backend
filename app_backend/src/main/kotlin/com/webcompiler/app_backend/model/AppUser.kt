package com.webcompiler.app_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "app_user")
data class AppUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = true)
    val name: String? = null,

    @Column(unique = true, nullable = true)
    val email: String? = null,

    @Column(nullable = true)
    val role: String? = null,

    val isEnabled: Boolean = true,

    @Column(nullable = true)
    val passwordPart1: String? = null,

    @OneToMany(mappedBy = "appUser", cascade = [CascadeType.ALL], orphanRemoval = true)
    val compilationResults: MutableList<CompilationResult> = mutableListOf(),

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "user_task",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "task_id")]
    )
    val tasks: MutableList<Task> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "user_task_solution",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "task_solution_id")]
    )
    val taskSolutions: MutableList<TaskSolution> = mutableListOf()
)
