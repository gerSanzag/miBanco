package com.mibanco.model;

import com.mibanco.model.enums.TipoTarjeta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Clase que representa una tarjeta bancaria
 * Utilizamos enfoque completamente inmutable con @Value para mayor seguridad
 */
@Value
@Builder(toBuilder = true)
@AllArgsConstructor
public class Tarjeta {
    String numero;
    Cliente titular;
    String numeroCuentaAsociada;
    TipoTarjeta tipo;
    String cvv;
    LocalDate fechaExpiracion;
    boolean activa;
    
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
     * Crea una nueva instancia con fecha de expiración actualizada
     * @return Una nueva instancia con la fecha actualizada
     */
    public Tarjeta withFechaExpiracion(LocalDate nuevaFecha) {
        return this.toBuilder()
                .fechaExpiracion(nuevaFecha)
                .build();
    }
    
    /**
     * Crea una nueva instancia con estado activo actualizado
     * @return Una nueva instancia con el estado actualizado
     */
    public Tarjeta withActiva(boolean nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva)
                .build();
    }
    
    /**
     * Crea una nueva instancia actualizando múltiples campos a la vez
     * @return Una nueva instancia con los campos actualizados
     */
    public Tarjeta withActualizaciones(Optional<LocalDate> nuevaFecha, Optional<Boolean> nuevaActiva) {
        return this.toBuilder()
                .fechaExpiracion(nuevaFecha.orElse(this.fechaExpiracion))
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 