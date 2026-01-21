package com.civilportal.repository;

import com.civilportal.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByCorreo(String correo);
    List<Usuario> findByEmpresaIdAndEstadoTrue(Integer empresaId);
    boolean existsByUsername(String username);
    boolean existsByCorreo(String correo);
}
