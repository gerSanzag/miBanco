package com.mibanco.repository.internal;

import com.mibanco.model.Cliente;
import com.mibanco.model.enums.TipoOperacionCliente;
import com.mibanco.repository.ClienteRepository;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Implementación del repositorio de Clientes con acceso restringido
 * Solo accesible a través de la Factory y las interfaces públicas
 */
class ClienteRepositoryImpl extends BaseRepositoryImpl<Cliente, Long, TipoOperacionCliente> implements ClienteRepository {
    
    /**
     * Constructor package-private
     */
    ClienteRepositoryImpl() {
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
    
    @Override
    protected Cliente crearConNuevoId(Cliente cliente) {
        return cliente.toBuilder()
                .id(idContador.getAndIncrement())
                .build();
    }
    
    @Override
    public ArrayList<Cliente> obtenerClientesEliminados() {
        return new ArrayList<>(entidadesEliminadas);
    }
} 