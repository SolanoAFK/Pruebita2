package com.civilportal.repository;

import com.civilportal.entity.UsuarioSesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioSesionRepository extends JpaRepository<UsuarioSesion, Integer> {
    Optional<UsuarioSesion> findByTokenAndActivoTrue(String token);
    Optional<UsuarioSesion> findByUsuarioIdAndActivoTrue(Integer usuarioId);
}
