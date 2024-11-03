package com.webcompiler.app_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "app_user")
data class AppUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val name: String,

    @Column(unique = true, nullable = false)
    val email: String,

    @Column(nullable = true)
    val role: String?,

    @Column(nullable = true)
    val passwordPart1: String?,

    @OneToMany(mappedBy = "appUser", cascade = [CascadeType.ALL], orphanRemoval = true)
    val compilationResults: List<CompilationResult> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "user_task",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "task_id")]
    )
    val tasks: List<Task> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "user_task_solution",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "task_solution_id")]
    )
    val taskSolutions: List<TaskSolution> = mutableListOf()
)
