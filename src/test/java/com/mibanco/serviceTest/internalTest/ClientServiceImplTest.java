package com.mibanco.serviceTest.internalTest;

import com.mibanco.dto.ClientDTO;
import com.mibanco.model.Client;
import com.mibanco.model.enums.AccountType;
import com.mibanco.service.ClientService;
import com.mibanco.service.internal.ServiceFactory;
import com.mibanco.TestClientHelper;import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ClientServiceImpl Tests")
class ClientServiceImplTest {

    private ClientService clientService;
    private TestClientHelper testClientHelper;
    @BeforeEach
    void setUp() {
        clientService = ServiceFactory.getInstance().getClientService();
        testClientHelper = new TestClientHelper(clientService);    }

    @Nested
    @DisplayName("Create Client Tests")
    class CreateClientTests {

        @Test
        @DisplayName("Should create client successfully")
        void shouldCreateClientSuccessfully() {
            // Given
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("birthDate", "1990-01-01");
            clientData.put("email", "john@test.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Test Address 123");

            // When
            Optional<ClientDTO> result = clientService.createClientDto(clientData);

            // Then
            assertThat(result).isPresent();
            ClientDTO createdClient = result.get();
            assertThat(createdClient.getFirstName()).isEqualTo("John");
            assertThat(createdClient.getLastName()).isEqualTo("Doe");
            assertThat(createdClient.getDni()).isEqualTo("12345678");
            assertThat(createdClient.getEmail()).isEqualTo("john@test.com");
            assertThat(createdClient.getPhone()).isEqualTo("123456789");
            assertThat(createdClient.getAddress()).isEqualTo("Test Address 123");
        }

        @Test
        @DisplayName("Should create client with null DNI successfully")
        void shouldCreateClientWithNullDniSuccessfully() {
            // Given
            ClientDTO clientDto = ClientDTO.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .dni(null) // DNI null para cubrir el branch en validateUniqueDni
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .email("john@test.com")
                    .phone("123456789")
                    .address("Test Address 123")
                    .build();

            // When
            Optional<ClientDTO> result = testClientHelper.createTestClient(clientDto);

            // Then
            assertThat(result).isPresent();
            ClientDTO savedClient = result.get();
            assertThat(savedClient.getFirstName()).isEqualTo("John");
            assertThat(savedClient.getLastName()).isEqualTo("Doe");
            assertThat(savedClient.getDni()).isNull();
        }

        @Test
        @DisplayName("Should return empty when creating client with invalid data")
        void shouldReturnEmptyWhenCreatingClientWithInvalidData() {
            // Given - Invalid data (missing required fields)
            Map<String, String> invalidClientData = new HashMap<>();
            invalidClientData.put("firstName", ""); // Empty firstName
            invalidClientData.put("lastName", "Doe");
            invalidClientData.put("dni", "12345678");
            invalidClientData.put("birthDate", "1990-01-01");
            invalidClientData.put("email", "john@test.com");
            invalidClientData.put("phone", "123456789");
            invalidClientData.put("address", "Test Address 123");

            // When
            Optional<ClientDTO> result = clientService.createClientDto(invalidClientData);

            // Then - Should return empty due to validation failure
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when creating client with invalid birth date")
        void shouldReturnEmptyWhenCreatingClientWithInvalidBirthDate() {
            // Given - Invalid birth date format
            Map<String, String> invalidClientData = new HashMap<>();
            invalidClientData.put("firstName", "John");
            invalidClientData.put("lastName", "Doe");
            invalidClientData.put("dni", "12345678");
            invalidClientData.put("birthDate", "invalid-date"); // Invalid date format
            invalidClientData.put("email", "john@test.com");
            invalidClientData.put("phone", "123456789");
            invalidClientData.put("address", "Test Address 123");

            // When
            Optional<ClientDTO> result = clientService.createClientDto(invalidClientData);

            // Then - Should return empty due to date parsing failure
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when creating client with null data")
        void shouldReturnEmptyWhenCreatingClientWithNullData() {
            // Given - Null data
            Map<String, String> nullClientData = null;

            // When
            Optional<ClientDTO> result = clientService.createClientDto(nullClientData);

            // Then - Should return empty due to null data
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when creating client with duplicate DNI")
        void shouldReturnEmptyWhenCreatingClientWithDuplicateDni() {
            // Given - First client with valid data
            Map<String, String> clientData1 = new HashMap<>();
            clientData1.put("firstName", "John");
            clientData1.put("lastName", "Doe");
            clientData1.put("dni", "11111111"); // Unique DNI
            clientData1.put("birthDate", "1990-01-01");
            clientData1.put("email", "john@test.com");
            clientData1.put("phone", "123456789");
            clientData1.put("address", "Test Address 123");

            // Given - Second client with same DNI
            Map<String, String> clientData2 = new HashMap<>();
            clientData2.put("firstName", "Jane");
            clientData2.put("lastName", "Smith");
            clientData2.put("dni", "11111111"); // Same DNI as first client
            clientData2.put("birthDate", "1992-02-02");
            clientData2.put("email", "jane@test.com");
            clientData2.put("phone", "987654321");
            clientData2.put("address", "Test Address 456");

            // When - Create first client
            Optional<ClientDTO> firstResult = clientService.createClientDto(clientData1);
            
            // Then - First client should be created successfully
            assertThat(firstResult).isPresent();
            assertThat(firstResult.get().getDni()).isEqualTo("11111111");

            // When - Try to create second client with same DNI
            Optional<ClientDTO> secondResult = clientService.createClientDto(clientData2);

            // Then - Second client should return empty due to ValidationException being caught
            assertThat(secondResult).isEmpty();
        }

        @Test
        @DisplayName("Should create client successfully with unique DNI")
        void shouldCreateClientSuccessfullyWithUniqueDni() {
            // Given - Client with unique DNI (not in database)
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "Alice");
            clientData.put("lastName", "Johnson");
            clientData.put("dni", "99999999"); // Unique DNI that doesn't exist
            clientData.put("birthDate", "1985-03-15");
            clientData.put("email", "alice@test.com");
            clientData.put("phone", "555555555");
            clientData.put("address", "Unique Address 789");

            // When
            Optional<ClientDTO> result = clientService.createClientDto(clientData);

            // Then - Should be created successfully
            assertThat(result).isPresent();
            ClientDTO createdClient = result.get();
            assertThat(createdClient.getFirstName()).isEqualTo("Alice");
            assertThat(createdClient.getLastName()).isEqualTo("Johnson");
            assertThat(createdClient.getDni()).isEqualTo("99999999");
            assertThat(createdClient.getEmail()).isEqualTo("alice@test.com");
        }

        @Test
        @DisplayName("Should handle validateUniqueDni with null DNI")
        void shouldHandleValidateUniqueDniWithNullDni() {
            // Given - Client DTO with null DNI
            ClientDTO clientDto = ClientDTO.builder()
                    .firstName("Test")
                    .lastName("User")
                    .dni(null) // Null DNI to test the first if
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .email("test@test.com")
                    .phone("123456789")
                    .address("Test Address")
                    .build();

            // When - Call validateUniqueDni directly using reflection
            try {
                java.lang.reflect.Method validateMethod = clientService.getClass()
                        .getDeclaredMethod("validateUniqueDni", ClientDTO.class);
                validateMethod.setAccessible(true);
                validateMethod.invoke(clientService, clientDto);
                
                // Then - Should not throw exception (DNI is null, so first if is false)
                // If we reach here, the method executed successfully
                assertThat(clientDto.getDni()).isNull(); // Verify DNI is null
            } catch (Exception e) {
                throw new RuntimeException("Error calling validateUniqueDni", e);
            }
        }

        @Test
        @DisplayName("Should save client with null DNI using saveClient")
        void shouldSaveClientWithNullDniUsingSaveClient() {
            // Given - Client DTO with null DNI (bypassing processClientDto validation)
            ClientDTO clientDto = ClientDTO.builder()
                    .firstName("Test")
                    .lastName("User")
                    .dni(null) // Null DNI
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .email("test@test.com")
                    .phone("123456789")
                    .address("Test Address")
                    .build();

            // When - Use saveClient which doesn't call validateUniqueDni
            Optional<ClientDTO> result = testClientHelper.createTestClient(clientDto);

            // Then - Should be saved successfully
            assertThat(result).isPresent();
            ClientDTO savedClient = result.get();
            assertThat(savedClient.getDni()).isNull();
        }
    }

    @Nested
    @DisplayName("Retrieve Client Tests")
    class RetrieveClientTests {

        @Test
        @DisplayName("Should get client by ID successfully")
        void shouldGetClientByIdSuccessfully() {
            // Given
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("birthDate", "1990-01-01");
            clientData.put("email", "john@test.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Test Address 123");

            Optional<ClientDTO> createdClient = clientService.createClientDto(clientData);
            Long clientId = createdClient.get().getId();

            // When
            Optional<ClientDTO> result = clientService.getClientById(Optional.of(clientId));

            // Then
            assertThat(result).isPresent();
            ClientDTO foundClient = result.get();
            assertThat(foundClient.getId()).isEqualTo(clientId);
            assertThat(foundClient.getFirstName()).isEqualTo("John");
            assertThat(foundClient.getLastName()).isEqualTo("Doe");
        }

        @Test
        @DisplayName("Should return empty when getting non-existent client by ID")
        void shouldReturnEmptyWhenGettingNonExistentClientById() {
            // When
            Optional<ClientDTO> result = clientService.getClientById(Optional.of(999L));

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should get client by DNI successfully")
        void shouldGetClientByDniSuccessfully() {
            // Given
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("birthDate", "1990-01-01");
            clientData.put("email", "john@test.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Test Address 123");

            clientService.createClientDto(clientData);

            // When
            Optional<ClientDTO> result = clientService.getClientByDni(Optional.of("12345678"));

            // Then
            assertThat(result).isPresent();
            ClientDTO foundClient = result.get();
            assertThat(foundClient.getDni()).isEqualTo("12345678");
            assertThat(foundClient.getFirstName()).isEqualTo("John");
        }

        @Test
        @DisplayName("Should return empty when getting non-existent client by DNI")
        void shouldReturnEmptyWhenGettingNonExistentClientByDni() {
            // When
            Optional<ClientDTO> result = clientService.getClientByDni(Optional.of("99999999"));

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should get all clients successfully")
        void shouldGetAllClientsSuccessfully() {
            // Given
            Map<String, String> clientData1 = new HashMap<>();
            clientData1.put("firstName", "John");
            clientData1.put("lastName", "Doe");
            clientData1.put("dni", "12345678");
            clientData1.put("birthDate", "1990-01-01");
            clientData1.put("email", "john@test.com");
            clientData1.put("phone", "123456789");
            clientData1.put("address", "Test Address 123");

            Map<String, String> clientData2 = new HashMap<>();
            clientData2.put("firstName", "Jane");
            clientData2.put("lastName", "Smith");
            clientData2.put("dni", "87654321");
            clientData2.put("birthDate", "1992-02-02");
            clientData2.put("email", "jane@test.com");
            clientData2.put("phone", "987654321");
            clientData2.put("address", "Test Address 456");

            clientService.createClientDto(clientData1);
            clientService.createClientDto(clientData2);

            // When
            Optional<List<ClientDTO>> result = clientService.getAllClients();

            // Then
            assertThat(result).isPresent();
            List<ClientDTO> clients = result.get();
            assertThat(clients).hasSize(2);
            assertThat(clients).anyMatch(client -> client.getDni().equals("12345678"));
            assertThat(clients).anyMatch(client -> client.getDni().equals("87654321"));
        }
    }

    @Nested
    @DisplayName("Update Client Tests")
    class UpdateClientTests {

        @Test
        @DisplayName("Should update client email successfully")
        void shouldUpdateClientEmailSuccessfully() {
            // Given
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("birthDate", "1990-01-01");
            clientData.put("email", "john@test.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Test Address 123");

            Optional<ClientDTO> createdClient = clientService.createClientDto(clientData);
            Long clientId = createdClient.get().getId();

            // When
            Optional<ClientDTO> result = clientService.updateClientEmail(clientId, Optional.of("john.updated@test.com"));

            // Then
            assertThat(result).isPresent();
            ClientDTO updatedClient = result.get();
            assertThat(updatedClient.getEmail()).isEqualTo("john.updated@test.com");
            assertThat(updatedClient.getFirstName()).isEqualTo("John"); // Other fields unchanged
        }

        @Test
        @DisplayName("Should update client phone successfully")
        void shouldUpdateClientPhoneSuccessfully() {
            // Given
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("birthDate", "1990-01-01");
            clientData.put("email", "john@test.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Test Address 123");

            Optional<ClientDTO> createdClient = clientService.createClientDto(clientData);
            Long clientId = createdClient.get().getId();

            // When
            Optional<ClientDTO> result = clientService.updateClientPhone(clientId, Optional.of("987654321"));

            // Then
            assertThat(result).isPresent();
            ClientDTO updatedClient = result.get();
            assertThat(updatedClient.getPhone()).isEqualTo("987654321");
        }

        @Test
        @DisplayName("Should update client address successfully")
        void shouldUpdateClientAddressSuccessfully() {
            // Given
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("birthDate", "1990-01-01");
            clientData.put("email", "john@test.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Test Address 123");

            Optional<ClientDTO> createdClient = clientService.createClientDto(clientData);
            Long clientId = createdClient.get().getId();

            // When
            Optional<ClientDTO> result = clientService.updateClientAddress(clientId, Optional.of("New Address 456"));

            // Then
            assertThat(result).isPresent();
            ClientDTO updatedClient = result.get();
            assertThat(updatedClient.getAddress()).isEqualTo("New Address 456");
        }

        @Test
        @DisplayName("Should update multiple fields using generic method successfully")
        void shouldUpdateMultipleFieldsUsingGenericMethodSuccessfully() {
            // Given
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("birthDate", "1990-01-01");
            clientData.put("email", "john@test.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Test Address 123");

            Optional<ClientDTO> createdClient = clientService.createClientDto(clientData);
            Long clientId = createdClient.get().getId();

            Map<String, Object> updates = new HashMap<>();
            updates.put("firstName", "Johnny");
            updates.put("email", "johnny@test.com");

            // When
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("firstName", "Johnny");
            updateData.put("email", "johnny@test.com");
            Optional<ClientDTO> result = clientService.updateMultipleFields(clientId, updateData);

            // Then
            assertThat(result).isPresent();
            ClientDTO updatedClient = result.get();
            assertThat(updatedClient.getFirstName()).isEqualTo("Johnny");
            assertThat(updatedClient.getEmail()).isEqualTo("johnny@test.com");
            assertThat(updatedClient.getLastName()).isEqualTo("Doe"); // Unchanged
            assertThat(updatedClient.getId()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Delete and Restore Client Tests")
    class DeleteAndRestoreClientTests {

        @Test
        @DisplayName("Should delete client successfully")
        void shouldDeleteClientSuccessfully() {
            // Given
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("birthDate", "1990-01-01");
            clientData.put("email", "john@test.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Test Address 123");

            Optional<ClientDTO> createdClient = clientService.createClientDto(clientData);
            Long clientId = createdClient.get().getId();

            // When
            boolean result = clientService.deleteClient(Optional.of(clientId));

            // Then
            assertThat(result).isTrue();
            Optional<ClientDTO> deletedClient = clientService.getClientById(Optional.of(clientId));
            assertThat(deletedClient).isEmpty();
        }

        @Test
        @DisplayName("Should restore deleted client successfully")
        void shouldRestoreDeletedClientSuccessfully() {
            // Given
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("birthDate", "1990-01-01");
            clientData.put("email", "john@test.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Test Address 123");

            Optional<ClientDTO> createdClient = clientService.createClientDto(clientData);
            Long clientId = createdClient.get().getId();
            clientService.deleteClient(Optional.of(clientId));

            // When
            Optional<ClientDTO> result = clientService.restoreClient(Optional.of(clientId));

            // Then
            assertThat(result).isPresent();
            ClientDTO restoredClient = result.get();
            assertThat(restoredClient.getId()).isEqualTo(clientId);
            assertThat(restoredClient.getFirstName()).isEqualTo("John");
        }
    }

    @Nested
    @DisplayName("Current User and Audit Tests")
    class CurrentUserAndAuditTests {

        @Test
        @DisplayName("Should set current user successfully")
        void shouldSetCurrentUserSuccessfully() {
            // When
            clientService.setCurrentUser("testUser");

            // Then - No exception should be thrown
            // The method doesn't return anything, so we just verify it executes without error
        }

        @Test
        @DisplayName("Should get deleted clients successfully")
        void shouldGetDeletedClientsSuccessfully() {
            // Given
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("birthDate", "1990-01-01");
            clientData.put("email", "john@test.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Test Address 123");

            Optional<ClientDTO> createdClient = clientService.createClientDto(clientData);
            Long clientId = createdClient.get().getId();
            clientService.deleteClient(Optional.of(clientId));

            // When
            List<ClientDTO> result = clientService.getDeleted();

            // Then
            assertThat(result).anyMatch(client -> client.getId().equals(clientId));
        }
    }

    @Nested
    @DisplayName("Utility Tests")
    class UtilityTests {

        @Test
        @DisplayName("Should count clients successfully")
        void shouldCountClientsSuccessfully() {
            // Given
            Map<String, String> clientData1 = new HashMap<>();
            clientData1.put("firstName", "John");
            clientData1.put("lastName", "Doe");
            clientData1.put("dni", "12345678");
            clientData1.put("birthDate", "1990-01-01");
            clientData1.put("email", "john@test.com");
            clientData1.put("phone", "123456789");
            clientData1.put("address", "Test Address 123");

            Map<String, String> clientData2 = new HashMap<>();
            clientData2.put("firstName", "Jane");
            clientData2.put("lastName", "Smith");
            clientData2.put("dni", "87654321");
            clientData2.put("birthDate", "1992-02-02");
            clientData2.put("email", "jane@test.com");
            clientData2.put("phone", "987654321");
            clientData2.put("address", "Test Address 456");

            clientService.createClientDto(clientData1);
            clientService.createClientDto(clientData2);

            // When
            long count = clientService.countClients();

            // Then
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("Should update field successfully")
        void shouldUpdateFieldSuccessfully() {
            // Given
            Map<String, String> clientData = new HashMap<>();
            clientData.put("firstName", "John");
            clientData.put("lastName", "Doe");
            clientData.put("dni", "12345678");
            clientData.put("birthDate", "1990-01-01");
            clientData.put("email", "john@test.com");
            clientData.put("phone", "123456789");
            clientData.put("address", "Test Address 123");

            Optional<ClientDTO> createdClient = clientService.createClientDto(clientData);
            Long clientId = createdClient.get().getId();

            // When
            Optional<ClientDTO> result = clientService.updateField(
                clientId, 
                Optional.of("Johnny"),
                ClientDTO::getFirstName,
                (client, newValue) -> client.toBuilder().firstName(newValue).build()
            );

            // Then
            assertThat(result).isPresent();
            ClientDTO updatedClient = result.get();
            assertThat(updatedClient.getFirstName()).isEqualTo("Johnny");
        }
    }
}
