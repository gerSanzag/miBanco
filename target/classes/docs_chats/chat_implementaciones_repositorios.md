# Implementaciones de Repositorios

## TransaccionRepositoryImpl.java

```java
package com.mibanco.repository.impl;

import com.mibanco.model.RegistroAuditoria;
import com.mibanco.model.Transaccion;
import com.mibanco.model.enums.TipoOperacionTransaccion;
import com.mibanco.model.enums.TipoTransaccion;
import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.TransaccionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de Transacciones usando una lista en memoria
 * Utilizamos enfoque estrictamente funcional con streams y Optional
 * Ahora con soporte para auditoría de operaciones
 */
public class TransaccionRepositoryImpl implements TransaccionRepository {
    
    // Lista para almacenar transacciones en memoria
    private final List<Transaccion> transacciones = new ArrayList<>();
    
    // Cache para restauración de transacciones anuladas
    private final List<Transaccion> transaccionesAnuladas = new ArrayList<>();
    
    // Contador para generar IDs automáticamente
    private final AtomicLong idCounter = new AtomicLong(1);
    
    // Repositorio de auditoría
    private final AuditoriaRepository auditoriaRepository;
    
    // Usuario actual (en un sistema real vendría de un sistema de autenticación)
    private String usuarioActual = "sistema";
    
    // Función para registrar auditoría en un estilo más funcional
    private BiFunction<TipoOperacionTransaccion, Transaccion, RegistroAuditoria<Transaccion, TipoOperacionTransaccion>> registrarAuditoria;
    
    /**
     * Constructor con inyección del repositorio de auditoría
     */
    public TransaccionRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
        // Inicializar la función después de asignar el repositorio
        this.registrarAuditoria = (operacion, transaccion) -> auditoriaRepository.registrar(
            RegistroAuditoria.of(operacion, transaccion, usuarioActual)
        );
    }
    
    /**
     * Establece el usuario actual que realiza las operaciones
     */
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    @Override
    public Optional<Transaccion> save(Optional<Transaccion> transaccion) {
        // Si la transacción es null (Optional vacío), devolvemos Optional vacío
        return transaccion.map(t -> {
            // Utilizamos Optional para manejar el ID de forma funcional
            return Optional.ofNullable(t.getId())
                .map(id -> {
                    // Si tiene ID, es una actualización
                    transacciones.removeIf(existente -> existente.getId().equals(id));
                    transacciones.add(t);
                    
                    // Registrar auditoría de verificación
                    registrarAuditoria.apply(TipoOperacionTransaccion.VERIFICAR, t);
                    
                    return t;
                })
                .orElseGet(() -> {
                    // Si no tiene ID, es una creación
                    Transaccion nuevaTransaccion = t.withNuevoId(idCounter.getAndIncrement());
                    transacciones.add(nuevaTransaccion);
                    
                    // Registrar auditoría de creación
                    registrarAuditoria.apply(TipoOperacionTransaccion.CREAR, nuevaTransaccion);
                    
                    return nuevaTransaccion;
                });
        });
    }
    
    @Override
    public Optional<Transaccion> findById(Optional<Long> id) {
        return id.flatMap(idValue -> 
            transacciones.stream()
                .filter(transaccion -> transaccion.getId().equals(idValue))
                .findFirst()
        );
    }
    
    @Override
    public List<Transaccion> findByCuenta(Optional<String> numeroCuenta) {
        return numeroCuenta.map(numero -> 
            transacciones.stream()
                .filter(transaccion -> transaccion.getNumeroCuenta().equals(numero))
                .collect(Collectors.toList())
        ).orElse(new ArrayList<>());
    }
    
    @Override
    public List<Transaccion> findByTipo(Optional<TipoTransaccion> tipo) {
        return tipo.map(t -> 
            transacciones.stream()
                .filter(transaccion -> transaccion.getTipo().equals(t))
                .collect(Collectors.toList())
        ).orElse(new ArrayList<>());
    }
    
    @Override
    public List<Transaccion> findByFecha(Optional<LocalDate> fecha) {
        return fecha.map(f -> 
            transacciones.stream()
                .filter(transaccion -> transaccion.getFecha().toLocalDate().equals(f))
                .collect(Collectors.toList())
        ).orElse(new ArrayList<>());
    }
    
    @Override
    public List<Transaccion> findByRangoFechas(Optional<LocalDate> fechaInicio, Optional<LocalDate> fechaFin) {
        // Usando un enfoque más funcional para manejar las diferentes combinaciones de fechas
        
        // Predicado para filtrar por fecha de inicio (si está presente)
        Predicate<Transaccion> filtroInicio = fechaInicio
            .map(inicio -> (Predicate<Transaccion>) t -> 
                !t.getFecha().toLocalDate().isBefore(inicio))
            .orElse(t -> true); // Si no hay fecha inicio, no filtramos
        
        // Predicado para filtrar por fecha de fin (si está presente)
        Predicate<Transaccion> filtroFin = fechaFin
            .map(fin -> (Predicate<Transaccion>) t -> 
                !t.getFecha().toLocalDate().isAfter(fin))
            .orElse(t -> true); // Si no hay fecha fin, no filtramos
        
        // Verificamos si ambos Optionals están vacíos usando un enfoque funcional
        boolean ambasVacias = !fechaInicio.isPresent() && !fechaFin.isPresent();
        
        // Usando operador ternario en lugar de if
        return ambasVacias ? 
            new ArrayList<>() : 
            transacciones.stream()
                .filter(filtroInicio)
                .filter(filtroFin)
                .collect(Collectors.toList());
    }
    
    @Override
    public Optional<List<Transaccion>> findAll() {
        // Devolvemos una copia para no exponer la lista interna
        return Optional.of(new ArrayList<>(transacciones));
    }
    
    @Override
    public boolean deleteById(Optional<Long> id) {
        return id.map(idValue -> {
            Optional<Transaccion> transaccionAEliminar = findById(Optional.of(idValue));
            
            return transaccionAEliminar.map(transaccion -> {
                // Guardamos la transacción en la caché para posible restauración
                transaccionesAnuladas.add(transaccion);
                
                // Eliminamos la transacción
                transacciones.removeIf(t -> t.getId().equals(idValue));
                
                // Registramos la anulación
                registrarAuditoria.apply(TipoOperacionTransaccion.ANULAR, transaccion);
                
                return true;
            }).orElse(false);
        }).orElse(false);
    }
    
    /**
     * Restaura una transacción previamente anulada
     * @param id ID de la transacción a restaurar
     * @return Transacción restaurada o empty si no se encuentra
     */
    public Optional<Transaccion> restaurarTransaccion(Optional<Long> id) {
        return id.flatMap(idValue -> {
            Optional<Transaccion> transaccionARestaurar = transaccionesAnuladas.stream()
                    .filter(transaccion -> transaccion.getId().equals(idValue))
                    .findFirst();
                    
            transaccionARestaurar.ifPresent(transaccion -> {
                // Añadimos de nuevo la transacción a la lista principal
                transacciones.add(transaccion);
                
                // La quitamos de la caché de anuladas
                transaccionesAnuladas.removeIf(t -> t.getId().equals(idValue));
                
                // Registra la auditoría de restauración (revertir anulación)
                registrarAuditoria.apply(TipoOperacionTransaccion.REVERSAR, transaccion);
            });
            
            return transaccionARestaurar;
        });
    }
    
    /**
     * Obtiene la lista de transacciones recientemente anuladas
     * @return Lista de transacciones anuladas
     */
    public List<Transaccion> getTransaccionesAnuladas() {
        return new ArrayList<>(transaccionesAnuladas);
    }
    
    /**
     * Autoriza una transacción
     * @param idTransaccion ID de la transacción a autorizar
     * @return La transacción autorizada o empty si no existe
     */
    public Optional<Transaccion> autorizarTransaccion(Optional<Long> idTransaccion) {
        return idTransaccion.flatMap(id -> 
            findById(Optional.of(id)).map(transaccion -> {
                // Podríamos actualizar la transacción si fuera necesario
                
                // Registrar auditoría de autorización
                registrarAuditoria.apply(TipoOperacionTransaccion.AUTORIZAR, transaccion);
                
                return transaccion;
            })
        );
    }
    
    /**
     * Rechaza una transacción
     * @param idTransaccion ID de la transacción a rechazar
     * @param motivo Motivo del rechazo
     * @return La transacción rechazada o empty si no existe
     */
    public Optional<Transaccion> rechazarTransaccion(Optional<Long> idTransaccion, String motivo) {
        return idTransaccion.flatMap(id -> 
            findById(Optional.of(id)).map(transaccion -> {
                // Actualizamos la descripción para incluir el motivo de rechazo
                Transaccion transaccionRechazada = transaccion.withDescripcion(
                    "RECHAZADA: " + motivo + ". " + transaccion.getDescripcion()
                );
                
                // Actualizamos la transacción en la lista
                transacciones.removeIf(t -> t.getId().equals(id));
                transacciones.add(transaccionRechazada);
                
                // Registrar auditoría de rechazo
                registrarAuditoria.apply(TipoOperacionTransaccion.RECHAZAR, transaccionRechazada);
                
                return transaccionRechazada;
            })
        );
    }
    
    @Override
    public long count() {
        return transacciones.size();
    }
} 
```

## CuentaRepositoryImpl.java

```java
package com.mibanco.repository.impl;

import com.mibanco.model.Cuenta;
import com.mibanco.model.RegistroAuditoria;
import com.mibanco.model.enums.TipoCuenta;
import com.mibanco.model.enums.TipoOperacionCuenta;
import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.CuentaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de Cuentas usando una lista en memoria
 * Utilizamos enfoque estrictamente funcional con streams y Optional
 * Ahora con soporte para auditoría de operaciones
 */
public class CuentaRepositoryImpl implements CuentaRepository {
    
    // Lista para almacenar cuentas en memoria
    private final List<Cuenta> cuentas = new ArrayList<>();
    
    // Cache para restauración de cuentas eliminadas
    private final List<Cuenta> cuentasEliminadas = new ArrayList<>();
    
    // Contador para generar IDs automáticamente (si se requiere en el futuro)
    private final AtomicLong idCounter = new AtomicLong(1);
    
    // Repositorio de auditoría
    private final AuditoriaRepository auditoriaRepository;
    
    // Usuario actual (en un sistema real vendría de un sistema de autenticación)
    private String usuarioActual = "sistema";
    
    // Función para registrar auditoría en un estilo más funcional
    private BiFunction<TipoOperacionCuenta, Cuenta, RegistroAuditoria<Cuenta, TipoOperacionCuenta>> registrarAuditoria;
    
    /**
     * Constructor con inyección del repositorio de auditoría
     */
    public CuentaRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
        // Inicializar la función después de asignar el repositorio
        this.registrarAuditoria = (operacion, cuenta) -> auditoriaRepository.registrar(
            RegistroAuditoria.of(operacion, cuenta, usuarioActual)
        );
    }
    
    /**
     * Establece el usuario actual que realiza las operaciones
     */
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    @Override
    public Optional<Cuenta> save(Optional<Cuenta> cuenta) {
        // Si la cuenta es null (Optional vacío), devolvemos Optional vacío
        return cuenta.map(c -> {
            // Verificamos si la cuenta ya existe
            Optional<String> numeroCuenta = Optional.ofNullable(c.getNumeroCuenta());
            
            return findByNumero(numeroCuenta)
                .map(existente -> {
                    // Si existe, la actualizamos
                    cuentas.removeIf(e -> e.getNumeroCuenta().equals(c.getNumeroCuenta()));
                    
                    // Registrar auditoría de modificación
                    registrarAuditoria.apply(TipoOperacionCuenta.MODIFICAR, c);
                    
                    cuentas.add(c);
                    return c;
                })
                .orElseGet(() -> {
                    // Si no existe, la creamos
                    // Registrar auditoría de creación
                    registrarAuditoria.apply(TipoOperacionCuenta.CREAR, c);
                    
                    cuentas.add(c);
                    return c;
                });
        });
    }
    
    @Override
    public Optional<Cuenta> findByNumero(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            cuentas.stream()
                .filter(cuenta -> cuenta.getNumeroCuenta().equals(numero))
                .findFirst()
        );
    }
    
    @Override
    public Optional<List<Cuenta>> findByTitularId(Optional<Long> idTitular) {
        return idTitular.map(id -> 
            cuentas.stream()
                .filter(cuenta -> cuenta.getTitular().getId().equals(id))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Optional<List<Cuenta>> findByTipo(Optional<TipoCuenta> tipo) {
        return tipo.map(t -> 
            cuentas.stream()
                .filter(cuenta -> cuenta.getTipo().equals(t))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Optional<List<Cuenta>> findAll() {
        // Devolvemos una copia para no exponer la lista interna
        return Optional.of(new ArrayList<>(cuentas));
    }
    
    @Override
    public Optional<List<Cuenta>> findAllActivas() {
        return Optional.of(
            cuentas.stream()
                .filter(Cuenta::isActiva)
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public boolean deleteByNumero(Optional<String> numeroCuenta) {
        return numeroCuenta.map(numero -> {
            Optional<Cuenta> cuentaAEliminar = findByNumero(Optional.of(numero));
            
            return cuentaAEliminar.map(cuenta -> {
                // Removemos la cuenta de la lista principal
                cuentas.removeIf(c -> c.getNumeroCuenta().equals(numero));
                
                // Guardamos la cuenta en la caché para posible restauración
                cuentasEliminadas.add(cuenta);
                
                // Registrar auditoría de eliminación
                registrarAuditoria.apply(TipoOperacionCuenta.ELIMINAR, cuenta);
                
                return true;
            }).orElse(false);
        }).orElse(false);
    }
    
    /**
     * Restaura una cuenta previamente eliminada
     * @param numeroCuenta Número de la cuenta a restaurar
     * @return Cuenta restaurada o empty si no se encuentra
     */
    public Optional<Cuenta> restaurarCuenta(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> {
            Optional<Cuenta> cuentaARestaurar = cuentasEliminadas.stream()
                    .filter(cuenta -> cuenta.getNumeroCuenta().equals(numero))
                    .findFirst();
                    
            cuentaARestaurar.ifPresent(cuenta -> {
                // Añadimos de nuevo la cuenta a la lista principal
                cuentas.add(cuenta);
                
                // La quitamos de la caché de eliminados
                cuentasEliminadas.removeIf(c -> c.getNumeroCuenta().equals(numero));
                
                // Registra la auditoría de restauración
                registrarAuditoria.apply(TipoOperacionCuenta.ACTIVAR, cuenta);
            });
            
            return cuentaARestaurar;
        });
    }
    
    /**
     * Obtiene la lista de cuentas recientemente eliminadas
     * @return Lista de cuentas eliminadas
     */
    public List<Cuenta> getCuentasEliminadas() {
        return new ArrayList<>(cuentasEliminadas);
    }
    
    /**
     * Activa una cuenta previamente desactivada
     * @param numeroCuenta Número de la cuenta a activar
     * @return true si se activó la cuenta, false si no existía
     */
    public boolean activarCuenta(Optional<String> numeroCuenta) {
        return numeroCuenta.map(numero -> 
            findByNumero(Optional.of(numero)).map(cuenta -> 
                // Usando el enfoque funcional para evitar if
                Optional.of(cuenta.isActiva())
                    .filter(activa -> activa) // Si ya está activa
                    .map(activa -> false) // No hacemos nada y devolvemos false
                    .orElseGet(() -> {
                        // Si no está activa, la activamos
                        cuentas.removeIf(c -> c.getNumeroCuenta().equals(numero));
                        
                        // Creamos una versión activada
                        Cuenta cuentaActivada = cuenta.toBuilder()
                                .activa(true)
                                .build();
                        
                        // Añadimos la cuenta activada
                        cuentas.add(cuentaActivada);
                        
                        // Registramos la activación
                        registrarAuditoria.apply(TipoOperacionCuenta.ACTIVAR, cuentaActivada);
                        
                        return true;
                    })
            ).orElse(false)
        ).orElse(false);
    }
    
    /**
     * Desactiva una cuenta
     * @param numeroCuenta Número de la cuenta a desactivar
     * @return true si se desactivó la cuenta, false si no existía
     */
    public boolean desactivarCuenta(Optional<String> numeroCuenta) {
        return numeroCuenta.map(numero -> 
            findByNumero(Optional.of(numero)).map(cuenta -> 
                // Usando el enfoque funcional para evitar if
                Optional.of(!cuenta.isActiva())
                    .filter(inactiva -> inactiva) // Si ya está inactiva
                    .map(inactiva -> false) // No hacemos nada y devolvemos false
                    .orElseGet(() -> {
                        // Si está activa, la desactivamos
                        cuentas.removeIf(c -> c.getNumeroCuenta().equals(numero));
                        
                        // Creamos una versión desactivada
                        Cuenta cuentaDesactivada = cuenta.toBuilder()
                                .activa(false)
                                .build();
                        
                        // Añadimos la cuenta desactivada
                        cuentas.add(cuentaDesactivada);
                        
                        // Registramos la desactivación
                        registrarAuditoria.apply(TipoOperacionCuenta.DESACTIVAR, cuentaDesactivada);
                        
                        return true;
                    })
            ).orElse(false)
        ).orElse(false);
    }
    
    @Override
    public long count() {
        return cuentas.size();
    }
} 
```

## TarjetaRepositoryImpl.java

```java
package com.mibanco.repository.impl;

import com.mibanco.model.RegistroAuditoria;
import com.mibanco.model.Tarjeta;
import com.mibanco.model.enums.TipoOperacionTarjeta;
import com.mibanco.model.enums.TipoTarjeta;
import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.TarjetaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de Tarjetas usando una lista en memoria
 * Utilizamos enfoque estrictamente funcional con streams y Optional
 * Ahora con soporte para auditoría de operaciones
 */
public class TarjetaRepositoryImpl implements TarjetaRepository {
    
    // Lista para almacenar tarjetas en memoria
    private final List<Tarjeta> tarjetas = new ArrayList<>();
    
    // Cache para restauración de tarjetas eliminadas
    private final List<Tarjeta> tarjetasEliminadas = new ArrayList<>();
    
    // Repositorio de auditoría
    private final AuditoriaRepository auditoriaRepository;
    
    // Usuario actual (en un sistema real vendría de un sistema de autenticación)
    private String usuarioActual = "sistema";
    
    // Función para registrar auditoría en un estilo más funcional
    private BiFunction<TipoOperacionTarjeta, Tarjeta, RegistroAuditoria<Tarjeta, TipoOperacionTarjeta>> registrarAuditoria;
    
    /**
     * Constructor con inyección del repositorio de auditoría
     */
    public TarjetaRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
        // Inicializar la función después de asignar el repositorio
        this.registrarAuditoria = (operacion, tarjeta) -> auditoriaRepository.registrar(
            RegistroAuditoria.of(operacion, tarjeta, usuarioActual)
        );
    }
    
    /**
     * Establece el usuario actual que realiza las operaciones
     */
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    @Override
    public Optional<Tarjeta> save(Optional<Tarjeta> tarjeta) {
        // Si la tarjeta es null (Optional vacío), devolvemos Optional vacío
        return tarjeta.map(t -> {
            // Verificamos si la tarjeta ya existe
            Optional<String> numeroTarjeta = Optional.ofNullable(t.getNumero());
            
            return findByNumero(numeroTarjeta)
                .map(existente -> {
                    // Si existe, la actualizamos
                    tarjetas.removeIf(e -> e.getNumero().equals(t.getNumero()));
                    
                    // Registrar auditoría de modificación
                    registrarAuditoria.apply(TipoOperacionTarjeta.MODIFICAR, t);
                    
                    tarjetas.add(t);
                    return t;
                })
                .orElseGet(() -> {
                    // Si no existe, la creamos
                    // Registrar auditoría de creación
                    registrarAuditoria.apply(TipoOperacionTarjeta.CREAR, t);
                    
                    tarjetas.add(t);
                    return t;
                });
        });
    }
    
    @Override
    public Optional<Tarjeta> findByNumero(Optional<String> numeroTarjeta) {
        return numeroTarjeta.flatMap(numero -> 
            tarjetas.stream()
                .filter(tarjeta -> tarjeta.getNumero().equals(numero))
                .findFirst()
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> findByTitularId(Optional<Long> idTitular) {
        return idTitular.map(id -> 
            tarjetas.stream()
                .filter(tarjeta -> tarjeta.getTitular().getId().equals(id))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> findByCuentaAsociada(Optional<String> numeroCuenta) {
        return numeroCuenta.map(numero -> 
            tarjetas.stream()
                .filter(tarjeta -> tarjeta.getNumeroCuentaAsociada().equals(numero))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> findByTipo(Optional<TipoTarjeta> tipo) {
        return tipo.map(t -> 
            tarjetas.stream()
                .filter(tarjeta -> tarjeta.getTipo().equals(t))
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> findAll() {
        // Devolvemos una copia para no exponer la lista interna
        return Optional.of(new ArrayList<>(tarjetas));
    }
    
    @Override
    public Optional<List<Tarjeta>> findAllActivas() {
        return Optional.of(
            tarjetas.stream()
                .filter(Tarjeta::isActiva)
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public boolean deleteByNumero(Optional<String> numeroTarjeta) {
        return numeroTarjeta.map(numero -> {
            Optional<Tarjeta> tarjetaAEliminar = findByNumero(Optional.of(numero));
            
            return tarjetaAEliminar.map(tarjeta -> {
                // Removemos la tarjeta de la lista principal
                tarjetas.removeIf(t -> t.getNumero().equals(numero));
                
                // Guardamos la tarjeta en la caché para posible restauración
                tarjetasEliminadas.add(tarjeta);
                
                // Registrar auditoría de eliminación
                registrarAuditoria.apply(TipoOperacionTarjeta.ELIMINAR, tarjeta);
                
                return true;
            }).orElse(false);
        }).orElse(false);
    }
    
    /**
     * Restaura una tarjeta previamente eliminada
     * @param numeroTarjeta Número de la tarjeta a restaurar
     * @return Tarjeta restaurada o empty si no se encuentra
     */
    public Optional<Tarjeta> restaurarTarjeta(Optional<String> numeroTarjeta) {
        return numeroTarjeta.flatMap(numero -> {
            Optional<Tarjeta> tarjetaARestaurar = tarjetasEliminadas.stream()
                    .filter(tarjeta -> tarjeta.getNumero().equals(numero))
                    .findFirst();
                    
            tarjetaARestaurar.ifPresent(tarjeta -> {
                // Añadimos de nuevo la tarjeta a la lista principal
                tarjetas.add(tarjeta);
                
                // La quitamos de la caché de eliminados
                tarjetasEliminadas.removeIf(t -> t.getNumero().equals(numero));
                
                // Registra la auditoría de restauración
                registrarAuditoria.apply(TipoOperacionTarjeta.ACTIVAR, tarjeta);
            });
            
            return tarjetaARestaurar;
        });
    }
    
    /**
     * Obtiene la lista de tarjetas recientemente eliminadas
     * @return Lista de tarjetas eliminadas
     */
    public List<Tarjeta> getTarjetasEliminadas() {
        return new ArrayList<>(tarjetasEliminadas);
    }
    
    /**
     * Activa una tarjeta previamente desactivada
     * @param numeroTarjeta Número de la tarjeta a activar
     * @return true si se activó la tarjeta, false si no existía
     */
    public boolean activarTarjeta(Optional<String> numeroTarjeta) {
        return numeroTarjeta.map(numero -> 
            findByNumero(Optional.of(numero)).map(tarjeta -> 
                // Usando el enfoque funcional para evitar if
                Optional.of(tarjeta.isActiva())
                    .filter(activa -> activa) // Si ya está activa
                    .map(activa -> false) // No hacemos nada y devolvemos false
                    .orElseGet(() -> {
                        // Si no está activa, la activamos
                        tarjetas.removeIf(t -> t.getNumero().equals(numero));
                        
                        // Creamos una versión activada
                        Tarjeta tarjetaActivada = tarjeta.withActiva(true);
                        
                        // Añadimos la tarjeta activada
                        tarjetas.add(tarjetaActivada);
                        
                        // Registramos la activación
                        registrarAuditoria.apply(TipoOperacionTarjeta.ACTIVAR, tarjetaActivada);
                        
                        return true;
                    })
            ).orElse(false)
        ).orElse(false);
    }
    
    /**
     * Desactiva una tarjeta
     * @param numeroTarjeta Número de la tarjeta a desactivar
     * @return true si se desactivó la tarjeta, false si no existía
     */
    public boolean desactivarTarjeta(Optional<String> numeroTarjeta) {
        return numeroTarjeta.map(numero -> 
            findByNumero(Optional.of(numero)).map(tarjeta -> 
                // Usando el enfoque funcional para evitar if
                Optional.of(!tarjeta.isActiva())
                    .filter(inactiva -> inactiva) // Si ya está inactiva
                    .map(inactiva -> false) // No hacemos nada y devolvemos false
                    .orElseGet(() -> {
                        // Si está activa, la desactivamos
                        tarjetas.removeIf(t -> t.getNumero().equals(numero));
                        
                        // Creamos una versión desactivada
                        Tarjeta tarjetaDesactivada = tarjeta.withActiva(false);
                        
                        // Añadimos la tarjeta desactivada
                        tarjetas.add(tarjetaDesactivada);
                        
                        // Registramos la desactivación
                        registrarAuditoria.apply(TipoOperacionTarjeta.DESACTIVAR, tarjetaDesactivada);
                        
                        return true;
                    })
            ).orElse(false)
        ).orElse(false);
    }
    
    @Override
    public long count() {
        return tarjetas.size();
    }
} 
``` 