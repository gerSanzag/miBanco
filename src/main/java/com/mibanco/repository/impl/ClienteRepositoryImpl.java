package com.mibanco.repository.impl;

import com.mibanco.model.Cliente;
import com.mibanco.model.enums.TipoOperacionCliente;
import com.mibanco.repository.ClienteRepository;
import com.mibanco.repository.util.BaseRepositoryImpl;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Implementación del repositorio de Clientes
 * Extiende la implementación base para heredar funcionalidad CRUD genérica
 */
public class ClienteRepositoryImpl extends BaseRepositoryImpl<Cliente, Long, TipoOperacionCliente> implements ClienteRepository {
    
    /**
     * Constructor por defecto
     */
    public ClienteRepositoryImpl() {
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
                .id(idCounter.getAndIncrement())
                .build();
    }
    

    /**
     * Obtiene la lista de clientes recientemente eliminados
     * @return Lista de clientes eliminados
     */
    public ArrayList<Cliente> obtenerClientesEliminados() {
        return new ArrayList<>(deletedEntities);
    }
} 