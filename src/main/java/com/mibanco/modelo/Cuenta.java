package com.mibanco.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import com.mibanco.modelo.enums.TipoCuenta;

import lombok.Builder;
import lombok.Value;

/**
 * Clase que representa una cuenta bancaria
 * Implementa un enfoque completamente funcional con inmutabilidad total
 * Implementa Identificable para ser compatible con el repositorio base
 */
@Value
@Builder
public class Cuenta implements Identificable {
    
    String numeroCuenta;
    Cliente titular;
    TipoCuenta tipo;
    LocalDateTime fechaCreacion;
    BigDecimal saldoInicial;
    BigDecimal saldo;
    boolean activa;
    
    @JsonCreator
    public Cuenta(
        @JsonProperty("numeroCuenta") String numeroCuenta,
        @JsonProperty("titular") Cliente titular,
        @JsonProperty("tipo") TipoCuenta tipo,
        @JsonProperty("fechaCreacion") LocalDateTime fechaCreacion,
        @JsonProperty("saldoInicial") BigDecimal saldoInicial,
        @JsonProperty("saldo") BigDecimal saldo,
        @JsonProperty("activa") boolean activa
    ) {
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.tipo = tipo;
        this.fechaCreacion = fechaCreacion;
        this.saldoInicial = saldoInicial;
        this.saldo = saldo;
        this.activa = activa;
    }
    
    /**
     * Implementación de getId() para la interfaz Identificable
     * Extrae la parte numérica del IBAN (numeroCuenta) para usarla como ID
     * Usa solo los primeros 18 dígitos para que quepan en Long
     * Ejemplo: "ES3400000000000000001002" → 340000000000000000L
     * @return La parte numérica del numeroCuenta como Long
     */
    @Override
    public Long getId() {
        // Extraer solo la parte numérica del IBAN
        String parteNumerica = numeroCuenta.replaceAll("[^0-9]", "");
        
        try {
            // Usar solo los primeros 18 dígitos para que quepan en Long
            if (parteNumerica.length() > 18) {
                parteNumerica = parteNumerica.substring(0, 18);
            }
            return Long.parseLong(parteNumerica);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Identificador erróneo, no existe.");
        }
    }
    
    /**
     * Método factory para facilitar la creación de instancias
     */
    public static Cuenta of(String numeroCuenta, Cliente titular, TipoCuenta tipo, 
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