package com.civilportal.service;

import com.civilportal.entity.Usuario;
import com.civilportal.entity.UsuarioSesion;
import com.civilportal.repository.UsuarioRepository;
import com.civilportal.repository.UsuarioSesionRepository;
import com.civilportal.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private UsuarioSesionRepository usuarioSesionRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private AuditoriaService auditoriaService;
    
    public Map<String, Object> login(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);
        
        if (usuarioOpt.isEmpty() || !usuarioOpt.get().getEstado()) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        // Generar token JWT
        String token = jwtUtil.generarToken(
            usuario.getId(), 
            usuario.getUsername(), 
            usuario.getEmpresa().getId()
        );
        
        // Guardar sesión
        UsuarioSesion sesion = new UsuarioSesion();
        sesion.setUsuario(usuario);
        sesion.setToken(token);
        sesion.setFechaExpiracion(LocalDateTime.now().plusHours(24));
        sesion.setActivo(true);
        usuarioSesionRepository.save(sesion);
        
        // Actualizar último acceso
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);
        
        // Registrar en auditoría
        auditoriaService.registrarLogin(usuario);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", Map.of(
            "id", usuario.getId(),
            "username", usuario.getUsername(),
            "nombre", usuario.getNombre(),
            "correo", usuario.getCorreo(),
            "empresaId", usuario.getEmpresa().getId()
        ));
        
        return response;
    }
    
    @Transactional
    public void logout(String token) {
        Optional<UsuarioSesion> sesionOpt = usuarioSesionRepository.findByTokenAndActivoTrue(token);
        
        if (sesionOpt.isPresent()) {
            UsuarioSesion sesion = sesionOpt.get();
            sesion.setActivo(false);
            sesion.setFechaCierre(LocalDateTime.now());
            usuarioSesionRepository.save(sesion);
            
            // Registrar en auditoría
            auditoriaService.registrarLogout(sesion.getUsuario());
        }
    }
}
