package com.mibanco.model;

import com.mibanco.model.enums.TipoTransaccion;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Clase que representa una transacción bancaria
 * Una transacción es un evento que no debe modificarse una vez creada
 * por lo que implementamos un enfoque completamente inmutable
 */
@Value
@Builder(toBuilder = true)
public class Transaccion {
    // Todos los campos son inmutables (final)
    Long id;
    String numeroCuenta;
    String numeroCuentaDestino;
    TipoTransaccion tipo;
    BigDecimal monto;
    LocalDateTime fecha;
    String descripcion;
    
    /**
     * Método factory para crear transacciones
     */
    public static Transaccion of(Long id, String numeroCuenta, String numeroCuentaDestino,
                               TipoTransaccion tipo, BigDecimal monto, 
                               Optional<LocalDateTime> fecha, Optional<String> descripcion) {
        return Transaccion.builder()
                .id(id)
                .numeroCuenta(numeroCuenta)
                .numeroCuentaDestino(numeroCuentaDestino)
                .tipo(tipo)
                .monto(monto)
                .fecha(fecha.orElse(LocalDateTime.now()))
                .descripcion(descripcion.orElse(""))
                .build();
    }
} 