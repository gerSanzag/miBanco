package com.mibanco.repository.impl;

import com.mibanco.model.Cliente;
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
    
    // Lista para almacenar clientes en memoria
    private final List<Cliente> clientes = new ArrayList<>();
    
    // Contador para generar IDs automáticamente
    private final AtomicLong idCounter = new AtomicLong(1);
    
    // Cache para restauración de clientes eliminados
    private final List<Cliente> clientesEliminados = new ArrayList<>();
    
    // Repositorio de auditoría
    private final AuditoriaRepository auditoriaRepository;
    
    // Usuario actual (en un sistema real vendría de un sistema de autenticación)
    private String usuarioActual = "sistema";
    
    /**
     * Constructor que inicializa el repositorio de auditoría
     */
    public ClienteRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        super(auditoriaRepository);
        this.auditoriaRepository = auditoriaRepository;
    }
    
    /**
     * Establece el usuario actual que realiza las operaciones
     */
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    @Override
    public Optional<Cliente> findByDni(Optional<String> dniOpt) {
        return dniOpt.flatMap(dni -> 
            findByPredicate(cliente -> cliente.getDni().equals(dni))
        );
    }
    
    @Override
    public Optional<Cliente> restoreDeletedClient(Optional<Long> id) {
        return restoreDeletedEntity(id);
    }
    
    @Override
    protected Cliente createWithNewId(Cliente cliente) {
        return cliente.toBuilder()
                .id(idCounter.getAndIncrement())
                .build();
    }
    
    @Override
    protected void registrarAuditoria(Cliente cliente, TipoOperacionCliente tipoOperacion) {
        AuditoriaUtil.registrarOperacion(
            auditoriaRepository,
            tipoOperacion,
            cliente,
            usuarioActual
        );
    }
    
    @Override
    protected TipoOperacionCliente getOperationType(OperationType type) {
        return switch (type) {
            case CREATE -> TipoOperacionCliente.CREAR;
            case UPDATE -> TipoOperacionCliente.MODIFICAR;
            case DELETE -> TipoOperacionCliente.ELIMINAR;
            case RESTORE -> TipoOperacionCliente.RESTAURAR;
        };
    }
    
    @Override
    public Optional<List<Cliente>> findAll() {
        // Devolvemos una copia para no exponer la lista interna
        return Optional.of(new ArrayList<>(entities));
    }
    
    @Override
    public Optional<Cliente> deleteById(Optional<Long> idOpt) {
        return idOpt.map(id -> {
            Optional<Cliente> clienteAEliminar = entities.stream()
                    .filter(cliente -> cliente.getId().equals(id))
                    .findFirst();
                    
            clienteAEliminar.ifPresent(cliente -> {
                // Removemos el cliente de la lista principal
                entities.remove(cliente);
                
                // Guardamos el cliente en la caché para posible restauración
                clientesEliminados.add(cliente);
                
                // Registra la auditoría de eliminación directamente
                registrarAuditoria(cliente, getOperationType(OperationType.DELETE));
            });
            
            return clienteAEliminar;
        }).orElse(Optional.empty());
    }
    
    /**
     * Obtiene la lista de clientes recientemente eliminados
     * @return Lista de clientes eliminados
     */
    public List<Cliente> getRecentlyDeletedClients() {
        return new ArrayList<>(clientesEliminados);
    }
    
    @Override
    public long count() {
        return entities.size();
    }
} 