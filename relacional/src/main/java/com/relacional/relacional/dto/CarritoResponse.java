package com.relacional.relacional.dto;

import java.math.BigDecimal;
import java.util.List;

public class CarritoResponse {
    private Long id;
    private Long usuarioId;
    private boolean activo;
    private List<CarritoItemResponse> items;
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

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public List<CarritoItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CarritoItemResponse> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}

