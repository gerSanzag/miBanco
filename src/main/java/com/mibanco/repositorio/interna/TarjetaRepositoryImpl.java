package com.mibanco.repositorio.interna;

import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.enums.TipoOperacionTarjeta;
import com.mibanco.modelo.enums.TipoTarjeta;
import com.mibanco.repositorio.TarjetaRepositorio;

import java.util.Optional;
import java.util.List;

/**
 * Implementación del repositorio de Tarjetas
 * Visibilidad restringida al paquete internal
 */
class TarjetaRepositoryImpl extends BaseRepositorioImpl<Tarjeta, String, TipoOperacionTarjeta> implements TarjetaRepositorio {
    
    /**
     * Constructor con visibilidad de paquete
     */
    TarjetaRepositoryImpl() {
        super();
    }
    
    @Override
    public Optional<Tarjeta> buscarPorNumero(Optional<String> numeroTarjeta) {
        return numeroTarjeta.flatMap(numero -> 
            buscarPorPredicado(tarjeta -> tarjeta.getNumero().equals(numero))
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> buscarPorTitularId(Optional<Long> idTitular) {
        return idTitular.flatMap(id -> 
            buscarTodosPorPredicado(tarjeta -> tarjeta.getTitular().getId().equals(id))
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> buscarPorCuentaAsociada(Optional<String> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            buscarTodosPorPredicado(tarjeta -> tarjeta.getNumeroCuentaAsociada().equals(numero))
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> buscarPorTipo(Optional<TipoTarjeta> tipo) {
        return tipo.flatMap(t -> 
            buscarTodosPorPredicado(tarjeta -> tarjeta.getTipo() == t)
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> buscarActivas() {
        return buscarTodosPorPredicado(Tarjeta::isActiva);
    }
    
    @Override
    protected Tarjeta crearConNuevoId(Tarjeta tarjeta) {
        return tarjeta.toBuilder()
                .numero(String.format("%016d", idContador.getAndIncrement()))
                .build();
    }
} 