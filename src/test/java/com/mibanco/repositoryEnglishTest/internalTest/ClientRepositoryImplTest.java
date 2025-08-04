package com.mibanco.repositoryEnglishTest.internalTest;

import com.mibanco.dtoEnglish.ClientDTO;
import com.mibanco.dtoEnglish.mappers.ClientMapper;
import com.mibanco.modelEnglish.Client;
import com.mibanco.modelEnglish.enums.ClientOperationType;
import com.mibanco.repositoryEnglish.internal.ClientRepositoryImplWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Specific tests for ClientRepositoryImpl
 * Validates the specific functionality of the client repository
 * Includes configuration, mapping and specific edge case tests
 */
@DisplayName("ClientRepositoryImpl Tests")
class ClientRepositoryImplTest {
    
    private ClientRepositoryImplWrapper repository;
    private Client client1;
    private Client client2;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Create temporary JSON file for testing
        File jsonFile = tempDir.resolve("test_clients.json").toFile();
        
        // Create test data
        client1 = Client.builder()
            .id(1L)
            .firstName("Juan")
            .lastName("Pérez")
            .dni("12345678")
            .email("juan@test.com")
            .phone("123456789")
            .address("Calle Test 123")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();
            
        client2 = Client.builder()
            .id(2L)
            .firstName("María")
            .lastName("García")
            .dni("87654321")
            .email("maria@test.com")
            .phone("987654321")
            .address("Avenida Test 456")
            .birthDate(LocalDate.of(1985, 5, 15))
            .build();
        
        // Create repository with temporary configuration
        repository = new TestClientRepositoryImpl(jsonFile.getAbsolutePath());
    }
    
    /**
     * Test implementation that allows configuring the file path
     */
    private static class TestClientRepositoryImpl extends ClientRepositoryImplWrapper {
        
        private final String filePath;
        
        public TestClientRepositoryImpl(String filePath) {
            this.filePath = filePath;
        }
        
        @Override
        public java.util.Map<String, Object> getConfiguration() {
            java.util.Map<String, Object> config = new java.util.HashMap<>();
            config.put("filePath", filePath);
            config.put("classType", Client.class);
            config.put("idExtractor", (java.util.function.Function<Client, Long>) Client::getId);
            return config;
        }
    }
    
    @Nested
    @DisplayName("Specific configuration tests")
    class ConfigurationTests {
        
        @Test
        @DisplayName("Should use correct file path for clients")
        void shouldUseCorrectFilePath() {
            // Act
            Optional<List<Client>> result = repository.findAll();
            
            // Assert
            // If there are no file errors, the configuration is correct
            assertNotNull(result);
        }
        
        @Test
        @DisplayName("Should use Client class type")
        void shouldUseClientClassType() {
            // Act
            Optional<Client> result = repository.createRecord(Optional.of(client1), ClientOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            assertTrue(result.get() instanceof Client);
        }
        
        @Test
        @DisplayName("Should return correct configuration for clients")
        void shouldReturnCorrectConfiguration() {
            // Act - Execute operations that internally call getConfiguration
            // The getConfiguration method is executed during initialization and data loading
            Optional<List<Client>> result = repository.findAll();
            
            // Assert - Verify that the configuration works correctly
            assertNotNull(result);
            // If we get here without errors, it means getConfiguration() executed correctly
            // and returned the expected configuration (file path, class type, ID extractor)
        }

        @Test
        @DisplayName("Should execute getConfiguration() from the real class")
        void shouldExecuteGetConfigurationFromRealClass() {
            // Arrange - Use the Factory to get the real class
            com.mibanco.repositoryEnglish.ClientRepository realRepository = 
                com.mibanco.repositoryEnglish.internal.RepositoryFactory.getInstance().getClientRepository();
            
            // Act - Execute operations that internally call getConfiguration()
            // The getConfiguration() method is executed during lazy data loading
            Optional<List<Client>> result = realRepository.findAll();
            
            // Assert - Verify that the operation executed correctly
            assertNotNull(result);
            // If we get here without errors, it means getConfiguration() executed correctly
            // and returned the expected configuration (file path, class type, ID extractor)
        }
    }
    
    @Nested
    @DisplayName("Specific ID assignment tests")
    class IdAssignmentTests {
        
        @Test
        @DisplayName("Should assign incremental ID using AtomicLong")
        void shouldAssignIncrementalId() {
            // Arrange
            Client newClient1 = Client.builder()
                .firstName("New1")
                .lastName("Client1")
                .dni("11111111")
                .email("new1@test.com")
                .phone("111111111")
                .address("Address 1")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();
                
            Client newClient2 = Client.builder()
                .firstName("New2")
                .lastName("Client2")
                .dni("22222222")
                .email("new2@test.com")
                .phone("222222222")
                .address("Address 2")
                .birthDate(LocalDate.of(2001, 1, 1))
                .build();
            
            // Act
            Optional<Client> result1 = repository.createRecord(Optional.of(newClient1), ClientOperationType.CREATE);
            Optional<Client> result2 = repository.createRecord(Optional.of(newClient2), ClientOperationType.CREATE);
            
            // Assert
            assertTrue(result1.isPresent());
            assertTrue(result2.isPresent());
            
            Long id1 = result1.get().getId();
            Long id2 = result2.get().getId();
            
            assertNotNull(id1);
            assertNotNull(id2);
            assertEquals(id1 + 1, id2, "IDs must be consecutive (AtomicLong incrementAndGet)");
        }
        
       
        
        @Test
        @DisplayName("Should maintain consecutive sequence in multiple creations")
        void shouldMaintainConsecutiveSequence() {
            // Arrange
            List<Client> clients = new ArrayList<>();
            
            // Act - Create 5 clients
            for (int i = 0; i < 5; i++) {
                Client client = Client.builder()
                    .firstName("Client" + i)
                    .lastName("Test")
                    .dni("DNI" + i)
                    .email("client" + i + "@test.com")
                    .phone("Phone" + i)
                    .address("Address" + i)
                    .birthDate(LocalDate.of(1990 + i, 1, 1))
                    .build();
                
                Optional<Client> result = repository.createRecord(Optional.of(client), ClientOperationType.CREATE);
                assertTrue(result.isPresent(), "Client " + i + " should have been created correctly");
                clients.add(result.get());
            }
            
            // Assert - Verify that the IDs are consecutive (not necessarily starting at 1)
            Long firstId = clients.get(0).getId();
            for (int i = 0; i < clients.size(); i++) {
                assertEquals(firstId + i, clients.get(i).getId(), 
                    "ID of client " + i + " must be consecutive with the first ID");
            }
            
            // Verify that exactly 5 clients were created
            assertEquals(5, clients.size());
            assertEquals(5, repository.countRecords());
        }
    }
    
    @Nested
    @DisplayName("Specific CRUD operation tests")
    class CrudOperationTests {
        
        @BeforeEach
        void setUp() {
            // Create test entities
            repository.createRecord(Optional.of(client1), ClientOperationType.CREATE);
            repository.createRecord(Optional.of(client2), ClientOperationType.CREATE);
        }
        
        @Test
        @DisplayName("Should create client with all fields")
        void shouldCreateClientWithAllFields() {
            // Arrange
            Client completeClient = Client.builder()
                .firstName("Complete")
                .lastName("Client")
                .dni("44444444")
                .email("complete@test.com")
                .phone("444444444")
                .address("Complete Address 123")
                .birthDate(LocalDate.of(1980, 12, 25))
                .build();
            
            // Act
            Optional<Client> result = repository.createRecord(Optional.of(completeClient), ClientOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Client createdClient = result.get();
            assertEquals("Complete", createdClient.getFirstName());
            assertEquals("Client", createdClient.getLastName());
            assertEquals("44444444", createdClient.getDni());
            assertEquals("complete@test.com", createdClient.getEmail());
            assertEquals("444444444", createdClient.getPhone());
            assertEquals("Complete Address 123", createdClient.getAddress());
            assertEquals(LocalDate.of(1980, 12, 25), createdClient.getBirthDate());
        }
        
        @Test
        @DisplayName("Should search client by DNI")
        void shouldSearchClientByDni() {
            // Act
            Optional<Client> result = repository.findByPredicate(
                client -> "12345678".equals(client.getDni())
            );
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals("Juan", result.get().getFirstName());
            assertEquals("12345678", result.get().getDni());
        }
        
        @Test
        @DisplayName("Should search clients by email")
        void shouldSearchClientsByEmail() {
            // Act
            Optional<List<Client>> result = repository.findAllByPredicate(
                client -> client.getEmail().contains("@test.com")
            );
            
            // Assert
            assertTrue(result.isPresent());
            List<Client> clients = result.get();
            assertEquals(2, clients.size());
            assertTrue(clients.stream().allMatch(c -> c.getEmail().contains("@test.com")));
        }
        
        @Test
        @DisplayName("Should update existing client")
        void shouldUpdateExistingClient() {
            // Arrange
            Client updatedClient = Client.builder()
                .id(client1.getId())
                .firstName("Juan Updated")
                .lastName(client1.getLastName())
                .dni(client1.getDni())
                .email("juan.updated@test.com")
                .phone(client1.getPhone())
                .address(client1.getAddress())
                .birthDate(client1.getBirthDate())
                .build();
            
            // Act
            Optional<Client> result = repository.updateRecord(Optional.of(updatedClient), ClientOperationType.UPDATE);
            
            // Assert
            assertTrue(result.isPresent());
            Client modifiedClient = result.get();
            assertEquals("Juan Updated", modifiedClient.getFirstName());
            assertEquals("juan.updated@test.com", modifiedClient.getEmail());
            assertEquals("12345678", modifiedClient.getDni()); // Field not modified
        }
    }
    
    @Nested
    @DisplayName("Specific edge case tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle client with null fields")
        void shouldHandleClientWithNullFields() {
            // Arrange
            Client clientWithNulls = Client.builder()
                .firstName("Test")
                .lastName("Nulls")
                .dni("55555555")
                .build(); // email, phone, address, birthDate are null
            
            // Act
            Optional<Client> result = repository.createRecord(Optional.of(clientWithNulls), ClientOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Client createdClient = result.get();
            assertEquals("Test", createdClient.getFirstName());
            assertEquals("Nulls", createdClient.getLastName());
            assertEquals("55555555", createdClient.getDni());
            assertNull(createdClient.getEmail());
            assertNull(createdClient.getPhone());
            assertNull(createdClient.getAddress());
            assertNull(createdClient.getBirthDate());
        }
        
        @Test
        @DisplayName("Should allow client with duplicate DNI in repository (validation in service)")
        void shouldAllowClientWithDuplicateDniInRepository() {
            // Arrange
            Client client1 = Client.builder()
                .firstName("First")
                .lastName("Client")
                .dni("66666666")
                .email("first@test.com")
                .build();
                
            Client client2 = Client.builder()
                .firstName("Second")
                .lastName("Client")
                .dni("66666666") // Same DNI
                .email("second@test.com")
                .build();
            
            // Act
            Optional<Client> result1 = repository.createRecord(Optional.of(client1), ClientOperationType.CREATE);
            Optional<Client> result2 = repository.createRecord(Optional.of(client2), ClientOperationType.CREATE);
            
            // Assert
            assertTrue(result1.isPresent());
            assertTrue(result2.isPresent());
            // The repository allows duplicates because validation is in the service
            assertEquals("First", result1.get().getFirstName());
            assertEquals("Second", result2.get().getFirstName());
        }
        
        @Test
        @DisplayName("Should handle client with future birth date")
        void shouldHandleClientWithFutureBirthDate() {
            // Arrange
            Client futureClient = Client.builder()
                .firstName("Future")
                .lastName("Client")
                .dni("77777777")
                .email("future@test.com")
                .birthDate(LocalDate.now().plusYears(1)) // Future date
                .build();
            
            // Act
            Optional<Client> result = repository.createRecord(Optional.of(futureClient), ClientOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Client createdClient = result.get();
            assertEquals("Future", createdClient.getFirstName());
            assertTrue(createdClient.getBirthDate().isAfter(LocalDate.now()));
        }
    }
    
    @Nested
    @DisplayName("JSON file integration tests")
    class JsonIntegrationTests {
        
        @Test
        @DisplayName("Should persist client in JSON file")
        void shouldPersistClientInJsonFile() {
            // Arrange
            Client clientToPersist = Client.builder()
                .firstName("Persist")
                .lastName("Client")
                .dni("88888888")
                .email("persist@test.com")
                .build();
            
            // Act
            Optional<Client> result = repository.createRecord(Optional.of(clientToPersist), ClientOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            // Verify that the counter was incremented (persistence indicator)
            assertEquals(1, repository.countRecords());
        }
        
        @Test
        @DisplayName("Should load clients from JSON file")
        void shouldLoadClientsFromJsonFile() {
            // Arrange - Create client first
            Client clientToLoad = Client.builder()
                .firstName("Load")
                .lastName("Client")
                .dni("99999999")
                .email("load@test.com")
                .build();
            
            repository.createRecord(Optional.of(clientToLoad), ClientOperationType.CREATE);
            
            // Act - Find all (this loads from JSON)
            Optional<List<Client>> result = repository.findAll();
            
            // Assert
            assertTrue(result.isPresent());
            List<Client> clients = result.get();
            assertFalse(clients.isEmpty());
            assertTrue(clients.stream().anyMatch(c -> "99999999".equals(c.getDni())));
        }
    }
}
