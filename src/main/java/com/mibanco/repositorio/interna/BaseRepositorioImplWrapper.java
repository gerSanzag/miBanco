package com.mibanco.repositorio.interna;

import com.mibanco.modelo.Identificable;


/**
 * Wrapper para testing de BaseRepositorioImpl
 * Permite acceder a los métodos package-private para testing
 * Patrón: Test-Specific Subclass
 */
public abstract class BaseRepositorioImplWrapper<T extends Identificable, ID, E extends Enum<E>> extends BaseRepositorioImpl<T, ID, E> {
    // Clase vacía - hereda automáticamente todos los métodos públicos
    // No necesitamos escribir nada más
} 