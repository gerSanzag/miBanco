package com.mibanco.repositorio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.mibanco.modelo.Identificable;
import com.mibanco.modelo.RegistroAuditoria;

/**
 * Repositorio para almacenar y consultar registros de auditoría
 * Sigue el enfoque funcional con Optional y generics
 */
public interface AuditoriaRepositorio {
    
    /**
     * Guarda un registro de auditoría
     * @param registro Optional con el registro a guardar
     * @return Optional con el registro guardado
     */
    <T extends Identificable, E extends Enum<E>> Optional<RegistroAuditoria<T, E>> registrar(Optional<RegistroAuditoria<T, E>> registro);
    
    /**
     * Busca un registro por su ID único
     * @param id Optional con el ID único del registro
     * @return Optional con el registro si existe
     */
    <T extends Identificable, E extends Enum<E>> Optional<RegistroAuditoria<T, E>> buscarPorId(Optional<UUID> id);
    
    /**
     * Obtiene el historial de una entidad por su tipo e ID
     * @param tipoEntidad Optional con la clase de la entidad
     * @param idEntidad Optional con el ID de la entidad
     * @return Optional con lista de registros de auditoría relacionados con esa entidad
     */
    <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> obtenerHistorial(
            Optional<Class<T>> tipoEntidad, 
            Optional<Long> idEntidad);
    
    /**
     * Obtiene registros en un rango de fechas
     * @param desde Optional con la fecha inicial
     * @param hasta Optional con la fecha final
     * @return Optional con lista de registros en ese rango de fechas
     */
    <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> buscarPorFechas(
            Optional<LocalDateTime> desde, 
            Optional<LocalDateTime> hasta);
    
    /**
     * Obtiene registros creados por un usuario específico
     * @param usuario Optional con el usuario que realizó las operaciones
     * @return Optional con lista de registros del usuario
     */
    <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> buscarPorUsuario(Optional<String> usuario);
    
    /**
     * Busca registros por tipo de operación
     * @param tipoOperacion Optional con el tipo de operación
     * @param tipoEnum Optional con la clase del enum
     * @return Optional con lista de registros que corresponden a esa operación
     */
    <T extends Identificable, E extends Enum<E>> Optional<List<RegistroAuditoria<T, E>>> buscarPorTipoOperacion(
            Optional<E> tipoOperacion, 
            Optional<Class<E>> tipoEnum);
} 