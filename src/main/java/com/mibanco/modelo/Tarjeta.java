package com.mibanco.modelo;

import com.mibanco.util.ReflexionUtil.NoSolicitar;
import java.time.LocalDate;
import java.util.Optional;

import com.mibanco.modelo.enums.TipoTarjeta;

import lombok.Builder;
import lombok.Value;


/**
 * Clase que representa una tarjeta bancaria
 * Implementa un enfoque completamente funcional con inmutabilidad total
 */
@Value
@Builder(toBuilder = true)
public class Tarjeta implements Identificable {
    @NoSolicitar(razon = "Se genera automáticamente")
    String numero;
    Cliente titular;
    String numeroCuentaAsociada;
    TipoTarjeta tipo;
    @NoSolicitar(razon = "Se genera automáticamente")
    String cvv;
    @NoSolicitar(razon = "Se establece automáticamente (+3 años)")
    LocalDate fechaExpiracion;
    @NoSolicitar(razon = "Se establece por defecto como activa")
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
    
    /**
     * Versión inmutable para actualizar la fecha de expiración
     * @return Una nueva instancia con la fecha actualizada
     */
    public Tarjeta conFechaExpiracion(LocalDate nuevaFecha) {
        return this.toBuilder()
                .fechaExpiracion(nuevaFecha)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el estado activo
     * @return Una nueva instancia con el estado actualizado
     */
    public Tarjeta conActiva(boolean nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar múltiples campos a la vez
     * @return Una nueva instancia con los campos actualizados
     */
    public Tarjeta conActualizaciones(Optional<LocalDate> nuevaFecha, Optional<Boolean> nuevaActiva) {
        return this.toBuilder()
                .fechaExpiracion(nuevaFecha.orElse(this.fechaExpiracion))
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 