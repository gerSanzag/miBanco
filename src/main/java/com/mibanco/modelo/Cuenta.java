package com.mibanco.modelo;

import com.mibanco.util.ReflexionUtil.NoSolicitar;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import com.mibanco.modelo.enums.TipoCuenta;

import lombok.Builder;
import lombok.Value;

/**
 * Clase que representa una cuenta bancaria
 * Implementa un enfoque completamente funcional con inmutabilidad total
 */
@Value
@Builder(toBuilder = true)
public class Cuenta implements Identificable {
    Long numeroCuenta;
    Cliente titular;
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
     * Implementación de getId() para la interfaz Identificable
     * @return El número de cuenta como Long
     */
    @Override
    public Long getId() {
        return numeroCuenta;
    }
    
    /**
     * Método factory para facilitar la creación de instancias
     */
    public static Cuenta of(Long numeroCuenta, Cliente titular, TipoCuenta tipo, 
                           BigDecimal saldoInicial, LocalDateTime fechaCreacion, boolean activa) {
        return Cuenta.builder()
                .numeroCuenta(numeroCuenta)
                .titular(titular)
                .tipo(tipo)
                .saldoInicial(saldoInicial != null ? saldoInicial : BigDecimal.ZERO)
                .saldo(saldoInicial != null ? saldoInicial : BigDecimal.ZERO)
                .fechaCreacion(fechaCreacion != null ? fechaCreacion : LocalDateTime.now())
                .activa(activa)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el saldo
     * @return Una nueva instancia con el saldo actualizado
     */
    public Cuenta conSaldo(BigDecimal nuevoSaldo) {
        return this.toBuilder()
                .saldo(nuevoSaldo)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el estado activo
     * @return Una nueva instancia con el estado actualizado
     */
    public Cuenta conActiva(boolean nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar múltiples campos a la vez
     * @return Una nueva instancia con los campos actualizados
     */
    public Cuenta conActualizaciones(Optional<BigDecimal> nuevoSaldo, Optional<Boolean> nuevaActiva) {
        return this.toBuilder()
                .saldo(nuevoSaldo.orElse(this.saldo))
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 