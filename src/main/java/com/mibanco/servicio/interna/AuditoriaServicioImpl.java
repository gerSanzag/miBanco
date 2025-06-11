package com.mibanco.servicio.interna;

import com.mibanco.modelo.Identificable;
import com.mibanco.modelo.RegistroAuditoria;
import com.mibanco.repositorio.AuditoriaRepositorio;
import com.mibanco.repositorio.interna.RepositorioFactoria;
import com.mibanco.servicio.AuditoriaServicio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio de consulta de Auditoría
 * El registro de auditorías se maneja a través de AuditoriaUtil
 */
class AuditoriaServicioImpl implements AuditoriaServicio {
    
    private static final AuditoriaRepositorio repositorioAuditoria;
    
    static {
        repositorioAuditoria = RepositorioFactoria.obtenerInstancia().obtenerRepositorioAuditoria();
    }
    
    /**
     * Constructor con visibilidad de paquete
     */
    AuditoriaServicioImpl() {
        // Constructor vacío con visibilidad de paquete
    }
    
    @Override
    public <T extends Identificable, E extends Enum<E>> Optional<RegistroAuditoria<T, E>> buscarPorId(
            Optional<UUID> id) {
        return repositorioAuditoria.buscarPorId(id);
    }
    
    @Override
    public <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> obtenerHistorial(
            Optional<Class<T>> tipoEntidad, 
            Optional<Long> idEntidad) {
        return repositorioAuditoria.obtenerHistorial(tipoEntidad, idEntidad);
    }
    
    @Override
    public <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> buscarPorFechas(
            Optional<LocalDateTime> desde, 
            Optional<LocalDateTime> hasta) {
        return repositorioAuditoria.buscarPorFechas(desde, hasta);
    }
    
    @Override
    public <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> buscarPorUsuario(
            Optional<String> usuario) {
        return repositorioAuditoria.buscarPorUsuario(usuario);
    }
    
    @Override
    public <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> buscarPorTipoOperacion(
            Optional<E> tipoOperacion, 
            Optional<Class<E>> tipoEnum) {
        return repositorioAuditoria.buscarPorTipoOperacion(tipoOperacion, tipoEnum);
    }
} 