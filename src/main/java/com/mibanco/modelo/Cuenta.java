package com.mibanco.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.mibanco.modelo.enums.TipoCuenta;

import lombok.Builder;
import lombok.Value;

/**
 * Clase que representa una cuenta bancaria
 * Implementa un enfoque completamente funcional con inmutabilidad total
 */
@Value
@Builder
public class Cuenta implements Identificable {
    
    Long numeroCuenta;
    Cliente titular;
    TipoCuenta tipo;
    LocalDateTime fechaCreacion;
    BigDecimal saldoInicial;
    BigDecimal saldo;
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
                .saldoInicial(saldoInicial)
                .saldo(saldoInicial)
                .fechaCreacion(fechaCreacion)
                .activa(activa)
                .build();
    }
    
} 