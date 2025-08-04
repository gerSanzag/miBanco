package com.mibanco.dtoTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.dto.ClientDTO;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.Optional;

@DisplayName("Tests for ClientDTO class")
class ClientDtoTest {

    Long id = 1L;
    String firstName = "Juan"; // nombre
    String lastName = "Pérez"; // apellido
    String dni = "12345678A";
    String email = "juan.perez@email.com";
    String phone = "123456789"; // telefono
    LocalDate birthDate = LocalDate.of(1990, 5, 15); // fechaNacimiento
    String address = "Calle Principal 123"; // direccion
    ClientDTO clientDto;

    @BeforeEach
    void setUp() {
        clientDto = ClientDTO.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .dni(dni)
                .email(email)
                .phone(phone)
                .birthDate(birthDate)
                .address(address)
                .build();
    }

    @Test
    @DisplayName("Should create a ClientDTO with valid data using Builder")
    void shouldCreateClientDtoWithValidDataBuilder() {
        // Assert (Verify)
        assertThat(clientDto).isNotNull();
        assertThat(clientDto.getId()).isEqualTo(id);
        assertThat(clientDto.getFirstName()).isEqualTo(firstName);
        assertThat(clientDto.getLastName()).isEqualTo(lastName);
        assertThat(clientDto.getDni()).isEqualTo(dni);
        assertThat(clientDto.getEmail()).isEqualTo(email);
        assertThat(clientDto.getPhone()).isEqualTo(phone);
        assertThat(clientDto.getBirthDate()).isEqualTo(birthDate);
        assertThat(clientDto.getAddress()).isEqualTo(address);
    }

    @Test
    @DisplayName("Should create a ClientDTO using factory method of() with optional values")
    void shouldCreateClientDtoUsingFactoryOfWithOptionalValues() {
        // Act (Act)
        ClientDTO clientWithOptionals = ClientDTO.of(
                id, Optional.of(firstName), Optional.of(lastName), Optional.of(dni),
                Optional.of(email),
                Optional.of(phone),
                Optional.of(birthDate),
                Optional.of(address)
        );

        // Assert (Verify)
        assertThat(clientWithOptionals).isNotNull();
        assertThat(clientWithOptionals.getId()).isEqualTo(id);
        assertThat(clientWithOptionals.getFirstName()).isEqualTo(firstName);
        assertThat(clientWithOptionals.getLastName()).isEqualTo(lastName);
        assertThat(clientWithOptionals.getDni()).isEqualTo(dni);
        assertThat(clientWithOptionals.getEmail()).isEqualTo(email);
        assertThat(clientWithOptionals.getPhone()).isEqualTo(phone);
        assertThat(clientWithOptionals.getBirthDate()).isEqualTo(birthDate);
        assertThat(clientWithOptionals.getAddress()).isEqualTo(address);
    }

    @Test
    @DisplayName("Should create a ClientDTO using factory method of() with null values")
    void shouldCreateClientDtoUsingFactoryOfWithNullValues() {
        // Act (Act)
        ClientDTO clientWithNulls = ClientDTO.of(
                id, Optional.of(firstName), Optional.of(lastName), Optional.of(dni),
                Optional.empty(), // email
                Optional.empty(), // telefono
                Optional.empty(), // fechaNacimiento
                Optional.empty()  // direccion
        );

        // Assert (Verify)
        assertThat(clientWithNulls).isNotNull();
        assertThat(clientWithNulls.getId()).isEqualTo(id);
        assertThat(clientWithNulls.getFirstName()).isEqualTo(firstName);
        assertThat(clientWithNulls.getLastName()).isEqualTo(lastName);
        assertThat(clientWithNulls.getDni()).isEqualTo(dni);
        assertThat(clientWithNulls.getEmail()).isNull();
        assertThat(clientWithNulls.getPhone()).isNull();
        assertThat(clientWithNulls.getBirthDate()).isNull();
        assertThat(clientWithNulls.getAddress()).isNull();
    }

    @Test
    @DisplayName("Should create a ClientDTO using factory method of() with mixed values")
    void shouldCreateClientDtoUsingFactoryOfWithMixedValues() {
        // Act (Act)
        ClientDTO clientWithMixedValues = ClientDTO.of(
                id, Optional.of(firstName), Optional.of(lastName), Optional.of(dni),
                Optional.of(email),        // email con valor
                Optional.empty(),          // telefono vacío
                Optional.of(birthDate), // fechaNacimiento con valor
                Optional.empty()           // direccion vacía
        );

        // Assert (Verify)
        assertThat(clientWithMixedValues).isNotNull();
        assertThat(clientWithMixedValues.getId()).isEqualTo(id);
        assertThat(clientWithMixedValues.getFirstName()).isEqualTo(firstName);
        assertThat(clientWithMixedValues.getLastName()).isEqualTo(lastName);
        assertThat(clientWithMixedValues.getDni()).isEqualTo(dni);
        assertThat(clientWithMixedValues.getEmail()).isEqualTo(email);
        assertThat(clientWithMixedValues.getPhone()).isNull();
        assertThat(clientWithMixedValues.getBirthDate()).isEqualTo(birthDate);
        assertThat(clientWithMixedValues.getAddress()).isNull();
    }

    @Test
    @DisplayName("Should update the email immutably using withEmail()")
    void shouldUpdateEmailImmutablyUsingWithEmail() {
        // Arrange (Prepare)
        String newEmail = "nuevo.email@test.com";

        // Act (Act)
        ClientDTO updatedClient = clientDto.withEmail(newEmail);

        // Assert (Verify)
        assertThat(updatedClient).isNotNull();
        assertThat(updatedClient.getEmail()).isEqualTo(newEmail);
        assertThat(updatedClient.getId()).isEqualTo(clientDto.getId());
        assertThat(updatedClient.getFirstName()).isEqualTo(clientDto.getFirstName());
        assertThat(updatedClient.getLastName()).isEqualTo(clientDto.getLastName());
        assertThat(updatedClient.getDni()).isEqualTo(clientDto.getDni());
        assertThat(updatedClient.getPhone()).isEqualTo(clientDto.getPhone());
        assertThat(updatedClient.getBirthDate()).isEqualTo(clientDto.getBirthDate());
        assertThat(updatedClient.getAddress()).isEqualTo(clientDto.getAddress());

        // Verify that the original did not change
        assertThat(clientDto.getEmail()).isEqualTo(email);
        assertThat(updatedClient).isNotSameAs(clientDto);
    }

    @Test
    @DisplayName("Should update the phone immutably using withPhone()")
    void shouldUpdatePhoneImmutablyUsingWithPhone() {
        // Arrange (Prepare)
        String newPhone = "987654321";

        // Act (Act)
        ClientDTO updatedClient = clientDto.withPhone(newPhone);

        // Assert (Verify)
        assertThat(updatedClient).isNotNull();
        assertThat(updatedClient.getPhone()).isEqualTo(newPhone);
        assertThat(updatedClient.getId()).isEqualTo(clientDto.getId());
        assertThat(updatedClient.getFirstName()).isEqualTo(clientDto.getFirstName());
        assertThat(updatedClient.getLastName()).isEqualTo(clientDto.getLastName());
        assertThat(updatedClient.getDni()).isEqualTo(clientDto.getDni());
        assertThat(updatedClient.getEmail()).isEqualTo(clientDto.getEmail());
        assertThat(updatedClient.getBirthDate()).isEqualTo(clientDto.getBirthDate());
        assertThat(updatedClient.getAddress()).isEqualTo(clientDto.getAddress());

        // Verify that the original did not change
        assertThat(clientDto.getPhone()).isEqualTo(phone);
        assertThat(updatedClient).isNotSameAs(clientDto);
    }

    @Test
    @DisplayName("Should update the address immutably using withAddress()")
    void shouldUpdateAddressImmutablyUsingWithAddress() {
        // Arrange (Prepare)
        String newAddress = "Nueva Calle 456";

        // Act (Act)
        ClientDTO updatedClient = clientDto.withAddress(newAddress);

        // Assert (Verify)
        assertThat(updatedClient).isNotNull();
        assertThat(updatedClient.getAddress()).isEqualTo(newAddress);
        assertThat(updatedClient.getId()).isEqualTo(clientDto.getId());
        assertThat(updatedClient.getFirstName()).isEqualTo(clientDto.getFirstName());
        assertThat(updatedClient.getLastName()).isEqualTo(clientDto.getLastName());
        assertThat(updatedClient.getDni()).isEqualTo(clientDto.getDni());
        assertThat(updatedClient.getEmail()).isEqualTo(clientDto.getEmail());
        assertThat(updatedClient.getPhone()).isEqualTo(clientDto.getPhone());
        assertThat(updatedClient.getBirthDate()).isEqualTo(clientDto.getBirthDate());

        // Verify that the original did not change
        assertThat(clientDto.getAddress()).isEqualTo(address);
        assertThat(updatedClient).isNotSameAs(clientDto);
    }

    @Test
    @DisplayName("Should update multiple contact fields using withContactData()")
    void shouldUpdateMultipleContactFieldsUsingWithContactData() {
        // Arrange (Prepare)
        String newEmail = "nuevo.email@test.com";
        String newPhone = "987654321";
        String newAddress = "Nueva Calle 456";

        // Act (Act)
        ClientDTO updatedClient = clientDto.withContactData(
                Optional.of(newEmail),     // nuevo email
                Optional.of(newPhone),  // nuevo teléfono
                Optional.of(newAddress)  // nueva dirección
        );

        // Assert (Verify)
        assertThat(updatedClient).isNotNull();
        assertThat(updatedClient.getEmail()).isEqualTo(newEmail);
        assertThat(updatedClient.getPhone()).isEqualTo(newPhone);
        assertThat(updatedClient.getAddress()).isEqualTo(newAddress);
        assertThat(updatedClient.getId()).isEqualTo(clientDto.getId());
        assertThat(updatedClient.getFirstName()).isEqualTo(clientDto.getFirstName());
        assertThat(updatedClient.getLastName()).isEqualTo(clientDto.getLastName());
        assertThat(updatedClient.getDni()).isEqualTo(clientDto.getDni());
        assertThat(updatedClient.getBirthDate()).isEqualTo(clientDto.getBirthDate());

        // Verify that the original did not change
        assertThat(clientDto.getEmail()).isEqualTo(email);
        assertThat(clientDto.getPhone()).isEqualTo(phone);
        assertThat(clientDto.getAddress()).isEqualTo(address);
        assertThat(updatedClient).isNotSameAs(clientDto);
    }

    @Test
    @DisplayName("Should maintain original values when passing empty Optionals in withContactData()")
    void shouldMaintainOriginalValuesWhenPassingEmptyOptionalsInWithContactData() {
        // Act (Act)
        ClientDTO updatedClient = clientDto.withContactData(
                Optional.empty(), // mantener email original
                Optional.empty(), // mantener teléfono original
                Optional.empty()  // mantener dirección original
        );

        // Assert (Verify)
        assertThat(updatedClient).isNotNull();
        assertThat(updatedClient.getEmail()).isEqualTo(clientDto.getEmail());
        assertThat(updatedClient.getPhone()).isEqualTo(clientDto.getPhone());
        assertThat(updatedClient.getAddress()).isEqualTo(clientDto.getAddress());
        assertThat(updatedClient.getId()).isEqualTo(clientDto.getId());
        assertThat(updatedClient.getFirstName()).isEqualTo(clientDto.getFirstName());
        assertThat(updatedClient.getLastName()).isEqualTo(clientDto.getLastName());
        assertThat(updatedClient.getDni()).isEqualTo(clientDto.getDni());
        assertThat(updatedClient.getBirthDate()).isEqualTo(clientDto.getBirthDate());
    }

    @Test
    @DisplayName("Should update only some fields when passing mixed Optionals in withContactData()")
    void shouldUpdateOnlySomeFieldsWhenPassingMixedOptionalsInWithContactData() {
        // Arrange (Prepare)
        String newEmail = "nuevo.email@test.com";
        String newAddress = "Nueva Calle 456";

        // Act (Act)
        ClientDTO updatedClient = clientDto.withContactData(
                Optional.of(newEmail),     // actualizar email
                Optional.empty(),            // mantener teléfono original
                Optional.of(newAddress)  // actualizar dirección
        );

        // Assert (Verify)
        assertThat(updatedClient).isNotNull();
        assertThat(updatedClient.getEmail()).isEqualTo(newEmail);
        assertThat(updatedClient.getPhone()).isEqualTo(clientDto.getPhone()); // Mantiene original
        assertThat(updatedClient.getAddress()).isEqualTo(newAddress);
        assertThat(updatedClient.getId()).isEqualTo(clientDto.getId());
        assertThat(updatedClient.getFirstName()).isEqualTo(clientDto.getFirstName());
        assertThat(updatedClient.getLastName()).isEqualTo(clientDto.getLastName());
        assertThat(updatedClient.getDni()).isEqualTo(clientDto.getDni());
        assertThat(updatedClient.getBirthDate()).isEqualTo(clientDto.getBirthDate());
    }

    @Test
    @DisplayName("Should handle null values in optional fields")
    void shouldHandleNullValuesInOptionalFields() {
        // Act (Act)
        ClientDTO clientWithNulls = ClientDTO.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .dni(dni)
                .email(null)
                .phone(null)
                .birthDate(null)
                .address(null)
                .build();

        // Assert (Verify)
        assertThat(clientWithNulls).isNotNull();
        assertThat(clientWithNulls.getId()).isEqualTo(id);
        assertThat(clientWithNulls.getFirstName()).isEqualTo(firstName);
        assertThat(clientWithNulls.getLastName()).isEqualTo(lastName);
        assertThat(clientWithNulls.getDni()).isEqualTo(dni);
        assertThat(clientWithNulls.getEmail()).isNull();
        assertThat(clientWithNulls.getPhone()).isNull();
        assertThat(clientWithNulls.getBirthDate()).isNull();
        assertThat(clientWithNulls.getAddress()).isNull();
    }

    @Test
    @DisplayName("Should handle different types of birth dates")
    void shouldHandleDifferentTypesOfBirthDates() {
        // Arrange (Prepare)
        LocalDate pastDate = LocalDate.of(1980, 1, 1);
        LocalDate futureDate = LocalDate.of(2030, 12, 31);
        LocalDate currentDate = LocalDate.now();

        // Act (Act)
        ClientDTO clientPast = ClientDTO.builder()
                .id(1L)
                .firstName("Client")
                .lastName("Past")
                .dni("11111111A")
                .birthDate(pastDate)
                .build();

        ClientDTO clientFuture = ClientDTO.builder()
                .id(2L)
                .firstName("Client")
                .lastName("Future")
                .dni("22222222B")
                .birthDate(futureDate)
                .build();

        ClientDTO clientCurrent = ClientDTO.builder()
                .id(3L)
                .firstName("Client")
                .lastName("Current")
                .dni("33333333C")
                .birthDate(currentDate)
                .build();

        // Assert (Verify)
        assertThat(clientPast.getBirthDate()).isEqualTo(pastDate);
        assertThat(clientFuture.getBirthDate()).isEqualTo(futureDate);
        assertThat(clientCurrent.getBirthDate()).isEqualTo(currentDate);
    }

    @Test
    @DisplayName("Should maintain immutability in all fields")
    void shouldMaintainImmutabilityInAllFields() {
        // Arrange (Prepare)
        ClientDTO originalClient = clientDto;

        // Act (Act) - Create a new client with different data
        ClientDTO newClient = ClientDTO.builder()
                .id(999L)
                .firstName("New")
                .lastName("Client")
                .dni("99999999Z")
                .email("nuevo@test.com")
                .phone("999999999")
                .birthDate(LocalDate.of(2000, 1, 1))
                .address("New Address")
                .build();

        // Assert (Verify) - Original should not change
        assertThat(originalClient.getId()).isEqualTo(id);
        assertThat(originalClient.getFirstName()).isEqualTo(firstName);
        assertThat(originalClient.getLastName()).isEqualTo(lastName);
        assertThat(originalClient.getDni()).isEqualTo(dni);
        assertThat(originalClient.getEmail()).isEqualTo(email);
        assertThat(originalClient.getPhone()).isEqualTo(phone);
        assertThat(originalClient.getBirthDate()).isEqualTo(birthDate);
        assertThat(originalClient.getAddress()).isEqualTo(address);

        // Verify that they are different objects
        assertThat(newClient).isNotSameAs(originalClient);
        assertThat(newClient.getId()).isNotEqualTo(originalClient.getId());
    }
}
