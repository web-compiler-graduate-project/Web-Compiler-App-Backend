package com.webcompiler.app_backend.api.session

import com.webcompiler.app_backend.api.session.response.UserResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/session-context")
class SessionContextApi {

    @GetMapping("/current-user")
    fun getCurrentUser(@AuthenticationPrincipal userDetails: UserDetails?): ResponseEntity<UserResponse> =
        ResponseEntity.ok(
            UserResponse(
                username = userDetails?.username,
                role = userDetails?.authorities?.firstOrNull()?.authority,
            )
        )
}