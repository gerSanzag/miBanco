package com.mibanco.dto;

import com.mibanco.model.enums.TipoCuenta;
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
@Builder(toBuilder = true)
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
    
    /**
     * Crea una nueva instancia con saldo actualizado
     * @param nuevoSaldo El nuevo saldo (opcional)
     * @return Una nueva instancia con el saldo actualizado
     */
    public CuentaDTO withSaldo(Optional<BigDecimal> nuevoSaldo) {
        return this.toBuilder()
                .saldo(nuevoSaldo.orElse(this.saldo))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public CuentaDTO withSaldo(BigDecimal nuevoSaldo) {
        return withSaldo(Optional.ofNullable(nuevoSaldo));
    }
    
    /**
     * Crea una nueva instancia con titular actualizado
     * @param nuevoTitular El nuevo titular (opcional)
     * @return Una nueva instancia con el titular actualizado
     */
    public CuentaDTO withTitular(Optional<ClienteDTO> nuevoTitular) {
        return this.toBuilder()
                .titular(nuevoTitular.orElse(this.titular))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public CuentaDTO withTitular(ClienteDTO nuevoTitular) {
        return withTitular(Optional.ofNullable(nuevoTitular));
    }
    
    /**
     * Crea una nueva instancia con estado activo actualizado
     * @param nuevaActiva El nuevo estado (opcional)
     * @return Una nueva instancia con el estado actualizado
     */
    public CuentaDTO withActiva(Optional<Boolean> nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public CuentaDTO withActiva(boolean nuevaActiva) {
        return withActiva(Optional.of(nuevaActiva));
    }
    
    /**
     * Crea una nueva instancia actualizando múltiples campos a la vez
     * Los únicos campos que suelen cambiar en una cuenta son el saldo y su estado activo
     */
    public CuentaDTO withActualizaciones(
            Optional<BigDecimal> nuevoSaldo,
            Optional<ClienteDTO> nuevoTitular,
            Optional<Boolean> nuevaActiva) {
        
        return this.toBuilder()
                .saldo(nuevoSaldo.orElse(this.saldo))
                .titular(nuevoTitular.orElse(this.titular))
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 