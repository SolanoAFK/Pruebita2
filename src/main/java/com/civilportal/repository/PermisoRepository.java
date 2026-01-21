package com.civilportal.repository;

import com.civilportal.entity.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Integer> {
    Optional<Permiso> findByNombre(String nombre);
    List<Permiso> findByEstadoTrue();
    List<Permiso> findByRecursoAndAccion(String recurso, String accion);
}
