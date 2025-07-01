package com.mibanco.modelo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;


@DisplayName("Tests para la clase Cliente")
class ClienteTest {

    @Test
    @DisplayName("Debería crear un cliente con datos válidos")
    void deberiaCrearClienteConDatosValidos() {
        // Arrange (Preparar)
        String nombre = "Juan";
        String apellido = "Pérez";
        String dni = "12345678A";
        LocalDate fechaNacimiento = LocalDate.of(1990, 5, 15);
        String email = "juan.perez@email.com";
        String telefono = "123456789";
        String direccion = "Calle Principal 123";
        
        // Act (Actuar)
        Cliente cliente = Cliente.builder()
                .nombre(nombre)
                .apellido(apellido)
                .dni(dni)
                .fechaNacimiento(fechaNacimiento)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .build();
        
        // Assert (Verificar)
        assertThat(cliente).isNotNull();
        assertThat(cliente.getNombre()).isEqualTo(nombre);
        assertThat(cliente.getApellido()).isEqualTo(apellido);
        assertThat(cliente.getDni()).isEqualTo(dni);
        assertThat(cliente.getEmail()).isEqualTo(email);
        assertThat(cliente.getTelefono()).isEqualTo(telefono);
        assertThat(cliente.getDireccion()).isEqualTo(direccion);
    }

    @Test
    @DisplayName("Debería crear cliente usando el método factory of()")
    void deberiaCrearClienteConMetodoFactory() {
        // Arrange (Preparar)
        Long id = 1L;
        String nombre = "María";
        String apellido = "García";
        String dni = "87654321B";
        LocalDate fechaNacimiento = LocalDate.of(1985, 8, 20);
        String email = "maria.garcia@email.com";
        String telefono = "987654321";
        String direccion = "Avenida Central 456";
        
        // Act (Actuar)
        Cliente cliente = Cliente.of(id, nombre, apellido, dni, fechaNacimiento, email, telefono, direccion);
        
        // Assert (Verificar)
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
        Cliente clienteOriginal = Cliente.builder()
                .nombre("Carlos")
                .apellido("López")
                .email("carlos.lopez@email.com")
                .build();
        String nuevoEmail = "carlos.nuevo@email.com";
        
        // Act (Actuar)
        Cliente clienteActualizado = clienteOriginal.conEmail(nuevoEmail);
        
        // Assert (Verificar)
        assertThat(clienteActualizado.getEmail()).isEqualTo(nuevoEmail);
        assertThat(clienteOriginal.getEmail()).isEqualTo("carlos.lopez@email.com"); // Original no cambia
        assertThat(clienteActualizado).isNotSameAs(clienteOriginal); // Son objetos diferentes
    }

    @Test
    @DisplayName("Debería actualizar teléfono de forma inmutable")
    void deberiaActualizarTelefonoDeFormaInmutable() {
        // Arrange (Preparar)
        Cliente clienteOriginal = Cliente.builder()
                .nombre("Ana")
                .apellido("Martínez")
                .telefono("123456789")
                .build();
        String nuevoTelefono = "987654321";
        
        // Act (Actuar)
        Cliente clienteActualizado = clienteOriginal.conTelefono(nuevoTelefono);
        
        // Assert (Verificar)
        assertThat(clienteActualizado.getTelefono()).isEqualTo(nuevoTelefono);
        assertThat(clienteOriginal.getTelefono()).isEqualTo("123456789"); // Original no cambia
        assertThat(clienteActualizado).isNotSameAs(clienteOriginal); // Son objetos diferentes
    }

    @Test
    @DisplayName("Debería actualizar dirección de forma inmutable")
    void deberiaActualizarDireccionDeFormaInmutable() {
        // Arrange (Preparar)
        Cliente clienteOriginal = Cliente.builder()
                .nombre("Luis")
                .apellido("González")
                .direccion("Calle Vieja 123")
                .build();
        String nuevaDireccion = "Avenida Nueva 456";
        
        // Act (Actuar)
        Cliente clienteActualizado = clienteOriginal.conDireccion(nuevaDireccion);
        
        // Assert (Verificar)
        assertThat(clienteActualizado.getDireccion()).isEqualTo(nuevaDireccion);
        assertThat(clienteOriginal.getDireccion()).isEqualTo("Calle Vieja 123"); // Original no cambia
        assertThat(clienteActualizado).isNotSameAs(clienteOriginal); // Son objetos diferentes
    }

    @Test
    @DisplayName("Debería actualizar múltiples datos de contacto de forma inmutable")
    void deberiaActualizarMultiplesDatosContactoDeFormaInmutable() {
        // Arrange (Preparar)
        Cliente clienteOriginal = Cliente.builder()
                .nombre("Pedro")
                .apellido("Sánchez")
                .email("pedro.viejo@email.com")
                .telefono("111111111")
                .direccion("Dirección Vieja 1")
                .build();
        
        // Act (Actuar)
        Cliente clienteActualizado = clienteOriginal.conDatosContacto(
                java.util.Optional.of("pedro.nuevo@email.com"),
                java.util.Optional.of("222222222"),
                java.util.Optional.of("Dirección Nueva 2")
        );
        
        // Assert (Verificar)
        assertThat(clienteActualizado.getEmail()).isEqualTo("pedro.nuevo@email.com");
        assertThat(clienteActualizado.getTelefono()).isEqualTo("222222222");
        assertThat(clienteActualizado.getDireccion()).isEqualTo("Dirección Nueva 2");
        
        // Verificar que el original no cambió
        assertThat(clienteOriginal.getEmail()).isEqualTo("pedro.viejo@email.com");
        assertThat(clienteOriginal.getTelefono()).isEqualTo("111111111");
        assertThat(clienteOriginal.getDireccion()).isEqualTo("Dirección Vieja 1");
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
}