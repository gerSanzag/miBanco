package com.mibanco.controlador;

/**
 * Interfaz del controlador para la entidad Cliente.
 * Define los métodos para manejar las operaciones de gestión de clientes.
 * El controlador actúa como intermediario entre la vista y el servicio.
 */
public interface ClienteControlador {

    /**
     * Maneja la creación de un nuevo cliente.
     * Recibe datos de la vista, los convierte a DTO y los envía al servicio.
     */
    void crearCliente();

    /**
     * Maneja la búsqueda de un cliente por ID.
     * Solicita el ID a la vista, lo busca en el servicio y muestra el resultado.
     */
    void buscarClientePorId();

    /**
     * Maneja la visualización de todos los clientes.
     * Obtiene la lista del servicio y la muestra a través de la vista.
     */
    void listarTodosLosClientes();

    /**
     * Maneja la actualización de un cliente.
     * Solicita ID, busca cliente, solicita modificaciones, confirma y guarda.
     */
    void actualizarCliente();

    /**
     * Maneja la eliminación de un cliente.
     * Solicita ID, busca cliente, confirma eliminación y elimina.
     */
    void eliminarCliente();

    /**
     * Maneja el menú principal de gestión de clientes.
     * Muestra opciones y dirige al método correspondiente según la selección.
     */
    void mostrarMenuPrincipal();
} 