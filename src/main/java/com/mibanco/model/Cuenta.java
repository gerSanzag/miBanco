package com.mibanco.model;

import com.mibanco.model.enums.TipoCuenta;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Clase que representa una cuenta bancaria
 * Implementa un enfoque mixto con inmutabilidad selectiva:
 * - Atributos inmutables: numeroCuenta, titular, tipo, fechaCreacion
 * - Atributos mutables: saldo, activa
 */
@Getter
@ToString
@Accessors(chain = true)
@Builder(toBuilder = true)
public class Cuenta implements Identificable {
    // Atributos inmutables (no deben cambiar después de la creación)
    private final String numeroCuenta;
    private final Cliente titular;
    private final TipoCuenta tipo;
    private final LocalDateTime fechaCreacion;
    
    // Atributos que pueden cambiar
    @Setter private BigDecimal saldo;
    @Setter private boolean activa;
    
    /**
     * Implementación de getId() para la interfaz Identificable
     * Utilizamos el número de cuenta como ID usando un hash del String
     * @return Un Long que representa el número de cuenta
     */
    @Override
    public Long getId() {
        // Convertimos el número de cuenta a un valor Long utilizando un hash
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