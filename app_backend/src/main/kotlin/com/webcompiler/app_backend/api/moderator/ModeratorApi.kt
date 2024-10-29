package com.webcompiler.app_backend.api.moderator

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/moderator/")
class ModeratorApi {

    @GetMapping
    fun getModerator(): ResponseEntity<String> {
        return ResponseEntity.ok("Moderator data")
    }
}