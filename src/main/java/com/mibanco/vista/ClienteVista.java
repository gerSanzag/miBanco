package com.mibanco.vista;

import com.mibanco.dto.ClienteDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interfaz para la vista de la entidad Cliente.
 * Define los métodos para interactuar con el usuario en lo que respecta a la gestión de clientes.
 */
public interface ClienteVista {

    /**
     * Muestra el menú principal de opciones para la gestión de clientes.
     */
    void mostrarMenuPrincipal();

    /**
     * Lee la opción numérica que el usuario introduce desde el teclado.
     *
     * @return un Optional con el número de la opción seleccionada, o un Optional vacío si la entrada no es un número válido.
     */
    Optional<Integer> obtenerOpcion();

    /**
     * Solicita al usuario los datos necesarios para crear un nuevo cliente.
     *
     * @return un Map con los datos crudos del cliente (ej: "nombre" -> "Juan Perez").
     */
    Map<String, String> solicitarDatosParaCrear();

    /**
     * Solicita al usuario el ID de un cliente.
     *
     * @return un Optional con el ID del cliente, o un Optional vacío si la entrada no es un número válido.
     */
    Optional<Long> solicitarIdCliente();

    /**
     * Solicita al usuario el DNI de un cliente.
     *
     * @return un Optional con el DNI del cliente, o un Optional vacío si la entrada está vacía.
     */
    Optional<String> solicitarDni();

    /**
     * Muestra los datos modificables de un cliente y solicita los nuevos valores.
     * Solo muestra y permite modificar email, teléfono y dirección.
     *
     * @param clienteActual el DTO del cliente con los datos actuales para mostrarlos como referencia.
     * @return un Map con los nuevos datos introducidos por el usuario (solo los campos modificados).
     */
    Map<String, String> solicitarDatosParaActualizar(ClienteDTO clienteActual);

    /**
     * Muestra los detalles de un cliente específico.
     * Si el Optional está vacío, informa al usuario que el cliente no fue encontrado.
     *
     * @param cliente el Optional que contiene el DTO del cliente a mostrar.
     */
    void mostrarCliente(Optional<ClienteDTO> cliente);

    /**
     * Muestra una lista de todos los clientes registrados.
     *
     * @param clientes la lista de DTOs de clientes a mostrar.
     */
    void mostrarTodosLosClientes(List<ClienteDTO> clientes);

    /**
     * Muestra una lista de clientes eliminados.
     *
     * @param clientesEliminados la lista de DTOs de clientes eliminados a mostrar.
     */
    void mostrarClientesEliminados(List<ClienteDTO> clientesEliminados);

    /**
     * Muestra un mensaje genérico en la consola.
     *
     * @param mensaje el mensaje a mostrar.
     */
    void mostrarMensaje(String mensaje);

    /**
     * Muestra los datos de un cliente y solicita confirmación para una acción específica.
     *
     * @param cliente el DTO del cliente sobre el cual se realizará la acción.
     * @param titulo el título que se mostrará en la cabecera.
     * @param mensaje el mensaje de confirmación que se mostrará al usuario.
     * @return true si el usuario confirma la acción, false en caso contrario.
     */
    boolean confirmarAccion(ClienteDTO cliente, String titulo, String mensaje);

    /**
     * Solicita confirmación para restaurar un cliente eliminado.
     *
     * @param cliente el DTO del cliente a restaurar.
     * @return true si el usuario confirma la restauración, false en caso contrario.
     */
    boolean confirmarRestauracion(ClienteDTO cliente);
} 