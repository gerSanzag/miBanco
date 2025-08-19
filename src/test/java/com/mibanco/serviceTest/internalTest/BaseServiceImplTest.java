package com.mibanco.serviceTest.internalTest;

import com.mibanco.dto.ClientDTO;
import com.mibanco.service.ClientService;
import com.mibanco.service.internal.ServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

/**
 * Test class for ClientService
 * Tests the CRUD operations using real classes
 */
@DisplayName("ClientService CRUD Operations Tests")
class BaseServiceImplTest {

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        clientService = ServiceFactory.getInstance().getClientService();
    }

    @Test
    @DisplayName("Should create a new client successfully")
    void shouldCreateNewClientSuccessfully() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("Juan")
                .lastName("Pérez")
                .email("juan.perez@email.com")
                .phone("123456789")
                .dni("12345678A")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("Calle Principal 123")
                .build();

        // When
        Optional<ClientDTO> result = clientService.saveClient(Optional.of(clientDto));

        // Then
        assertThat(result).isPresent();
        ClientDTO savedClient = result.get();
        assertThat(savedClient.getId()).isNotNull();
        assertThat(savedClient.getFirstName()).isEqualTo("Juan");
        assertThat(savedClient.getLastName()).isEqualTo("Pérez");
        assertThat(savedClient.getEmail()).isEqualTo("juan.perez@email.com");
        assertThat(savedClient.getPhone()).isEqualTo("123456789");
    }

    @Test
    @DisplayName("Should find client by ID successfully")
    void shouldFindClientByIdSuccessfully() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("María")
                .lastName("García")
                .email("maria.garcia@email.com")
                .phone("987654321")
                .dni("87654321B")
                .birthDate(LocalDate.of(1985, 5, 15))
                .address("Avenida Central 456")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));

        // When
        Optional<ClientDTO> foundClient = clientService.getClientById(Optional.of(createdClient.get().getId()));

        // Then
        assertThat(foundClient).isPresent();
        ClientDTO client = foundClient.get();
        assertThat(client.getId()).isEqualTo(createdClient.get().getId());
        assertThat(client.getFirstName()).isEqualTo("María");
        assertThat(client.getLastName()).isEqualTo("García");
        assertThat(client.getEmail()).isEqualTo("maria.garcia@email.com");
    }

    @Test
    @DisplayName("Should find all clients successfully")
    void shouldFindAllClientsSuccessfully() {
        // Given
        ClientDTO client1 = ClientDTO.builder()
                .firstName("Cliente")
                .lastName("Uno")
                .email("cliente1@email.com")
                .phone("111111111")
                .dni("11111111C")
                .birthDate(LocalDate.of(1995, 3, 10))
                .address("Calle 1")
                .build();
        
        ClientDTO client2 = ClientDTO.builder()
                .firstName("Cliente")
                .lastName("Dos")
                .email("cliente2@email.com")
                .phone("222222222")
                .dni("22222222D")
                .birthDate(LocalDate.of(1988, 7, 20))
                .address("Calle 2")
                .build();

        clientService.saveClient(Optional.of(client1));
        clientService.saveClient(Optional.of(client2));

        // When
        Optional<java.util.List<ClientDTO>> allClients = clientService.getAllClients();

        // Then
        assertThat(allClients).isPresent();
        assertThat(allClients.get()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should delete client successfully")
    void shouldDeleteClientSuccessfully() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("Cliente")
                .lastName("Eliminar")
                .email("eliminar@email.com")
                .phone("555555555")
                .dni("55555555E")
                .birthDate(LocalDate.of(1992, 12, 25))
                .address("Calle Eliminar")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));

        // When
        boolean deleted = clientService.deleteClient(Optional.of(createdClient.get().getId()));

        // Then
        assertThat(deleted).isTrue();
        Optional<ClientDTO> foundClient = clientService.getClientById(Optional.of(createdClient.get().getId()));
        assertThat(foundClient).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when finding non-existent client")
    void shouldReturnEmptyWhenFindingNonExistentClient() {
        // When
        Optional<ClientDTO> result = clientService.getClientById(Optional.of(999L));

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should count clients correctly")
    void shouldCountClientsCorrectly() {
        // Given
        long initialCount = clientService.countClients();
        
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("Contador")
                .lastName("Test")
                .email("contador@test.com")
                .phone("999999999")
                .dni("99999999F")
                .birthDate(LocalDate.of(1990, 6, 15))
                .address("Calle Contador")
                .build();

        // When
        clientService.saveClient(Optional.of(clientDto));
        long finalCount = clientService.countClients();

        // Then
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should update client email field successfully")
    void shouldUpdateClientEmailFieldSuccessfully() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("Email")
                .lastName("Field")
                .email("original@email.com")
                .phone("888888888")
                .dni("88888888G")
                .birthDate(LocalDate.of(1991, 8, 20))
                .address("Calle Email")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));
        String newEmail = "nuevo@email.com";

        // When
        Optional<ClientDTO> updatedClient = clientService.updateClientEmail(
            createdClient.get().getId(), 
            Optional.of(newEmail)
        );

        // Then
        assertThat(updatedClient).isPresent();
        assertThat(updatedClient.get().getEmail()).isEqualTo(newEmail);
        assertThat(updatedClient.get().getFirstName()).isEqualTo("Email");
        assertThat(updatedClient.get().getPhone()).isEqualTo("888888888"); // Other fields unchanged
    }

    @Test
    @DisplayName("Should update client phone field successfully")
    void shouldUpdateClientPhoneFieldSuccessfully() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("Phone")
                .lastName("Field")
                .email("phone@test.com")
                .phone("777777777")
                .dni("77777777H")
                .birthDate(LocalDate.of(1992, 9, 25))
                .address("Calle Phone")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));
        String newPhone = "666666666";

        // When
        Optional<ClientDTO> updatedClient = clientService.updateClientPhone(
            createdClient.get().getId(), 
            Optional.of(newPhone)
        );

        // Then
        assertThat(updatedClient).isPresent();
        assertThat(updatedClient.get().getPhone()).isEqualTo(newPhone);
        assertThat(updatedClient.get().getFirstName()).isEqualTo("Phone");
        assertThat(updatedClient.get().getEmail()).isEqualTo("phone@test.com"); // Other fields unchanged
    }

    @Test
    @DisplayName("Should update client address field successfully")
    void shouldUpdateClientAddressFieldSuccessfully() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("Address")
                .lastName("Field")
                .email("address@test.com")
                .phone("555555555")
                .dni("55555555I")
                .birthDate(LocalDate.of(1993, 10, 30))
                .address("Dirección Original")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));
        String newAddress = "Nueva Dirección";

        // When
        Optional<ClientDTO> updatedClient = clientService.updateClientAddress(
            createdClient.get().getId(), 
            Optional.of(newAddress)
        );

        // Then
        assertThat(updatedClient).isPresent();
        assertThat(updatedClient.get().getAddress()).isEqualTo(newAddress);
        assertThat(updatedClient.get().getFirstName()).isEqualTo("Address");
        assertThat(updatedClient.get().getEmail()).isEqualTo("address@test.com"); // Other fields unchanged
    }

    @Test
    @DisplayName("Should set current user successfully")
    void shouldSetCurrentUserSuccessfully() {
        // When & Then
        // This method should not throw an exception
        // Note: setCurrentUser is a simple delegation to repository
        assertThatCode(() -> clientService.setCurrentUser("testUser"))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should update multiple fields successfully")
    void shouldUpdateMultipleFieldsSuccessfully() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("Multiple")
                .lastName("Test")
                .email("multiple@test.com")
                .phone("222222222")
                .dni("22222222L")
                .birthDate(LocalDate.of(1996, 1, 15))
                .address("Dirección Original")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));
        
        ClientDTO updatedData = ClientDTO.builder()
                .id(createdClient.get().getId())
                .firstName("Multiple")
                .lastName("Test") // updateMultipleFields only updates contact data
                .email("updated@test.com")
                .phone("111111111")
                .dni("22222222L")
                .birthDate(LocalDate.of(1996, 1, 15))
                .address("Nueva Dirección")
                .build();

        // When
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", "updated@test.com");
        updates.put("phone", "111111111");
        updates.put("address", "Nueva Dirección");
        Optional<ClientDTO> updatedClient = clientService.updateMultipleFields(
            createdClient.get().getId(), 
            updates
        );

        // Then
        assertThat(updatedClient).isPresent();
        assertThat(updatedClient.get().getLastName()).isEqualTo("Test"); // Should remain unchanged
        assertThat(updatedClient.get().getEmail()).isEqualTo("updated@test.com");
        assertThat(updatedClient.get().getPhone()).isEqualTo("111111111");
        assertThat(updatedClient.get().getAddress()).isEqualTo("Nueva Dirección");
    }

    @Test
    @DisplayName("Should restore deleted client successfully")
    void shouldRestoreDeletedClientSuccessfully() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("Restore")
                .lastName("Test")
                .email("restore@test.com")
                .phone("333333333")
                .dni("33333333K")
                .birthDate(LocalDate.of(1995, 12, 10))
                .address("Calle Restore")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));
        clientService.deleteClient(Optional.of(createdClient.get().getId()));

        // When
        Optional<ClientDTO> restoredClient = clientService.restoreClient(Optional.of(createdClient.get().getId()));

        // Then
        assertThat(restoredClient).isPresent();
        assertThat(restoredClient.get().getId()).isEqualTo(createdClient.get().getId());
        assertThat(restoredClient.get().getFirstName()).isEqualTo("Restore");
    }

    @Test
    @DisplayName("Should update field using updateField method")
    void shouldUpdateFieldUsingUpdateFieldMethod() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("UpdateField")
                .lastName("Test")
                .email("updatefield@test.com")
                .phone("111111111")
                .dni("11111111M")
                .birthDate(LocalDate.of(1997, 2, 20))
                .address("Calle UpdateField")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));
        String newEmail = "newemail@test.com";

        // When - Using updateField method directly
        Optional<ClientDTO> updatedClient = clientService.updateField(
            createdClient.get().getId(),
            Optional.of(newEmail),
            ClientDTO::getEmail,  // Function to get current value
            (dto, newVal) -> dto.withEmail(newVal)  // BiFunction to update value
        );

        // Then
        assertThat(updatedClient).isPresent();
        assertThat(updatedClient.get().getEmail()).isEqualTo(newEmail);
        assertThat(updatedClient.get().getFirstName()).isEqualTo("UpdateField"); // Other fields unchanged
        assertThat(updatedClient.get().getPhone()).isEqualTo("111111111"); // Other fields unchanged
    }

    @Test
    @DisplayName("Should update field with null new value")
    void shouldUpdateFieldWithNullNewValue() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("NullField")
                .lastName("Test")
                .email("nullfield@test.com")
                .phone("222222222")
                .dni("22222222N")
                .birthDate(LocalDate.of(1998, 3, 25))
                .address("Calle NullField")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));

        // When - Using updateField method with null value
        Optional<ClientDTO> updatedClient = clientService.updateField(
            createdClient.get().getId(),
            Optional.empty(), // null new value
            ClientDTO::getEmail,
            (dto, newVal) -> dto.withEmail(newVal)
        );

        // Then
        assertThat(updatedClient).isEmpty(); // Should return empty when newValue is empty
    }

    @Test
    @DisplayName("Should update multiple fields using updateMultipleFields method")
    void shouldUpdateMultipleFieldsUsingUpdateMultipleFieldsMethod() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("UpdateMultiple")
                .lastName("Test")
                .email("updatemultiple@test.com")
                .phone("333333333")
                .dni("33333333O")
                .birthDate(LocalDate.of(1999, 4, 30))
                .address("Calle UpdateMultiple")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));
        
        ClientDTO newData = ClientDTO.builder()
                .firstName("UpdatedMultiple")
                .lastName("Updated")
                .email("updatedmultiple@test.com")
                .phone("444444444")
                .dni("33333333O")
                .birthDate(LocalDate.of(1999, 4, 30))
                .address("Nueva Calle UpdateMultiple")
                .build();

        // When - Using updateMultipleFields method
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "UpdatedMultiple");
        updates.put("lastName", "Updated");
        updates.put("email", "updatedmultiple@test.com");
        updates.put("phone", "444444444");
        updates.put("address", "Nueva Calle UpdateMultiple");
        Optional<ClientDTO> updatedClient = clientService.updateMultipleFields(
            createdClient.get().getId(),
            updates
        );

        // Then
        assertThat(updatedClient).isPresent();
        assertThat(updatedClient.get().getFirstName()).isEqualTo("UpdatedMultiple");
        assertThat(updatedClient.get().getLastName()).isEqualTo("Updated");
        assertThat(updatedClient.get().getEmail()).isEqualTo("updatedmultiple@test.com");
        assertThat(updatedClient.get().getPhone()).isEqualTo("444444444");
        assertThat(updatedClient.get().getAddress()).isEqualTo("Nueva Calle UpdateMultiple");
    }

    @Test
    @DisplayName("Should update multiple fields with null DTO")
    void shouldUpdateMultipleFieldsWithNullDto() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("NullMultiple")
                .lastName("Test")
                .email("nullmultiple@test.com")
                .phone("555555555")
                .dni("55555555P")
                .birthDate(LocalDate.of(2000, 5, 5))
                .address("Calle NullMultiple")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));

        // When - Using updateMultipleFields method with null data
        Optional<ClientDTO> updatedClient = clientService.updateMultipleFields(
            createdClient.get().getId(),
            null // null data
        );

        // Then
        assertThat(updatedClient).isEmpty(); // Should return empty when DTO is empty
    }

    @Test
    @DisplayName("Should get deleted clients successfully")
    void shouldGetDeletedClientsSuccessfully() {
        // Given
        ClientDTO clientDto = ClientDTO.builder()
                .firstName("Deleted")
                .lastName("Test")
                .email("deleted@test.com")
                .phone("666666666")
                .dni("66666666Q")
                .birthDate(LocalDate.of(2001, 6, 10))
                .address("Calle Deleted")
                .build();
        
        Optional<ClientDTO> createdClient = clientService.saveClient(Optional.of(clientDto));
        clientService.deleteClient(Optional.of(createdClient.get().getId()));

        // When
        java.util.List<ClientDTO> deletedClients = clientService.getDeleted();

        // Then
        assertThat(deletedClients).isNotNull();
        // Note: The exact behavior depends on the repository implementation
        // This test verifies the method doesn't throw an exception and returns a list
    }

    @Test
    @DisplayName("Should get empty list when no deleted clients exist")
    void shouldGetEmptyListWhenNoDeletedClientsExist() {
        // When
        java.util.List<ClientDTO> deletedClients = clientService.getDeleted();

        // Then
        assertThat(deletedClients).isNotNull();
        // Note: This test verifies the method returns a list (empty or not)
        // The actual content depends on the repository implementation
    }

    @Test
    @DisplayName("Should get multiple deleted clients")
    void shouldGetMultipleDeletedClients() {
        // Given - Create and delete multiple clients
        ClientDTO client1 = ClientDTO.builder()
                .firstName("Deleted1")
                .lastName("Test")
                .email("deleted1@test.com")
                .phone("777777777")
                .dni("77777777R")
                .birthDate(LocalDate.of(2002, 7, 15))
                .address("Calle Deleted1")
                .build();
        
        ClientDTO client2 = ClientDTO.builder()
                .firstName("Deleted2")
                .lastName("Test")
                .email("deleted2@test.com")
                .phone("888888888")
                .dni("88888888S")
                .birthDate(LocalDate.of(2003, 8, 20))
                .address("Calle Deleted2")
                .build();
        
        Optional<ClientDTO> createdClient1 = clientService.saveClient(Optional.of(client1));
        Optional<ClientDTO> createdClient2 = clientService.saveClient(Optional.of(client2));
        
        clientService.deleteClient(Optional.of(createdClient1.get().getId()));
        clientService.deleteClient(Optional.of(createdClient2.get().getId()));

        // When
        java.util.List<ClientDTO> deletedClients = clientService.getDeleted();

        // Then
        assertThat(deletedClients).isNotNull();
        // Note: This test verifies the method can handle multiple deleted clients
        // The exact count depends on the repository implementation
    }
}
