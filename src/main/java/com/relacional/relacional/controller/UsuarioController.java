package com.relacional.relacional.controller;

import com.relacional.relacional.dto.UsuarioRequest;
import com.relacional.relacional.dto.UsuarioResponse;
import com.relacional.relacional.model.Usuario;
import com.relacional.relacional.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> list = usuarioService.listar().stream().map(this::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        Usuario u = usuarioService.obtener(id);
        if (u == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(toResponse(u));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@RequestBody UsuarioRequest request) {
        Usuario entity = new Usuario();
        entity.setNombre(request.getNombre());
        entity.setEmail(request.getEmail());
        Usuario creado = usuarioService.crear(entity, request.getPassword());
        if (creado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.created(URI.create("/api/usuarios/" + creado.getId())).body(toResponse(creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        Usuario datos = new Usuario();
        datos.setNombre(request.getNombre());
        datos.setEmail(request.getEmail());
        Usuario actualizado = usuarioService.actualizar(id, datos, request.getPassword());
        if (actualizado == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(toResponse(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean ok = usuarioService.eliminar(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }

    private UsuarioResponse toResponse(Usuario u) {
        UsuarioResponse r = new UsuarioResponse();
        r.setId(u.getId());
        r.setNombre(u.getNombre());
        r.setEmail(u.getEmail());
        return r;
    }
}
