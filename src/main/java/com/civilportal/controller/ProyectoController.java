package com.civilportal.controller;

import com.civilportal.entity.Proyecto;
import com.civilportal.service.ProyectoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {
    
    @Autowired
    private ProyectoService proyectoService;
    
    @GetMapping
    public ResponseEntity<List<Proyecto>> listarTodos() {
        return ResponseEntity.ok(proyectoService.buscarTodos());
    }
    
    @GetMapping("/empresa/{empresaId}")
    public ResponseEntity<List<Proyecto>> listarPorEmpresa(@PathVariable Integer empresaId) {
        return ResponseEntity.ok(proyectoService.buscarPorEmpresa(empresaId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return proyectoService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Proyecto proyecto) {
        try {
            Proyecto nuevoProyecto = proyectoService.guardar(proyecto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProyecto);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Proyecto proyecto) {
        try {
            proyecto.setId(id);
            Proyecto proyectoActualizado = proyectoService.actualizar(proyecto);
            return ResponseEntity.ok(proyectoActualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            proyectoService.eliminar(id);
            return ResponseEntity.ok(Map.of("message", "Proyecto eliminado exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
