package com.mibanco.repositoryTest.internalTest;

import com.mibanco.dto.ClientDTO;
import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Client;
import com.mibanco.model.enums.ClientOperationType;
import com.mibanco.repository.internal.BaseRepositoryImplWrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BaseRepositoryImpl using a concrete implementation for testing
 * Validates all base repository functionality
 */
@DisplayName("BaseRepositoryImpl Tests")
class BaseRepositoryImplTest {
    
    private TestRepositoryImpl repository;
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
        repository = new TestRepositoryImpl(jsonFile.getAbsolutePath());
    }
    
    /**
     * Concrete implementation of BaseRepositoryImpl for testing
     */
    private static class TestRepositoryImpl extends BaseRepositoryImplWrapper<Client, Long, ClientOperationType> {
        
        private final String filePath;
        
        public TestRepositoryImpl(String filePath) {
            this.filePath = filePath; // Allow null to test defensive validation
        }
        
        @Override
        protected Client createWithNewId(Client entity) {
            // Simulate ID assignment using counter
            // Pure functional approach with Optional
            ClientMapper mapper = new ClientMapper();
            
            return mapper.toDtoDirect(entity)
                .map(dto -> dto.toBuilder()
                    .id(idCounter.getAndIncrement())
                    .build())
                .flatMap(mapper::toEntityDirect)
                .orElseThrow(() -> new IllegalStateException("Could not process Client entity"));
        }
        
        @Override
        protected Map<String, Object> getConfiguration() {
            Map<String, Object> config = new HashMap<>();
            config.put("filePath", filePath);
            config.put("classType", Client.class);
            config.put("idExtractor", (Function<Client, Long>) Client::getId);
            return config;
        }

        // Public "bridge" methods for testing only
        public Optional<Client> findByPredicatePublic(java.util.function.Predicate<Client> predicate) {
            return super.findByPredicate(predicate);
        }

        public Optional<List<Client>> findAllByPredicatePublic(java.util.function.Predicate<Client> predicate) {
            return super.findAllByPredicate(predicate);
        }

        public void incrementCounterAndSavePublic() {
            super.incrementCounterAndSave();
        }
    }
    
    @Nested
    @DisplayName("Tests for create")
    class CreateTests {
        
        @Test
        @DisplayName("Should create a new entity with automatic ID")
        void shouldCreateEntityWithAutomaticId() {
            // Arrange
            Client newClient = Client.builder()
                .firstName("Nuevo")
                .lastName("Cliente")
                .dni("11111111")
                .email("nuevo@test.com")
                .phone("111111111")
                .address("Nueva Dirección")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();
            
            // Act
            Optional<Client> result = repository.createRecord(Optional.of(newClient), ClientOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Client createdClient = result.get();
            assertNotNull(createdClient.getId());
            assertEquals("Nuevo", createdClient.getFirstName());
            assertEquals(1, repository.countRecords());
        }
        
        @Test
        @DisplayName("Should return empty for null entity")
        void shouldReturnEmptyForNullEntity() {
            // Act
            Optional<Client> result = repository.createRecord(Optional.empty(), ClientOperationType.CREATE);
            
            // Assert
            assertFalse(result.isPresent());
            assertEquals(0, repository.countRecords());
        }
    }
    
    @Nested
    @DisplayName("Tests for search")
    class SearchTests {
        
        @BeforeEach
        void setUp() {
            // Create test entities
            repository.createRecord(Optional.of(client1), ClientOperationType.CREATE);
            repository.createRecord(Optional.of(client2), ClientOperationType.CREATE);
        }
        
        @Test
        @DisplayName("Should search entity by existing ID")
        void shouldSearchByExistingId() {
            // Act
            Optional<Client> result = repository.findById(Optional.of(1L));
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals("Juan", result.get().getFirstName());
        }
        
        @Test
        @DisplayName("Should return empty for non-existent ID")
        void shouldReturnEmptyForNonExistentId() {
            // Act
            Optional<Client> result = repository.findById(Optional.of(999L));
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should return empty for null ID")
        void shouldReturnEmptyForNullId() {
            // Act
            Optional<Client> result = repository.findById(Optional.empty());
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should search all entities")
        void shouldSearchAllEntities() {
            // Act
            Optional<List<Client>> result = repository.findAll();
            
            // Assert
            assertTrue(result.isPresent());
            List<Client> clients = result.get();
            assertEquals(2, clients.size());
            assertTrue(clients.stream().anyMatch(c -> c.getFirstName().equals("Juan")));
            assertTrue(clients.stream().anyMatch(c -> c.getFirstName().equals("María")));
        }
        
        @Test
        @DisplayName("Should count records correctly")
        void shouldCountRecordsCorrectly() {
            // Act
            long count = repository.countRecords();
            
            // Assert
            assertEquals(2, count);
        }
    }
    
    @Nested
    @DisplayName("Tests for predicate searches")
    class SearchByPredicateTests {

        @BeforeEach
        void setUp() {
            repository.createRecord(Optional.of(client1), ClientOperationType.CREATE);
            repository.createRecord(Optional.of(client2), ClientOperationType.CREATE);
        }

        @Test
        @DisplayName("Should return Optional.empty() if no client matches the predicate")
        void shouldReturnEmptyIfNoMatches() {
            // Act
            Optional<Client> result = repository.findByPredicatePublic(c -> c.getFirstName().equals("NoExiste"));

            // Assert
            assertFalse(result.isPresent());
        }

        @Test
        @DisplayName("Should return the correct client if the predicate finds it")
        void shouldReturnClientIfPredicateMatches() {
            // Act
            Optional<Client> result = repository.findByPredicatePublic(c -> c.getFirstName().equals("Juan"));

            // Assert
            assertTrue(result.isPresent());
            assertEquals("Juan", result.get().getFirstName());
        }
    }

    @Nested
    @DisplayName("Tests for list searches by predicate")
    class SearchAllByPredicateTests {

        @BeforeEach
        void setUp() {
            repository.createRecord(Optional.of(client1), ClientOperationType.CREATE);
            repository.createRecord(Optional.of(client2), ClientOperationType.CREATE);
        }

        @Test
        @DisplayName("Should return Optional with empty list if no client matches the predicate")
        void shouldReturnEmptyListIfNoMatches() {
            // Act
            Optional<List<Client>> result = repository.findAllByPredicatePublic(c -> c.getFirstName().equals("NoExiste"));

            // Assert
            assertTrue(result.isPresent());
            assertTrue(result.get().isEmpty());
        }

        @Test
        @DisplayName("Should return all clients that match the predicate")
        void shouldReturnAllClientsThatMatchPredicate() {
            // Act
            Optional<List<Client>> result = repository.findAllByPredicatePublic(c -> c.getLastName().contains("a"));

            // Assert
            assertTrue(result.isPresent());
            List<Client> list = result.get();
            assertFalse(list.isEmpty());
            assertTrue(list.stream().anyMatch(c -> c.getFirstName().equals("María")));
        }
    }
    
    @Nested
    @DisplayName("Tests for update")
    class UpdateTests {
        
        @BeforeEach
        void setUp() {
            
        }
        
        @Test
        @DisplayName("Should update existing entity")
        void shouldUpdateExistingEntity() {
            // Arrange
            ClientMapper mapper = new ClientMapper();
            
            // 1. Map client1 to DTO
            ClientDTO originalDto = mapper.toDtoDirect(client1).orElseThrow();
            
            // 2. Create updated DTO using toBuilder (keeping the same ID)
            ClientDTO updatedDto = originalDto.toBuilder()
                .firstName("Juan Actualizado")
                .email("juan.actualizado@test.com")
                .build();
            
            // 3. Map updated DTO to entity
            Client updatedClient = mapper.toEntityDirect(updatedDto).orElseThrow();
            
            // Act
            Optional<Client> result = repository.updateRecord(Optional.of(updatedClient), ClientOperationType.UPDATE);
            
            // Assert
            assertTrue(result.isPresent());
            
            // ✅ Verify that the ID remains the same
            assertEquals(client1.getId(), result.get().getId());
            
            // ✅ Verify that the data was updated
            assertEquals("Juan Actualizado", result.get().getFirstName());
            assertEquals("juan.actualizado@test.com", result.get().getEmail());
            
            // ✅ Verify that other fields didn't change
            assertEquals(client1.getLastName(), result.get().getLastName());
            assertEquals(client1.getDni(), result.get().getDni());
            assertEquals(client1.getPhone(), result.get().getPhone());
            assertEquals(client1.getAddress(), result.get().getAddress());
            assertEquals(client1.getBirthDate(), result.get().getBirthDate());
            
            // ✅ Verify that there's only one record
            assertEquals(1, repository.countRecords());
        }
        
       
    }
    
    @Nested
    @DisplayName("Tests for delete")
    class DeleteTests {
        
        private Client createdClient;
        
        @BeforeEach
        void setUp() {
            // Create test entity
            Optional<Client> result = repository.createRecord(Optional.of(client1), ClientOperationType.CREATE);
            createdClient = result.get();
        }
        
        @Test
        @DisplayName("Should delete entity by ID")
        void shouldDeleteById() {
            // Act
            Optional<Client> result = repository.deleteById(Optional.of(createdClient.getId()), ClientOperationType.DELETE);
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals("Juan", result.get().getFirstName());
            assertEquals(0, repository.countRecords());
            assertEquals(1, repository.getDeleted().size());
        }
        
        @Test
        @DisplayName("Should return empty for non-existent ID")
        void shouldReturnEmptyForNonExistentId() {
            // Act
            Optional<Client> result = repository.deleteById(Optional.of(999L), ClientOperationType.DELETE);
            
            // Assert
            assertFalse(result.isPresent());
            assertEquals(1, repository.countRecords());
        }
    }
    
    @Nested
    @DisplayName("Tests for restore")
    class RestoreTests {
        
        private Client deletedClient;
        
        @BeforeEach
        void setUp() {
            // Create and delete test entity
            Optional<Client> result = repository.createRecord(Optional.of(client1), ClientOperationType.CREATE);
            Client createdClient = result.get();
            deletedClient = repository.deleteById(Optional.of(createdClient.getId()), ClientOperationType.DELETE).get();
            // deletedClient = createdClient;
        }
        
        @Test
        @DisplayName("Should restore deleted entity")
        void shouldRestoreDeletedEntity() {
            // Act
            Optional<Client> result = repository.restore(Optional.of(deletedClient.getId()), ClientOperationType.RESTORE);
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals("Juan", result.get().getFirstName());
            assertEquals(1, repository.countRecords());
            assertEquals(0, repository.getDeleted().size());
        }
        
        @Test
        @DisplayName("Should return empty for non-existent ID in deleted")
        void shouldReturnEmptyForNonExistentIdInDeleted() {
            // Act
            Optional<Client> result = repository.restore(Optional.of(999L), ClientOperationType.RESTORE);
            
            // Assert
            assertFalse(result.isPresent());
            assertEquals(0, repository.countRecords());
            assertEquals(1, repository.getDeleted().size());
        }
    }
    
    @Nested
    @DisplayName("Tests for current user")
    class CurrentUserTests {
        
        @Test
        @DisplayName("Should set current user")
        void shouldSetCurrentUser() {
            // Arrange
            String newUser = "test_user";
            
            // Act
            repository.setCurrentUser(newUser);
            
            // Assert
            // Note: We cannot directly access the currentUser field because it's protected
            // But we can verify that the method executes without errors
            assertDoesNotThrow(() -> repository.setCurrentUser(newUser));
        }
    }

    @Nested
    @DisplayName("Tests for counter and periodic saving")
    class CounterAndSavingTests {

        @Test
        @DisplayName("Should not save before 10 operations")
        void shouldNotSaveBeforeTenOperations() {
            // Simulate 9 operations
            for (int i = 0; i < 9; i++) {
                repository.incrementCounterAndSavePublic();
            }
            // If you had access to the counter or could mock the saving, you would verify it here
            // For now, we only check that it doesn't throw an exception
            assertDoesNotThrow(() -> repository.incrementCounterAndSavePublic());
        }

        @Test
        @DisplayName("Should save on operation 10 and reset the counter")
        void shouldSaveOnOperationTenAndResetCounter() {
            for (int i = 0; i < 10; i++) {
                repository.incrementCounterAndSavePublic();
            }
            // Here you should verify that saveJson was called and the counter was reset
            // If you can mock the JSON processor, verify the call
            assertDoesNotThrow(() -> repository.incrementCounterAndSavePublic());
        }
    }

    @Nested
    @DisplayName("Tests for data loading")
    class DataLoadingTests {
        
        @Test
        @DisplayName("Should load data manually using loadData()")
        void shouldLoadDataManually() {
            // Arrange - Create repository with valid JSON file
            File jsonFile = new File("src/test/resources/data/test_clients.json");
            TestRepositoryImpl repositoryWithData = new TestRepositoryImpl(jsonFile.getAbsolutePath());
            
            // Act - Call the public loadData() method
            repositoryWithData.loadData();
            
            // Assert - Verify that data was loaded
            long recordCount = repositoryWithData.countRecords();
            assertTrue(recordCount > 0, "Should have loaded data from JSON file");
        }
        
        @Test
        @DisplayName("Should handle data loading with null path")
        void shouldHandleDataLoadingWithNullPath() {
            // Arrange - Create repository with null path
            TestRepositoryImpl repositoryWithoutPath = new TestRepositoryImpl(null);
            
            // Act - Call the public loadData() method
            repositoryWithoutPath.loadData();
            
            // Assert - Verify that no data was loaded (null path)
            long recordCount = repositoryWithoutPath.countRecords();
            assertEquals(0, recordCount, "Should not load data when path is null");
        }
    }
    
    @Nested
    @DisplayName("Tests for defensive validation")
    class DefensiveValidationTests {

        @Test
        @DisplayName("Should handle correctly when file path is null")
        void shouldHandleNullPathCorrectly() {
            // Arrange - Create repository with null path
            TestRepositoryImpl repositoryWithNullPath = new TestRepositoryImpl(null);
            
            // Act & Assert - Should not throw exception
            assertDoesNotThrow(() -> {
                // Try to create an entity (this will trigger data loading)
                Client newClient = Client.builder()
                    .firstName("Test")
                    .lastName("Cliente")
                    .dni("12345678")
                    .email("test@test.com")
                    .phone("123456789")
                    .address("Test Dirección")
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .build();
                
                Optional<Client> result = repositoryWithNullPath.createRecord(Optional.of(newClient), ClientOperationType.CREATE);
                
                // Verify that the operation was successful despite null path
                assertTrue(result.isPresent());
                assertNotNull(result.get().getId());
            });
        }

        @Test
        @DisplayName("Should handle correctly when configuration has critical null fields")
        void shouldThrowExceptionForCriticalNullFields() {
            // Arrange - Create a subclass that returns configuration with critical null fields
            class TestRepositoryInvalidConfiguration extends BaseRepositoryImplWrapper<Client, Long, ClientOperationType> {
                
                @Override
                protected Client createWithNewId(Client entity) {
                    ClientMapper mapper = new ClientMapper();
                    return mapper.toDtoDirect(entity)
                        .map(dto -> dto.toBuilder()
                            .id(1L)
                            .build())
                        .flatMap(mapper::toEntityDirect)
                        .orElseThrow(() -> new IllegalStateException("Could not process Client entity"));
                }
                
                @Override
                protected Map<String, Object> getConfiguration() {
                    Map<String, Object> config = new HashMap<>();
                    config.put("filePath", "test.json"); // Valid path
                    config.put("classType", null); // Critical field null
                    config.put("idExtractor", (Function<Client, Long>) Client::getId);
                    return config;
                }
            }
            
            TestRepositoryInvalidConfiguration repository = new TestRepositoryInvalidConfiguration();
            
            // Act & Assert - Should throw exception for critical null field when accessing data
            assertThrows(NullPointerException.class, () -> {
                repository.findAll(); // This will trigger lazy loading and throw the exception
            });
        }
    }
}
