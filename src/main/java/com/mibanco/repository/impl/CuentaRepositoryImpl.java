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