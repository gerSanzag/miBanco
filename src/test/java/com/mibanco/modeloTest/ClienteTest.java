package com.mibanco.modeloTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mibanco.modelo.Cliente;

import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.Optional;

@DisplayName("Tests para la clase Cliente")
class ClienteTest {

    Cliente cliente;
    Long id = 1L;
    String nombre = "Juan";
    String apellido = "Pérez";
    String dni = "12345678A";
    LocalDate fechaNacimiento = LocalDate.of(1990, 5, 15);
    String email = "juan.perez@email.com";
    String telefono = "123456789";
    String direccion = "Calle Principal 123";

    @BeforeEach
    void setUp() {
        cliente = Cliente.of(id, nombre, apellido, dni, fechaNacimiento, email, telefono, direccion);
    }

    @Test
    @DisplayName("Debería crear un cliente con datos válidos")
    void deberiaCrearClienteConDatosValidos() {
        // Act (Actuar)
        Cliente clienteBuilder = Cliente.builder()
                .nombre(nombre)
                .apellido(apellido)
                .dni(dni)
                .fechaNacimiento(fechaNacimiento)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .build();
        
        // Assert (Verificar)
        assertThat(clienteBuilder).isNotNull();
        assertThat(clienteBuilder.getNombre()).isEqualTo(nombre);
        assertThat(clienteBuilder.getApellido()).isEqualTo(apellido);
        assertThat(clienteBuilder.getDni()).isEqualTo(dni);
        assertThat(clienteBuilder.getEmail()).isEqualTo(email);
        assertThat(clienteBuilder.getTelefono()).isEqualTo(telefono);
        assertThat(clienteBuilder.getDireccion()).isEqualTo(direccion);
    }

    @Test
    @DisplayName("Debería crear cliente usando el método factory of()")
    void deberiaCrearClienteConMetodoFactory() {
        // Assert (Verificar)
        assertThat(cliente).isNotNull();
        assertThat(cliente.getId()).isEqualTo(id);
        assertThat(cliente.getNombre()).isEqualTo(nombre);
        assertThat(cliente.getApellido()).isEqualTo(apellido);
        assertThat(cliente.getDni()).isEqualTo(dni);
        assertThat(cliente.getFechaNacimiento()).isEqualTo(fechaNacimiento);
        assertThat(cliente.getEmail()).isEqualTo(email);
        assertThat(cliente.getTelefono()).isEqualTo(telefono);
        assertThat(cliente.getDireccion()).isEqualTo(direccion);
    }

    @Test
    @DisplayName("Debería actualizar email de forma inmutable")
    void deberiaActualizarEmailDeFormaInmutable() {
        // Arrange (Preparar)
        String nuevoEmail = "carlos.nuevo@email.com";
        
        // Act (Actuar)
        Cliente clienteActualizado = cliente.conEmail(nuevoEmail);
        
        // Assert (Verificar)
        assertThat(clienteActualizado.getEmail()).isEqualTo(nuevoEmail);
        assertThat(cliente.getEmail()).isEqualTo(email); // Original no cambia
        assertThat(clienteActualizado).isNotSameAs(cliente); // Son objetos diferentes
    }

    @Test
    @DisplayName("Debería actualizar teléfono de forma inmutable")
    void deberiaActualizarTelefonoDeFormaInmutable() {
        // Arrange (Preparar)
        String nuevoTelefono = "987654321";
        
        // Act (Actuar)
        Cliente clienteActualizado = cliente.conTelefono(nuevoTelefono);
        
        // Assert (Verificar)
        assertThat(clienteActualizado.getTelefono()).isEqualTo(nuevoTelefono);
        assertThat(cliente.getTelefono()).isEqualTo(telefono); // Original no cambia
        assertThat(clienteActualizado).isNotSameAs(cliente); // Son objetos diferentes
    }

    @Test
    @DisplayName("Debería actualizar dirección de forma inmutable")
    void deberiaActualizarDireccionDeFormaInmutable() {
        // Arrange (Preparar)
        String nuevaDireccion = "Avenida Nueva 456";
        
        // Act (Actuar)
        Cliente clienteActualizado = cliente.conDireccion(nuevaDireccion);
        
        // Assert (Verificar)
        assertThat(clienteActualizado.getDireccion()).isEqualTo(nuevaDireccion);
        assertThat(cliente.getDireccion()).isEqualTo(direccion); // Original no cambia
        assertThat(clienteActualizado).isNotSameAs(cliente); // Son objetos diferentes
    }

    @Test
    @DisplayName("Debería actualizar múltiples datos de contacto de forma inmutable")
    void deberiaActualizarMultiplesDatosContactoDeFormaInmutable() {
        // Arrange (Preparar)
        String nuevoEmail = "pedro.nuevo@email.com";
        String nuevoTelefono = "222222222";
        String nuevaDireccion = "Dirección Nueva 2";
        
        // Act (Actuar)
        Cliente clienteActualizado = cliente.conDatosContacto(
                Optional.of(nuevoEmail),
                Optional.of(nuevoTelefono),
                Optional.of(nuevaDireccion)
        );
        
        // Assert (Verificar)
        assertThat(clienteActualizado.getEmail()).isEqualTo(nuevoEmail);
        assertThat(clienteActualizado.getTelefono()).isEqualTo(nuevoTelefono);
        assertThat(clienteActualizado.getDireccion()).isEqualTo(nuevaDireccion);
        
        // Verificar que el original no cambió
        assertThat(cliente.getEmail()).isEqualTo(email);
        assertThat(cliente.getTelefono()).isEqualTo(telefono);
        assertThat(cliente.getDireccion()).isEqualTo(direccion);
    }

    @Test
    @DisplayName("Debería retornar la lista de campos requeridos")
    void deberiaRetornarListaDeCamposRequeridos() {
        // Act (Actuar)
        java.util.List<String> camposRequeridos = Cliente.obtenerCamposRequeridos();
        
        // Assert (Verificar)
        assertThat(camposRequeridos).isNotNull();
        assertThat(camposRequeridos).hasSize(7);
        assertThat(camposRequeridos).contains("nombre", "apellido", "dni", "email", "telefono", "direccion", "fechaNacimiento");
    }

    @Test
    @DisplayName("Debería devolver el identificador del cliente")
    void deberiaDevolverElIdentificadorDelCliente() {
        // Assert (Verificar)
        assertThat(cliente.getId()).isEqualTo(id);
        assertThat(cliente.getId()).isNotNull();
    }

    @Test
    @DisplayName("Debería devolver el identificador del cliente cuando es nulo")
    void deberiaDevolverElIdentificadorDelClienteCuandoEsNulo() {
        // Arrange (Preparar)
        Cliente clienteBuilder = Cliente.builder()
                .id(null)
                .nombre(nombre)
                .apellido(apellido)
                .dni(dni)
                .fechaNacimiento(fechaNacimiento)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .build();
        
        // Act (Actuar)
        Long id = clienteBuilder.getId();
        
        // Assert (Verificar)
        assertThat(id).isNull();
    }

    @Test
    @DisplayName("Debería manejar Optional vacío en actualizaciones múltiples")
    void deberiaManejarOptionalVacioEnActualizacionesMultiples() {
        // Arrange (Preparar)
        String nuevoEmail = "pedro.nuevo@email.com";
        
        // Act (Actuar) - Solo actualizar email, mantener teléfono y dirección originales
        Cliente clienteActualizado = cliente.conDatosContacto(
                Optional.of(nuevoEmail),
                Optional.empty(), // No cambiar teléfono
                Optional.empty()  // No cambiar dirección
        );
        
        // Assert (Verificar)
        assertThat(clienteActualizado.getEmail()).isEqualTo(nuevoEmail);
        assertThat(clienteActualizado.getTelefono()).isEqualTo(telefono); // Mantiene valor original
        assertThat(clienteActualizado.getDireccion()).isEqualTo(direccion); // Mantiene valor original
        
        // Verificar que el original no cambió
        assertThat(cliente.getEmail()).isEqualTo(email);
        assertThat(cliente.getTelefono()).isEqualTo(telefono);
        assertThat(cliente.getDireccion()).isEqualTo(direccion);
    }

    @Test
    @DisplayName("Debería validar que los campos requeridos no estén vacíos")
    void deberiaValidarQueLosCamposRequeridosNoEstenVacios() {
        // Act (Actuar)
        java.util.List<String> camposRequeridos = Cliente.obtenerCamposRequeridos();
        
        // Assert (Verificar)
        assertThat(camposRequeridos).isNotNull();
        assertThat(camposRequeridos).isNotEmpty();
        assertThat(camposRequeridos).allMatch(campo -> !campo.isEmpty());
        assertThat(camposRequeridos).allMatch(campo -> campo != null);
    }

    @Test
    @DisplayName("Debería validar que los campos requeridos sean únicos")
    void deberiaValidarQueLosCamposRequeridosSeanUnicos() {
        // Act (Actuar)
        java.util.List<String> camposRequeridos = Cliente.obtenerCamposRequeridos();
        
        // Assert (Verificar)
        assertThat(camposRequeridos).doesNotHaveDuplicates();
        assertThat(camposRequeridos).hasSize((int) camposRequeridos.stream().distinct().count());
    }
}