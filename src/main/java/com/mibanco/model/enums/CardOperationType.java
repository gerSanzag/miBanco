package com.mibanco.model.enums;

/**
 * Types of operations that can be performed with cards
 */
public enum CardOperationType {
    CREATE,             // Crear
    MODIFY,             // Modificar
    DELETE,             // Eliminar
    ACTIVATE,           // Activar
    DEACTIVATE,         // Desactivar
    BLOCK,              // Bloquear
    UNBLOCK,            // Desbloquear
    INCREASE_LIMIT,     // Aumentar límite
    REDUCE_LIMIT,       // Reducir límite
    UPDATE              // Actualizar
} 