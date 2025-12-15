package com.relacional.relacional.controller;

import com.relacional.relacional.dto.OrdenItemResponse;
import com.relacional.relacional.dto.OrdenRequest;
import com.relacional.relacional.dto.OrdenResponse;
import com.relacional.relacional.model.Orden;
import com.relacional.relacional.model.OrdenItem;
import com.relacional.relacional.service.OrdenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {
    private final OrdenService ordenService;

    public OrdenController(OrdenService ordenService) {
        this.ordenService = ordenService;
    }

    @GetMapping
    public ResponseEntity<List<OrdenResponse>> listar() {
        List<OrdenResponse> list = ordenService.listar().stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponse> obtener(@PathVariable Long id) {
        Orden o = ordenService.obtener(id);
        if (o == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toResponse(o));
    }

    @PostMapping
    public ResponseEntity<OrdenResponse> crear(@RequestBody OrdenRequest request) {
        Orden creada = ordenService.crearDesdeCarrito(request.getCarritoId());
        if (creada == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(toResponse(creada));
    }

    @PostMapping("/{id}/pagar")
    public ResponseEntity<OrdenResponse> pagar(@PathVariable Long id) {
        Orden o = ordenService.marcarPagada(id);
        if (o == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(toResponse(o));
    }

    @PostMapping("/{id}/cancelar")
    public ResponseEntity<OrdenResponse> cancelar(@PathVariable Long id) {
        Orden o = ordenService.cancelar(id);
        if (o == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(toResponse(o));
    }

    private OrdenResponse toResponse(Orden o) {
        OrdenResponse r = new OrdenResponse();
        r.setId(o.getId());
        r.setUsuarioId(o.getUsuario().getId());
        r.setCarritoId(o.getCarritoOrigen().getId());
        r.setEstado(o.getEstado());
        r.setTotal(o.getTotal());
        List<OrdenItemResponse> items = o.getItems().stream().map(this::toItemResponse).collect(Collectors.toList());
        r.setItems(items);
        return r;
    }

    private OrdenItemResponse toItemResponse(OrdenItem item) {
        OrdenItemResponse r = new OrdenItemResponse();
        r.setProductoId(item.getProductoId());
        r.setNombre(item.getProductoNombre());
        r.setPrecio(item.getProductoPrecio());
        r.setCantidad(item.getCantidad());
        return r;
    }
}

