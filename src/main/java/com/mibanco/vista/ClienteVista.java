package com.mibanco.vista;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.vista.util.BaseVista;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interfaz para la vista de la entidad Cliente.
 * Define los métodos específicos para interactuar con el usuario en lo que respecta a la gestión de clientes.
 */
public interface ClienteVista extends BaseVista<ClienteDTO> {

    /**
     * Solicita al usuario el ID de un cliente.
     * @return un Optional con el ID del cliente, o un Optional vacío si la entrada no es un número válido.
     */
    Optional<Long> solicitarIdCliente();

    /**
     * Solicita al usuario el DNI de un cliente.
     * @return un Optional con el DNI del cliente, o un Optional vacío si la entrada está vacía.
     */
    Optional<String> solicitarDni();

    /**
     * Muestra los detalles de un cliente específico.
     * Si el Optional está vacío, informa al usuario que el cliente no fue encontrado.
     * @param cliente el Optional que contiene el DTO del cliente a mostrar.
     */
    void mostrarCliente(Optional<ClienteDTO> cliente);

    /**
     * Muestra una lista de todos los clientes registrados.
     * @param clientes la lista de DTOs de clientes a mostrar.
     */
    void mostrarTodosLosClientes(List<ClienteDTO> clientes);

    /**
     * Solicita datos para actualizar un cliente existente.
     * @param clienteActual el cliente actual que se va a modificar
     * @return Map con los datos a modificar o Map vacío si se cancela
     */
    Map<String, String> solicitarDatosParaActualizar(ClienteDTO clienteActual);

    /**
     * Muestra una lista de clientes eliminados.
     * @param clientesEliminados la lista de DTOs de clientes eliminados a mostrar.
     */
    void mostrarClientesEliminados(List<ClienteDTO> clientesEliminados);

    /**
     * Solicita confirmación para restaurar un cliente eliminado.
     * @param cliente el DTO del cliente a restaurar.
     * @return true si el usuario confirma la restauración, false en caso contrario.
     */
    boolean confirmarRestauracion(ClienteDTO cliente);
} 