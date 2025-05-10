# Interfaces de Repositorios

## TransaccionRepository.java

```java
package com.mibanco.repository;

import com.mibanco.model.Transaccion;
import com.mibanco.model.enums.TipoTransaccion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el repositorio de Transacciones
 * Utilizamos Optional para manejar valores nulos de forma funcional
 */
public interface TransaccionRepository {
    
    /**
     * Guarda una transacción
     * @param transaccion Transacción a guardar
     * @return Transacción guardada o empty si no se pudo guardar
     */
    Optional<Transaccion> save(Optional<Transaccion> transaccion);
    
    /**
     * Busca una transacción por su ID
     * @param id ID de la transacción
     * @return Transacción encontrada o empty si no existe
     */
    Optional<Transaccion> findById(Optional<Long> id);
    
    /**
     * Busca transacciones por número de cuenta
     * @param numeroCuenta Número de cuenta
     * @return Lista de transacciones encontradas
     */
    List<Transaccion> findByCuenta(Optional<String> numeroCuenta);
    
    /**
     * Busca transacciones por tipo
     * @param tipo Tipo de transacción
     * @return Lista de transacciones encontradas
     */
    List<Transaccion> findByTipo(Optional<TipoTransaccion> tipo);
    
    /**
     * Busca transacciones por fecha
     * @param fecha Fecha de la transacción
     * @return Lista de transacciones encontradas
     */
    List<Transaccion> findByFecha(Optional<LocalDate> fecha);
    
    /**
     * Busca transacciones por rango de fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de transacciones encontradas
     */
    List<Transaccion> findByRangoFechas(Optional<LocalDate> fechaInicio, Optional<LocalDate> fechaFin);
    
    /**
     * Obtiene todas las transacciones
     * @return Lista de todas las transacciones
     */
    Optional<List<Transaccion>> findAll();
    
    /**
     * Elimina una transacción por su ID
     * @param id ID de la transacción a eliminar
     * @return true si se eliminó, false si no existía
     */
    boolean deleteById(Optional<Long> id);
    
    /**
     * Cuenta el número total de transacciones
     * @return Número de transacciones
     */
    long count();
}
```

## CuentaRepository.java

```java
package com.mibanco.repository;

import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el repositorio de Cuentas
 * Utilizamos Optional para manejar valores nulos de forma funcional
 */
public interface CuentaRepository {
    
    /**
     * Guarda una cuenta
     * @param cuenta Cuenta a guardar
     * @return Cuenta guardada o empty si no se pudo guardar
     */
    Optional<Cuenta> save(Optional<Cuenta> cuenta);
    
    /**
     * Busca una cuenta por su número
     * @param numeroCuenta Número de cuenta
     * @return Cuenta encontrada o empty si no existe
     */
    Optional<Cuenta> findByNumero(Optional<String> numeroCuenta);
    
    /**
     * Busca cuentas por ID del titular
     * @param idTitular ID del titular
     * @return Lista de cuentas encontradas
     */
    Optional<List<Cuenta>> findByTitularId(Optional<Long> idTitular);
    
    /**
     * Busca cuentas por tipo
     * @param tipo Tipo de cuenta
     * @return Lista de cuentas encontradas
     */
    Optional<List<Cuenta>> findByTipo(Optional<TipoCuenta> tipo);
    
    /**
     * Obtiene todas las cuentas
     * @return Lista de todas las cuentas
     */
    Optional<List<Cuenta>> findAll();
    
    /**
     * Obtiene todas las cuentas activas
     * @return Lista de cuentas activas
     */
    Optional<List<Cuenta>> findAllActivas();
    
    /**
     * Elimina una cuenta por su número
     * @param numeroCuenta Número de la cuenta a eliminar
     * @return true si se eliminó, false si no existía
     */
    boolean deleteByNumero(Optional<String> numeroCuenta);
    
    /**
     * Cuenta el número total de cuentas
     * @return Número de cuentas
     */
    long count();
}
```

## TarjetaRepository.java

```java
package com.mibanco.repository;

import com.mibanco.model.Tarjeta;
import com.mibanco.model.enums.TipoTarjeta;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el repositorio de Tarjetas
 * Utilizamos Optional para manejar valores nulos de forma funcional
 */
public interface TarjetaRepository {
    
    /**
     * Guarda una tarjeta
     * @param tarjeta Tarjeta a guardar
     * @return Tarjeta guardada o empty si no se pudo guardar
     */
    Optional<Tarjeta> save(Optional<Tarjeta> tarjeta);
    
    /**
     * Busca una tarjeta por su número
     * @param numeroTarjeta Número de tarjeta
     * @return Tarjeta encontrada o empty si no existe
     */
    Optional<Tarjeta> findByNumero(Optional<String> numeroTarjeta);
    
    /**
     * Busca tarjetas por ID del titular
     * @param idTitular ID del titular
     * @return Lista de tarjetas encontradas
     */
    Optional<List<Tarjeta>> findByTitularId(Optional<Long> idTitular);
    
    /**
     * Busca tarjetas por número de cuenta asociada
     * @param numeroCuenta Número de cuenta
     * @return Lista de tarjetas encontradas
     */
    Optional<List<Tarjeta>> findByCuentaAsociada(Optional<String> numeroCuenta);
    
    /**
     * Busca tarjetas por tipo
     * @param tipo Tipo de tarjeta
     * @return Lista de tarjetas encontradas
     */
    Optional<List<Tarjeta>> findByTipo(Optional<TipoTarjeta> tipo);
    
    /**
     * Obtiene todas las tarjetas
     * @return Lista de todas las tarjetas
     */
    Optional<List<Tarjeta>> findAll();
    
    /**
     * Obtiene todas las tarjetas activas
     * @return Lista de tarjetas activas
     */
    Optional<List<Tarjeta>> findAllActivas();
    
    /**
     * Elimina una tarjeta por su número
     * @param numeroTarjeta Número de la tarjeta a eliminar
     * @return true si se eliminó, false si no existía
     */
    boolean deleteByNumero(Optional<String> numeroTarjeta);
    
    /**
     * Cuenta el número total de tarjetas
     * @return Número de tarjetas
     */
    long count();
}
```

## AuditoriaRepository.java

```java
package com.mibanco.repository;

import com.mibanco.model.RegistroAuditoria;

/**
 * Interfaz para el repositorio de Auditoría
 * Utilizamos genéricos para manejar diferentes tipos de entidades y operaciones
 */
public interface AuditoriaRepository {
    
    /**
     * Registra una operación de auditoría
     * @param registro Registro de auditoría a guardar
     * @return El registro guardado
     * @param <T> Tipo de entidad
     * @param <E> Tipo de operación
     */
    <T, E extends Enum<E>> RegistroAuditoria<T, E> registrar(RegistroAuditoria<T, E> registro);
    
    /**
     * Obtiene todos los registros de auditoría
     * @return Lista de registros de auditoría
     * @param <T> Tipo de entidad
     * @param <E> Tipo de operación
     */
    <T, E extends Enum<E>> List<RegistroAuditoria<T, E>> findAll();
    
    /**
     * Busca registros de auditoría por tipo de entidad
     * @param tipoEntidad Tipo de entidad
     * @return Lista de registros de auditoría
     * @param <T> Tipo de entidad
     * @param <E> Tipo de operación
     */
    <T, E extends Enum<E>> List<RegistroAuditoria<T, E>> findByTipoEntidad(Class<T> tipoEntidad);
    
    /**
     * Busca registros de auditoría por tipo de operación
     * @param tipoOperacion Tipo de operación
     * @return Lista de registros de auditoría
     * @param <T> Tipo de entidad
     * @param <E> Tipo de operación
     */
    <T, E extends Enum<E>> List<RegistroAuditoria<T, E>> findByTipoOperacion(E tipoOperacion);
    
    /**
     * Busca registros de auditoría por usuario
     * @param usuario Usuario que realizó la operación
     * @return Lista de registros de auditoría
     * @param <T> Tipo de entidad
     * @param <E> Tipo de operación
     */
    <T, E extends Enum<E>> List<RegistroAuditoria<T, E>> findByUsuario(String usuario);
    
    /**
     * Busca registros de auditoría por rango de fechas
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de registros de auditoría
     * @param <T> Tipo de entidad
     * @param <E> Tipo de operación
     */
    <T, E extends Enum<E>> List<RegistroAuditoria<T, E>> findByRangoFechas(java.time.LocalDateTime fechaInicio, java.time.LocalDateTime fechaFin);
    
    /**
     * Cuenta el número total de registros de auditoría
     * @return Número de registros
     */
    long count();
}
``` 