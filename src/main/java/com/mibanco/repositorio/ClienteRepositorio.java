package com.mibanco.repositorio;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.repositorio.util.BaseRepositorio;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Interfaz para el repositorio de Clientes
 * Extiende la interfaz base para heredar operaciones CRUD genéricas
 */
public interface ClienteRepositorio extends BaseRepositorio<Cliente, Long, TipoOperacionCliente> {
    
    /**
     * Busca un cliente por su DNI
     * @param dni Optional con el DNI del cliente a buscar
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> buscarPorDni(Optional<String> dni);
    
    /**
     * Restaura un cliente previamente eliminado
     * @param id Optional con el ID del cliente a restaurar
     * @return Optional con el cliente restaurado o Optional vacío si no se encuentra
     */
    Optional<Cliente> restaurarClienteEliminado(Optional<Long> id);
    

} 