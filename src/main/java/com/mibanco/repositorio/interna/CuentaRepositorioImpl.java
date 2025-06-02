package com.mibanco.repositorio.interna;

import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.enums.TipoCuenta;
import com.mibanco.modelo.enums.TipoOperacionCuenta;
import com.mibanco.repositorio.CuentaRepositorio;

import java.util.Optional;
import java.util.List;

/**
 * Implementación del repositorio de Cuentas
 * Visibilidad restringida al paquete internal
 */
class CuentaRepositorioImpl extends BaseRepositorioImpl<Cuenta, String, TipoOperacionCuenta> implements CuentaRepositorio {
    
    /**
     * Constructor con visibilidad de paquete
     */
    CuentaRepositorioImpl() {
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