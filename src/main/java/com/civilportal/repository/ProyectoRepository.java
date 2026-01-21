package com.civilportal.repository;

import com.civilportal.entity.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {
    List<Proyecto> findByEmpresaIdAndActivoTrue(Integer empresaId);
    List<Proyecto> findByResponsableId(Integer responsableId);
}
