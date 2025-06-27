package com.mibanco.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.mibanco.modelo.enums.TipoCuenta;

import lombok.Builder;
import lombok.Value;

/**
 * DTO para transferir información de Cuenta entre capas
 * Utilizamos enfoque inmutable y funcional con @Value
 */
@Value
@Builder(toBuilder = true) // Habilitamos toBuilder para facilitar métodos "with"
public class CuentaDTO {
    Long numeroCuenta;
    ClienteDTO titular;
    TipoCuenta tipo;
    BigDecimal saldoInicial;
    BigDecimal saldo;
    LocalDateTime fechaCreacion;
    boolean activa;

    /**
     * Método estático que construye un CuentaDTO con valores opcionales
     * Ejemplo de uso del enfoque funcional y Optionals
     */
    public static CuentaDTO of(Long numeroCuenta, ClienteDTO titular, TipoCuenta tipo,
                               BigDecimal saldoInicial, Optional<LocalDateTime> fechaCreacion, 
                               Optional<Boolean> activa) {
        return CuentaDTO.builder()
                .numeroCuenta(numeroCuenta)
                .titular(titular)
                .tipo(tipo)
                .saldoInicial(saldoInicial != null ? saldoInicial : BigDecimal.ZERO)
                .saldo(saldoInicial != null ? saldoInicial : BigDecimal.ZERO)
                .fechaCreacion(fechaCreacion.orElse(LocalDateTime.now()))
                .activa(activa.orElse(true))
                .build();
    }
    
    /**
     * Crea una nueva instancia con saldo actualizado
     * Método "with" para transformación inmutable
     */
    public CuentaDTO conSaldo(BigDecimal nuevoSaldo) {
        return this.toBuilder()
                .saldo(nuevoSaldo)
                .build();
    }
    
    /**
     * Crea una nueva instancia con estado activo actualizado
     * Método "with" para transformación inmutable
     */
    public CuentaDTO conActiva(boolean nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva)
                .build();
    }
    
    /**
     * Crea una nueva instancia con titular actualizado
     * Método "with" para transformación inmutable
     */
    public CuentaDTO conTitular(ClienteDTO nuevoTitular) {
        return this.toBuilder()
                .titular(nuevoTitular)
                .build();
    }
    
    /**
     * Crea una nueva instancia actualizando múltiples campos a la vez
     * Método para actualizaciones inmutables múltiples
     */
        public CuentaDTO conActualizaciones(
            Optional<BigDecimal> nuevoSaldo,
            Optional<Boolean> nuevaActiva) {
        
        return this.toBuilder()
                .saldo(nuevoSaldo.orElse(this.saldo))
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 