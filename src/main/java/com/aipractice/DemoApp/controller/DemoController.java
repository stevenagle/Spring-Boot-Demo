package com.aipractice.DemoApp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/demo")
public class DemoController {

    // GET endpoint to fetch a resource
    @GetMapping
    public ResponseEntity<String> getResource() {
        // Simulated response - in practice, you'd call a service or fetch from a database
        return ResponseEntity.ok("Resource data retrieved successfully.");
    }

    // POST endpoint to create or process a resource
    @PostMapping
    public ResponseEntity<String> createResource(@RequestBody Map<String, Object> payload) {
        // Process the incoming payload
        String name = payload.getOrDefault("name", "Unnamed").toString();
        return ResponseEntity.ok("Resource '" + name + "' created successfully.");
    }
}
