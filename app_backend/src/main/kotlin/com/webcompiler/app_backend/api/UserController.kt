package com.webcompiler.app_backend.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user/")
class UserController {

    @GetMapping
    fun getUser(): ResponseEntity<String> {
        return ResponseEntity.ok("User data")
    }
}
