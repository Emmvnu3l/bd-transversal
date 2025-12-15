package com.relacional.relacional.service;

import com.relacional.relacional.model.Usuario;
import com.relacional.relacional.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

    @Transactional
    public Usuario actualizar(Long id, Usuario datos, String rawPassword) {
        Usuario actual = usuarioRepository.findById(id).orElse(null);
        if (actual == null) return null;
        if (datos.getNombre() != null) actual.setNombre(datos.getNombre());
        if (datos.getEmail() != null) {
            if (!datos.getEmail().equals(actual.getEmail()) && usuarioRepository.existsByEmail(datos.getEmail())) {
                return null;
            }
            actual.setEmail(datos.getEmail());
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
