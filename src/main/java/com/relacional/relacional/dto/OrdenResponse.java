package com.relacional.relacional.dto;

import com.relacional.relacional.model.OrdenEstado;
import java.math.BigDecimal;
import java.util.List;

public class OrdenResponse {
    private Long id;
    private Long usuarioId;
    private Long carritoId;
    private OrdenEstado estado;
    private List<OrdenItemResponse> items;
    private BigDecimal total;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getCarritoId() {
        return carritoId;
    }

    public void setCarritoId(Long carritoId) {
        this.carritoId = carritoId;
    }

    public OrdenEstado getEstado() {
        return estado;
    }

    public void setEstado(OrdenEstado estado) {
        this.estado = estado;
    }

    public List<OrdenItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrdenItemResponse> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}

