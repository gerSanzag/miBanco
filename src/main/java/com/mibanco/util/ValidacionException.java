package com.mibanco.util;

/**
 * Excepción específica para errores de validación de negocio
 * Se lanza cuando se violan reglas del dominio
 */
public class ValidacionException extends RuntimeException {
    
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
    
    public ValidacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
} 