package com.civilportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;
    
    @Column(name = "proyecto_id")
    private Integer proyectoId;
    
    @Column(nullable = false, length = 100)
    private String evento;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(length = 50)
    private String entidad; // Nombre de la entidad afectada (Usuario, Proyecto, etc.)
    
    @Column(name = "entidad_id")
    private Integer entidadId;
    
    @Column(length = 20)
    private String accion; // CREAR, ACTUALIZAR, ELIMINAR, CONSULTAR
    
    @Column(columnDefinition = "TEXT")
    private String datosAnteriores; // JSON con datos anteriores
    
    @Column(columnDefinition = "TEXT")
    private String datosNuevos; // JSON con datos nuevos
    
    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();
    
    @Column(length = 50)
    private String ip;
    
    @Column(length = 500)
    private String userAgent;
    
    @Column(nullable = false)
    private Boolean estado = true;
}
