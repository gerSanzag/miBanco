package com.mibanco.repository.internal;

import com.mibanco.model.Cuenta;
import com.mibanco.model.enums.TipoCuenta;
import com.mibanco.model.enums.TipoOperacionCuenta;
import com.mibanco.repository.CuentaRepository;

import java.util.Optional;
import java.util.List;

/**
 * Implementación del repositorio de Cuentas
 * Visibilidad restringida al paquete internal
 */
class CuentaRepositoryImpl extends BaseRepositoryImpl<Cuenta, String, TipoOperacionCuenta> implements CuentaRepository {
    
    /**
     * Constructor con visibilidad de paquete
     */
    CuentaRepositoryImpl() {
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
    protected Cuenta crearConNuevoId(Cuenta cuenta) {
        return cuenta.toBuilder()
                .numeroCuenta(String.format("%020d", idContador.getAndIncrement()))
                .build();
    }
} 