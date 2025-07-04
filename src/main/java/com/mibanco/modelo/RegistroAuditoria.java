package com.mibanco.modelo;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;


/**
 * Registro inmutable de auditoría para cualquier entidad
 * Utiliza dos tipos genéricos:
 * - T para la entidad (debe implementar Identificable)
 * - E para el tipo de operación (debe ser un enum)
 */
@Value
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE) // Constructor privado para forzar uso del Builder
public class RegistroAuditoria<T extends Identificable, E extends Enum<E>> {
    // ID único del registro
    UUID id;
    
    // Tipo de operación (cualquier enum)
    E tipoOperacion;
    
    // Fecha y hora exacta del evento
    LocalDateTime fechaHora;
    
    // La entidad involucrada (debe implementar Identificable)
    T entidad;
    
    // Usuario que realizó la operación
    String usuario;
    
    // Monto de la operación (útil para operaciones financieras)
    Double monto;
    
    // Información adicional de la operación
    String detalles;
    
    /**
     * Método factory para crear registros básicos
     * @param tipoOperacion Tipo de operación (enum)
     * @param entidad Entidad afectada
     * @param usuario Usuario que realizó la operación
     * @return Registro de auditoría inmutable
     */
    public static <T extends Identificable, E extends Enum<E>> RegistroAuditoria<T, E> of(
            E tipoOperacion, 
            T entidad, 
            String usuario) {
        
        return RegistroAuditoria.<T, E>builder()
                .id(UUID.randomUUID())
                .tipoOperacion(tipoOperacion)
                .fechaHora(LocalDateTime.now())
                .entidad(entidad)
                .usuario(usuario)
                .build();
    }
    
    /**
     * Método factory para crear registros con información financiera
     * @param tipoOperacion Tipo de operación (enum)
     * @param entidad Entidad afectada
     * @param usuario Usuario que realizó la operación
     * @param monto Monto de la operación financiera
     * @param detalles Detalles adicionales
     * @return Registro de auditoría inmutable
     */
    public static <T extends Identificable, E extends Enum<E>> RegistroAuditoria<T, E> ofDetallado(
            E tipoOperacion, 
            T entidad, 
            String usuario,
            Double monto,
            String detalles) {
        
        return RegistroAuditoria.<T, E>builder()
                .id(UUID.randomUUID())
                .tipoOperacion(tipoOperacion)
                .fechaHora(LocalDateTime.now())
                .entidad(entidad)
                .usuario(usuario)
                .monto(monto)
                .detalles(detalles)
                .build();
    }
} 