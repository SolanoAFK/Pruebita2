package com.civilportal.controller;

import com.civilportal.entity.Permiso;
import com.civilportal.entity.Rol;
import com.civilportal.repository.PermisoRepository;
import com.civilportal.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RolController {
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private PermisoRepository permisoRepository;
    
    @GetMapping
    public ResponseEntity<List<Rol>> listarTodos() {
        return ResponseEntity.ok(rolRepository.findAll());
    }
    
    @GetMapping("/activos")
    public ResponseEntity<List<Rol>> listarActivos() {
        return ResponseEntity.ok(rolRepository.findByEstadoTrue());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return rolRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Rol rol) {
        try {
            if (rolRepository.existsByNombre(rol.getNombre())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Ya existe un rol con el nombre: " + rol.getNombre()));
            }
            Rol nuevoRol = rolRepository.save(rol);
            return ResponseEntity.status(201).body(nuevoRol);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Rol rol) {
        try {
            if (!rolRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            rol.setId(id);
            Rol rolActualizado = rolRepository.save(rol);
            return ResponseEntity.ok(rolActualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            if (!rolRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            Rol rol = rolRepository.findById(id).get();
            rol.setEstado(false);
            rolRepository.save(rol);
            return ResponseEntity.ok(Map.of("message", "Rol eliminado exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/permisos")
    public ResponseEntity<?> listarPermisos(@PathVariable Integer id) {
        return rolRepository.findById(id)
            .map(rol -> ResponseEntity.ok(rol.getPermisos()))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/permisos")
    public ResponseEntity<?> asignarPermisos(
        @PathVariable Integer id, 
        @RequestBody Set<Integer> permisoIds
    ) {
        try {
            Rol rol = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
            
            List<Permiso> permisos = permisoRepository.findAllById(permisoIds);
            if (permisos.size() != permisoIds.size()) {
                Set<Integer> encontrados = new HashSet<>();
                for (Permiso permiso : permisos) {
                    encontrados.add(permiso.getId());
                }
                Set<Integer> faltantes = new HashSet<>(permisoIds);
                faltantes.removeAll(encontrados);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Permisos no encontrados", "ids", faltantes));
            }
            
            rol.setPermisos(new HashSet<>(permisos));
            Rol rolActualizado = rolRepository.save(rol);
            return ResponseEntity.ok(rolActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
