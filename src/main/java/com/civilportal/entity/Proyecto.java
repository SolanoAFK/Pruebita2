package com.civilportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "proyectos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Proyecto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
    
    @Column(nullable = false, length = 200)
    private String nombre;
    
    @Column(length = 50)
    private String codigo;
    
    @Column(columnDefinition = "TEXT")
    private String descripcion;
    
    @Column(length = 255)
    private String ubicacion;
    
    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;
    
    @Column(name = "fecha_fin_estimada")
    private LocalDate fechaFinEstimada;
    
    @Column(name = "fecha_fin_real")
    private LocalDate fechaFinReal;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoProyecto estado = EstadoProyecto.PLANIFICACION;
    
    @Column(name = "presupuesto_estimado", precision = 18, scale = 2)
    private BigDecimal presupuestoEstimado;
    
    @Column(name = "presupuesto_real", precision = 18, scale = 2)
    private BigDecimal presupuestoReal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsable_id")
    private Usuario responsable;
    
    @ManyToMany
    @JoinTable(
        name = "proyecto_usuarios",
        joinColumns = @JoinColumn(name = "proyecto_id"),
        inverseJoinColumns = @JoinColumn(name = "usuario_id")
    )
    private Set<Usuario> usuariosAsignados = new HashSet<>();
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    public enum EstadoProyecto {
        PLANIFICACION,
        EN_EJECUCION,
        EN_PAUSA,
        COMPLETADO,
        CANCELADO
    }
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
