package com.relacional.relacional.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.relacional.relacional.model.Usuario;
import com.relacional.relacional.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Transactional
    public Usuario crear(Usuario usuario, String rawPassword) {
        if (usuario.getEmail() != null && usuarioRepository.existsByEmail(usuario.getEmail())) {
            return null;
        }
        if (rawPassword != null && !rawPassword.isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(rawPassword));
        } else {
            return null;
        }
        return usuarioRepository.save(usuario);
    }

    public List<String> parseTelefonos(String telefonosJson) {
        if (telefonosJson == null || telefonosJson.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(telefonosJson, List.class);
        } catch (JsonProcessingException e) {
            return new ArrayList<>();
        }
    }

    public String serializeTelefonos(List<String> telefonos) {
        if (telefonos == null || telefonos.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(telefonos);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Transactional
    public Usuario actualizar(Long id, Usuario datos, String rawPassword) {
        Usuario actual = usuarioRepository.findById(id).orElse(null);
        if (actual == null) return null;
        if (datos.getNombre() != null) actual.setNombre(datos.getNombre());
        if (datos.getApellido() != null) actual.setApellido(datos.getApellido());
        if (datos.getEmail() != null) {
            if (!datos.getEmail().equals(actual.getEmail()) && usuarioRepository.existsByEmail(datos.getEmail())) {
                return null;
            }
            actual.setEmail(datos.getEmail());
        }
        if (datos.getTelefono() != null) {
            actual.setTelefono(datos.getTelefono());
        }
        if (datos.getTelefonos() != null) {
            actual.setTelefonos(datos.getTelefonos());
        }
        if (datos.getDireccion() != null) {
            actual.setDireccion(datos.getDireccion());
        }
        if (datos.getCiudad() != null) {
            actual.setCiudad(datos.getCiudad());
        }
        if (datos.getEstado() != null) {
            actual.setEstado(datos.getEstado());
        }
        if (datos.getCodigoPostal() != null) {
            actual.setCodigoPostal(datos.getCodigoPostal());
        }
        if (rawPassword != null && !rawPassword.isBlank()) {
            actual.setPasswordHash(passwordEncoder.encode(rawPassword));
        }
        return usuarioRepository.save(actual);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) return false;
        usuarioRepository.deleteById(id);
        return true;
    }
}
