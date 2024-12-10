package com.webcompiler.app_backend.api.session

import com.webcompiler.app_backend.api.session.response.UserResponse
import com.webcompiler.app_backend.config.CustomUserDetails
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/session-context")
class SessionContextApi {

    @GetMapping("/current-user")
    fun getCurrentUser(@AuthenticationPrincipal userDetails: CustomUserDetails?): ResponseEntity<UserResponse> =
        ResponseEntity.ok(
            UserResponse(
                username = userDetails?.username,
                role = userDetails?.authorities?.firstOrNull()?.authority,
            )
        )

    @GetMapping("/session-expired")
    fun sessionExpired(): ResponseEntity<String> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Your session has expired. Please log in again.")
}