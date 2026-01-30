package com.civilportal.repository;

import com.civilportal.entity.Usuario;
import com.civilportal.entity.UsuarioSesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioSesionRepository extends JpaRepository<UsuarioSesion, Integer> {
    Optional<UsuarioSesion> findByTokenAndActivoTrue(String token);

    Optional<UsuarioSesion> findByUsuarioIdAndActivoTrue(Integer usuarioId);

    // Métodos para gestión de sesiones
    Long countByUsuarioAndActivoTrue(Usuario usuario);

    Optional<UsuarioSesion> findTopByUsuarioAndActivoTrueOrderByFechaInicioAsc(Usuario usuario);

    List<UsuarioSesion> findByUsuarioAndActivoTrue(Usuario usuario);

    void deleteByUsuarioAndFechaExpiracionBefore(Usuario usuario, LocalDateTime fecha);
}
