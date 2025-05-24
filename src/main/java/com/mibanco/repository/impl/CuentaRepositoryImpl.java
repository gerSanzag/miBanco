package com.mibanco.repository.impl;

import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;
import com.mibanco.model.enums.TipoOperacionCuenta;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.repository.util.BaseRepositoryImpl;

import java.util.Optional;
import java.util.List;

/**
 * Implementación del repositorio de Cuentas
 * Extiende la implementación base para heredar funcionalidad CRUD genérica
 */
public class CuentaRepositoryImpl extends BaseRepositoryImpl<Cuenta, String, TipoOperacionCuenta> implements CuentaRepository {
    
    /**
     * Constructor por defecto
     */
    public CuentaRepositoryImpl() {
        super();
    }
    
    @Override
    public Optional<Cuenta> findByNumero(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            findByPredicate(cuenta -> cuenta.getNumeroCuenta().equals(numero))
        );
    }
    
    @Override
    public Optional<List<Cuenta>> findByTitularId(Optional<Long> idTitular) {
        return idTitular.flatMap(id -> 
            findAllByPredicate(cuenta -> cuenta.getTitular().getId().equals(id))
        );
    }
    
    @Override
    public Optional<List<Cuenta>> findByTipo(Optional<TipoCuenta> tipo) {
        return tipo.flatMap(t -> 
            findAllByPredicate(cuenta -> cuenta.getTipo() == t)
        );
    }
    
    @Override
    public Optional<List<Cuenta>> findAllActivas() {
        return findAllByPredicate(Cuenta::isActiva);
    }
    
    @Override
    public Optional<Cuenta> deleteByNumero(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            findByNumero(Optional.of(numero))
                .flatMap(cuenta -> deleteById(Optional.of(cuenta.getNumeroCuenta()), TipoOperacionCuenta.ELIMINAR))
        );
    }
    
    @Override
    protected Cuenta createWithNewId(Cuenta cuenta) {
        return cuenta.toBuilder()
                .numeroCuenta(cuenta.getNumeroCuenta()) // El número de cuenta ya viene asignado
                .build();
    }
    
    @Override
    public Optional<Cuenta> save(Optional<Cuenta> cuentaOpt) {
        return cuentaOpt.flatMap(cuenta -> 
            cuenta.getNumeroCuenta() != null
                ? actualizar(Optional.of(cuenta), TipoOperacionCuenta.MODIFICAR)
                : crear(Optional.of(cuenta), TipoOperacionCuenta.CREAR)
        );
    }
} 