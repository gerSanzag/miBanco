package com.mibanco.repository.impl;

import com.mibanco.model.Cliente;
import com.mibanco.model.RegistroAuditoria;
import com.mibanco.model.enums.TipoOperacionCliente;
import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.ClienteRepository;
import com.mibanco.util.AuditoriaUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementación del repositorio de Clientes
 * Extiende la implementación base para heredar funcionalidad CRUD genérica
 */
public class ClienteRepositoryImpl extends BaseRepositoryImpl<Cliente, Long, TipoOperacionCliente> implements ClienteRepository {
    
    /**
     * Constructor que inicializa el repositorio de auditoría
     */
    public ClienteRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        super(auditoriaRepository);
    }
    
    @Override
    public Optional<Cliente> findByDni(Optional<String> dniOpt) {
        return dniOpt.flatMap(dni -> 
            findByPredicate(cliente -> cliente.getDni().equals(dni))
        );
    }
    
    @Override
    public Optional<Cliente> restoreDeletedClient(Optional<Long> id) {
        return restaurar(id, TipoOperacionCliente.RESTAURAR);
    }
    
    @Override
    protected Cliente createWithNewId(Cliente cliente) {
        return cliente.toBuilder()
                .id(idCounter.getAndIncrement())
                .build();
    }
    
    @Override
    protected void registrarAuditoria(Cliente cliente, TipoOperacionCliente tipoOperacion) {
        RegistroAuditoria<Cliente, TipoOperacionCliente> registro = AuditoriaUtil.registrarOperacion(
            auditoriaRepository,
            tipoOperacion,
            cliente,
            usuarioActual
        );
    }
    
    /**
     * Obtiene la lista de clientes recientemente eliminados
     * @return Lista de clientes eliminados
     */
    public ArrayList<Cliente> getRecentlyDeletedClients() {
        return new ArrayList<>(deletedEntities);
    }
} 