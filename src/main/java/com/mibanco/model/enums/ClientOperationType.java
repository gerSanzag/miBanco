package com.mibanco.model.enums;

/**
 * Types of operations that can be performed with clients
 */
public enum ClientOperationType {
    CREATE,                 // Crear
    UPDATE,                 // Actualizar
    MODIFY,                 // Modificar
    DELETE,                 // Eliminar
    QUERY,                  // Consultar
    RESTORE,                // Restaurar
    BLOCK,                  // Bloquear
    UNBLOCK,                // Desbloquear
    CHANGE_CONTACT_DATA     // Cambiar datos de contacto
} 