package com.relacional.relacional.repository;

import com.relacional.relacional.model.Orden;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdenRepository extends JpaRepository<Orden, Long> {
}

