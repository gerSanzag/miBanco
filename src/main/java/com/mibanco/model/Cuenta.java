package com.mibanco.model;

import com.mibanco.model.enums.TipoCuenta;
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
public class Cuenta {
    String numeroCuenta;
    Cliente titular;
    TipoCuenta tipo;
    LocalDateTime fechaCreacion;
    BigDecimal saldo;
    boolean activa;
    
    /**
     * Método factory para facilitar la creación de instancias
     * Utilizamos Optional para los campos que pueden ser opcionales
     */
    public static Cuenta of(String numeroCuenta, Cliente titular, TipoCuenta tipo, 
                           Optional<BigDecimal> saldo, Optional<LocalDateTime> fechaCreacion, 
                           Optional<Boolean> activa) {
        return Cuenta.builder()
                .numeroCuenta(numeroCuenta)
                .titular(titular)
                .tipo(tipo)
                .saldo(saldo.orElse(BigDecimal.ZERO))
                .fechaCreacion(fechaCreacion.orElse(LocalDateTime.now()))
                .activa(activa.orElse(true))
                .build();
    }
    
    /**
     * Crea una nueva instancia con saldo actualizado
     * @param nuevoSaldo El nuevo saldo (opcional)
     * @return Una nueva instancia con el saldo actualizado
     */
    public Cuenta withSaldo(Optional<BigDecimal> nuevoSaldo) {
        return this.toBuilder()
                .saldo(nuevoSaldo.orElse(this.saldo))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public Cuenta withSaldo(BigDecimal nuevoSaldo) {
        return withSaldo(Optional.ofNullable(nuevoSaldo));
    }
    
    /**
     * Crea una nueva instancia con estado activo actualizado
     * @param nuevaActiva El nuevo estado (opcional)
     * @return Una nueva instancia con el estado actualizado
     */
    public Cuenta withActiva(Optional<Boolean> nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public Cuenta withActiva(boolean nuevaActiva) {
        return withActiva(Optional.of(nuevaActiva));
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