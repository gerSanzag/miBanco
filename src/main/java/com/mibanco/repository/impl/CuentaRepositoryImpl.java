package com.mibanco.repository.impl;

import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;
import com.mibanco.model.enums.TipoOperacionCuenta;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.repository.util.BaseRepositoryImpl;

import java.util.Optional;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación del repositorio de Cuentas
 * Extiende la implementación base para heredar funcionalidad CRUD genérica
 */
public class CuentaRepositoryImpl extends BaseRepositoryImpl<Cuenta, String, TipoOperacionCuenta> implements CuentaRepository {
    
    private static final AtomicLong idCounter = new AtomicLong(1);
    
    /**
     * Constructor por defecto
     */
    public CuentaRepositoryImpl() {
        super();
    }
    
    @Override
    public Optional<Cuenta> buscarPorNumero(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            buscarPorPredicado(cuenta -> cuenta.getNumeroCuenta().equals(numero))
        );
    }
    
    @Override
    public Optional<List<Cuenta>> buscarPorTitularId(Optional<Long> idTitular) {
        return idTitular.flatMap(id -> 
            buscarTodosPorPredicado(cuenta -> cuenta.getTitular().getId().equals(id))
        );
    }
    
    @Override
    public Optional<List<Cuenta>> buscarPorTipo(Optional<TipoCuenta> tipo) {
        return tipo.flatMap(t -> 
            buscarTodosPorPredicado(cuenta -> cuenta.getTipo() == t)
        );
    }
    
    @Override
    public Optional<List<Cuenta>> buscarActivas() {
        return buscarTodosPorPredicado(Cuenta::isActiva);
    }
    
    @Override
    public Optional<Cuenta> eliminarPorNumero(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            buscarPorNumero(Optional.of(numero))
                .flatMap(cuenta -> eliminarPorId(Optional.of(cuenta.getNumeroCuenta()), TipoOperacionCuenta.ELIMINAR))
        );
    }
    
    @Override
    public Optional<Cuenta> guardar(Optional<Cuenta> cuenta) {
        return cuenta.map(c -> {
            if (c.getNumeroCuenta() == null) {
                return crear(Optional.of(c), TipoOperacionCuenta.CREAR).orElse(null);
            } else {
                return actualizar(Optional.of(c), TipoOperacionCuenta.MODIFICAR).orElse(null);
            }
        });
    }
    
    @Override
    protected Cuenta crearConNuevoId(Cuenta cuenta) {
        return cuenta.toBuilder()
                .numeroCuenta(generarNumeroCuenta())
                .build();
    }
    
    private String generarNumeroCuenta() {
        return String.format("%020d", idCounter.getAndIncrement());
    }
} 