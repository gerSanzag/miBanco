package com.mibanco.repository;

import com.mibanco.model.Transaccion;
import com.mibanco.model.enums.TipoTransaccion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones CRUD de Transaccion
 * Siguiendo enfoque estrictamente funcional con Optional
 * tanto para parámetros como para resultados
 */
public interface TransaccionRepository {
    
    /**
     * Guarda una transacción en el repositorio
     * @param transaccion Optional con la transacción a guardar
     * @return Optional con la transacción guardada con ID generado
     * o Optional vacío si la transacción era null
     */
    Optional<Transaccion> save(Optional<Transaccion> transaccion);
    
    /**
     * Busca una transacción por su ID
     * @param id Optional con el ID de la transacción a buscar
     * @return Optional con la transacción si existe
     */
    Optional<Transaccion> findById(Optional<Long> id);
    
    /**
     * Busca transacciones por el número de cuenta
     * @param numeroCuenta Optional con el número de cuenta
     * @return Lista de transacciones de la cuenta
     */
    List<Transaccion> findByCuenta(Optional<String> numeroCuenta);
    
    /**
     * Busca transacciones por el tipo
     * @param tipo Optional con el tipo de transacción
     * @return Lista de transacciones del tipo especificado
     */
    List<Transaccion> findByTipo(Optional<TipoTransaccion> tipo);
    
    /**
     * Busca transacciones por fecha
     * @param fecha Optional con la fecha de las transacciones
     * @return Lista de transacciones de la fecha especificada
     */
    List<Transaccion> findByFecha(Optional<LocalDate> fecha);
    
    /**
     * Busca transacciones en un rango de fechas
     * @param fechaInicio Optional con la fecha de inicio
     * @param fechaFin Optional con la fecha de fin
     * @return Lista de transacciones en el rango especificado
     */
    List<Transaccion> findByRangoFechas(Optional<LocalDate> fechaInicio, Optional<LocalDate> fechaFin);
    
    /**
     * Obtiene todas las transacciones
     * @return Lista de transacciones
     */
    Optional<List<Transaccion>> findAll();
    
    /**
     * Elimina una transacción por su ID
     * @param id Optional con el ID de la transacción a eliminar
     * @return Optional con la transacción eliminada o Optional vacío si no existía
     */
    Optional<Transaccion> deleteById(Optional<Long> id);
    
    /**
     * Obtiene el número de transacciones en el repositorio
     * @return Número de transacciones
     */
    long count();
} 