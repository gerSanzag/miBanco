package com.mibanco.repositorio.interna;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.repositorio.ClienteRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Implementación del repositorio de Clientes con acceso restringido
 * Solo accesible a través de la Factory y las interfaces públicas
 */
class ClienteRepositorioImpl extends BaseRepositorioImpl<Cliente, Long, TipoOperacionCliente> implements ClienteRepositorio {
    
    /**
     * Constructor package-private
     */
    ClienteRepositorioImpl() {
        super();
    }
    
    @Override
    public Optional<Cliente> buscarPorDni(Optional<String> dniOpt) {
        return dniOpt.flatMap(dni -> 
            buscarPorPredicado(cliente -> cliente.getDni().equals(dni))
        );
    }
    
    @Override
    public Optional<Cliente> restaurarClienteEliminado(Optional<Long> id) {
        return restaurar(id, TipoOperacionCliente.RESTAURAR);
    }
    
  

    
    /**
     * Implementación específica para asignar nuevo ID a Cliente
     * Usa el contador de la base
     * @param cliente Cliente sin ID asignado
     * @return Cliente con nuevo ID asignado
     */
    @Override
    protected Cliente crearConNuevoId(Cliente cliente) {
        // ✅ Usar el contador de la base
        Long nuevoId = idContador.incrementAndGet();
        // ✅ Asignar nuevo ID al Cliente
        return Cliente.builder()
            .id(nuevoId)
            .build();
    }
    
    /**
     * Implementación específica para obtener la configuración del repositorio
     * @return Map con la configuración necesaria
     */
    @Override
    protected Map<String, Object> obtenerConfiguracion() {
        Map<String, Object> config = new HashMap<>();
        config.put("rutaArchivo", "src/main/resources/data/clientes.json");
        config.put("tipoClase", Cliente.class);
        config.put("extractorId", (Function<Cliente, Long>) Cliente::getId);
        return config;
    }
} 