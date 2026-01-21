package com.civilportal.service;

import com.civilportal.entity.Auditoria;
import com.civilportal.entity.Usuario;
import com.civilportal.repository.AuditoriaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditoriaService {
    
    @Autowired
    private AuditoriaRepository auditoriaRepository;
    
    @Transactional
    public void registrarLogin(Usuario usuario) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setEmpresa(usuario.getEmpresa());
        auditoria.setEvento("INICIO_DE_SESION");
        auditoria.setDescripcion("El usuario ha iniciado sesión en el sistema.");
        auditoria.setAccion("LOGIN");
        auditoria.setEntidad("Usuario");
        auditoria.setEntidadId(usuario.getId());
        auditoriaRepository.save(auditoria);
    }
    
    @Transactional
    public void registrarLogout(Usuario usuario) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setEmpresa(usuario.getEmpresa());
        auditoria.setEvento("CIERRE_DE_SESION");
        auditoria.setDescripcion("El usuario cerró su sesión en el sistema.");
        auditoria.setAccion("LOGOUT");
        auditoria.setEntidad("Usuario");
        auditoria.setEntidadId(usuario.getId());
        auditoriaRepository.save(auditoria);
    }
    
    @Transactional
    public void registrarEvento(Usuario usuario, String evento, String descripcion, 
                               String entidad, Integer entidadId, String accion,
                               HttpServletRequest request) {
        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setEmpresa(usuario.getEmpresa());
        auditoria.setEvento(evento);
        auditoria.setDescripcion(descripcion);
        auditoria.setEntidad(entidad);
        auditoria.setEntidadId(entidadId);
        auditoria.setAccion(accion);
        
        if (request != null) {
            auditoria.setIp(obtenerIp(request));
            auditoria.setUserAgent(request.getHeader("User-Agent"));
        }
        
        auditoriaRepository.save(auditoria);
    }
    
    private String obtenerIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
