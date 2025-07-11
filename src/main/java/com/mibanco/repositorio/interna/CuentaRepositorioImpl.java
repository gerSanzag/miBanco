package com.mibanco.repositorio.interna;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.dto.mapeador.CuentaMapeador;
import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.enums.TipoCuenta;
import com.mibanco.modelo.enums.TipoOperacionCuenta;
import com.mibanco.repositorio.CuentaRepositorio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementación del repositorio de Cuentas
 * Visibilidad restringida al paquete internal
 */
class CuentaRepositorioImpl extends BaseRepositorioImpl<Cuenta, Long, TipoOperacionCuenta> implements CuentaRepositorio {
    
    /**
     * Constructor con visibilidad de paquete
     */
    CuentaRepositorioImpl() {
        super();
    }
    
    @Override
    public Optional<Cuenta> buscarPorNumero(Optional<Long> numeroCuenta) {
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
    public Optional<Cuenta> eliminarPorNumero(Optional<Long> numeroCuenta) {
        return numeroCuenta.flatMap(numero -> 
            buscarPorNumero(Optional.of(numero))
                .flatMap(cuenta -> eliminarPorId(Optional.of(cuenta.getNumeroCuenta()), TipoOperacionCuenta.ELIMINAR))
        );
    }
    
    /**
     * Implementación específica para asignar nuevo ID a Cuenta
     * Usa DTOs para mantener la inmutabilidad de la entidad
     * Enfoque funcional puro con Optional
     * @param cuenta Cuenta sin ID asignado
     * @return Cuenta con nuevo ID asignado
     */
    @Override
    protected Cuenta crearConNuevoId(Cuenta cuenta) {
        ClienteMapeador clienteMapeador = new ClienteMapeador();
        CuentaMapeador mapeador = new CuentaMapeador(clienteMapeador);
        
        return mapeador.aDtoDirecto(cuenta)
            .map(dto -> {
                String numero = String.format("%09d", (int) (Math.random() * 1000000000));
                return dto.toBuilder()
                    .numeroCuenta(Long.parseLong(numero))
                    .build();
            })
            .flatMap(mapeador::aEntidadDirecta)
            .orElseThrow(() -> new IllegalStateException("No se pudo procesar la entidad Cuenta"));
    }
    
    /**
     * Implementación específica para obtener la configuración del repositorio
     * @return Map con la configuración necesaria
     */
    @Override
    protected Map<String, Object> obtenerConfiguracion() {
        Map<String, Object> config = new HashMap<>();
        config.put("rutaArchivo", "src/main/resources/data/cuenta.json");
        config.put("tipoClase", Cuenta.class);
        config.put("extractorId", (Function<Cuenta, Long>) Cuenta::getId);
        return config;
    }
} 