package com.mibanco.servicio;

import java.math.BigDecimal;
import java.util.Optional;

import com.mibanco.dto.TransaccionDTO;

public interface TransaccionOperacionesServicio {

    Optional<TransaccionDTO> ingresar(Optional<Long> idCuenta, Optional<BigDecimal> monto, Optional<String> descripcion);

    Optional<TransaccionDTO> retirar(Optional<Long> idCuenta, Optional<BigDecimal> monto, Optional<String> descripcion);
    
    Optional<TransaccionDTO> transferir(Optional<Long> idCuentaOrigen, Optional<Long> idCuentaDestino, Optional<BigDecimal> monto, Optional<String> descripcion);
    /**
     * Crea una transacción de anulación para una transacción existente
     * @param idTransaccion Optional con el ID de la transacción a anular
     * @return Optional con el DTO de la transacción de anulación creada
     */
    Optional<TransaccionDTO> anularTransaccion(Optional<Long> idTransaccion);
}
