package com.relacional.relacional.controller;

import com.relacional.relacional.dto.ProductoRequest;
import com.relacional.relacional.dto.ProductoResponse;
import com.relacional.relacional.model.Producto;
import com.relacional.relacional.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listar() {
        List<ProductoResponse> list = productoService.listar().stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> obtener(@PathVariable Long id) {
        Producto p = productoService.obtener(id);
        if (p == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toResponse(p));
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@RequestBody ProductoRequest request) {
        Producto p = new Producto();
        p.setNombre(request.getNombre());
        p.setPrecio(request.getPrecio());
        p.setDescripcion(request.getDescripcion());
        p.setCategoria(request.getCategoria());
        p.setSku(request.getSku());
        p.setStock(request.getStock());
        p.setImagenUrl(request.getImagenUrl());
        if (request.getActivo() != null) {
            p.setActivo(request.getActivo());
        }
        Producto creado = productoService.crear(p);
        if (creado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.created(URI.create("/api/productos/" + creado.getId())).body(toResponse(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(@PathVariable Long id, @RequestBody ProductoRequest request) {
        Producto datos = new Producto();
        datos.setNombre(request.getNombre());
        datos.setPrecio(request.getPrecio());
        datos.setDescripcion(request.getDescripcion());
        datos.setCategoria(request.getCategoria());
        datos.setSku(request.getSku());
        datos.setStock(request.getStock());
        datos.setImagenUrl(request.getImagenUrl());
        if (request.getActivo() != null) {
            datos.setActivo(request.getActivo());
        } else {
            datos.setActivo(false);
        }
        Producto actualizado = productoService.actualizar(id, datos);
        if (actualizado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean ok = productoService.eliminar(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    private ProductoResponse toResponse(Producto p) {
        ProductoResponse r = new ProductoResponse();
        r.setId(p.getId());
        r.setNombre(p.getNombre());
        r.setPrecio(p.getPrecio());
        r.setDescripcion(p.getDescripcion());
        r.setCategoria(p.getCategoria());
        r.setSku(p.getSku());
        r.setStock(p.getStock());
        r.setActivo(p.isActivo());
        r.setImagenUrl(p.getImagenUrl());
        return r;
    }
}
