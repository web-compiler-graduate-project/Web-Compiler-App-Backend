package com.webcompiler.app_backend.config

import com.webcompiler.app_backend.model.AppUserRole
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationSuccessHandler : AuthenticationSuccessHandler {
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val userDetails = authentication.principal as UserDetails
        val roles = userDetails.authorities.map { it.authority }
        when {
            roles.contains("ROLE_" + AppUserRole.ADMIN) -> response.sendRedirect("http://localhost/admin")
            roles.contains("ROLE_" + AppUserRole.MODERATOR) -> response.sendRedirect("http://localhost/moderator")
            roles.contains("ROLE_" + AppUserRole.USER) -> response.sendRedirect("http://localhost/user/compile")
            else -> response.sendRedirect("http://localhost")
        }
    }

}