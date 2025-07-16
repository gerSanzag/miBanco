package com.mibanco.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.mibanco.modelo.Transaccion;
import com.mibanco.modelo.enums.TipoOperacionTransaccion;
import com.mibanco.modelo.enums.TipoTransaccion;
import com.mibanco.repositorio.util.BaseRepositorio;

/**
 * Interfaz para operaciones CRUD de Transaccion
 * Extiende la interfaz base para heredar operaciones CRUD genéricas
 * Incluye métodos específicos para búsquedas de transacciones
 */
public interface TransaccionRepositorio extends BaseRepositorio<Transaccion, Long, TipoOperacionTransaccion> {
    
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
} 