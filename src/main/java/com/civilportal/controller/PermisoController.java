package com.civilportal.controller;

import com.civilportal.entity.Permiso;
import com.civilportal.repository.PermisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/permisos")
public class PermisoController {
    
    @Autowired
    private PermisoRepository permisoRepository;
    
    @GetMapping
    public ResponseEntity<List<Permiso>> listarTodos() {
        return ResponseEntity.ok(permisoRepository.findAll());
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Permiso>> listarActivos() {
        return ResponseEntity.ok(permisoRepository.findByEstadoTrue());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return permisoRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Permiso permiso) {
        try {
            if (permisoRepository.existsById(permiso.getId())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ya existe un permiso con ese ID"));
            }
            Permiso nuevoPermiso = permisoRepository.save(permiso);
            return ResponseEntity.status(201).body(nuevoPermiso);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Permiso permiso) {
        try {
            if (!permisoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            permiso.setId(id);
            Permiso permisoActualizado = permisoRepository.save(permiso);
            return ResponseEntity.ok(permisoActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            if (!permisoRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            Permiso permiso = permisoRepository.findById(id).get();
            permiso.setEstado(false);
            permisoRepository.save(permiso);
            return ResponseEntity.ok(Map.of("message", "Permiso eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
