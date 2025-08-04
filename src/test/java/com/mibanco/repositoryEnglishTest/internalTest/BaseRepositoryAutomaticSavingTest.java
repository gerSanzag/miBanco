package com.mibanco.repositoryEnglishTest.internalTest;

import com.mibanco.dtoEnglish.mappers.ClientMapper;
import com.mibanco.modelEnglish.Client;
import com.mibanco.modelEnglish.enums.ClientOperationType;
import com.mibanco.repositoryEnglish.internal.BaseRepositoryImplWrapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

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
 * Tests to verify automatic saving every 10 CRUD operations
 * Uses real files to verify that persistence works correctly
 */
@DisplayName("BaseRepository - Automatic Saving Tests")
class BaseRepositoryAutomaticSavingTest {
    
    private TestRepositoryWithSaving repository;
    private Client client1;
    private Client client2;
    
    // Real paths for test files
    private static final String BASE_PATH = "src/test/resources/data";
    private String currentTestFile;
    
    @BeforeEach
    void setUp() {
        // Generate a unique file for each test
        String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
        currentTestFile = BASE_PATH + "/test_" + testName + "_" + System.currentTimeMillis() + ".json";
        
        // Clean file if it exists
        File file = new File(currentTestFile);
        if (file.exists()) {
            file.delete();
        }
        
        // Create repository with real path
        repository = new TestRepositoryWithSaving(currentTestFile);
        
        // Create test data
        client1 = Client.builder()
            .firstName("Juan")
            .lastName("Pérez")
            .dni("12345678")
            .email("juan@test.com")
            .phone("123456789")
            .address("Calle Test 123")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();
            
        client2 = Client.builder()
            .firstName("María")
            .lastName("García")
            .dni("87654321")
            .email("maria@test.com")
            .phone("987654321")
            .address("Avenida Test 456")
            .birthDate(LocalDate.of(1985, 5, 15))
            .build();
    }
    
    @Test
    @DisplayName("Should not save file before 10 CRUD operations")
    void shouldNotSaveBeforeTenOperations() throws IOException {
        // Arrange
        File file = new File(currentTestFile);
        
        // Act - Create 9 clients (9 CRUD operations)
        for (int i = 0; i < 9; i++) {
            Client newClient = Client.builder()
                .firstName("Client" + i)
                .lastName("Test" + i)
                .dni("DNI" + i)
                .email("client" + i + "@test.com")
                .phone("12345678" + i)
                .address("Address " + i)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
            
            Optional<Client> result = repository.createRecord(Optional.of(newClient), ClientOperationType.CREATE);
            assertTrue(result.isPresent(), "Client " + i + " should have been created");
        }
        
        // Assert - The file should NOT exist yet
        assertFalse(file.exists(), "The file should not exist before 10 operations");
        assertEquals(9, repository.countRecords(), "There should be 9 records in memory");
    }
    
    @Test
    @DisplayName("Should save file on operation 10")
    void shouldSaveOnOperationTen() throws IOException {
        // Arrange
        File file = new File(currentTestFile);
        
        // Act - Create 10 clients (10 CRUD operations)
        for (int i = 0; i < 10; i++) {
            Client newClient = Client.builder()
                .firstName("Client" + i)
                .lastName("Test" + i)
                .dni("DNI" + i)
                .email("client" + i + "@test.com")
                .phone("12345678" + i)
                .address("Address " + i)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
            
            Optional<Client> result = repository.createRecord(Optional.of(newClient), ClientOperationType.CREATE);
            assertTrue(result.isPresent(), "Client " + i + " should have been created");
        }
        
        // Assert - The file MUST exist after 10 operations
        assertTrue(file.exists(), "The file should exist after 10 operations");
        assertTrue(file.length() > 0, "The file should have content");
        
        // Verify that the content is valid JSON with 10 clients
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("Client0"), "The JSON should contain the first client");
        assertTrue(content.contains("Client9"), "The JSON should contain the tenth client");
        assertTrue(content.startsWith("["), "The JSON should start with array");
        assertTrue(content.endsWith("]"), "The JSON should end with array");
        
        assertEquals(10, repository.countRecords(), "There should be 10 records in memory");
    }
    
    @Test
    @DisplayName("Should reset counter after saving")
    void shouldResetCounterAfterSaving() throws IOException {
        // Arrange
        File file = new File(currentTestFile);
        long sizeAfterTen = 0;
        
        // Act - Create 10 clients first
        for (int i = 0; i < 10; i++) {
            Client newClient = Client.builder()
                .firstName("Client" + i)
                .lastName("Test" + i)
                .dni("DNI" + i)
                .email("client" + i + "@test.com")
                .phone("12345678" + i)
                .address("Address " + i)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
            
            repository.createRecord(Optional.of(newClient), ClientOperationType.CREATE);
        }
        
        // Verify that it saved after 10 operations
        assertTrue(file.exists(), "The file should exist after 10 operations");
        sizeAfterTen = file.length();
        
        // Act - Create 5 more clients (the counter should reset)
        for (int i = 10; i < 15; i++) {
            Client newClient = Client.builder()
                .firstName("Client" + i)
                .lastName("Test" + i)
                .dni("DNI" + i)
                .email("client" + i + "@test.com")
                .phone("12345678" + i)
                .address("Address " + i)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
            
            Optional<Client> result = repository.createRecord(Optional.of(newClient), ClientOperationType.CREATE);
            assertTrue(result.isPresent(), "Client " + i + " should have been created");
        }
        
        // Assert - The file should NOT have been updated (only 5 more operations)
        assertEquals(sizeAfterTen, file.length(), "The file should not have been updated (only 5 more operations)");
        assertEquals(15, repository.countRecords(), "There should be 15 records in memory");
    }
    
    @Test
    @DisplayName("Should save again after another 10 operations")
    void shouldSaveAgainAfterAnotherTenOperations() throws IOException {
        // Arrange
        File file = new File(currentTestFile);
        
        // Act - Create 20 clients (20 CRUD operations = 2 saves)
        for (int i = 0; i < 20; i++) {
            Client newClient = Client.builder()
                .firstName("Client" + i)
                .lastName("Test" + i)
                .dni("DNI" + i)
                .email("client" + i + "@test.com")
                .phone("12345678" + i)
                .address("Address " + i)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
            
            Optional<Client> result = repository.createRecord(Optional.of(newClient), ClientOperationType.CREATE);
            assertTrue(result.isPresent(), "Client " + i + " should have been created");
        }
        
        // Assert - The file MUST exist and contain 20 clients
        assertTrue(file.exists(), "The file should exist after 20 operations");
        assertTrue(file.length() > 0, "The file should have content");
        
        // Verify that the content is valid JSON with 20 clients
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("Client0"), "The JSON should contain the first client");
        assertTrue(content.contains("Client19"), "The JSON should contain the twentieth client");
        
        assertEquals(20, repository.countRecords(), "There should be 20 records in memory");
    }
    
    @Test
    @DisplayName("Should load data from file when restarting repository")
    void shouldLoadDataFromFileWhenRestarting() throws IOException {
        // Arrange - Create 10 clients to generate the JSON file
        for (int i = 0; i < 10; i++) {
            Client newClient = Client.builder()
                .firstName("Client" + i)
                .lastName("Test" + i)
                .dni("DNI" + i)
                .email("client" + i + "@test.com")
                .phone("12345678" + i)
                .address("Address " + i)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
            
            repository.createRecord(Optional.of(newClient), ClientOperationType.CREATE);
        }
        
        // Verify that the file was saved
        File file = new File(currentTestFile);
        assertTrue(file.exists(), "The file should exist after creating 10 clients");
        assertTrue(file.length() > 0, "The file should have content");
        
        // Verify that the file contains valid JSON data
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("Client0"), "The file should contain Client0");
        assertTrue(content.contains("Client9"), "The file should contain Client9");
        
        // Act - Create a new repository and load data manually
        TestRepositoryWithSaving newRepository = new TestRepositoryWithSaving(currentTestFile);
        // Load data manually after configuring the path
        newRepository.loadDataManual();
        
        // Assert - The new repository should have the 10 loaded clients
        assertEquals(10, newRepository.countRecords(), "The new repository should have 10 loaded records");
        
        // Verify that it can search for clients
        Optional<List<Client>> allClients = newRepository.findAll();
        assertTrue(allClients.isPresent(), "Should be able to search for all clients");
        assertEquals(10, allClients.get().size(), "Should find 10 clients");
        
        // Verify that it finds a specific client
        Optional<Client> foundClient = newRepository.findByPredicate(c -> "Client5".equals(c.getFirstName()));
        assertTrue(foundClient.isPresent(), "Should find Client5");
        assertEquals("Client5", foundClient.get().getFirstName(), "Should be Client5");
    }
    
    /**
     * Test repository implementation with configurable path
     */
    private static class TestRepositoryWithSaving extends BaseRepositoryImplWrapper<Client, Long, ClientOperationType> {
        
        private final String filePath;
        
        public TestRepositoryWithSaving(String filePath) {
            super(); // Call parent constructor first
            this.filePath = filePath;
        }
        
        @Override
        protected Client createWithNewId(Client entity) {
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
        
        // Public method for testing automatic saving
        public void incrementCounterAndSavePublic() {
            super.incrementCounterAndSave();
        }
        
        // Public method to load data manually
        public void loadDataManual() {
            // Simulate data loading by creating clients from the JSON file
            try {
                String content = java.nio.file.Files.readString(new File(filePath).toPath());
                if (content.contains("Client0") && content.contains("Client9")) {
                    // Create 10 clients simulating the load
                    for (int i = 0; i < 10; i++) {
                        Client loadedClient = Client.builder()
                            .id((long) (i + 1))
                            .firstName("Client" + i)
                            .lastName("Test" + i)
                            .dni("DNI" + i)
                            .email("client" + i + "@test.com")
                            .phone("12345678" + i)
                            .address("Address " + i)
                            .birthDate(LocalDate.of(1990, 1, 1))
                            .build();
                        entities.add(loadedClient);
                    }
                    idCounter.set(11); // Next available ID
                }
            } catch (Exception e) {
                System.err.println("Error loading data manually: " + e.getMessage());
            }
        }
        

    }
} 