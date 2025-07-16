package com.mibanco.repositorio.interna;

import com.mibanco.modelo.Identificable;
import com.mibanco.modelo.RegistroAuditoria;
import com.mibanco.repositorio.AuditoriaRepositorio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación en memoria del repositorio de auditoría
 * Visibilidad restringida al paquete internal
 */
class AuditoriaRepositorioImpl implements AuditoriaRepositorio {
    
    // Lista para almacenar los registros
    private final List<RegistroAuditoria<?, ?>> registros = new ArrayList<>();
    
    /**
     * Constructor con visibilidad de paquete
     */
    AuditoriaRepositorioImpl() {
        // Constructor vacío con visibilidad de paquete
    }
    
    @Override
    public <T extends Identificable, E extends Enum<E>> Optional<RegistroAuditoria<T, E>> registrar(
            Optional<RegistroAuditoria<T, E>> registroOpt) {
        return registroOpt.map(registro -> {
            registros.add(registro);
            return registro;
        });
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identificable, E extends Enum<E>> Optional<RegistroAuditoria<T, E>> buscarPorId(Optional<UUID> idOpt) {
        return idOpt.flatMap(id -> 
            registros.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .map(r -> (RegistroAuditoria<T, E>) r)
        );
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> obtenerHistorial(
            Optional<Class<T>> tipoEntidad, 
            Optional<Long> idEntidad) {
        return tipoEntidad.flatMap(tipo -> 
            idEntidad.map(id -> 
                registros.stream()
                    .filter(r -> tipo.isInstance(r.getEntidad()) && 
                               r.getEntidad().getId().equals(id))
                    .map(r -> (RegistroAuditoria<T, E>) r)
                    .collect(Collectors.toList())
            ).map(Optional::of)
        ).orElse(Optional.of(new ArrayList<>()));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> buscarPorFechas(
            Optional<LocalDateTime> desdeOpt, 
            Optional<LocalDateTime> hastaOpt) {
        
        return desdeOpt.flatMap(desde -> 
            hastaOpt.map(hasta -> 
                registros.stream()
                    .filter(r -> r.getFechaHora() != null && !r.getFechaHora().isBefore(desde) && !r.getFechaHora().isAfter(hasta))
                    .map(r -> (RegistroAuditoria<T, E>) r)
                    .collect(Collectors.toList())
            )
        ).map(Optional::of).orElse(Optional.of(new ArrayList<>()));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> buscarPorUsuario(
            Optional<String> usuario) {
        return usuario.map(u -> 
            registros.stream()
                .filter(r -> r.getUsuario().equals(u))
                .map(r -> (RegistroAuditoria<T, E>) r)
                .collect(Collectors.toList())
        );
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> buscarPorTipoOperacion(
            Optional<E> tipoOperacion,
            Optional<Class<E>> tipoEnum) {
        return tipoOperacion.flatMap(tipo ->
            tipoEnum.map(enumClass ->
                registros.stream()
                    .filter(r -> r.getTipoOperacion().equals(tipo))
                    .map(r -> (RegistroAuditoria<T, E>) r)
                    .collect(Collectors.toList())
            )
        );
    }
} 