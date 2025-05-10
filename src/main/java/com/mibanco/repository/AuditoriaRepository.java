package com.mibanco.repository;

import com.mibanco.model.Identificable;
import com.mibanco.model.RegistroAuditoria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para almacenar y consultar registros de auditoría
 * Sigue el enfoque funcional con Optional y generics
 */
public interface AuditoriaRepository {
    
    /**
     * Guarda un registro de auditoría
     * @param registro Registro a guardar
     * @return El registro guardado (nunca se modifica, solo se almacena)
     */
    <T extends Identificable, E extends Enum<E>> RegistroAuditoria<T, E> registrar(RegistroAuditoria<T, E> registro);
    
    /**
     * Busca un registro por su ID único
     * @param id ID único del registro
     * @return Optional con el registro si existe
     */
    <T extends Identificable, E extends Enum<E>> Optional<RegistroAuditoria<T, E>> buscarPorId(UUID id);
    
    /**
     * Obtiene el historial de una entidad por su tipo e ID
     * @param tipoEntidad Clase de la entidad
     * @param idEntidad ID de la entidad
     * @return Lista de registros de auditoría relacionados con esa entidad
     */
    <T extends Identificable, E extends Enum<E>> List<RegistroAuditoria<T, E>> obtenerHistorial(
            Class<T> tipoEntidad, 
            Long idEntidad);
    
    /**
     * Obtiene registros en un rango de fechas
     * @param desde Fecha inicial
     * @param hasta Fecha final
     * @return Lista de registros en ese rango de fechas
     */
    <T extends Identificable, E extends Enum<E>> List<RegistroAuditoria<T, E>> buscarPorFechas(
            LocalDateTime desde, 
            LocalDateTime hasta);
    
    /**
     * Obtiene registros creados por un usuario específico
     * @param usuario Usuario que realizó las operaciones
     * @return Lista de registros del usuario
     */
    <T extends Identificable, E extends Enum<E>> List<RegistroAuditoria<T, E>> buscarPorUsuario(String usuario);
    
    /**
     * Busca registros por tipo de operación
     * @param tipoOperacion Tipo de operación
     * @param tipoEnum Clase del enum
     * @return Lista de registros que corresponden a esa operación
     */
    <T extends Identificable, E extends Enum<E>> List<RegistroAuditoria<T, E>> buscarPorTipoOperacion(
            E tipoOperacion, 
            Class<E> tipoEnum);
} 