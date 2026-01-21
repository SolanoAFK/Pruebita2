package com.civilportal.controller;

import com.civilportal.entity.Empresa;
import com.civilportal.service.EmpresaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {
    
    @Autowired
    private EmpresaService empresaService;
    
    @GetMapping
    public ResponseEntity<List<Empresa>> listarTodas() {
        return ResponseEntity.ok(empresaService.buscarTodas());
    }
    
    @GetMapping("/activas")
    public ResponseEntity<List<Empresa>> listarActivas() {
        return ResponseEntity.ok(empresaService.buscarActivas());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        return empresaService.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Empresa empresa, HttpServletRequest request) {
        try {
            Empresa nuevaEmpresa = empresaService.guardar(empresa);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaEmpresa);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Integer id, @RequestBody Empresa empresa) {
        try {
            empresa.setId(id);
            Empresa empresaActualizada = empresaService.actualizar(empresa);
            return ResponseEntity.ok(empresaActualizada);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        try {
            empresaService.eliminar(id);
            return ResponseEntity.ok(Map.of("message", "Empresa eliminada exitosamente"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
