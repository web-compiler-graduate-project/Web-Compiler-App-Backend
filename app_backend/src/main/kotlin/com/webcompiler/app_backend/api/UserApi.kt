package com.webcompiler.app_backend.api

import com.webcompiler.app_backend.service.PasswordService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user/")
class UserApi {

    @GetMapping
    fun getUser(): ResponseEntity<String> {
        return ResponseEntity.ok("User data")
    }
}
