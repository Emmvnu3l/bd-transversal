package com.noRelacional.baseDeDatos.controller;

import com.noRelacional.baseDeDatos.model.ProductoDoc;
import com.noRelacional.baseDeDatos.repository.ProductoDocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/mongo")
public class MongoTestController {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ProductoDocRepository productoDocRepository;

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok(mongoTemplate.getDb().getName());
    }

    @PostMapping("/productos")
    public ResponseEntity<ProductoDoc> crear(@Valid @RequestBody ProductoDoc doc) {
        if (doc.getSku() != null && productoDocRepository.existsBySku(doc.getSku())) {
            return ResponseEntity.badRequest().build();
        }
        ProductoDoc saved = productoDocRepository.save(doc);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoDoc>> listar() {
        return ResponseEntity.ok(productoDocRepository.findAll());
    }
}
