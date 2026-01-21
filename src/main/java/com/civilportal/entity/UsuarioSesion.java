package com.civilportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_sesiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSesion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Column(nullable = false, length = 500)
    private String token;
    
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;
    
    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;
    
    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;
    
    @Column(length = 50)
    private String ip;
    
    @Column(length = 500)
    private String userAgent;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @PrePersist
    protected void onCreate() {
        fechaInicio = LocalDateTime.now();
    }
}
