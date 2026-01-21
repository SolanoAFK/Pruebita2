package com.civilportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permisos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Permiso {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(nullable = false, length = 100, unique = true)
    private String nombre;
    
    @Column(length = 255)
    private String descripcion;
    
    @Column(length = 50)
    private String recurso; // Ej: "proyecto", "usuario", "documento"
    
    @Column(length = 50)
    private String accion; // Ej: "crear", "leer", "actualizar", "eliminar"
    
    @Column(nullable = false)
    private Boolean estado = true;
}
