package com.mibanco.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.mibanco.modelo.enums.TipoCuenta;
import com.mibanco.util.ReflexionUtil.NoSolicitar;

import lombok.Builder;
import lombok.Value;

/**
 * DTO para transferir información de Cuenta entre capas
 * Utilizamos enfoque inmutable y funcional con @Value
 */
@Value
@Builder(toBuilder = true) // Habilitamos toBuilder para facilitar métodos "with"
public class CuentaDTO {
    @NoSolicitar(razon = "Se establece automáticamente en el repositorio")
    Long numeroCuenta;
    ClienteDTO titular;
    TipoCuenta tipo;
    @NoSolicitar(razon = "Se establece automáticamente al crear")
    LocalDateTime fechaCreacion;
    @NoSolicitar(razon = "Se establece automáticamente al crear y nunca cambia")
    BigDecimal saldoInicial;
    @NoSolicitar(razon = "Se inicializa igual a saldoInicial y solo cambia por transacciones")
    BigDecimal saldo;
    @NoSolicitar(razon = "Se establece por defecto como activa")
    boolean activa;

    /**
     * Método estático que construye un CuentaDTO con valores opcionales
     * Ejemplo de uso del enfoque funcional y Optionals
     */
        public static CuentaDTO of(Long numeroCuenta, Optional<ClienteDTO> titular, Optional<TipoCuenta> tipo,
                               BigDecimal saldoInicial, Optional<LocalDateTime> fechaCreacion, 
                               Optional<Boolean> activa) {
        return CuentaDTO.builder()
                .numeroCuenta(numeroCuenta)
                .titular(titular.orElse(null))
                .tipo(tipo.orElse(null))
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