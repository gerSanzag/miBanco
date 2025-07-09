package com.mibanco.repositorio.interna;

import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.enums.TipoOperacionTarjeta;
import com.mibanco.modelo.enums.TipoTarjeta;
import com.mibanco.repositorio.TarjetaRepositorio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementación del repositorio de Tarjetas
 * Visibilidad restringida al paquete internal
 */
class TarjetaRepositorioImpl extends BaseRepositorioImpl<Tarjeta, String, TipoOperacionTarjeta> implements TarjetaRepositorio {
    
    /**
     * Constructor con visibilidad de paquete
     */
    TarjetaRepositorioImpl() {
        super();
    }
    
    @Override
    public Optional<Tarjeta> buscarPorNumero(Optional<String> numeroTarjeta) {
        return numeroTarjeta.flatMap(numero -> 
            buscarPorPredicado(tarjeta -> tarjeta.getNumero().equals(numero))
        );
    }
    
    @Override
    public Optional<List<Tarjeta>> buscarPorTitularId(Optional<Long> idCliente) {
        return idCliente.flatMap(id -> 
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
    
    /**
     * Implementación específica para asignar nuevo ID a Tarjeta
     * @param tarjeta Tarjeta sin ID asignado
     * @return Tarjeta con nuevo ID asignado
     */
    @Override
    protected Tarjeta crearConNuevoId(Tarjeta tarjeta) {
        // Generar número aleatorio de 16 dígitos
        String numero = String.format("%016d", (long) (Math.random() * 10000000000000000L));
        
        // Generar CVV aleatorio de 3 dígitos
        String cvv = String.format("%03d", (int) (Math.random() * 1000));
        
        return Tarjeta.builder()
            .numero(numero)
            .cvv(cvv)
            .build();
    }
    
    /**
     * Implementación específica para obtener la configuración del repositorio
     * @return Map con la configuración necesaria
     */
    @Override
    protected Map<String, Object> obtenerConfiguracion() {
        Map<String, Object> config = new HashMap<>();
        config.put("rutaArchivo", "src/main/resources/data/tarjeta.json");
        config.put("tipoClase", Tarjeta.class);
        config.put("extractorId", (Function<Tarjeta, Long>) Tarjeta::getId);
        return config;
    }
} 