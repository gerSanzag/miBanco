# Enumeraciones de Tipos de Operaciones

## TipoOperacionTransaccion.java

```java
package com.mibanco.model.enums;

/**
 * Enumeración para los tipos de operaciones que se pueden realizar sobre transacciones
 */
public enum TipoOperacionTransaccion {
    CREAR,      // Crear una nueva transacción
    MODIFICAR,  // Modificar una transacción existente
    ELIMINAR,   // Eliminar una transacción
    VERIFICAR,  // Verificar una transacción
    AUTORIZAR,  // Autorizar una transacción
    RECHAZAR,   // Rechazar una transacción
    ANULAR,     // Anular una transacción
    REVERSAR    // Revertir una anulación
}
```

## TipoOperacionCuenta.java

```java
package com.mibanco.model.enums;

/**
 * Enumeración para los tipos de operaciones que se pueden realizar sobre cuentas
 */
public enum TipoOperacionCuenta {
    CREAR,      // Crear una nueva cuenta
    MODIFICAR,  // Modificar una cuenta existente
    ELIMINAR,   // Eliminar una cuenta
    ACTIVAR,    // Activar una cuenta
    DESACTIVAR  // Desactivar una cuenta
}
```

## TipoOperacionTarjeta.java

```java
package com.mibanco.model.enums;

/**
 * Enumeración para los tipos de operaciones que se pueden realizar sobre tarjetas
 */
public enum TipoOperacionTarjeta {
    CREAR,      // Crear una nueva tarjeta
    MODIFICAR,  // Modificar una tarjeta existente
    ELIMINAR,   // Eliminar una tarjeta
    ACTIVAR,    // Activar una tarjeta
    DESACTIVAR  // Desactivar una tarjeta
}
```

## TipoTransaccion.java

```java
package com.mibanco.model.enums;

/**
 * Enumeración para los tipos de transacciones bancarias
 */
public enum TipoTransaccion {
    DEPOSITO,           // Depósito en efectivo
    RETIRO,            // Retiro en efectivo
    TRANSFERENCIA,     // Transferencia entre cuentas
    PAGO_SERVICIO,     // Pago de servicios
    PAGO_TARJETA,      // Pago de tarjeta de crédito
    COMPRA_TARJETA,    // Compra con tarjeta
    INTERES,           // Interés generado
    COMISION,          // Comisión cobrada
    REEMBOLSO,         // Reembolso
    ANULACION          // Anulación de transacción
}
```

## TipoCuenta.java

```java
package com.mibanco.model.enums;

/**
 * Enumeración para los tipos de cuentas bancarias
 */
public enum TipoCuenta {
    AHORRO,     // Cuenta de ahorro
    CORRIENTE,  // Cuenta corriente
    PLAZO_FIJO, // Cuenta a plazo fijo
    INVERSION   // Cuenta de inversión
}
```

## TipoTarjeta.java

```java
package com.mibanco.model.enums;

/**
 * Enumeración para los tipos de tarjetas bancarias
 */
public enum TipoTarjeta {
    DEBITO,     // Tarjeta de débito
    CREDITO,    // Tarjeta de crédito
    PREPAGO     // Tarjeta prepago
}
``` 