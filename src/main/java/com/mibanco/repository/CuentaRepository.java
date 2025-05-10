package com.mibanco.repository;

import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones CRUD de Cuenta
 * Siguiendo enfoque estrictamente funcional con Optional
 * tanto para parámetros como para resultados
 */
public interface CuentaRepository {
    
    /**
     * Guarda una cuenta en el repositorio
     * @param cuenta Optional con la cuenta a guardar
     * @return Optional con la cuenta guardada o Optional vacío si la cuenta era null
     */
    Optional<Cuenta> save(Optional<Cuenta> cuenta);
    
    /**
     * Busca una cuenta por su número
     * @param numeroCuenta Optional con el número de la cuenta a buscar
     * @return Optional con la cuenta si existe
     */
    Optional<Cuenta> findByNumero(Optional<String> numeroCuenta);
    
    /**
     * Busca cuentas por el ID del titular
     * @param idTitular Optional con el ID del titular
     * @return Lista de cuentas del titular
     */
    Optional<List<Cuenta>>findByTitularId(Optional<Long> idTitular);
    
    /**
     * Busca cuentas por el tipo
     * @param tipo Optional con el tipo de cuenta
     * @return Lista de cuentas del tipo especificado
     */
    
    Optional<List<Cuenta>> findByTipo(Optional<TipoCuenta> tipo);
    
    /**
     * Obtiene todas las cuentas
     * @return Lista de cuentas
     */
    Optional<List<Cuenta>> findAll();
    
    /**
     * Obtiene todas las cuentas activas
     * @return Lista de cuentas activas
     */
    Optional<List<Cuenta>> findAllActivas();
    
    /**
     * Elimina una cuenta por su número
     * @param numeroCuenta Optional con el número de la cuenta a eliminar
     * @return true si se eliminó la cuenta, false si no existía
     */
    boolean deleteByNumero(Optional<String> numeroCuenta);
    
    /**
     * Obtiene el número de cuentas en el repositorio
     * @return Número de cuentas
     */
    long count();
} 