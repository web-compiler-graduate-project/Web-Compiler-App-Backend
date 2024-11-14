package com.webcompiler.app_backend.model

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class AppUserRole(val role: String) {
    USER("USER"),
    MODERATOR("MODERATOR"),
    ADMIN("ADMIN");

    companion object {
        fun getAllRoles(): List<SimpleGrantedAuthority> {
            return entries.map { SimpleGrantedAuthority("ROLE_${it.role}") }
        }
    }
}