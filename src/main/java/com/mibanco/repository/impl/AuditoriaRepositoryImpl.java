package com.mibanco.repository.impl;

import com.mibanco.model.Identificable;
import com.mibanco.model.RegistroAuditoria;
import com.mibanco.repository.AuditoriaRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación en memoria del repositorio de auditoría
 * Utiliza enfoque funcional con streams y Optional
 */
public class AuditoriaRepositoryImpl implements AuditoriaRepository {
    
    // Lista inmutable para almacenar los registros
    private final List<RegistroAuditoria<?, ?>> registros = new ArrayList<>();
    
    @Override
    public <T extends Identificable, E extends Enum<E>> RegistroAuditoria<T, E> registrar(
            RegistroAuditoria<T, E> registro) {
        // Solo añadimos el registro, nunca lo modificamos (inmutabilidad)
        registros.add(registro);
        return registro;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identificable, E extends Enum<E>> Optional<RegistroAuditoria<T, E>> buscarPorId(UUID id) {
        return registros.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .map(r -> (RegistroAuditoria<T, E>) r);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identificable, E extends Enum<E>> List<RegistroAuditoria<T, E>> obtenerHistorial(
            Class<T> tipoEntidad, 
            Long idEntidad) {
        
        return registros.stream()
                .filter(r -> tipoEntidad.isInstance(r.getEntidad()))
                .filter(r -> {
                    Identificable entidad = (Identificable) r.getEntidad();
                    return entidad.getId().equals(idEntidad);
                })
                .map(r -> (RegistroAuditoria<T, E>) r)
                .collect(Collectors.toList());
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identificable, E extends Enum<E>> List<RegistroAuditoria<T, E>> buscarPorFechas(
            LocalDateTime desde, 
            LocalDateTime hasta) {
        
        return registros.stream()
                .filter(r -> !r.getFechaHora().isBefore(desde) && !r.getFechaHora().isAfter(hasta))
                .map(r -> (RegistroAuditoria<T, E>) r)
                .collect(Collectors.toList());
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identificable, E extends Enum<E>> List<RegistroAuditoria<T, E>> buscarPorUsuario(
            String usuario) {
        
        return registros.stream()
                .filter(r -> r.getUsuario().equals(usuario))
                .map(r -> (RegistroAuditoria<T, E>) r)
                .collect(Collectors.toList());
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identificable, E extends Enum<E>> List<RegistroAuditoria<T, E>> buscarPorTipoOperacion(
            E tipoOperacion, 
            Class<E> tipoEnum) {
        
        return registros.stream()
                .filter(r -> r.getTipoOperacion().getClass().equals(tipoEnum))
                .filter(r -> r.getTipoOperacion().equals(tipoOperacion))
                .map(r -> (RegistroAuditoria<T, E>) r)
                .collect(Collectors.toList());
    }
} 