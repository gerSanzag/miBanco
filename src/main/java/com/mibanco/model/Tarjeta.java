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
     * Utilizamos Optional para los campos que pueden ser opcionales
     */
    public static Tarjeta of(String numero, Cliente titular, String numeroCuentaAsociada, 
                            TipoTarjeta tipo, Optional<LocalDate> fechaExpiracion, 
                            Optional<String> cvv, Optional<Boolean> activa) {
        return Tarjeta.builder()
                .numero(numero)
                .titular(titular)
                .numeroCuentaAsociada(numeroCuentaAsociada)
                .tipo(tipo)
                .fechaExpiracion(fechaExpiracion.orElse(LocalDate.now().plusYears(3)))
                .cvv(cvv.orElse(""))
                .activa(activa.orElse(true))
                .build();
    }
    
    /**
     * Crea una nueva instancia con fecha de expiración actualizada
     * @param nuevaFecha La nueva fecha de expiración (opcional)
     * @return Una nueva instancia con la fecha actualizada
     */
    public Tarjeta withFechaExpiracion(Optional<LocalDate> nuevaFecha) {
        return this.toBuilder()
                .fechaExpiracion(nuevaFecha.orElse(this.fechaExpiracion))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public Tarjeta withFechaExpiracion(LocalDate nuevaFecha) {
        return withFechaExpiracion(Optional.ofNullable(nuevaFecha));
    }
    
    /**
     * Crea una nueva instancia con estado activo actualizado
     * @param nuevaActiva El nuevo estado (opcional)
     * @return Una nueva instancia con el estado actualizado
     */
    public Tarjeta withActiva(Optional<Boolean> nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public Tarjeta withActiva(boolean nuevaActiva) {
        return withActiva(Optional.of(nuevaActiva));
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