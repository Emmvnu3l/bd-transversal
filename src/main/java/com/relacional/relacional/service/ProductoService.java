package com.relacional.relacional.service;

import com.relacional.relacional.model.Producto;
import com.relacional.relacional.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listar() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto obtener(Long id) {
        return productoRepository.findById(id).orElse(null);
    }

    @Transactional
    public Producto crear(Producto p) {
        if (p.getNombre() != null && productoRepository.existsByNombre(p.getNombre())) {
            return null;
        }
        if (p.getSku() != null && productoRepository.existsBySku(p.getSku())) {
            return null;
        }
        if (p.getStock() != null && p.getStock() < 0) {
            return null;
        }
        return productoRepository.save(p);
    }

    @Transactional
    public Producto actualizar(Long id, Producto datos) {
        Producto actual = productoRepository.findById(id).orElse(null);
        if (actual == null) return null;
        if (datos.getNombre() != null) {
            if (!datos.getNombre().equals(actual.getNombre()) && productoRepository.existsByNombre(datos.getNombre())) {
                return null;
            }
            actual.setNombre(datos.getNombre());
        }
        if (datos.getPrecio() != null) actual.setPrecio(datos.getPrecio());
        if (datos.getDescripcion() != null) actual.setDescripcion(datos.getDescripcion());
        if (datos.getCategoria() != null) actual.setCategoria(datos.getCategoria());
        if (datos.getSku() != null) {
            if (!datos.getSku().equals(actual.getSku()) && productoRepository.existsBySku(datos.getSku())) {
                return null;
            }
            actual.setSku(datos.getSku());
        }
        if (datos.getStock() != null) {
            if (datos.getStock() < 0) return null;
            actual.setStock(datos.getStock());
        }
        if (datos.getImagenUrl() != null) actual.setImagenUrl(datos.getImagenUrl());
        if (datos.isActivo() != actual.isActivo()) actual.setActivo(datos.isActivo());
        return productoRepository.save(actual);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!productoRepository.existsById(id)) return false;
        productoRepository.deleteById(id);
        return true;
    }
}
