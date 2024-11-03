package com.webcompiler.app_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "task")
data class Task(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val title: String,

    @Column(nullable = true)
    val description: String?,

    @OneToMany(mappedBy = "task", cascade = [CascadeType.ALL], orphanRemoval = true)
    val taskSolutions: List<TaskSolution> = mutableListOf(),

    @ManyToMany(mappedBy = "tasks")
    val users: List<AppUser> = mutableListOf()
)


