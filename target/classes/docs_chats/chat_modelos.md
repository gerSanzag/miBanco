# Clases de Modelo

## Identificable.java

```java
package com.mibanco.model;

/**
 * Interfaz para entidades que tienen un identificador único
 * @param <T> Tipo del identificador
 */
public interface Identificable<T> {
    /**
     * Obtiene el identificador único de la entidad
     * @return Identificador único
     */
    T getId();
}
```

## RegistroAuditoria.java

```java
package com.mibanco.model;

import java.time.LocalDateTime;

/**
 * Clase para registrar operaciones de auditoría
 * @param <T> Tipo de entidad
 * @param <E> Tipo de operación
 */
public class RegistroAuditoria<T, E extends Enum<E>> {
    private final T entidad;
    private final E tipoOperacion;
    private final String usuario;
    private final LocalDateTime fechaHora;
    
    private RegistroAuditoria(T entidad, E tipoOperacion, String usuario) {
        this.entidad = entidad;
        this.tipoOperacion = tipoOperacion;
        this.usuario = usuario;
        this.fechaHora = LocalDateTime.now();
    }
    
    /**
     * Crea un nuevo registro de auditoría
     * @param entidad Entidad sobre la que se realizó la operación
     * @param tipoOperacion Tipo de operación realizada
     * @param usuario Usuario que realizó la operación
     * @return Nuevo registro de auditoría
     * @param <T> Tipo de entidad
     * @param <E> Tipo de operación
     */
    public static <T, E extends Enum<E>> RegistroAuditoria<T, E> of(T entidad, E tipoOperacion, String usuario) {
        return new RegistroAuditoria<>(entidad, tipoOperacion, usuario);
    }
    
    public T getEntidad() {
        return entidad;
    }
    
    public E getTipoOperacion() {
        return tipoOperacion;
    }
    
    public String getUsuario() {
        return usuario;
    }
    
    public LocalDateTime getFechaHora() {
        return fechaHora;
    }
    
    @Override
    public String toString() {
        return String.format("RegistroAuditoria{entidad=%s, tipoOperacion=%s, usuario='%s', fechaHora=%s}",
            entidad, tipoOperacion, usuario, fechaHora);
    }
}
```

## Transaccion.java

```java
package com.mibanco.model;

import com.mibanco.model.enums.TipoTransaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase que representa una transacción bancaria
 * Implementa inmutabilidad total
 */
public final class Transaccion implements Identificable<Long> {
    private final Long id;
    private final String numeroCuenta;
    private final TipoTransaccion tipo;
    private final BigDecimal monto;
    private final String descripcion;
    private final LocalDateTime fecha;
    
    private Transaccion(Long id, String numeroCuenta, TipoTransaccion tipo, 
                       BigDecimal monto, String descripcion, LocalDateTime fecha) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }
    
    /**
     * Crea una nueva transacción
     * @param numeroCuenta Número de cuenta
     * @param tipo Tipo de transacción
     * @param monto Monto de la transacción
     * @param descripcion Descripción de la transacción
     * @return Nueva transacción
     */
    public static Transaccion of(String numeroCuenta, TipoTransaccion tipo, 
                               BigDecimal monto, String descripcion) {
        return new Transaccion(null, numeroCuenta, tipo, monto, descripcion, LocalDateTime.now());
    }
    
    /**
     * Crea una copia de la transacción con un nuevo ID
     * @param nuevoId Nuevo ID
     * @return Nueva transacción con el ID actualizado
     */
    public Transaccion withNuevoId(Long nuevoId) {
        return new Transaccion(nuevoId, numeroCuenta, tipo, monto, descripcion, fecha);
    }
    
    /**
     * Crea una copia de la transacción con una nueva descripción
     * @param nuevaDescripcion Nueva descripción
     * @return Nueva transacción con la descripción actualizada
     */
    public Transaccion withDescripcion(String nuevaDescripcion) {
        return new Transaccion(id, numeroCuenta, tipo, monto, nuevaDescripcion, fecha);
    }
    
    @Override
    public Long getId() {
        return id;
    }
    
    public String getNumeroCuenta() {
        return numeroCuenta;
    }
    
    public TipoTransaccion getTipo() {
        return tipo;
    }
    
    public BigDecimal getMonto() {
        return monto;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public LocalDateTime getFecha() {
        return fecha;
    }
    
    @Override
    public String toString() {
        return String.format("Transaccion{id=%d, numeroCuenta='%s', tipo=%s, monto=%s, descripcion='%s', fecha=%s}",
            id, numeroCuenta, tipo, monto, descripcion, fecha);
    }
}
```

## Cuenta.java

```java
package com.mibanco.model;

import com.mibanco.model.enums.TipoCuenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Clase que representa una cuenta bancaria
 * Implementa inmutabilidad selectiva
 */
public class Cuenta implements Identificable<String> {
    private final String numeroCuenta;
    private final Cliente titular;
    private final TipoCuenta tipo;
    private final BigDecimal saldo;
    private final boolean activa;
    private final LocalDateTime fechaCreacion;
    private final LocalDateTime fechaUltimaOperacion;
    
    private Cuenta(String numeroCuenta, Cliente titular, TipoCuenta tipo, 
                  BigDecimal saldo, boolean activa, LocalDateTime fechaCreacion,
                  LocalDateTime fechaUltimaOperacion) {
        this.numeroCuenta = numeroCuenta;
        this.titular = titular;
        this.tipo = tipo;
        this.saldo = saldo;
        this.activa = activa;
        this.fechaCreacion = fechaCreacion;
        this.fechaUltimaOperacion = fechaUltimaOperacion;
    }
    
    /**
     * Crea una nueva cuenta
     * @param numeroCuenta Número de cuenta
     * @param titular Titular de la cuenta
     * @param tipo Tipo de cuenta
     * @param saldo Saldo inicial
     * @return Nueva cuenta
     */
    public static Cuenta of(String numeroCuenta, Cliente titular, TipoCuenta tipo, BigDecimal saldo) {
        LocalDateTime ahora = LocalDateTime.now();
        return new Cuenta(numeroCuenta, titular, tipo, saldo, true, ahora, ahora);
    }
    
    /**
     * Crea una copia de la cuenta con un nuevo saldo
     * @param nuevoSaldo Nuevo saldo
     * @return Nueva cuenta con el saldo actualizado
     */
    public Cuenta withSaldo(BigDecimal nuevoSaldo) {
        return new Cuenta(numeroCuenta, titular, tipo, nuevoSaldo, activa, 
                         fechaCreacion, LocalDateTime.now());
    }
    
    /**
     * Crea una copia de la cuenta con un nuevo estado de activación
     * @param activa Nuevo estado de activación
     * @return Nueva cuenta con el estado de activación actualizado
     */
    public Cuenta withActiva(boolean activa) {
        return new Cuenta(numeroCuenta, titular, tipo, saldo, activa, 
                         fechaCreacion, LocalDateTime.now());
    }
    
    @Override
    public String getId() {
        return numeroCuenta;
    }
    
    public String getNumeroCuenta() {
        return numeroCuenta;
    }
    
    public Cliente getTitular() {
        return titular;
    }
    
    public TipoCuenta getTipo() {
        return tipo;
    }
    
    public BigDecimal getSaldo() {
        return saldo;
    }
    
    public boolean isActiva() {
        return activa;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public LocalDateTime getFechaUltimaOperacion() {
        return fechaUltimaOperacion;
    }
    
    @Override
    public String toString() {
        return String.format("Cuenta{numeroCuenta='%s', titular=%s, tipo=%s, saldo=%s, activa=%b, fechaCreacion=%s, fechaUltimaOperacion=%s}",
            numeroCuenta, titular, tipo, saldo, activa, fechaCreacion, fechaUltimaOperacion);
    }
}
```

## Tarjeta.java

```java
package com.mibanco.model;

import com.mibanco.model.enums.TipoTarjeta;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Clase que representa una tarjeta bancaria
 * Implementa inmutabilidad selectiva
 */
public class Tarjeta implements Identificable<String> {
    private final String numero;
    private final Cliente titular;
    private final String numeroCuentaAsociada;
    private final TipoTarjeta tipo;
    private final BigDecimal limite;
    private final LocalDate fechaVencimiento;
    private final boolean activa;
    
    private Tarjeta(String numero, Cliente titular, String numeroCuentaAsociada,
                   TipoTarjeta tipo, BigDecimal limite, LocalDate fechaVencimiento,
                   boolean activa) {
        this.numero = numero;
        this.titular = titular;
        this.numeroCuentaAsociada = numeroCuentaAsociada;
        this.tipo = tipo;
        this.limite = limite;
        this.fechaVencimiento = fechaVencimiento;
        this.activa = activa;
    }
    
    /**
     * Crea una nueva tarjeta
     * @param numero Número de tarjeta
     * @param titular Titular de la tarjeta
     * @param numeroCuentaAsociada Número de cuenta asociada
     * @param tipo Tipo de tarjeta
     * @param limite Límite de la tarjeta
     * @param fechaVencimiento Fecha de vencimiento
     * @return Nueva tarjeta
     */
    public static Tarjeta of(String numero, Cliente titular, String numeroCuentaAsociada,
                           TipoTarjeta tipo, BigDecimal limite, LocalDate fechaVencimiento) {
        return new Tarjeta(numero, titular, numeroCuentaAsociada, tipo, limite, 
                          fechaVencimiento, true);
    }
    
    /**
     * Crea una copia de la tarjeta con un nuevo límite
     * @param nuevoLimite Nuevo límite
     * @return Nueva tarjeta con el límite actualizado
     */
    public Tarjeta withLimite(BigDecimal nuevoLimite) {
        return new Tarjeta(numero, titular, numeroCuentaAsociada, tipo, nuevoLimite,
                          fechaVencimiento, activa);
    }
    
    /**
     * Crea una copia de la tarjeta con una nueva fecha de vencimiento
     * @param nuevaFechaVencimiento Nueva fecha de vencimiento
     * @return Nueva tarjeta con la fecha de vencimiento actualizada
     */
    public Tarjeta withFechaVencimiento(LocalDate nuevaFechaVencimiento) {
        return new Tarjeta(numero, titular, numeroCuentaAsociada, tipo, limite,
                          nuevaFechaVencimiento, activa);
    }
    
    /**
     * Crea una copia de la tarjeta con un nuevo estado de activación
     * @param activa Nuevo estado de activación
     * @return Nueva tarjeta con el estado de activación actualizado
     */
    public Tarjeta withActiva(boolean activa) {
        return new Tarjeta(numero, titular, numeroCuentaAsociada, tipo, limite,
                          fechaVencimiento, activa);
    }
    
    @Override
    public String getId() {
        return numero;
    }
    
    public String getNumero() {
        return numero;
    }
    
    public Cliente getTitular() {
        return titular;
    }
    
    public String getNumeroCuentaAsociada() {
        return numeroCuentaAsociada;
    }
    
    public TipoTarjeta getTipo() {
        return tipo;
    }
    
    public BigDecimal getLimite() {
        return limite;
    }
    
    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }
    
    public boolean isActiva() {
        return activa;
    }
    
    @Override
    public String toString() {
        return String.format("Tarjeta{numero='%s', titular=%s, numeroCuentaAsociada='%s', tipo=%s, limite=%s, fechaVencimiento=%s, activa=%b}",
            numero, titular, numeroCuentaAsociada, tipo, limite, fechaVencimiento, activa);
    }
}
```

## Cliente.java

```java
package com.mibanco.model;

import java.time.LocalDate;

/**
 * Clase que representa un cliente bancario
 * Implementa inmutabilidad selectiva
 */
public class Cliente implements Identificable<Long> {
    private final Long id;
    private final String nombre;
    private final String apellido;
    private final String documento;
    private final LocalDate fechaNacimiento;
    private final String email;
    private final String telefono;
    private final boolean activo;
    
    private Cliente(Long id, String nombre, String apellido, String documento,
                   LocalDate fechaNacimiento, String email, String telefono,
                   boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.telefono = telefono;
        this.activo = activo;
    }
    
    /**
     * Crea un nuevo cliente
     * @param nombre Nombre del cliente
     * @param apellido Apellido del cliente
     * @param documento Número de documento
     * @param fechaNacimiento Fecha de nacimiento
     * @param email Correo electrónico
     * @param telefono Número de teléfono
     * @return Nuevo cliente
     */
    public static Cliente of(String nombre, String apellido, String documento,
                           LocalDate fechaNacimiento, String email, String telefono) {
        return new Cliente(null, nombre, apellido, documento, fechaNacimiento,
                          email, telefono, true);
    }
    
    /**
     * Crea una copia del cliente con un nuevo ID
     * @param nuevoId Nuevo ID
     * @return Nuevo cliente con el ID actualizado
     */
    public Cliente withId(Long nuevoId) {
        return new Cliente(nuevoId, nombre, apellido, documento, fechaNacimiento,
                          email, telefono, activo);
    }
    
    /**
     * Crea una copia del cliente con un nuevo email
     * @param nuevoEmail Nuevo email
     * @return Nuevo cliente con el email actualizado
     */
    public Cliente withEmail(String nuevoEmail) {
        return new Cliente(id, nombre, apellido, documento, fechaNacimiento,
                          nuevoEmail, telefono, activo);
    }
    
    /**
     * Crea una copia del cliente con un nuevo teléfono
     * @param nuevoTelefono Nuevo teléfono
     * @return Nuevo cliente con el teléfono actualizado
     */
    public Cliente withTelefono(String nuevoTelefono) {
        return new Cliente(id, nombre, apellido, documento, fechaNacimiento,
                          email, nuevoTelefono, activo);
    }
    
    /**
     * Crea una copia del cliente con un nuevo estado de activación
     * @param activo Nuevo estado de activación
     * @return Nuevo cliente con el estado de activación actualizado
     */
    public Cliente withActivo(boolean activo) {
        return new Cliente(id, nombre, apellido, documento, fechaNacimiento,
                          email, telefono, activo);
    }
    
    @Override
    public Long getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public String getDocumento() {
        return documento;
    }
    
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    @Override
    public String toString() {
        return String.format("Cliente{id=%d, nombre='%s', apellido='%s', documento='%s', fechaNacimiento=%s, email='%s', telefono='%s', activo=%b}",
            id, nombre, apellido, documento, fechaNacimiento, email, telefono, activo);
    }
} 