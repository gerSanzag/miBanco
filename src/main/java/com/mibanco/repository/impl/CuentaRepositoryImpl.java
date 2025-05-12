package com.mibanco.repository.impl;

import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;
import com.mibanco.model.enums.TipoOperacionCuenta;
import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.util.AuditoriaUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Implementación del repositorio de Cuentas usando una lista en memoria
 * Utilizamos enfoque estrictamente funcional con streams y Optional
 * Ahora con soporte para auditoría de operaciones usando AuditoriaUtil simplificado
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
    
    /**
     * Constructor con inyección del repositorio de auditoría
     */
    public CuentaRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
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
                    
                    // Registrar auditoría de modificación directamente
                    AuditoriaUtil.registrarOperacion(
                        auditoriaRepository,
                        TipoOperacionCuenta.MODIFICAR,
                        c,
                        usuarioActual
                    );
                    
                    cuentas.add(c);
                    return c;
                })
                .orElseGet(() -> {
                    // Si no existe, la creamos
                    // Registrar auditoría de creación directamente
                    AuditoriaUtil.registrarOperacion(
                        auditoriaRepository,
                        TipoOperacionCuenta.CREAR,
                        c,
                        usuarioActual
                    );
                    
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
                .filter(cuenta -> cuenta.getTipo() == t)
                .collect(Collectors.toList())
        );
    }
    
    @Override
    public Optional<List<Cuenta>> findAll() {
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
            
            cuentaAEliminar.ifPresent(cuenta -> {
                // Removemos la cuenta de la lista principal
                cuentas.remove(cuenta);
                
                // Guardamos la cuenta en la caché para posible restauración
                cuentasEliminadas.add(cuenta);
                
                // Registrar auditoría de eliminación directamente
                AuditoriaUtil.registrarOperacion(
                    auditoriaRepository,
                    TipoOperacionCuenta.ELIMINAR,
                    cuenta,
                    usuarioActual
                );
            });
            
            return cuentaAEliminar.isPresent();
        }).orElse(false);
    }
    
    /**
     * Restaura una cuenta previamente eliminada
     * @param numeroCuenta Número de la cuenta a restaurar
     * @return Cuenta restaurada o empty si no se encuentra
     */
    public Optional<Cuenta> restoreDeletedAccount(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> {
            Optional<Cuenta> cuentaARestaurar = cuentasEliminadas.stream()
                    .filter(cuenta -> cuenta.getNumeroCuenta().equals(numero))
                    .findFirst();
                    
            cuentaARestaurar.ifPresent(cuenta -> {
                // Añadimos de nuevo la cuenta a la lista principal
                cuentas.add(cuenta);
                
                // La quitamos de la caché de eliminados
                cuentasEliminadas.removeIf(c -> c.getNumeroCuenta().equals(numero));
                
                // Registrar auditoría de restauración directamente
                AuditoriaUtil.registrarOperacion(
                    auditoriaRepository,
                    TipoOperacionCuenta.ACTIVAR,
                    cuenta,
                    usuarioActual
                );
            });
            
            return cuentaARestaurar;
        });
    }
    
    /**
     * Cambia el estado activo de una cuenta
     * @param numeroCuenta Número de la cuenta
     * @param activa Nuevo estado
     * @return Cuenta actualizada o empty si no existe
     */
    public Optional<Cuenta> cambiarEstadoActiva(Optional<String> numeroCuenta, boolean activa) {
        return numeroCuenta.flatMap(numero -> 
            findByNumero(Optional.of(numero))
                .map(cuenta -> {
                    // Creamos una nueva cuenta con el estado actualizado (inmutabilidad)
                    Cuenta cuentaActualizada = cuenta.withActiva(activa);
                    
                    // Actualizamos en el repositorio
                    cuentas.removeIf(c -> c.getNumeroCuenta().equals(numero));
                    cuentas.add(cuentaActualizada);
                    
                    // Registrar auditoría del cambio de estado directamente
                    TipoOperacionCuenta tipoOperacion = activa ? 
                            TipoOperacionCuenta.ACTIVAR : TipoOperacionCuenta.DESACTIVAR;
                    
                    AuditoriaUtil.registrarOperacion(
                        auditoriaRepository,
                        tipoOperacion,
                        cuentaActualizada,
                        usuarioActual
                    );
                    
                    return cuentaActualizada;
                })
        );
    }
    
    @Override
    public long count() {
        return cuentas.size();
    }
} 