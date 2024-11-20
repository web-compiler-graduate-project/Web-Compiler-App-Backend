package com.webcompiler.app_backend.model

import jakarta.persistence.*

import jakarta.persistence.*

@Entity
@Table(name = "task_solution")
data class TaskSolution(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(columnDefinition = "TEXT", nullable = true)
    val comments: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task", nullable = true)
    val task: Task? = null,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "compilation_result", referencedColumnName = "id")
    val compilationResult: CompilationResult? = null,

    @ManyToMany(mappedBy = "taskSolutions")
    val users: MutableList<AppUser> = mutableListOf()
)
