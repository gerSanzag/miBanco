# 007-22/05/2025 00:38:13-Implementación CuentaService - Parte 2

## Usuario
Perfecto. Si tienes alguna otra pregunta sobre el código o cualquier otra duda relacionada con Java, programación funcional o el proyecto, estoy aquí para ayudarte.

## Usuario
Crea cuentaServicio, fijate en como has desarrollado el cliente para sigas la misma estructura de los metodos.

## Asistente
Voy a crear el servicio para Cuenta siguiendo la misma estructura que hemos usado para Cliente. Primero, necesito ver cómo están implementadas las clases relacionadas para mantener coherencia.

/Users/german/ejerciciosJava/miBanco/src/main/java/com/mibanco/model/Cuenta.java
/Users/german/ejerciciosJava/miBanco/src/main/java/com/mibanco/model/enums/TipoCuenta.java
/Users/german/ejerciciosJava/miBanco/src/main/java/com/mibanco/model/enums/TipoOperacionCuenta.java
/Users/german/ejerciciosJava/miBanco/src/main/java/com/mibanco/dto/CuentaDTO.java
/Users/german/ejerciciosJava/miBanco/src/main/java/com/mibanco/repository/CuentaRepository.java
/Users/german/ejerciciosJava/miBanco/src/main/java/com/mibanco/repository/impl/CuentaRepositoryImpl.java
/Users/german/ejerciciosJava/miBanco/src/main/java/com/mibanco/model/enums/TipoOperacionTarjeta.java
/Users/german/ejerciciosJava/miBanco/src/main/java/com/mibanco/model/enums/TipoOperacionTransaccion.java

Primero, revisaré el modelo de Cuenta:

Contents of /Users/german/ejerciciosJava/miBanco/src/main/java/com/mibanco/model/Cuenta.java, lines 1-80 (entire file):
```
package com.mibanco.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import lombok.Builder;
import lombok.Value;
import com.mibanco.model.enums.TipoCuenta;

/**
 * Clase que representa una cuenta bancaria
 * Implementa un enfoque completamente funcional con inmutabilidad total
 */
@Value
@Builder(toBuilder = true)
public class Cuenta implements Identificable {
    String numeroCuenta;
    Cliente titular;
    TipoCuenta tipo;
    LocalDateTime fechaCreacion;
    BigDecimal saldo;
    boolean activa;
    
    /**
     * Implementación de getId() para la interfaz Identificable
     * Utilizamos el número de cuenta como ID usando un hash del String
     * @return Un Long que representa el número de cuenta
     */
    @Override
    public Long getId() {
        return numeroCuenta != null ? (long) numeroCuenta.hashCode() : null;
    }
    
    /**
     * Método factory para facilitar la creación de instancias
     */
    public static Cuenta of(String numeroCuenta, Cliente titular, TipoCuenta tipo, 
                           BigDecimal saldo, LocalDateTime fechaCreacion, boolean activa) {
        return Cuenta.builder()
                .numeroCuenta(numeroCuenta)
                .titular(titular)
                .tipo(tipo)
                .saldo(saldo != null ? saldo : BigDecimal.ZERO)
                .fechaCreacion(fechaCreacion != null ? fechaCreacion : LocalDateTime.now())
                .activa(activa)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el saldo
     * @return Una nueva instancia con el saldo actualizado
     */
    public Cuenta withSaldo(BigDecimal nuevoSaldo) {
        return this.toBuilder()
                .saldo(nuevoSaldo)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar el estado activo
     * @return Una nueva instancia con el estado actualizado
     */
    public Cuenta withActiva(boolean nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva)
                .build();
    }
    
    /**
     * Versión inmutable para actualizar múltiples campos a la vez
     * @return Una nueva instancia con los campos actualizados
     */
    public Cuenta withActualizaciones(Optional<BigDecimal> nuevoSaldo, Optional<Boolean> nuevaActiva) {
        return this.toBuilder()
                .saldo(nuevoSaldo.orElse(this.saldo))
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 
```

Ahora, revisaré el DTO:

Contents of /Users/german/ejerciciosJava/miBanco/src/main/java/com/mibanco/dto/CuentaDTO.java, lines 1-85 (entire file):
```
package com.mibanco.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import lombok.Builder;
import lombok.Value;
import com.mibanco.model.enums.TipoCuenta;

/**
 * DTO para transferir información de Cuenta entre capas
 * Utilizamos enfoque inmutable y funcional con @Value
 */
@Value
@Builder(toBuilder = true) // Habilitamos toBuilder para facilitar métodos "with"
public class CuentaDTO {
    String numeroCuenta;
    ClienteDTO titular;
    TipoCuenta tipo;
    BigDecimal saldo;
    LocalDateTime fechaCreacion;
    boolean activa;

    /**
     * Método estático que construye un CuentaDTO con valores opcionales
     * Ejemplo de uso del enfoque funcional y Optionals
     */
    public static CuentaDTO of(String numeroCuenta, ClienteDTO titular, TipoCuenta tipo,
                               BigDecimal saldo, Optional<LocalDateTime> fechaCreacion, 
                               Optional<Boolean> activa) {
        return CuentaDTO.builder()
                .numeroCuenta(numeroCuenta)
                .titular(titular)
                .tipo(tipo)
                .saldo(saldo)
                .fechaCreacion(fechaCreacion.orElse(LocalDateTime.now()))
                .activa(activa.orElse(true))
                .build();
    }
    
    /**
     * Crea una nueva instancia con saldo actualizado
     * Método "with" para transformación inmutable
     */
    public CuentaDTO withSaldo(BigDecimal nuevoSaldo) {
        return this.toBuilder()
                .saldo(nuevoSaldo)
                .build();
    }
    
    /**
     * Crea una nueva instancia con estado activo actualizado
     * Método "with" para transformación inmutable
     */
    public CuentaDTO withActiva(boolean nuevaActiva) {
        return this.toBuilder()
                .activa(nuevaActiva)
                .build();
    }
    
    /**
     * Crea una nueva instancia con titular actualizado
     * Método "with" para transformación inmutable
     */
    public CuentaDTO withTitular(ClienteDTO nuevoTitular) {
        return this.toBuilder()
                .titular(nuevoTitular)
                .build();
    }
    
    /**
     * Crea una nueva instancia actualizando múltiples campos a la vez
     * Método para actualizaciones inmutables múltiples
     */
    public CuentaDTO withActualizaciones(
            Optional<BigDecimal> nuevoSaldo,
            Optional<Boolean> nuevaActiva) {
        
        return this.toBuilder()
                .saldo(nuevoSaldo.orElse(this.saldo))
                .activa(nuevaActiva.orElse(this.activa))
                .build();
    }
} 
```

Ahora, revisaré el repositorio:

Contents of /Users/german/ejerciciosJava/miBanco/src/main/java/com/mibanco/repository/CuentaRepository.java, lines 1-69 (entire file):
```
package com.mibanco.repository;

import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones CRUD de Cuenta
 * Siguiendo enfoque estrictamente funcional con Optional
 * tanto para parámetros como para resultados
 */
public interface CuentaRepository {
    
    /**
     * Guarda una cuenta en el repositorio
     * @param cuenta Optional con la cuenta a guardar
     * @return Optional con la cuenta guardada o Optional vacío si la cuenta era null
     */
    Optional<Cuenta> save(Optional<Cuenta> cuenta);
    
    /**
     * Busca una cuenta por su número
     * @param numeroCuenta Optional con el número de la cuenta a buscar
     * @return Optional con la cuenta si existe
     */
    Optional<Cuenta> findByNumero(Optional<String> numeroCuenta);
    
    /**
     * Busca cuentas por el ID del titular
     * @param idTitular Optional con el ID del titular
     * @return Lista de cuentas del titular
     */
    Optional<List<Cuenta>>findByTitularId(Optional<Long> idTitular);
    
    /**
     * Busca cuentas por el tipo
     * @param tipo Optional con el tipo de cuenta
     * @return Lista de cuentas del tipo especificado
     */
    
    Optional<List<Cuenta>> findByTipo(Optional<TipoCuenta> tipo);
    
    /**
     * Obtiene todas las cuentas
     * @return Lista de cuentas
     */
    Optional<List<Cuenta>> findAll();
    
    /**
     * Obtiene todas las cuentas activas
     * @return Lista de cuentas activas
     */
    Optional<List<Cuenta>> findAllActivas();
    
    /**
     * Elimina una cuenta por su número
     * @param numeroCuenta Optional con el número de la cuenta a eliminar
     * @return Optional con la cuenta eliminada o Optional vacío si no existía
     */
    Optional<Cuenta> deleteByNumero(Optional<String> numeroCuenta);
    
    /**
     * Obtiene el número de cuentas en el repositorio
     * @return Número de cuentas
     */
    long count();
} 