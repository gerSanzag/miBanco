package com.mibanco.modelo;

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

    String numero;
    Cliente titular;
    String numeroCuentaAsociada;
    TipoTarjeta tipo;
    String cvv;
    LocalDate fechaExpiracion;
    boolean activa;
    
    /**
     * Implementación de getId() para la interfaz Identificable
     * Utilizamos el número de tarjeta como ID usando un hash del String
     * @return Un Long que representa el número de tarjeta
     */
    @Override
    public Long getId() {
        return numero != null ? (long) numero.hashCode() : null;
    }
    
    /**
     * Método factory para facilitar la creación de instancias
     */
    public static Tarjeta of(String numero, Cliente titular, String numeroCuentaAsociada, 
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