package com.mibanco.model;

/**
 * Interfaz para entidades que tienen un identificador único
 * Permite restringir los tipos genéricos a entidades que puedan ser identificadas
 */
public interface Identificable {
    /**
     * Obtiene el identificador único de la entidad
     * @return El ID único de la entidad
     */
    Long getId();
} 