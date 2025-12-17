package com.relacional.relacional.controller;

import com.relacional.relacional.dto.UsuarioRequest;
import com.relacional.relacional.dto.UsuarioResponse;
import com.relacional.relacional.model.Usuario;
import com.relacional.relacional.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios registrados")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> list = usuarioService.listar().stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario", description = "Obtiene un usuario por su ID")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        Usuario u = usuarioService.obtener(id);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toResponse(u));
    }

    @PostMapping
    @Operation(summary = "Crear usuario", description = "Crea un nuevo usuario con teléfonos opcionales")
    public ResponseEntity<UsuarioResponse> crear(@RequestBody UsuarioRequest request) {
        Usuario entity = new Usuario();
        entity.setNombre(request.getNombre());
        entity.setApellido(request.getApellido());
        entity.setEmail(request.getEmail());
        entity.setTelefono(request.getTelefono());
        entity.setTelefonos(usuarioService.serializeTelefonos(request.getTelefonos()));
        entity.setDireccion(request.getDireccion());
        entity.setCiudad(request.getCiudad());
        entity.setEstado(request.getEstado());
        entity.setCodigoPostal(request.getCodigoPostal());
        Usuario creado = usuarioService.crear(entity, request.getPassword());
        if (creado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.getId())).body(toResponse(creado));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        Usuario datos = new Usuario();
        datos.setNombre(request.getNombre());
        datos.setApellido(request.getApellido());
        datos.setEmail(request.getEmail());
        datos.setTelefono(request.getTelefono());
        datos.setTelefonos(usuarioService.serializeTelefonos(request.getTelefonos()));
        datos.setDireccion(request.getDireccion());
        datos.setCiudad(request.getCiudad());
        datos.setEstado(request.getEstado());
        datos.setCodigoPostal(request.getCodigoPostal());
        Usuario actualizado = usuarioService.actualizar(id, datos, request.getPassword());
        if (actualizado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario por su ID")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean ok = usuarioService.eliminar(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    private UsuarioResponse toResponse(Usuario u) {
        UsuarioResponse r = new UsuarioResponse();
        r.setId(u.getId());
        r.setNombre(u.getNombre());
        r.setApellido(u.getApellido());
        r.setEmail(u.getEmail());
        r.setTelefono(u.getTelefono());
        r.setTelefonos(usuarioService.parseTelefonos(u.getTelefonos()));
        r.setDireccion(u.getDireccion());
        r.setCiudad(u.getCiudad());
        r.setEstado(u.getEstado());
        r.setCodigoPostal(u.getCodigoPostal());
        return r;
    }
}
