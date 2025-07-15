package com.mibanco.dto;

import java.time.LocalDate;
import java.util.Optional;

import com.mibanco.modelo.enums.TipoTarjeta;
import com.mibanco.util.ReflexionUtil.NoSolicitar;

import lombok.Builder;
import lombok.Value;

/**
 * DTO para transferir información de Tarjeta entre capas
 * Utilizamos enfoque inmutable con @Value para mantener la integridad de los datos
 * Omitimos el CVV por razones de seguridad en transferencias entre capas
 */
@Value
@Builder(toBuilder = true) // Habilitamos toBuilder para facilitar métodos "with"
public class TarjetaDTO {
    @NoSolicitar(razon = "Se establece automáticamente en el repositorio")
    Long numero;
    ClienteDTO titular;
    String numeroCuentaAsociada;
    TipoTarjeta tipo;
    LocalDate fechaExpiracion;
    boolean activa;

    /**
     * Método estático que construye un TarjetaDTO con valores opcionales
     * Aplicando enfoque funcional con Optional para manejar valores nulos
     */
    public static TarjetaDTO of(Long numero, Optional<ClienteDTO> titular, Optional<String> numeroCuentaAsociada,
                               Optional<TipoTarjeta> tipo, Optional<LocalDate> fechaExpiracion,
                               Optional<Boolean> activa) {
        
        LocalDate fechaExp = fechaExpiracion.orElse(LocalDate.now().plusYears(3));
        
        return TarjetaDTO.builder()
                .numero(numero)
                .titular(titular.orElse(null))
                .numeroCuentaAsociada(numeroCuentaAsociada.orElse(null))
                .tipo(tipo.orElse(null))
                .fechaExpiracion(fechaExp)
                .activa(activa.orElse(true))
                .build();
    }
    
    /**
     * Crea una nueva instancia con fecha de expiración actualizada
     */
    public TarjetaDTO conFechaExpiracion(LocalDate nuevaFecha) {
        return this.toBuilder()
                .fechaExpiracion(nuevaFecha)
                .build();
    }
    
    /**
     * Crea una nueva instancia con estado activo actualizado
     */
    public TarjetaDTO conActiva(boolean nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva)
                .build();
    }
    
    /**
     * Crea una nueva instancia con titular actualizado
     */
    public TarjetaDTO conTitular(ClienteDTO nuevoTitular) {
        return this.toBuilder()
                .titular(nuevoTitular)
                .build();
    }
    
    /**
     * Crea una nueva instancia actualizando múltiples campos a la vez
     */
    public TarjetaDTO conActualizaciones(
            Optional<LocalDate> nuevaFecha,
            Optional<Boolean> nuevaActiva) {
        
        LocalDate fechaFinal = nuevaFecha.orElse(this.fechaExpiracion);
        
        return this.toBuilder()
                .fechaExpiracion(fechaFinal)
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 