package com.civilportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;
    
    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(length = 100, unique = true)
    private String correo;
    
    @Column(nullable = false, length = 100)
    private String username;
    
    @Column(nullable = false, length = 255)
    private String password;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "usuario_roles",
        joinColumns = @JoinColumn(name = "usuario_id"),
        inverseJoinColumns = @JoinColumn(name = "rol_id")
    )
    private Set<Rol> roles = new HashSet<>();
    
    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;
    
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;
    
    @Column(nullable = false)
    private Boolean estado = true;
    
    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}
