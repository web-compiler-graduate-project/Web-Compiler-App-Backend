package com.webcompiler.app_backend.config

import com.webcompiler.app_backend.model.AppUser
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val appUser: AppUser,
    private val passwordPart2: String? = null
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(
            SimpleGrantedAuthority("ROLE_${appUser.role}")
        )

    override fun getPassword(): String = (appUser.passwordPart1 ?: "") + (passwordPart2 ?: "")

    override fun getUsername(): String = appUser.name ?: ""

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = appUser.isEnabled
}
