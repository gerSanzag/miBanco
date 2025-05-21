package com.mibanco.service;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.model.enums.TipoCuenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para servicios relacionados con cuentas bancarias
 * Utilizando enfoque funcional con Optional
 */
public interface CuentaService {

    /**
     * Crea una nueva cuenta en el sistema
     * @param cuentaDTO Optional con los datos de la cuenta a crear
     * @return Optional con la cuenta creada
     */
    Optional<CuentaDTO> crearCuenta(Optional<CuentaDTO> cuentaDTO);
    
    /**
     * Obtiene una cuenta por su número
     * @param numeroCuenta Optional con el número de cuenta a buscar
     * @return Optional con la cuenta encontrada
     */
    Optional<CuentaDTO> obtenerCuentaPorNumero(Optional<String> numeroCuenta);
    
    /**
     * Obtiene todas las cuentas de un titular
     * @param idTitular Optional con el ID del titular
     * @return Optional con la lista de cuentas del titular
     */
    Optional<List<CuentaDTO>> obtenerCuentasPorTitular(Optional<Long> idTitular);
    
    /**
     * Obtiene todas las cuentas de un tipo específico
     * @param tipo Optional con el tipo de cuenta
     * @return Optional con la lista de cuentas del tipo especificado
     */
    Optional<List<CuentaDTO>> obtenerCuentasPorTipo(Optional<TipoCuenta> tipo);
    
    /**
     * Obtiene todas las cuentas del sistema
     * @return Optional con la lista de todas las cuentas
     */
    Optional<List<CuentaDTO>> obtenerTodasLasCuentas();
    
    /**
     * Obtiene todas las cuentas activas
     * @return Optional con la lista de cuentas activas
     */
    Optional<List<CuentaDTO>> obtenerCuentasActivas();
    
    /**
     * Actualiza el saldo de una cuenta
     * @param numeroCuenta Número de la cuenta a actualizar
     * @param nuevoSaldo Optional con el nuevo saldo
     * @return Optional con la cuenta actualizada
     */
    Optional<CuentaDTO> actualizarSaldoCuenta(String numeroCuenta, Optional<BigDecimal> nuevoSaldo);
    
    /**
     * Activa o desactiva una cuenta
     * @param numeroCuenta Número de la cuenta a actualizar
     * @param activa Optional con el nuevo estado
     * @return Optional con la cuenta actualizada
     */
    Optional<CuentaDTO> actualizarEstadoCuenta(String numeroCuenta, Optional<Boolean> activa);
    
    /**
     * Actualiza múltiples campos de una cuenta
     * @param numeroCuenta Número de la cuenta a actualizar
     * @param cuentaDTO Optional con los datos a actualizar
     * @return Optional con la cuenta actualizada
     */
    Optional<CuentaDTO> actualizarCuenta(String numeroCuenta, Optional<CuentaDTO> cuentaDTO);
    
    /**
     * Elimina una cuenta
     * @param numeroCuenta Optional con el número de la cuenta a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    boolean eliminarCuenta(Optional<String> numeroCuenta);
} 