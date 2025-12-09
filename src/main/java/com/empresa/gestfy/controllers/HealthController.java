package com.empresa.gestfy.controllers;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {
    private final JdbcTemplate jdbc;

    public HealthController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/api/health-db")
    public Map<String, Object> healthDb() {
        // executa query simples para garantir que o DB responde
        Integer result = jdbc.queryForObject("SELECT 1", Integer.class);
        return Map.of("db", result == 1 ? "OK" : "NOK");
    }
}
