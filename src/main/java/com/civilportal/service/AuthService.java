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
import java.util.List;
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

        // 1. Limpiar sesiones expiradas del usuario
        usuarioSesionRepository.deleteByUsuarioAndFechaExpiracionBefore(
                usuario,
                LocalDateTime.now());

        // 2. Verificar límite de sesiones activas (máximo 3)
        Long sesionesActivas = usuarioSesionRepository.countByUsuarioAndActivoTrue(usuario);

        if (sesionesActivas >= 3) {
            // Cerrar la sesión más antigua
            Optional<UsuarioSesion> sesionMasAntiguaOpt = usuarioSesionRepository
                    .findTopByUsuarioAndActivoTrueOrderByFechaInicioAsc(usuario);

            if (sesionMasAntiguaOpt.isPresent()) {
                UsuarioSesion sesionAntigua = sesionMasAntiguaOpt.get();
                sesionAntigua.setActivo(false);
                sesionAntigua.setFechaCierre(LocalDateTime.now());
                usuarioSesionRepository.save(sesionAntigua);
            }
        }

        // 3. Generar token JWT
        String token = jwtUtil.generarToken(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getEmpresa().getId());

        // 4. Guardar nueva sesión
        UsuarioSesion sesion = new UsuarioSesion();
        sesion.setUsuario(usuario);
        sesion.setToken(token);
        sesion.setFechaExpiracion(LocalDateTime.now().plusHours(24));
        sesion.setActivo(true);
        usuarioSesionRepository.save(sesion);

        // 5. Actualizar último acceso
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        // 6. Registrar en auditoría
        auditoriaService.registrarLogin(usuario);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", Map.of(
                "id", usuario.getId(),
                "username", usuario.getUsername(),
                "nombre", usuario.getNombre(),
                "correo", usuario.getCorreo(),
                "empresaId", usuario.getEmpresa().getId()));

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

    public List<Map<String, Object>> obtenerSesionesActivas(Integer usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        List<UsuarioSesion> sesiones = usuarioSesionRepository.findByUsuarioAndActivoTrue(usuario);

        return sesiones.stream()
                .map(sesion -> {
                    Map<String, Object> sesionMap = new HashMap<>();
                    sesionMap.put("id", sesion.getId());
                    sesionMap.put("fechaInicio", sesion.getFechaInicio().toString());
                    sesionMap.put("fechaExpiracion", sesion.getFechaExpiracion().toString());
                    sesionMap.put("ip", sesion.getIp() != null ? sesion.getIp() : "N/A");
                    sesionMap.put("userAgent", sesion.getUserAgent() != null ? sesion.getUserAgent() : "N/A");
                    return sesionMap;
                })
                .toList();
    }

    @Transactional
    public void cerrarSesion(Integer sesionId, Integer usuarioId) {
        Optional<UsuarioSesion> sesionOpt = usuarioSesionRepository.findById(sesionId);

        if (sesionOpt.isEmpty()) {
            throw new RuntimeException("Sesión no encontrada");
        }

        UsuarioSesion sesion = sesionOpt.get();

        // Validar que la sesión pertenece al usuario
        if (!sesion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tiene permiso para cerrar esta sesión");
        }

        sesion.setActivo(false);
        sesion.setFechaCierre(LocalDateTime.now());
        usuarioSesionRepository.save(sesion);
    }

    public Map<String, Object> obtenerInfoUsuario(Integer usuarioId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", usuario.getId());
        userInfo.put("username", usuario.getUsername());
        userInfo.put("nombre", usuario.getNombre());
        userInfo.put("correo", usuario.getCorreo());
        userInfo.put("empresaId", usuario.getEmpresa().getId());
        userInfo.put("estado", usuario.getEstado());

        // Add roles with permissions
        List<Map<String, Object>> rolesInfo = usuario.getRoles().stream()
                .map(rol -> {
                    Map<String, Object> rolMap = new HashMap<>();
                    rolMap.put("id", rol.getId());
                    rolMap.put("nombre", rol.getNombre());
                    rolMap.put("descripcion", rol.getDescripcion());

                    // Add permissions for this role
                    List<Map<String, Object>> permisosInfo = rol.getPermisos().stream()
                            .map(permiso -> {
                                Map<String, Object> permisoMap = new HashMap<>();
                                permisoMap.put("id", permiso.getId());
                                permisoMap.put("nombre", permiso.getNombre());
                                permisoMap.put("recurso", permiso.getRecurso());
                                permisoMap.put("accion", permiso.getAccion());
                                return permisoMap;
                            })
                            .toList();

                    rolMap.put("permisos", permisosInfo);
                    return rolMap;
                })
                .toList();

        userInfo.put("roles", rolesInfo);

        return userInfo;
    }
}
