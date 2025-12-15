package com.relacional.relacional.controller;

import com.relacional.relacional.dto.*;
import com.relacional.relacional.model.Carrito;
import com.relacional.relacional.model.CarritoItem;
import com.relacional.relacional.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carritos")
public class CarritoController {
    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<List<CarritoResponse>> listar() {
        List<CarritoResponse> list = carritoService.listar().stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoResponse> obtener(@PathVariable Long id) {
        Carrito c = carritoService.obtener(id);
        if (c == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toResponse(c));
    }

    @PostMapping
    public ResponseEntity<CarritoResponse> crear(@RequestBody CarritoRequest request) {
        Carrito creado = carritoService.crear(request.getUsuarioId());
        if (creado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.created(URI.create("/api/carritos/" + creado.getId())).body(toResponse(creado));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<CarritoResponse> agregarItem(@PathVariable Long id, @RequestBody CarritoItemRequest request) {
        Carrito actualizado = carritoService.agregarProducto(id, request.getProductoId(), request.getCantidad());
        if (actualizado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(toResponse(actualizado));
    }

    @DeleteMapping("/{id}/items/{productoId}")
    public ResponseEntity<CarritoResponse> quitarItem(@PathVariable Long id, @PathVariable Long productoId) {
        Carrito actualizado = carritoService.quitarProducto(id, productoId);
        if (actualizado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(toResponse(actualizado));
    }

    @PostMapping("/{id}/vaciar")
    public ResponseEntity<CarritoResponse> vaciar(@PathVariable Long id) {
        Carrito actualizado = carritoService.vaciar(id);
        if (actualizado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(toResponse(actualizado));
    }

    @PostMapping("/{id}/cerrar")
    public ResponseEntity<CarritoResponse> cerrar(@PathVariable Long id) {
        Carrito actualizado = carritoService.cerrar(id);
        if (actualizado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean ok = carritoService.eliminar(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    private CarritoResponse toResponse(Carrito c) {
        CarritoResponse r = new CarritoResponse();
        r.setId(c.getId());
        r.setUsuarioId(c.getUsuario().getId());
        r.setActivo(c.isActivo());
        List<CarritoItemResponse> items = c.getItems().stream().map(this::toItemResponse).collect(Collectors.toList());
        r.setItems(items);
        BigDecimal total = carritoService.calcularTotal(c);
        r.setTotal(total);
        return r;
    }

    private CarritoItemResponse toItemResponse(CarritoItem item) {
        CarritoItemResponse r = new CarritoItemResponse();
        r.setProductoId(item.getProducto().getId());
        r.setNombre(item.getProducto().getNombre());
        r.setPrecio(item.getProducto().getPrecio());
        r.setCantidad(item.getCantidad());
        return r;
    }
}

