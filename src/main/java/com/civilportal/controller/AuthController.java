package com.civilportal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.civilportal.security.JwtUtil;
import com.civilportal.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            String username = credenciales.get("username");
            String password = credenciales.get("password");

            if (username == null || password == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Username y password son requeridos"));
            }

            Map<String, Object> response = authService.login(username, password);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error interno del servidor"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                authService.logout(token);
                return ResponseEntity.ok(Map.of("message", "Sesión cerrada exitosamente"));
            }
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Token no proporcionado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cerrar sesión"));
        }
    }

    @GetMapping("/sessions")
    public ResponseEntity<?> obtenerSesionesActivas(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Integer usuarioId = extractUsuarioIdFromToken(token);

                var sesiones = authService.obtenerSesionesActivas(usuarioId);
                return ResponseEntity.ok(Map.of("sesiones", sesiones));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token no proporcionado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener sesiones"));
        }
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<?> cerrarSesion(@PathVariable Integer id, HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Integer usuarioId = extractUsuarioIdFromToken(token);

                authService.cerrarSesion(id, usuarioId);
                return ResponseEntity.ok(Map.of("message", "Sesión cerrada exitosamente"));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token no proporcionado"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al cerrar sesión"));
        }
    }

    private Integer extractUsuarioIdFromToken(String token) {
        return jwtUtil.extraerUsuarioId(token);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                Integer usuarioId = extractUsuarioIdFromToken(token);

                var userInfo = authService.obtenerInfoUsuario(usuarioId);
                return ResponseEntity.ok(userInfo);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token no proporcionado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener información del usuario"));
        }
    }
}
