package com.civilportal.repository;

import com.civilportal.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {
    List<Auditoria> findByUsuarioId(Integer usuarioId);
    List<Auditoria> findByEmpresaId(Integer empresaId);
    List<Auditoria> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);
    List<Auditoria> findByEvento(String evento);
}
