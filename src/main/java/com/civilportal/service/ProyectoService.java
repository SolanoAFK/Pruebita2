package com.civilportal.service;

import com.civilportal.entity.Proyecto;
import com.civilportal.repository.ProyectoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProyectoService {
    
    @Autowired
    private ProyectoRepository proyectoRepository;
    
    public List<Proyecto> buscarTodos() {
        return proyectoRepository.findAll();
    }
    
    public List<Proyecto> buscarPorEmpresa(Integer empresaId) {
        return proyectoRepository.findByEmpresaIdAndActivoTrue(empresaId);
    }
    
    public Optional<Proyecto> buscarPorId(Integer id) {
        return proyectoRepository.findById(id);
    }
    
    @Transactional
    public Proyecto guardar(Proyecto proyecto) {
        return proyectoRepository.save(proyecto);
    }
    
    @Transactional
    public Proyecto actualizar(Proyecto proyecto) {
        if (!proyectoRepository.existsById(proyecto.getId())) {
            throw new RuntimeException("Proyecto no encontrado con ID: " + proyecto.getId());
        }
        return proyectoRepository.save(proyecto);
    }
    
    @Transactional
    public void eliminar(Integer id) {
        if (!proyectoRepository.existsById(id)) {
            throw new RuntimeException("Proyecto no encontrado con ID: " + id);
        }
        Proyecto proyecto = proyectoRepository.findById(id).get();
        proyecto.setActivo(false);
        proyectoRepository.save(proyecto);
    }
}
