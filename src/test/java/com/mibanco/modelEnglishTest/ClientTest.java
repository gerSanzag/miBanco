package com.mibanco.modelEnglishTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mibanco.modelEnglish.Client;

import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.Optional;

@DisplayName("Tests for Client class")
class ClientTest {

    Client client;
    Long id = 1L;
    String firstName = "Juan";        // nombre
    String lastName = "Pérez";        // apellido
    String dni = "12345678A";
    LocalDate birthDate = LocalDate.of(1990, 5, 15);  // fechaNacimiento
    String email = "juan.perez@email.com";
    String phone = "123456789";       // telefono
    String address = "Calle Principal 123";  // direccion

    @BeforeEach
    void setUp() {
        client = Client.of(id, firstName, lastName, dni, birthDate, email, phone, address);
    }

    @Test
    @DisplayName("Should create a client with valid data")
    void shouldCreateClientWithValidData() {
        // Act (Actuar)
        Client clientBuilder = Client.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dni(dni)
                .birthDate(birthDate)
                .email(email)
                .phone(phone)
                .address(address)
                .build();
        
        // Assert (Verificar)
        assertThat(clientBuilder).isNotNull();
        assertThat(clientBuilder.getFirstName()).isEqualTo(firstName);
        assertThat(clientBuilder.getLastName()).isEqualTo(lastName);
        assertThat(clientBuilder.getDni()).isEqualTo(dni);
        assertThat(clientBuilder.getEmail()).isEqualTo(email);
        assertThat(clientBuilder.getPhone()).isEqualTo(phone);
        assertThat(clientBuilder.getAddress()).isEqualTo(address);
    }

    @Test
    @DisplayName("Should create client using the factory method of()")
    void shouldCreateClientWithFactoryMethod() {
        // Assert (Verificar)
        assertThat(client).isNotNull();
        assertThat(client.getId()).isEqualTo(id);
        assertThat(client.getFirstName()).isEqualTo(firstName);
        assertThat(client.getLastName()).isEqualTo(lastName);
        assertThat(client.getDni()).isEqualTo(dni);
        assertThat(client.getBirthDate()).isEqualTo(birthDate);
        assertThat(client.getEmail()).isEqualTo(email);
        assertThat(client.getPhone()).isEqualTo(phone);
        assertThat(client.getAddress()).isEqualTo(address);
    }

    @Test
    @DisplayName("Should return the list of required fields")
    void shouldReturnListOfRequiredFields() {
        // Act (Actuar)
        java.util.List<String> requiredFields = Client.getRequiredFields();
        
        // Assert (Verificar)
        assertThat(requiredFields).isNotNull();
        assertThat(requiredFields).hasSize(7);
        assertThat(requiredFields).contains("firstName", "lastName", "dni", "email", "phone", "address", "birthDate");
    }

    @Test
    @DisplayName("Should return the client identifier")
    void shouldReturnClientIdentifier() {
        // Assert (Verificar)
        assertThat(client.getId()).isEqualTo(id);
        assertThat(client.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should return the client identifier when it is null")
    void shouldReturnClientIdentifierWhenItIsNull() {
        // Arrange (Preparar)
        Client clientBuilder = Client.builder()
                .id(null)
                .firstName(firstName)
                .lastName(lastName)
                .dni(dni)
                .birthDate(birthDate)
                .email(email)
                .phone(phone)
                .address(address)
                .build();
        
        // Act (Actuar)
        Long id = clientBuilder.getId();
        
        // Assert (Verificar)
        assertThat(id).isNull();
    }

} 