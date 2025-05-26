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
     * Busca una cuenta por su número
     * @param numeroCuenta Optional con el número de la cuenta a buscar
     * @return Optional con la cuenta si existe
     */
    Optional<Cuenta> buscarPorNumero(Optional<String> numeroCuenta);
    
    /**
     * Busca cuentas por el ID del titular
     * @param idTitular Optional con el ID del titular
     * @return Lista de cuentas del titular
     */
    Optional<List<Cuenta>> buscarPorTitularId(Optional<Long> idTitular);
    
    /**
     * Busca cuentas por el tipo
     * @param tipo Optional con el tipo de cuenta
     * @return Lista de cuentas del tipo especificado
     */
    Optional<List<Cuenta>> buscarPorTipo(Optional<TipoCuenta> tipo);
    
    /**
     * Obtiene todas las cuentas
     * @return Lista de cuentas
     */
    Optional<List<Cuenta>> buscarTodos();
    
    /**
     * Obtiene todas las cuentas activas
     * @return Lista de cuentas activas
     */
    Optional<List<Cuenta>> buscarActivas();
    
    /**
     * Elimina una cuenta por su número
     * @param numeroCuenta Optional con el número de la cuenta a eliminar
     * @return Optional con la cuenta eliminada o Optional vacío si no existía
     */
    Optional<Cuenta> eliminarPorNumero(Optional<String> numeroCuenta);
    
    /**
     * Obtiene el número de cuentas en el repositorio
     * @return Número de cuentas
     */
    long contarRegistros();
} 