package com.mibanco.repository;

import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;
import com.mibanco.model.enums.TipoOperacionCuenta;
import com.mibanco.repository.util.BaseRepository;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el repositorio de Cuentas
 * Extiende la interfaz base para heredar operaciones CRUD genéricas
 */
public interface CuentaRepository extends BaseRepository<Cuenta, String, TipoOperacionCuenta> {
    
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
    Optional<List<Cuenta>> findByTitularId(Optional<Long> idTitular);
    
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
     * @return Optional con la cuenta eliminada o Optional vacío si no existía
     */
    Optional<Cuenta> deleteByNumero(Optional<String> numeroCuenta);
    
    /**
     * Obtiene el número de cuentas en el repositorio
     * @return Número de cuentas
     */
    long count();
} 