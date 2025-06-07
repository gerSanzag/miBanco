package com.mibanco.servicio;

import com.mibanco.dto.TransaccionDTO;
import com.mibanco.modelo.enums.TipoTransaccion;
import com.mibanco.modelo.enums.TipoOperacionTransaccion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz de servicio para operaciones con Transacciones
 */
public interface TransaccionServicio {
    
    /**
     * Crea una nueva transacción
     * @param transaccionDTO Optional con los datos de la nueva transacción
     * @return Optional con el DTO de la transacción creada
     */
    Optional<TransaccionDTO> crearTransaccion(Optional<TransaccionDTO> transaccionDTO);
    
    /**
     * Obtiene una transacción por su ID
     * @param id Optional con el ID de la transacción
     * @return Optional con el DTO de la transacción
     */
    Optional<TransaccionDTO> obtenerTransaccionPorId(Optional<Long> id);
    
    /**
     * Obtiene todas las transacciones
     * @return Optional con la lista de DTOs de transacciones
     */
    Optional<List<TransaccionDTO>> obtenerTodasLasTransacciones();
    
    /**
     * Busca transacciones por el número de cuenta
     * @param numeroCuenta Optional con el número de cuenta
     * @return Optional con la lista de DTOs de transacciones
     */
    Optional<List<TransaccionDTO>> buscarPorCuenta(Optional<String> numeroCuenta);
    
    /**
     * Busca transacciones por el tipo
     * @param tipo Optional con el tipo de transacción
     * @return Optional con la lista de DTOs de transacciones
     */
    Optional<List<TransaccionDTO>> buscarPorTipo(Optional<TipoTransaccion> tipo);
    
    /**
     * Busca transacciones por fecha
     * @param fecha Optional con la fecha de las transacciones
     * @return Optional con la lista de DTOs de transacciones
     */
    Optional<List<TransaccionDTO>> buscarPorFecha(Optional<LocalDate> fecha);
    
    /**
     * Busca transacciones en un rango de fechas
     * @param fechaInicio Optional con la fecha de inicio
     * @param fechaFin Optional con la fecha de fin
     * @return Optional con la lista de DTOs de transacciones
     */
    Optional<List<TransaccionDTO>> buscarPorRangoFechas(Optional<LocalDate> fechaInicio, Optional<LocalDate> fechaFin);
    
    /**
     * Crea una transacción de anulación para una transacción existente
     * @param idTransaccion Optional con el ID de la transacción a anular
     * @return Optional con el DTO de la transacción de anulación creada
     */
    Optional<TransaccionDTO> anularTransaccion(Optional<Long> idTransaccion);
    
    /**
     * Elimina una transacción por su ID
     * @param id Optional con el ID de la transacción a eliminar
     * @return true si se eliminó correctamente, false si no
     */
    boolean eliminarTransaccion(Optional<Long> id);
    
    /**
     * Obtiene el número total de transacciones
     * @return Número de transacciones
     */
    long contarTransacciones();
    
    /**
     * Establece el usuario actual para operaciones de auditoría
     * @param usuario Usuario actual
     */
    void establecerUsuarioActual(String usuario);

    /**
     * Realiza un ingreso en una cuenta
     * @param numeroCuenta Optional con el número de cuenta donde se realiza el ingreso
     * @param monto Optional con el monto a ingresar
     * @param descripcion Optional con la descripción del ingreso
     * @return Optional con el DTO de la transacción de ingreso creada
     */
    Optional<TransaccionDTO> ingresar(Optional<String> numeroCuenta, Optional<BigDecimal> monto, Optional<String> descripcion);

    /**
     * Realiza un retiro de una cuenta
     * @param numeroCuenta Optional con el número de cuenta de donde se realiza el retiro
     * @param monto Optional con el monto a retirar
     * @param descripcion Optional con la descripción del retiro
     * @return Optional con el DTO de la transacción de retiro creada
     */
    Optional<TransaccionDTO> retirar(Optional<String> numeroCuenta, Optional<BigDecimal> monto, Optional<String> descripcion);

    /**
     * Realiza una transferencia entre cuentas
     * @param numeroCuentaOrigen Optional con el número de cuenta de origen
     * @param numeroCuentaDestino Optional con el número de cuenta de destino
     * @param monto Optional con el monto a transferir
     * @param descripcion Optional con la descripción de la transferencia
     * @return Optional con el DTO de la transacción de transferencia creada
     */
    Optional<TransaccionDTO> transferir(Optional<String> numeroCuentaOrigen, Optional<String> numeroCuentaDestino, Optional<BigDecimal> monto, Optional<String> descripcion);
} 