package com.mibanco.model;

import com.mibanco.model.enums.TipoTarjeta;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Clase que representa una tarjeta bancaria
 * Implementa un enfoque mixto con inmutabilidad selectiva:
 * - Atributos inmutables: numero, titular, numeroCuentaAsociada, tipo, cvv
 * - Atributos mutables: fechaExpiracion, activa
 */
@Getter
@ToString
@Accessors(chain = true)
@Builder(toBuilder = true)
public class Tarjeta implements Identificable {
    // Atributos inmutables (información que nunca cambia)
    private final String numero;
    private final Cliente titular;
    private final String numeroCuentaAsociada;
    private final TipoTarjeta tipo;
    private final String cvv;
    
    // Atributos que pueden cambiar
    @Setter private LocalDate fechaExpiracion;
    @Setter private boolean activa;
    
    /**
     * Implementación de getId() para la interfaz Identificable
     * Utilizamos el número de tarjeta como ID usando un hash del String
     * @return Un Long que representa el número de tarjeta
     */
    @Override
    public Long getId() {
        // Convertimos el número de tarjeta a un valor Long utilizando un hash
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
    
    /**
     * Versión inmutable para actualizar la fecha de expiración
     * @return Una nueva instancia con la fecha actualizada
     */
    public Tarjeta withFechaExpiracion(LocalDate nuevaFecha) {
        return this.toBuilder()
                .fechaExpiracion(nuevaFecha)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el estado activo
     * @return Una nueva instancia con el estado actualizado
     */
    public Tarjeta withActiva(boolean nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar múltiples campos a la vez
     * @return Una nueva instancia con los campos actualizados
     */
    public Tarjeta withActualizaciones(Optional<LocalDate> nuevaFecha, Optional<Boolean> nuevaActiva) {
        return this.toBuilder()
                .fechaExpiracion(nuevaFecha.orElse(this.fechaExpiracion))
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 