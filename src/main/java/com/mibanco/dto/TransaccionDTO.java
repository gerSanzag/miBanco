package com.mibanco.dto;

import com.mibanco.model.enums.TipoTransaccion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * DTO para transferir información de Transacción entre capas
 * Utilizamos enfoque inmutable con @Value para promover la programación funcional
 */
@Value
@Builder
@AllArgsConstructor
public class TransaccionDTO {
    Long id;
    String numeroCuenta;
    String numeroCuentaDestino;
    TipoTransaccion tipo;
    BigDecimal monto;
    LocalDateTime fecha;
    String descripcion;

    /**
     * Método estático que construye un TransaccionDTO con valores opcionales
     * Aplicando enfoque funcional con Optional para manejar valores nulos
     */
    public static TransaccionDTO of(Long id, String numeroCuenta, 
                                   Optional<String> numeroCuentaDestino,
                                   TipoTransaccion tipo, BigDecimal monto,
                                   Optional<LocalDateTime> fecha,
                                   Optional<String> descripcion) {
        
        // Validación del tipo de transacción y cuenta destino
        if (tipo == TipoTransaccion.TRANSFERENCIA_ENVIADA && numeroCuentaDestino.isEmpty()) {
            throw new IllegalArgumentException("Las transferencias requieren una cuenta destino");
        }
        
        return TransaccionDTO.builder()
                .id(id)
                .numeroCuenta(numeroCuenta)
                .numeroCuentaDestino(numeroCuentaDestino.orElse(null))
                .tipo(tipo)
                .monto(monto)
                .fecha(fecha.orElse(LocalDateTime.now()))
                .descripcion(descripcion.orElse(""))
                .build();
    }
} 