package com.mibanco.model;

import com.mibanco.model.enums.TipoCuenta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Clase que representa una cuenta bancaria
 * Utilizamos enfoque completamente inmutable con @Value para mayor seguridad
 */
@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class Cuenta {
    String numeroCuenta;
    Cliente titular;
    TipoCuenta tipo;
    LocalDateTime fechaCreacion;
    BigDecimal saldo;
    boolean activa;
    
    /**
     * Método factory para facilitar la creación de instancias
     */
    public static Cuenta of(String numeroCuenta, Cliente titular, TipoCuenta tipo, 
                           BigDecimal saldo, LocalDateTime fechaCreacion, boolean activa) {
        return Cuenta.builder()
                .numeroCuenta(numeroCuenta)
                .titular(titular)
                .tipo(tipo)
                .saldo(saldo != null ? saldo : BigDecimal.ZERO)
                .fechaCreacion(fechaCreacion != null ? fechaCreacion : LocalDateTime.now())
                .activa(activa)
                .build();
    }
    
    /**
     * Crea una nueva instancia con saldo actualizado
     * @return Una nueva instancia con el saldo actualizado
     */
    public Cuenta withSaldo(BigDecimal nuevoSaldo) {
        return this.toBuilder()
                .saldo(nuevoSaldo)
                .build();
    }
    
    /**
     * Crea una nueva instancia con estado activo actualizado
     * @return Una nueva instancia con el estado actualizado
     */
    public Cuenta withActiva(boolean nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva)
                .build();
    }
    
    /**
     * Crea una nueva instancia actualizando múltiples campos a la vez
     * @return Una nueva instancia con los campos actualizados
     */
    public Cuenta withActualizaciones(Optional<BigDecimal> nuevoSaldo, Optional<Boolean> nuevaActiva) {
        return this.toBuilder()
                .saldo(nuevoSaldo.orElse(this.saldo))
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 