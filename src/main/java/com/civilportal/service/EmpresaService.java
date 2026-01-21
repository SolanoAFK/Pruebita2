package com.civilportal.service;

import com.civilportal.entity.Empresa;
import com.civilportal.repository.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {
    
    @Autowired
    private EmpresaRepository empresaRepository;
    
    public List<Empresa> buscarTodas() {
        return empresaRepository.findAll();
    }
    
    public List<Empresa> buscarActivas() {
        return empresaRepository.findByEstadoTrue();
    }
    
    public Optional<Empresa> buscarPorId(Integer id) {
        return empresaRepository.findById(id);
    }
    
    public Optional<Empresa> buscarPorRuc(String ruc) {
        return empresaRepository.findByRuc(ruc);
    }
    
    @Transactional
    public Empresa guardar(Empresa empresa) {
        if (empresa.getRuc() != null && empresaRepository.existsByRuc(empresa.getRuc())) {
            throw new RuntimeException("Ya existe una empresa con el RUC: " + empresa.getRuc());
        }
        return empresaRepository.save(empresa);
    }
    
    @Transactional
    public Empresa actualizar(Empresa empresa) {
        if (!empresaRepository.existsById(empresa.getId())) {
            throw new RuntimeException("Empresa no encontrada con ID: " + empresa.getId());
        }
        return empresaRepository.save(empresa);
    }
    
    @Transactional
    public void eliminar(Integer id) {
        if (!empresaRepository.existsById(id)) {
            throw new RuntimeException("Empresa no encontrada con ID: " + id);
        }
        Empresa empresa = empresaRepository.findById(id).get();
        empresa.setEstado(false);
        empresaRepository.save(empresa);
    }
}
