package com.mibanco.repositorio.interna;

/**
 * Wrapper para testing de BaseProcesadorJson
 * Permite acceder a los métodos package-private para testing
 * Patrón: Test-Specific Subclass
 */
public class BaseProcesadorJsonWrapper<T> extends BaseProcesadorJson<T> {
    // Clase vacía - hereda automáticamente todos los métodos públicos
    // No necesitamos escribir nada más
} 