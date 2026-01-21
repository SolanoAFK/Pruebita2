package com.civilportal.controller;

import com.civilportal.entity.Usuario;
import com.civilportal.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.buscarTodos());
    }
    
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Usuario>> listarPorEmpresa(@PathVariable Integer empresaId) {
        return ResponseEntity.ok(usuarioService.buscarPorEmpresa(empresaId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return usuarioService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Usuario usuario) {
        try {
            Usuario nuevoUsuario = usuarioService.guardar(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        try {
            usuario.setId(id);
            Usuario usuarioActualizado = usuarioService.actualizar(usuario);
            return ResponseEntity.ok(usuarioActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            usuarioService.eliminar(id);
            return ResponseEntity.ok(Map.of("message", "Usuario eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/roles")
    public ResponseEntity<?> asignarRoles(@PathVariable Integer id, @RequestBody Set<Integer> rolIds) {
        try {
            Usuario usuario = usuarioService.asignarRoles(id, rolIds);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
