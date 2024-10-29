package com.webcompiler.app_backend.api.admin

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin/")
class AdminApi {

    @GetMapping
    fun getAdmin(): ResponseEntity<String> {
        return ResponseEntity.ok("Admin data")
    }
}