package com.webcompiler.app_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "compilation_result")
data class CompilationResult(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(columnDefinition = "TEXT")
    val code: String? = null,

    @Column(columnDefinition = "TEXT")
    val output: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val appUser: AppUser? = null,

    @OneToOne(mappedBy = "compilationResult", fetch = FetchType.LAZY, optional = true)
    val taskSolution: TaskSolution? = null
)

