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
@Builder
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
        
        // Validar que la fecha de expiración sea futura
        LocalDate fechaExp = fechaExpiracion.orElse(LocalDate.now().plusYears(3));
        if (fechaExp.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de expiración debe ser futura");
        }
        
        return TarjetaDTO.builder()
                .numero(numero)
                .titular(titular)
                .numeroCuentaAsociada(numeroCuentaAsociada)
                .tipo(tipo)
                .fechaExpiracion(fechaExp)
                .activa(activa.orElse(true))
                .build();
    }
} 