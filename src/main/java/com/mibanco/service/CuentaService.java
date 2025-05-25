package com.mibanco.service;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.model.enums.TipoCuenta;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para operaciones con Cuentas
 */
public interface CuentaService {
    
    /**
     * Crea una nueva cuenta
     * @param cuentaDTO Optional con los datos de la cuenta a crear
     * @return Optional con el DTO de la cuenta creada
     */
    Optional<CuentaDTO> crearCuenta(Optional<CuentaDTO> cuentaDTO);
    
    /**
     * Obtiene una cuenta por su número
     * @param numeroCuenta Optional con el número de cuenta
     * @return Optional con el DTO de la cuenta
     */
    Optional<CuentaDTO> obtenerCuentaPorNumero(Optional<String> numeroCuenta);
    
    /**
     * Obtiene las cuentas de un titular
     * @param idTitular Optional con el ID del titular
     * @return Optional con la lista de DTOs de cuentas
     */
    Optional<List<CuentaDTO>> obtenerCuentasPorTitular(Optional<Long> idTitular);
    
    /**
     * Obtiene las cuentas de un tipo específico
     * @param tipo Optional con el tipo de cuenta
     * @return Optional con la lista de DTOs de cuentas
     */
    Optional<List<CuentaDTO>> obtenerCuentasPorTipo(Optional<TipoCuenta> tipo);
    
    /**
     * Obtiene todas las cuentas
     * @return Optional con la lista de DTOs de cuentas
     */
    Optional<List<CuentaDTO>> obtenerTodasLasCuentas();
    
    /**
     * Obtiene todas las cuentas activas
     * @return Optional con la lista de DTOs de cuentas activas
     */
    Optional<List<CuentaDTO>> obtenerCuentasActivas();
    
    /**
     * Actualiza el saldo de una cuenta
     * @param numeroCuenta Número de la cuenta
     * @param nuevoSaldo Optional con el nuevo saldo
     * @return Optional con el DTO de la cuenta actualizada
     */
    Optional<CuentaDTO> actualizarSaldoCuenta(String numeroCuenta, Optional<BigDecimal> nuevoSaldo);
    
    /**
     * Actualiza el estado de una cuenta
     * @param numeroCuenta Número de la cuenta
     * @param activa Optional con el nuevo estado
     * @return Optional con el DTO de la cuenta actualizada
     */
    Optional<CuentaDTO> actualizarEstadoCuenta(String numeroCuenta, Optional<Boolean> activa);
    
    /**
     * Actualiza múltiples campos de una cuenta
     * @param numeroCuenta Número de la cuenta
     * @param cuentaDTO Optional con los nuevos datos
     * @return Optional con el DTO de la cuenta actualizada
     */
    Optional<CuentaDTO> actualizarCuenta(String numeroCuenta, Optional<CuentaDTO> cuentaDTO);
    
    /**
     * Elimina una cuenta por su número
     * @param numeroCuenta Optional con el número de cuenta a eliminar
     * @return true si se eliminó correctamente, false si no
     */
    boolean eliminarCuenta(Optional<String> numeroCuenta);
} 