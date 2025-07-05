package com.mibanco.repositorio.interna;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.repositorio.ClienteRepositorio;

import java.util.ArrayList;
import java.util.Optional;

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
    
  
    @Override
    public ArrayList<Cliente> obtenerClientesEliminados() {
        return new ArrayList<>(entidadesEliminadas);
    }
} 