package com.mibanco.servicio;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.modelo.enums.TipoCuenta;
import java.util.List;
import java.util.Optional;
import java.math.BigDecimal;

/**
 * Interfaz de servicio para operaciones con Cuentas
 */
public interface CuentaServicio {
    
    /**
     * Crea una nueva cuenta
     * @param cuentaDTO Optional con los datos de la nueva cuenta
     * @return Optional con el DTO de la cuenta creada
     */
    Optional<CuentaDTO> crearCuenta(Optional<CuentaDTO> cuentaDTO);
    
    /**
     * Actualiza la información completa de una cuenta
     * @param numeroCuenta Número de cuenta a actualizar
     * @param cuentaDTO Optional con los nuevos datos de la cuenta
     * @return Optional con el DTO de la cuenta actualizada
     */
    Optional<CuentaDTO> actualizarVariosCampos(String numeroCuenta, Optional<CuentaDTO> cuentaDTO);
    
    /**
     * Obtiene una cuenta por su número
     * @param numeroCuenta Optional con el número de cuenta
     * @return Optional con el DTO de la cuenta
     */
    Optional<CuentaDTO> obtenerCuentaPorNumero(Optional<String> numeroCuenta);
    
    /**
     * Obtiene todas las cuentas
     * @return Optional con la lista de DTOs de cuentas
     */
    Optional<List<CuentaDTO>> obtenerTodasLasCuentas();
    
    /**
     * Actualiza el saldo de una cuenta
     * @param numeroCuenta Número de cuenta
     * @param nuevoSaldo Optional con el nuevo saldo
     * @return Optional con el DTO de la cuenta actualizada
     */
    Optional<CuentaDTO> actualizarSaldoCuenta(String numeroCuenta, Optional<BigDecimal> nuevoSaldo);
    
    /**
     * Actualiza el estado activo de una cuenta
     * @param numeroCuenta Número de cuenta
     * @param nuevaActiva Optional con el nuevo estado
     * @return Optional con el DTO de la cuenta actualizada
     */
    Optional<CuentaDTO> actualizarEstadoCuenta(String numeroCuenta, Optional<Boolean> nuevaActiva);
    
    /**
     * Elimina una cuenta por su número
     * @param numeroCuenta Optional con el número de cuenta a eliminar
     * @return true si se eliminó correctamente, false si no
     */
    boolean eliminarCuenta(Optional<String> numeroCuenta);

    /**
     * Elimina una cuenta por su número y devuelve la cuenta eliminada
     * @param numeroCuenta Optional con el número de cuenta a eliminar
     * @return Optional con el DTO de la cuenta eliminada
     */
    Optional<CuentaDTO> eliminarPorNumero(Optional<String> numeroCuenta);

    /**
     * Restaura una cuenta previamente eliminada
     * @param numeroCuenta Optional con el número de cuenta a restaurar
     * @return Optional con el DTO de la cuenta restaurada
     */
    Optional<CuentaDTO> restaurarCuenta(Optional<String> numeroCuenta);

    /**
     * Obtiene la lista de cuentas eliminadas
     * @return Lista de DTOs de cuentas eliminadas
     */
    List<CuentaDTO> obtenerCuentasEliminadas();

    /**
     * Obtiene el número total de cuentas
     * @return Número de cuentas
     */
    long contarCuentas();

    /**
     * Establece el usuario actual para operaciones de auditoría
     * @param usuario Usuario actual
     */
    void establecerUsuarioActual(String usuario);

    /**
     * Busca cuentas por el ID del titular
     * @param idTitular Optional con el ID del titular
     * @return Optional con la lista de DTOs de cuentas del titular
     */
    Optional<List<CuentaDTO>> buscarPorTitularId(Optional<Long> idTitular);

    /**
     * Busca cuentas por el tipo
     * @param tipo Optional con el tipo de cuenta
     * @return Optional con la lista de DTOs de cuentas del tipo especificado
     */
    Optional<List<CuentaDTO>> buscarPorTipo(Optional<TipoCuenta> tipo);

    /**
     * Obtiene todas las cuentas activas
     * @return Optional con la lista de DTOs de cuentas activas
     */
    Optional<List<CuentaDTO>> buscarActivas();
} 