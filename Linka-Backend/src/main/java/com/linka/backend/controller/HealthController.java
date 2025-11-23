package com.linka.backend.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString(),
            "service", "LinkA Backend API",
            "version", "1.0.0",
            "message", "LinkA Backend is running successfully"
        );
    }

    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
            "app", "LinkA Backend",
            "description", "Property connect springboot project",
            "java.version", System.getProperty("java.version"),
            "os.name", System.getProperty("os.name"),
            "timestamp", LocalDateTime.now().toString()
        );
    }
}