package com.civilportal.service;

import com.civilportal.entity.Rol;
import com.civilportal.entity.Usuario;
import com.civilportal.repository.RolRepository;
import com.civilportal.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UsuarioService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }
    
    public List<Usuario> buscarPorEmpresa(Integer empresaId) {
        return usuarioRepository.findByEmpresaIdAndEstadoTrue(empresaId);
    }
    
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }
    
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }
    
    @Transactional
    public Usuario guardar(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("Ya existe un usuario con el username: " + usuario.getUsername());
        }
        
        if (usuario.getCorreo() != null && usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con el correo: " + usuario.getCorreo());
        }
        
        // Encriptar contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public Usuario actualizar(Usuario usuario) {
        if (!usuarioRepository.existsById(usuario.getId())) {
            throw new RuntimeException("Usuario no encontrado con ID: " + usuario.getId());
        }
        
        Usuario usuarioExistente = usuarioRepository.findById(usuario.getId()).get();
        
        // Si se cambió el username, validar que no exista
        if (!usuarioExistente.getUsername().equals(usuario.getUsername()) 
            && usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new RuntimeException("Ya existe un usuario con el username: " + usuario.getUsername());
        }
        
        // Si se cambió el correo, validar que no exista
        if (usuario.getCorreo() != null 
            && !usuarioExistente.getCorreo().equals(usuario.getCorreo())
            && usuarioRepository.existsByCorreo(usuario.getCorreo())) {
            throw new RuntimeException("Ya existe un usuario con el correo: " + usuario.getCorreo());
        }
        
        // Si se cambió la contraseña, encriptarla
        if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        } else {
            usuario.setPassword(usuarioExistente.getPassword());
        }
        
        return usuarioRepository.save(usuario);
    }
    
    @Transactional
    public void eliminar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        Usuario usuario = usuarioRepository.findById(id).get();
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
    }
    
    @Transactional
    public Usuario asignarRoles(Integer usuarioId, Set<Integer> rolIds) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Set<Rol> roles = rolRepository.findAllById(rolIds).stream()
            .collect(java.util.stream.Collectors.toSet());
        
        usuario.setRoles(roles);
        return usuarioRepository.save(usuario);
    }
}
