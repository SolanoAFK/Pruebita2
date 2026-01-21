package com.civilportal.repository;

import com.civilportal.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    Optional<Empresa> findByRuc(String ruc);
    List<Empresa> findByEstadoTrue();
    boolean existsByRuc(String ruc);
}
