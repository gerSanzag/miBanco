package com.mibanco.vista.interna;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.vista.VistaCliente;
import com.mibanco.vista.util.Consola;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

/**
 * Implementación de la vista para la entidad Cliente.
 * Visibilidad de paquete para que solo pueda ser instanciada a través de VistaFactoria.
 */
class VistaClienteImpl implements VistaCliente {

    private final Consola consola;
    private static final String ACCION_BUSCAR = "buscar";
    private static final String ACCION_ACTUALIZAR = "actualizar";
    private static final String ACCION_ELIMINAR = "eliminar";

    /**
     * Constructor con visibilidad de paquete.
     * Obtiene la instancia de la consola para la interacción con el usuario.
     */
    VistaClienteImpl() {
        this.consola = new ConsolaImpl(new Scanner(System.in));
    }

    @Override
    public void mostrarMenuPrincipal() {
        consola.mostrar("\n--- Menú de Gestión de Clientes ---\n");
        consola.mostrar("1. Crear nuevo cliente\n");
        consola.mostrar("2. Buscar cliente por ID\n");
        consola.mostrar("3. Listar todos los clientes\n");
        consola.mostrar("4. Actualizar cliente\n");
        consola.mostrar("5. Eliminar cliente\n");
        consola.mostrar("0. Volver al menú principal\n");
        consola.mostrar("------------------------------------\n");
    }

    @Override
    public Optional<Integer> obtenerOpcion() {
        consola.mostrar("Seleccione una opción: ");
        try {
            return Optional.of(Integer.parseInt(consola.leerLinea()));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: Por favor, introduzca un número válido.");
            return Optional.empty();
        }
    }

    @Override
    public Map<String, String> solicitarDatosParaCrear() {
        consola.mostrar("\n--- Creación de Nuevo Cliente ---\n");
        Map<String, String> datos = new HashMap<>();
        datos.put("nombre", leerCampo("Nombre completo"));
        datos.put("apellido", leerCampo("Apellido"));
        datos.put("dni", leerCampo("DNI"));
        datos.put("email", leerCampo("Email"));
        datos.put("telefono", leerCampo("Teléfono"));
        datos.put("direccion", leerCampo("Dirección"));
        datos.put("fechaNacimiento", leerFecha("Fecha de Nacimiento (yyyy-MM-dd)"));
        return datos;
    }

    @Override
    public Optional<Long> solicitarIdCliente() {
        consola.mostrar("Introduzca el ID del cliente: ");
        try {
            return Optional.of(Long.parseLong(consola.leerLinea()));
        } catch (NumberFormatException e) {
            mostrarMensaje("Error: El ID debe ser un número válido.");
            return Optional.empty();
        }
    }

    @Override
    public Map<String, String> solicitarDatosParaActualizar(ClienteDTO clienteActual) {
        consola.mostrar("\n--- Datos modificables del Cliente ID: " + clienteActual.getId() + " ---\n");
        consola.mostrar("1. Email: " + clienteActual.getEmail() + "\n");
        consola.mostrar("2. Teléfono: " + clienteActual.getTelefono() + "\n");
        consola.mostrar("3. Dirección: " + clienteActual.getDireccion() + "\n");
        
        consola.mostrar("\n¿Cómo desea modificar?\n");
        consola.mostrar("1. Solo Email\n");
        consola.mostrar("2. Solo Teléfono\n");
        consola.mostrar("3. Solo Dirección\n");
        consola.mostrar("4. Todos los campos\n");
        consola.mostrar("0. Cancelar\n");
        
        Optional<Integer> opcion = obtenerOpcion();
        
        return opcion.map(opt -> {
            Map<String, String> datos = switch (opt) {
                case 1 -> Map.of("email", leerCampo("Nuevo Email"));
                case 2 -> Map.of("telefono", leerCampo("Nuevo Teléfono"));
                case 3 -> Map.of("direccion", leerCampo("Nueva Dirección"));
                case 4 -> Map.of(
                    "email", leerCampo("Nuevo Email"),
                    "telefono", leerCampo("Nuevo Teléfono"),
                    "direccion", leerCampo("Nueva Dirección")
                );
                case 0 -> {
                    mostrarMensaje("Operación cancelada.");
                    yield Map.of();
                }
                default -> {
                    mostrarMensaje("Opción no válida.");
                    yield Map.of();
                }
            };
            return datos;
        }).orElse(Map.of());
    }

    @Override
    public void mostrarCliente(Optional<ClienteDTO> clienteOpt) {
        clienteOpt.ifPresentOrElse(
            cliente -> {
                consola.mostrar("\n--- Detalles del Cliente ---\n");
                consola.mostrar("ID: " + cliente.getId() + "\n");
                consola.mostrar("Nombre: " + cliente.getNombre() + "\n");
                consola.mostrar("Apellido: " + cliente.getApellido() + "\n");
                consola.mostrar("DNI: " + cliente.getDni() + "\n");
                consola.mostrar("Email: " + cliente.getEmail() + "\n");
                consola.mostrar("Teléfono: " + cliente.getTelefono() + "\n");
                consola.mostrar("Dirección: " + cliente.getDireccion() + "\n");
                consola.mostrar("Fecha de Nacimiento: " + cliente.getFechaNacimiento().format(DateTimeFormatter.ISO_LOCAL_DATE) + "\n");
                consola.mostrar("----------------------------\n");
            },
            () -> mostrarMensaje("Cliente no encontrado.")
        );
    }

    @Override
    public void mostrarTodosLosClientes(List<ClienteDTO> clientes) {
        consola.mostrar("\n--- Listado de Clientes ---\n");
        
        Optional.of(clientes)
            .filter(lista -> !lista.isEmpty())
            .ifPresentOrElse(
                lista -> {
                    lista.forEach(cliente -> mostrarCliente(Optional.of(cliente)));
                    consola.mostrar("---------------------------\n");
                },
                () -> consola.mostrar("No hay clientes registrados.\n")
            );
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        consola.mostrar(">> " + mensaje + "\n");
    }

    @Override
    public boolean confirmarAccion(ClienteDTO cliente, String titulo, String mensaje) {
        consola.mostrar("\n--- " + titulo + " ---\n");
        mostrarCliente(Optional.of(cliente));
        
        consola.mostrar("\n" + mensaje + " (s/n): ");
        String respuesta = consola.leerLinea().toLowerCase();
        
        return "s".equals(respuesta);
    }

    private String leerCampo(String nombreCampo) {
        consola.mostrar(nombreCampo + ": ");
        return consola.leerLinea();
    }
    
    private String leerFecha(String mensaje) {
        while (true) {
            consola.mostrar(mensaje + ": ");
            String fechaStr = consola.leerLinea();
            try {
                LocalDate.parse(fechaStr, DateTimeFormatter.ISO_LOCAL_DATE);
                return fechaStr;
            } catch (DateTimeParseException e) {
                mostrarMensaje("Formato de fecha incorrecto. Use yyyy-MM-dd.");
            }
        }
    }
} 