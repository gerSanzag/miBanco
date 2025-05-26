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
     * Busca una transacción por su ID
     * @param id Optional con el ID de la transacción a buscar
     * @return Optional con la transacción si existe
     */
    Optional<Transaccion> buscarPorId(Optional<Long> id);
    
    /**
     * Busca transacciones por el número de cuenta
     * @param numeroCuenta Optional con el número de cuenta
     * @return Optional con lista de transacciones de la cuenta
     */
    Optional<List<Transaccion>> buscarPorCuenta(Optional<String> numeroCuenta);
    
    /**
     * Busca transacciones por el tipo
     * @param tipo Optional con el tipo de transacción
     * @return Optional con lista de transacciones del tipo especificado
     */
    Optional<List<Transaccion>> buscarPorTipo(Optional<TipoTransaccion> tipo);
    
    /**
     * Busca transacciones por fecha
     * @param fecha Optional con la fecha de las transacciones
     * @return Optional con lista de transacciones de la fecha especificada
     */
    Optional<List<Transaccion>> buscarPorFecha(Optional<LocalDate> fecha);
    
    /**
     * Busca transacciones en un rango de fechas
     * @param fechaInicio Optional con la fecha de inicio
     * @param fechaFin Optional con la fecha de fin
     * @return Optional con lista de transacciones en el rango especificado
     */
    Optional<List<Transaccion>> buscarPorRangoFechas(Optional<LocalDate> fechaInicio, Optional<LocalDate> fechaFin);
    
    /**
     * Obtiene todas las transacciones
     * @return Optional con lista de transacciones
     */
    Optional<List<Transaccion>> buscarTodas();
    
    /**
     * Elimina una transacción por su ID
     * @param id Optional con el ID de la transacción a eliminar
     * @return Optional con la transacción eliminada o Optional vacío si no existía
     */
    Optional<Transaccion> eliminarPorId(Optional<Long> id);
    
    /**
     * Obtiene el número de transacciones en el repositorio
     * @return Número de transacciones
     */
    long contarRegistros();
} 