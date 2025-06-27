package com.mibanco.vista.interna;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.modelo.Cliente;
import com.mibanco.vista.ClienteVista;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación de la vista para la entidad Cliente.
 * Visibilidad de paquete para que solo pueda ser instanciada a través de VistaFactoria.
 */
public class ClienteVistaImpl extends BaseVistaImpl<ClienteDTO> implements ClienteVista {

    /**
     * Constructor con visibilidad de paquete.
     */
    public ClienteVistaImpl() {
        super(); // Inicializa la consola a través de BaseVistaImpl
    }

    @Override
    public Map<String, String> solicitarDatosParaCrear(String mensaje) {
        mostrarMensaje(mensaje);
        return solicitarDatosGenerico(Cliente.class);
    }

    @Override
    public void mostrarMenuPrincipal() {
        consola.mostrar("\n=== GESTIÓN DE CLIENTES ===\n");
        consola.mostrar("1. Crear cliente\n");
        consola.mostrar("2. Buscar cliente por ID\n");
        consola.mostrar("3. Buscar cliente por DNI\n");
        consola.mostrar("4. Listar todos los clientes\n");
        consola.mostrar("5. Actualizar cliente\n");
        consola.mostrar("6. Eliminar cliente\n");
        consola.mostrar("7. Restaurar cliente\n");
        consola.mostrar("8. Mostrar clientes eliminados\n");
        consola.mostrar("0. Volver al menú principal\n");
        consola.mostrar("Seleccione una opción: ");
    }

    @Override
    public Optional<Long> solicitarIdCliente() {
        return leerNumero("Introduzca el ID del cliente");
    }

    @Override
    public Optional<String> solicitarDni() {
        consola.mostrar("Introduzca el DNI del cliente: ");
        String dni = consola.leerLinea().trim();
        return dni.isEmpty() ? Optional.empty() : Optional.of(dni);
    }

    @Override
    public void mostrarCliente(Optional<ClienteDTO> cliente) {
        cliente.ifPresentOrElse(
            c -> {
                consola.mostrar("\n--- DATOS DEL CLIENTE ---\n");
                consola.mostrar("ID: " + c.getId() + "\n");
                consola.mostrar("Nombre: " + c.getNombre() + "\n");
                consola.mostrar("Apellido: " + c.getApellido() + "\n");
                consola.mostrar("DNI: " + c.getDni() + "\n");
                consola.mostrar("Fecha de Nacimiento: " + c.getFechaNacimiento() + "\n");
                consola.mostrar("Email: " + c.getEmail() + "\n");
                consola.mostrar("Teléfono: " + c.getTelefono() + "\n");
                consola.mostrar("Dirección: " + c.getDireccion() + "\n");
            },
            () -> mostrarMensaje("Cliente no encontrado.")
        );
    }

    @Override
    public void mostrarTodosLosClientes(List<ClienteDTO> clientes) {
        if (clientes.isEmpty()) {
            mostrarMensaje("No hay clientes registrados.");
            return;
        }
        
        consola.mostrar("\n--- LISTA DE CLIENTES ---\n");
        clientes.forEach(cliente -> {
            consola.mostrar("ID: " + cliente.getId() + 
                           " | " + cliente.getNombre() + " " + cliente.getApellido() + 
                           " | DNI: " + cliente.getDni() + "\n");
        });
    }

    @Override
    public Map<String, String> solicitarDatosParaActualizar(ClienteDTO clienteActual) {
        consola.mostrar("\n--- ACTUALIZAR CLIENTE ---\n");
        consola.mostrar("Cliente actual: " + clienteActual.getNombre() + " " + clienteActual.getApellido() + "\n");
        consola.mostrar("Deje vacío el campo que no desee modificar.\n");
        
        Map<String, String> datos = new HashMap<>();
        
        // Solo pedir campos que se pueden modificar (no id, fechaCreacion, etc.)
        String email = leerCampo("Email actual: " + clienteActual.getEmail());
        if (!email.isEmpty()) datos.put("email", email);
        
        String telefono = leerCampo("Teléfono actual: " + clienteActual.getTelefono());
        if (!telefono.isEmpty()) datos.put("telefono", telefono);
        
        String direccion = leerCampo("Dirección actual: " + clienteActual.getDireccion());
        if (!direccion.isEmpty()) datos.put("direccion", direccion);
        
        return datos;
    }

    @Override
    public void mostrarEntidad(Optional<ClienteDTO> entidad) {
        mostrarCliente(entidad);
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        consola.mostrar(">> " + mensaje + "\n");
    }

    @Override
    public boolean confirmarRestauracion(ClienteDTO cliente) {
        consola.mostrar("\n--- CONFIRMAR RESTAURACIÓN ---\n");
        mostrarCliente(Optional.of(cliente));
        consola.mostrar("\n¿Está seguro de que desea restaurar este cliente? (s/n): ");
        String respuesta = consola.leerLinea().toLowerCase();
        return "s".equals(respuesta);
    }

    @Override
    public void mostrarClientesEliminados(List<ClienteDTO> clientesEliminados) {
        if (clientesEliminados.isEmpty()) {
            mostrarMensaje("No hay clientes eliminados.");
            return;
        }
        
        consola.mostrar("\n--- CLIENTES ELIMINADOS ---\n");
        clientesEliminados.forEach(cliente -> {
            consola.mostrar("ID: " + cliente.getId() + 
                           " | " + cliente.getNombre() + " " + cliente.getApellido() + 
                           " | DNI: " + cliente.getDni() + "\n");
        });
    }
} 