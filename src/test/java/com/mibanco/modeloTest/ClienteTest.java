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

}