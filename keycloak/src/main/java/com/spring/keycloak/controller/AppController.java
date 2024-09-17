package com.spring.keycloak.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppController {

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        System.out.println("Testing");
        return ResponseEntity.status(HttpStatus.OK).body("Hello normal user");
    }

    @GetMapping("/hello-pr")
    public ResponseEntity<String> helloProtected() {
        return ResponseEntity.ok("Hello premium user");
    }
}
