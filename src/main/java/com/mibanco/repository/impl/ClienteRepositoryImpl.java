package com.mibanco.repository.impl;

import com.mibanco.config.factory.RepositoryFactory;
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
 * Implementación del repositorio de Clientes usando una lista en memoria
 * Utilizamos enfoque funcional con streams y Optional como valores de retorno
 * Con soporte para auditoría de operaciones usando AuditoriaUtil simplificado
 */
public class ClienteRepositoryImpl implements ClienteRepository {
    
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
     * Constructor que inicializa el repositorio de auditoría mediante la Factory
     * llamando a getAuditoriaRepository() de RepositoryFactory directamente.   
     */
    public ClienteRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }
    
    /**
     * Establece el usuario actual que realiza las operaciones
     */
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    @Override
    public Optional<Cliente> save(Optional<Cliente> clienteOpt) {
        return clienteOpt
                .map(cliente -> 
                    Optional.ofNullable(cliente.getId())
                        .map(id -> {
                            // Si el cliente ya existe, lo actualizamos
                            Cliente clienteActualizado = update(cliente);
                            
                            // Registra la auditoría de modificación directamente
                            AuditoriaUtil.registrarOperacion(
                                auditoriaRepository,
                                TipoOperacionCliente.MODIFICAR, 
                                clienteActualizado,
                                usuarioActual
                            );
                            
                            return clienteActualizado;
                        })
                        .orElseGet(() -> {
                            // Si es nuevo, generamos un ID y lo guardamos
                            Cliente nuevoCliente = cliente.toBuilder()
                                    .id(idCounter.getAndIncrement())
                                    .build();
                            clientes.add(nuevoCliente);
                            
                            // Registra la auditoría de creación directamente
                            AuditoriaUtil.registrarOperacion(
                                auditoriaRepository,
                                TipoOperacionCliente.CREAR, 
                                nuevoCliente,
                                usuarioActual
                            );
                            
                            return nuevoCliente;
                        })
                );
    }
    
    /**
     * Método auxiliar para actualizar un cliente existente
     */
    private Cliente update(Cliente cliente) {
        // Buscamos y eliminamos el cliente actual
        clientes.removeIf(c -> c.getId().equals(cliente.getId()));
        // Añadimos el cliente actualizado
        clientes.add(cliente);
        return cliente;
    }
    
    @Override
    public Optional<Cliente> findById(Optional<Long> idOpt) {
        return idOpt.map(id -> clientes.stream()
                .filter(cliente -> cliente.getId().equals(id))
                .findFirst())
                .orElse(Optional.empty());
    }
    
    @Override
    public Optional<Cliente> findByDni(Optional<String> dniOpt) {
        return dniOpt.map(dni -> clientes.stream()
                .filter(cliente -> cliente.getDni().equals(dni))
                .findFirst())
                .orElse(Optional.empty());
    }
    
    @Override
    public Optional<List<Cliente>> findAll() {
        // Devolvemos una copia para no exponer la lista interna
        return Optional.of(new ArrayList<>(clientes));
    }
    
    @Override
    public Optional<Cliente> deleteById(Optional<Long> idOpt) {
        return idOpt.map(id -> {
            Optional<Cliente> clienteAEliminar = clientes.stream()
                    .filter(cliente -> cliente.getId().equals(id))
                    .findFirst();
                    
            clienteAEliminar.ifPresent(cliente -> {
                // Removemos el cliente de la lista principal
                clientes.remove(cliente);
                
                // Guardamos el cliente en la caché para posible restauración
                clientesEliminados.add(cliente);
                
                // Registra la auditoría de eliminación directamente
                AuditoriaUtil.registrarOperacion(
                    auditoriaRepository,
                    TipoOperacionCliente.ELIMINAR, 
                    cliente,
                    usuarioActual
                );
            });
            
            return clienteAEliminar;
        }).orElse(Optional.empty());
    }
    
    /**
     * Restaura un cliente previamente eliminado
     * @param idOpt Optional con el ID del cliente a restaurar
     * @return Optional con el cliente restaurado o vacío si no se encuentra
     */
    public Optional<Cliente> restoreDeletedClient(Optional<Long> idOpt) {
        return idOpt.map(id -> {
            Optional<Cliente> clienteARestaurar = clientesEliminados.stream()
                    .filter(cliente -> cliente.getId().equals(id))
                    .findFirst();
                    
            clienteARestaurar.ifPresent(cliente -> {
                // Añadimos de nuevo el cliente a la lista principal
                clientes.add(cliente);
                
                // Lo quitamos de la caché de eliminados
                clientesEliminados.removeIf(c -> c.getId().equals(id));
                
                // Registra la auditoría de restauración directamente
                AuditoriaUtil.registrarOperacion(
                    auditoriaRepository,
                    TipoOperacionCliente.RESTAURAR, 
                    cliente,
                    usuarioActual
                );
            });
            
            return clienteARestaurar;
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
        return clientes.size();
    }
} 