package com.relacional.relacional.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/connection")
    public String testConnection() {
        try (Connection conn = dataSource.getConnection()) {
            return "✅ Conexión exitosa a Oracle! Database: " + conn.getMetaData().getDatabaseProductName() 
                   + " " + conn.getMetaData().getDatabaseProductVersion();
        } catch (Exception e) {
            return "❌ Error de conexión: " + e.getMessage();
        }
    }
}
