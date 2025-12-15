package com.noRelacional.baseDeDatos.repository;

import com.noRelacional.baseDeDatos.model.ProductoDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductoDocRepository extends MongoRepository<ProductoDoc, String> {
    boolean existsByNombre(String nombre);
    boolean existsBySku(String sku);
}

