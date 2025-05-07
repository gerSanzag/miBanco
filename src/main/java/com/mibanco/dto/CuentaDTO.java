package com.mibanco.dto;

import com.mibanco.model.enums.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * DTO para transferir información de Cuenta entre capas
 * Utilizamos enfoque inmutable y funcional con @Value
 */
@Value
@Builder
@AllArgsConstructor
public class CuentaDTO {
    String numeroCuenta;
    ClienteDTO titular;
    TipoCuenta tipo;
    BigDecimal saldo;
    LocalDateTime fechaCreacion;
    boolean activa;

    /**
     * Método estático que construye un CuentaDTO con valores opcionales
     * Ejemplo de uso del enfoque funcional y Optionals
     */
    public static CuentaDTO of(String numeroCuenta, ClienteDTO titular, TipoCuenta tipo,
                               BigDecimal saldo, Optional<LocalDateTime> fechaCreacion, 
                               Optional<Boolean> activa) {
        return CuentaDTO.builder()
                .numeroCuenta(numeroCuenta)
                .titular(titular)
                .tipo(tipo)
                .saldo(saldo)
                .fechaCreacion(fechaCreacion.orElse(LocalDateTime.now()))
                .activa(activa.orElse(true))
                .build();
    }
} 