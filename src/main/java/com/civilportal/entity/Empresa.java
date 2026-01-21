package com.civilportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "empresas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 20, unique = true)
    private String ruc;
    
    @Column(length = 255)
    private String direccion;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 100)
    private String correo;
    
    @Column(length = 500)
    private String descripcion;
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @Column(nullable = false)
    private Boolean estado = true;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
