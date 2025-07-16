package com.mibanco.modelo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.mibanco.modelo.enums.TipoTransaccion;

/**
 * Clase que representa una transacción bancaria
 * Una transacción es un evento que no debe modificarse una vez creada
 * por lo que implementamos un enfoque completamente inmutable
 */
@Value
@Builder
public class Transaccion implements Identificable {
    Long id;
    String numeroCuenta;
    String numeroCuentaDestino;
    TipoTransaccion tipo;
    BigDecimal monto;
    LocalDateTime fecha;
    String descripcion;
    
    @JsonCreator
    public Transaccion(
        @JsonProperty("id") Long id,
        @JsonProperty("numeroCuenta") String numeroCuenta,
        @JsonProperty("numeroCuentaDestino") String numeroCuentaDestino,
        @JsonProperty("tipo") TipoTransaccion tipo,
        @JsonProperty("monto") BigDecimal monto,
        @JsonProperty("fecha") LocalDateTime fecha,
        @JsonProperty("descripcion") String descripcion
    ) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.numeroCuentaDestino = numeroCuentaDestino;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
        this.descripcion = descripcion;
    }
    
    /**
     * Método factory para crear transacciones
     */
    public static Transaccion of(Long id, String numeroCuenta, String numeroCuentaDestino,
                               TipoTransaccion tipo, BigDecimal monto, 
                               LocalDateTime fecha, String descripcion) {
        return Transaccion.builder()
                .id(id)
                .numeroCuenta(numeroCuenta)
                .numeroCuentaDestino(numeroCuentaDestino)
                .tipo(tipo)
                .monto(monto)
                .fecha(fecha != null ? fecha : LocalDateTime.now())
                .descripcion(descripcion != null ? descripcion : "")
                .build();
    }
} 