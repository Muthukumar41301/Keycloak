package com.spring.keycloak.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping("/hello")
    @PreAuthorize("hasRole('client_admin')")
    public ResponseEntity<String> hello() {
        return ResponseEntity.status(HttpStatus.OK).body("Hello normal user");
    }

    @GetMapping("/hello-pr")
    @PreAuthorize("hasRole('client_user')")
    public ResponseEntity<String> helloProtected() {
        return ResponseEntity.ok("Hello premium user");
    }
}
