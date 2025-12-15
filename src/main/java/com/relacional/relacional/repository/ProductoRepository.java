package com.relacional.relacional.repository;

import com.relacional.relacional.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByNombre(String nombre);
    boolean existsBySku(String sku);
}
