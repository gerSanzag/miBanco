package com.mibanco.model.enums;

/**
 * Types of operations that can be performed with transactions
 */
public enum TransactionOperationType {
    CREATE,         // Crear
    CANCEL,         // Anular
    REVERSE,        // Reversar
    VERIFY,         // Verificar
    AUTHORIZE,      // Autorizar
    REJECT,         // Rechazar
    UPDATE,         // Actualizar
    DELETE          // Eliminar
} 