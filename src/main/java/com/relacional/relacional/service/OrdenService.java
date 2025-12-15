package com.relacional.relacional.service;

import com.relacional.relacional.model.*;
import com.relacional.relacional.repository.CarritoRepository;
import com.relacional.relacional.repository.OrdenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrdenService {
    private final OrdenRepository ordenRepository;
    private final CarritoRepository carritoRepository;

    public OrdenService(OrdenRepository ordenRepository, CarritoRepository carritoRepository) {
        this.ordenRepository = ordenRepository;
        this.carritoRepository = carritoRepository;
    }

    @Transactional(readOnly = true)
    public List<Orden> listar() {
        return ordenRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Orden obtener(Long id) {
        return ordenRepository.findById(id).orElse(null);
    }

    @Transactional
    public Orden crearDesdeCarrito(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId).orElse(null);
        if (carrito == null || !carrito.isActivo()) return null;
        if (carrito.getItems().isEmpty()) return null;
        Orden orden = new Orden();
        orden.setUsuario(carrito.getUsuario());
        orden.setCarritoOrigen(carrito);
        orden.setEstado(OrdenEstado.CREADA);
        List<OrdenItem> ordenItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem ci : carrito.getItems()) {
            OrdenItem oi = new OrdenItem();
            oi.setOrden(orden);
            oi.setProductoId(ci.getProducto().getId());
            oi.setProductoNombre(ci.getProducto().getNombre());
            oi.setProductoPrecio(ci.getProducto().getPrecio());
            oi.setCantidad(ci.getCantidad());
            ordenItems.add(oi);
            total = total.add(ci.getProducto().getPrecio().multiply(BigDecimal.valueOf(ci.getCantidad())));
        }
        orden.setItems(ordenItems);
        orden.setTotal(total);
        carrito.setActivo(false);
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden marcarPagada(Long id) {
        Orden orden = ordenRepository.findById(id).orElse(null);
        if (orden == null) return null;
        if (orden.getEstado() != OrdenEstado.CREADA) return null;
        orden.setEstado(OrdenEstado.PAGADA);
        return ordenRepository.save(orden);
    }

    @Transactional
    public Orden cancelar(Long id) {
        Orden orden = ordenRepository.findById(id).orElse(null);
        if (orden == null) return null;
        if (orden.getEstado() == OrdenEstado.PAGADA) return null;
        orden.setEstado(OrdenEstado.CANCELADA);
        return ordenRepository.save(orden);
    }
}

