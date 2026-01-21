package com.civilportal.security;

import com.civilportal.entity.UsuarioSesion;
import com.civilportal.repository.UsuarioSesionRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private UsuarioSesionRepository usuarioSesionRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization");
        
        String username = null;
        String jwt = null;
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extraerUsername(jwt);
            } catch (Exception e) {
                logger.error("Error al extraer username del token", e);
            }
        }
        
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Validar token y sesión activa
            if (jwtUtil.validarToken(jwt)) {
                Optional<UsuarioSesion> sesionOpt = usuarioSesionRepository.findByTokenAndActivoTrue(jwt);
                
                if (sesionOpt.isPresent()) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            username, 
                            null, 
                            Collections.emptyList() // Los roles se pueden agregar aquí si es necesario
                        );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }
        
        chain.doFilter(request, response);
    }
}
