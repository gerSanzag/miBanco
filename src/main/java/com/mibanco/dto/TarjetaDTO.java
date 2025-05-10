package com.mibanco.dto;

import com.mibanco.model.enums.TipoTarjeta;
import lombok.AllArgsConstructor;
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
@Builder(toBuilder = true) // Habilitamos toBuilder para facilitar métodos "with"
@AllArgsConstructor
public class TarjetaDTO {
    String numero;
    ClienteDTO titular;
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
     */
    public TarjetaDTO withFechaExpiracion(LocalDate nuevaFecha) {
        return this.toBuilder()
                .fechaExpiracion(nuevaFecha)
                .build();
    }
    
    /**
     * Crea una nueva instancia con estado activo actualizado
     */
    public TarjetaDTO withActiva(boolean nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva)
                .build();
    }
    
    /**
     * Crea una nueva instancia con titular actualizado
     */
    public TarjetaDTO withTitular(ClienteDTO nuevoTitular) {
        return this.toBuilder()
                .titular(nuevoTitular)
                .build();
    }
    
    /**
     * Crea una nueva instancia actualizando múltiples campos a la vez
     */
    public TarjetaDTO withActualizaciones(
            Optional<LocalDate> nuevaFecha,
            Optional<Boolean> nuevaActiva) {
        
        LocalDate fechaFinal = nuevaFecha.orElse(this.fechaExpiracion);
        
        return this.toBuilder()
                .fechaExpiracion(fechaFinal)
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 