package com.mibanco.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.mibanco.modelo.enums.TipoTransaccion;
import com.mibanco.util.ReflexionUtil.NoSolicitar;

/**
 * DTO para transferir información de Transacción entre capas
 * Utilizamos enfoque inmutable con @Value para promover la programación funcional
 */
@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class TransaccionDTO {
    @NoSolicitar(razon = "Se establece automáticamente en el repositorio")
    Long id;
    Long numeroCuenta;
    Long numeroCuentaDestino;
    TipoTransaccion tipo;
    BigDecimal monto;
    LocalDateTime fecha;
    String descripcion;

    /**
     * Método estático que construye un TransaccionDTO con valores opcionales
     * Aplicando enfoque funcional con Optional para manejar valores nulos
     */
    public static TransaccionDTO of(Long id, Long numeroCuenta, 
                                   Optional<Long> numeroCuentaDestino,
                                   Optional<TipoTransaccion> tipo, Optional<BigDecimal> monto,
                                   Optional<LocalDateTime> fecha,
                                   Optional<String> descripcion) {
        
        return TransaccionDTO.builder()
                .id(id)
                .numeroCuenta(numeroCuenta)
                .numeroCuentaDestino(numeroCuentaDestino.orElse(null))
                .tipo(tipo.orElse(null))
                .monto(monto.orElse(null))
                .fecha(fecha.orElse(LocalDateTime.now()))
                .descripcion(descripcion.orElse(""))
                .build();
    }
} 