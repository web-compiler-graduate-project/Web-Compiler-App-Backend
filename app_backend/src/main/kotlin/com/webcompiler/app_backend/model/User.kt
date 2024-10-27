package com.webcompiler.app_backend.model

import jakarta.persistence.*

@Entity(name = "app_user")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String,
    val email: String,
    @Column(length = 50)
    val role: String = "USER",
    val password: String
)