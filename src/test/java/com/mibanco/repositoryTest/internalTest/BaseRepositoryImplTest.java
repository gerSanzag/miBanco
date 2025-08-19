package com.mibanco.repositoryTest.internalTest;

import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Client;
import com.mibanco.model.enums.ClientOperationType;
import com.mibanco.repository.internal.BaseRepositoryImplWrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify specific uncovered branches in BaseRepositoryImpl
 * Focuses on edge cases that are not covered by other tests
 */
@DisplayName("BaseRepositoryImpl - Uncovered Branches Tests")
class BaseRepositoryImplTest {

    private TestRepository repository;
    private String currentTestFile;

    @BeforeEach
    void setUp() throws IOException {
        // Create a temporary test file
        currentTestFile = "src/test/resources/data/test_base_repository.json";
        File file = new File(currentTestFile);
        file.getParentFile().mkdirs();
        
        // Create repository with test configuration
        repository = new TestRepository(currentTestFile);
    }

    @Test
    @DisplayName("Should handle updateRecord with empty Optional")
    void shouldHandleUpdateRecordWithEmptyOptional() {
        // Act - Try to update with empty Optional
        Optional<Client> result = repository.updateRecord(Optional.empty(), ClientOperationType.UPDATE);
        
        // Assert - Should return empty Optional
        assertTrue(result.isEmpty(), "Should return empty Optional when updating with empty Optional");
    }

    @Test
    @DisplayName("Should handle loadDataFromJson with empty data")
    void shouldHandleLoadDataFromJsonWithEmptyData() throws IOException {
        // Arrange - Create empty JSON file
        File file = new File(currentTestFile);
        Files.write(file.toPath(), "[]".getBytes());
        
        // Act - Load data (should trigger the if (!loadedData.isEmpty()) branch)
        repository.loadData();
        
        // Assert - Counter should remain at 1 (default value)
        assertEquals(1, repository.getIdCounterValue(), "Counter should remain at default value when no data is loaded");
    }

    @Test
    @DisplayName("Should handle loadDataFromJson with non-empty data")
    void shouldHandleLoadDataFromJsonWithNonEmptyData() throws IOException {
        // Arrange - Create JSON file with data
        String jsonData = """
            [
                {
                    "id": 5,
                    "firstName": "Test",
                    "lastName": "User",
                    "dni": "12345678",
                    "birthDate": "1990-01-01",
                    "email": "test@test.com",
                    "phone": "123456789",
                    "address": "Test Address"
                }
            ]
            """;
        File file = new File(currentTestFile);
        Files.write(file.toPath(), jsonData.getBytes());
        
        // Act - Load data (should trigger the if (!loadedData.isEmpty()) branch)
        repository.loadData();
        
        // Assert - Counter should be updated to max ID + 1
        assertEquals(6, repository.getIdCounterValue(), "Counter should be updated to max ID + 1 when data is loaded");
    }

    @Test
    @DisplayName("Should test real BaseRepositoryImpl through real ClientRepository")
    void shouldTestRealBaseRepositoryImplThroughRealClientRepository() {
        // Arrange - Get the REAL ClientRepository from Factory
        com.mibanco.repository.ClientRepository realRepository = 
            com.mibanco.repository.internal.RepositoryFactory.getInstance().getClientRepository();
        
        // Act - Test updateRecord with empty Optional (real BaseRepositoryImpl logic)
        Optional<Client> result = realRepository.updateRecord(Optional.empty(), ClientOperationType.UPDATE);
        
        // Assert - Should return empty Optional (real BaseRepositoryImpl behavior)
        assertTrue(result.isEmpty(), "Real BaseRepositoryImpl should return empty Optional when updating with empty Optional");
    }

    @Test
    @DisplayName("Should test real BaseRepositoryImpl loadDataFromJson through real repository")
    void shouldTestRealBaseRepositoryImplLoadDataFromJson() {
        // Arrange - Get the REAL ClientRepository from Factory
        com.mibanco.repository.ClientRepository realRepository = 
            com.mibanco.repository.internal.RepositoryFactory.getInstance().getClientRepository();
        
        // Act - Call findAll() which triggers the REAL loadDataFromJson method internally
        // This will load data from the real file: src/main/resources/data/clientes.json
        Optional<List<Client>> allClients = realRepository.findAll();
        
        // Assert - Verify that the real method executed without errors
        assertNotNull(allClients, "Real loadDataFromJson should execute without errors");
        
        // If we get here, it means the real if (!loadedData.isEmpty()) branch was executed
        // either with empty data (false branch) or with real data (true branch)
    }

    /**
     * Test-specific implementation of BaseRepositoryImplWrapper
     * Provides access to protected methods for testing
     */
    private static class TestRepository extends BaseRepositoryImplWrapper<Client, Long, ClientOperationType> {
        
        private final String filePath;
        
        TestRepository(String filePath) {
            this.filePath = filePath;
        }
        
        @Override
        protected Client createWithNewId(Client client) {
            ClientMapper mapper = new ClientMapper();
            
            return mapper.toDtoDirect(client)
                .map(dto -> dto.toBuilder()
                    .id(idCounter.incrementAndGet())
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
        
        /**
         * Get the current value of the ID counter for testing
         */
        public long getIdCounterValue() {
            return idCounter.get();
        }
    }
}
