package com.mibanco.dto;

import com.mibanco.model.enums.TipoTarjeta;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Optional;

/**
 * DTO para transferir información de Tarjeta entre capas
 * Utilizamos enfoque inmutable con @Value para mantener la integridad de los datos
 * Omitimos el CVV por razones de seguridad en transferencias entre capas
 */
@Value
@Builder(toBuilder = true)
public class TarjetaDTO {
    String numero;
    ClienteDTO titular; // El titular es inmutable, una vez asignado no puede cambiar
    String numeroCuentaAsociada;
    TipoTarjeta tipo;
    LocalDate fechaExpiracion;
    // Omitimos intencionalmente el CVV por seguridad en los DTOs
    boolean activa;

    /**
     * Método estático que construye un TarjetaDTO con valores opcionales
     * Aplicando enfoque funcional con Optional para manejar valores nulos
     */
    public static TarjetaDTO of(String numero, ClienteDTO titular, String numeroCuentaAsociada,
                               TipoTarjeta tipo, Optional<LocalDate> fechaExpiracion,
                               Optional<Boolean> activa) {
        
        LocalDate fechaExp = fechaExpiracion.orElse(LocalDate.now().plusYears(3));
        
        return TarjetaDTO.builder()
                .numero(numero)
                .titular(titular)
                .numeroCuentaAsociada(numeroCuentaAsociada)
                .tipo(tipo)
                .fechaExpiracion(fechaExp)
                .activa(activa.orElse(true))
                .build();
    }
    
    /**
     * Crea una nueva instancia con fecha de expiración actualizada
     * @param nuevaFecha La nueva fecha de expiración (opcional)
     * @return Una nueva instancia con la fecha actualizada
     */
    public TarjetaDTO withFechaExpiracion(Optional<LocalDate> nuevaFecha) {
        return this.toBuilder()
                .fechaExpiracion(nuevaFecha.orElse(this.fechaExpiracion))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public TarjetaDTO withFechaExpiracion(LocalDate nuevaFecha) {
        return withFechaExpiracion(Optional.ofNullable(nuevaFecha));
    }
    
    /**
     * Crea una nueva instancia con estado activo actualizado
     * @param nuevaActiva El nuevo estado (opcional)
     * @return Una nueva instancia con el estado actualizado
     */
    public TarjetaDTO withActiva(Optional<Boolean> nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
    
    /**
     * Sobrecarga para facilitar el uso cuando el valor no es null
     */
    public TarjetaDTO withActiva(boolean nuevaActiva) {
        return withActiva(Optional.of(nuevaActiva));
    }
    
    /**
     * Crea una nueva instancia actualizando múltiples campos a la vez
     * Los únicos campos que pueden cambiar en una tarjeta son
     * su fecha de expiración y estado activo.
     * El titular es inmutable según las prácticas bancarias reales.
     */
    public TarjetaDTO withActualizaciones(
            Optional<LocalDate> nuevaFecha,
            Optional<Boolean> nuevaActiva) {
        
        return this.toBuilder()
                .fechaExpiracion(nuevaFecha.orElse(this.fechaExpiracion))
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 