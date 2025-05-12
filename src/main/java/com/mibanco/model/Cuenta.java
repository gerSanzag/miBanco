package com.mibanco.model;

import com.mibanco.model.enums.TipoCuenta;
import lombok.Value;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Clase que representa una cuenta bancaria
 * Implementa un enfoque completamente funcional con inmutabilidad total
 */
@Value
@Builder(toBuilder = true)
public class Cuenta implements Identificable {
    String numeroCuenta;
    Cliente titular;
    TipoCuenta tipo;
    LocalDateTime fechaCreacion;
    BigDecimal saldo;
    boolean activa;
    
    /**
     * Implementación de getId() para la interfaz Identificable
     * Utilizamos el número de cuenta como ID usando un hash del String
     * @return Un Long que representa el número de cuenta
     */
    @Override
    public Long getId() {
        return numeroCuenta != null ? (long) numeroCuenta.hashCode() : null;
    }
    
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
     * Versión inmutable para actualizar el saldo
     * @return Una nueva instancia con el saldo actualizado
     */
    public Cuenta withSaldo(BigDecimal nuevoSaldo) {
        return this.toBuilder()
                .saldo(nuevoSaldo)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el estado activo
     * @return Una nueva instancia con el estado actualizado
     */
    public Cuenta withActiva(boolean nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar múltiples campos a la vez
     * @return Una nueva instancia con los campos actualizados
     */
    public Cuenta withActualizaciones(Optional<BigDecimal> nuevoSaldo, Optional<Boolean> nuevaActiva) {
        return this.toBuilder()
                .saldo(nuevoSaldo.orElse(this.saldo))
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 