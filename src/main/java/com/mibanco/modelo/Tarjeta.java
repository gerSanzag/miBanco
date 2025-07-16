package com.mibanco.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

import com.mibanco.modelo.enums.TipoTarjeta;

import lombok.Builder;
import lombok.Value;


/**
 * Clase que representa una tarjeta bancaria
 * Implementa un enfoque completamente funcional con inmutabilidad total
 */
@Value
@Builder
public class Tarjeta implements Identificable {

    Long numero;
    Cliente titular;
    String numeroCuentaAsociada;
    TipoTarjeta tipo;
    String cvv;
    LocalDate fechaExpiracion;
    boolean activa;
    
    @JsonCreator
    public Tarjeta(
        @JsonProperty("numero") Long numero,
        @JsonProperty("titular") Cliente titular,
        @JsonProperty("numeroCuentaAsociada") String numeroCuentaAsociada,
        @JsonProperty("tipo") TipoTarjeta tipo,
        @JsonProperty("cvv") String cvv,
        @JsonProperty("fechaExpiracion") LocalDate fechaExpiracion,
        @JsonProperty("activa") boolean activa
    ) {
        this.numero = numero;
        this.titular = titular;
        this.numeroCuentaAsociada = numeroCuentaAsociada;
        this.tipo = tipo;
        this.cvv = cvv;
        this.fechaExpiracion = fechaExpiracion;
        this.activa = activa;
    }
    
    /**
     * Implementación de getId() para la interfaz Identificable
     * El número de tarjeta es directamente el identificador
     * @return El número de tarjeta como Long
     */
    @Override
    public Long getId() {
        return numero;
    }
    
    /**
     * Método factory para facilitar la creación de instancias
     */
    public static Tarjeta of(Long numero, Cliente titular, String numeroCuentaAsociada, 
                            TipoTarjeta tipo, LocalDate fechaExpiracion, String cvv, boolean activa) {
        return Tarjeta.builder()
                .numero(numero)
                .titular(titular)
                .numeroCuentaAsociada(numeroCuentaAsociada)
                .tipo(tipo)
                .fechaExpiracion(fechaExpiracion)
                .cvv(cvv)
                .activa(activa)
                .build();
    }
    
} 