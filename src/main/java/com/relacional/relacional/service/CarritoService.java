package com.relacional.relacional.service;

import com.relacional.relacional.model.*;
import com.relacional.relacional.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CarritoService {
    private final CarritoRepository carritoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final CarritoItemRepository carritoItemRepository;

    public CarritoService(CarritoRepository carritoRepository,
                          UsuarioRepository usuarioRepository,
                          ProductoRepository productoRepository,
                          CarritoItemRepository carritoItemRepository) {
        this.carritoRepository = carritoRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.carritoItemRepository = carritoItemRepository;
    }

    @Transactional(readOnly = true)
    public List<Carrito> listar() {
        return carritoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Carrito obtener(Long id) {
        return carritoRepository.findById(id).orElse(null);
    }

    @Transactional
    public Carrito crear(Long usuarioId) {
        Usuario u = usuarioRepository.findById(usuarioId).orElse(null);
        if (u == null) return null;
        Carrito c = new Carrito();
        c.setUsuario(u);
        return carritoRepository.save(c);
    }

    @Transactional
    public Carrito agregarProducto(Long carritoId, Long productoId, int cantidad) {
        if (cantidad <= 0) return null;
        Carrito carrito = carritoRepository.findById(carritoId).orElse(null);
        if (carrito == null || !carrito.isActivo()) return null;
        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto == null) return null;
        if (!producto.isActivo()) return null;
        CarritoItem item = carritoItemRepository.findByCarritoIdAndProductoId(carritoId, productoId).orElse(null);
        if (item == null) {
            item = new CarritoItem();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(cantidad);
            carrito.getItems().add(item);
        } else {
            item.setCantidad(item.getCantidad() + cantidad);
        }
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito quitarProducto(Long carritoId, Long productoId) {
        Carrito carrito = carritoRepository.findById(carritoId).orElse(null);
        if (carrito == null || !carrito.isActivo()) return null;
        CarritoItem item = carritoItemRepository.findByCarritoIdAndProductoId(carritoId, productoId).orElse(null);
        if (item == null) return null;
        carrito.getItems().remove(item);
        carritoItemRepository.delete(item);
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito vaciar(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId).orElse(null);
        if (carrito == null || !carrito.isActivo()) return null;
        List<CarritoItem> toDelete = new ArrayList<>(carrito.getItems());
        carrito.getItems().clear();
        carritoItemRepository.deleteAll(toDelete);
        return carritoRepository.save(carrito);
    }

    @Transactional
    public Carrito cerrar(Long carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId).orElse(null);
        if (carrito == null) return null;
        carrito.setActivo(false);
        return carritoRepository.save(carrito);
    }

    @Transactional
    public boolean eliminar(Long id) {
        Carrito carrito = carritoRepository.findById(id).orElse(null);
        if (carrito == null) return false;
        carritoRepository.delete(carrito);
        return true;
    }

    public BigDecimal calcularTotal(Carrito carrito) {
        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem item : carrito.getItems()) {
            BigDecimal precio = item.getProducto().getPrecio();
            total = total.add(precio.multiply(BigDecimal.valueOf(item.getCantidad())));
        }
        return total;
    }
}
