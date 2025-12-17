package com.relacional.relacional.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuarios_email", columnNames = {"email"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(length = 100)
    private String apellido;

    @Column(nullable = false, length = 180)
    private String email;

    @JsonIgnore
    @Schema(hidden = true)
    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Column(length = 15)
    private String telefono;

    @Column(length = 200)
    private String telefonos;

    @Column(length = 255)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 100)
    private String estado;

    @Column(length = 10)
    private String codigoPostal;

    @Schema(hidden = true)
    @Column(nullable = false)
    private OffsetDateTime creadoEn;

    @Schema(hidden = true)
    @Column(nullable = false)
    private OffsetDateTime actualizadoEn;

    @PrePersist
    void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        this.creadoEn = now;
        this.actualizadoEn = now;
    }

    @PreUpdate
    void preUpdate() {
        this.actualizadoEn = OffsetDateTime.now();
    }
}
