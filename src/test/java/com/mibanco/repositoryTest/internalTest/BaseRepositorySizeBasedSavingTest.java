package com.mibanco.repositoryTest.internalTest;

import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.BaseTest;
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
 * Tests to verify size-based automatic saving functionality
 */
@DisplayName("BaseRepository - Size Based Saving Tests")
class BaseRepositorySizeBasedSavingTest extends BaseTest {
    
    private TestRepositoryWithSaving repository;
    
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
    }
    
    @Test
    @DisplayName("Should save automatically at size 10")
    void shouldSaveAutomaticallyAtSize10() throws IOException {
        // Arrange
        File file = new File(currentTestFile);
        
        // Act - Create 10 clients (should trigger automatic save)
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
        
        // Act - Manual save (required in new architecture)
        repository.saveData();
        
        // Assert - The file should exist after manual save
        assertTrue(file.exists(), "The file should exist after manual save");
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
    @DisplayName("Should save automatically at size 20")
    void shouldSaveAutomaticallyAtSize20() throws IOException {
        // Arrange
        File file = new File(currentTestFile);
        
        // Act - Create 20 clients (should trigger automatic save)
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
        
        // Act - Manual save (required in new architecture)
        repository.saveData();
        
        // Assert - The file should exist after manual save
        assertTrue(file.exists(), "The file should exist after manual save");
        assertTrue(file.length() > 0, "The file should have content");
        
        // Verify that the content is valid JSON with 20 clients
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("Client0"), "The JSON should contain the first client");
        assertTrue(content.contains("Client19"), "The JSON should contain the twentieth client");
        assertTrue(content.startsWith("["), "The JSON should start with array");
        assertTrue(content.endsWith("]"), "The JSON should end with array");
        
        assertEquals(20, repository.countRecords(), "There should be 20 records in memory");
    }
    
    @Test
    @DisplayName("Should create empty file if list is empty")
    void shouldCreateEmptyFileIfListIsEmpty() throws IOException {
        // Arrange
        File file = new File(currentTestFile);
        
        // Act - Try to save with empty list
        repository.saveData();
        
        // Assert - The file should exist but be empty (new architecture behavior)
        assertTrue(file.exists(), "The file should exist even when list is empty");
        assertEquals(0, repository.countRecords(), "There should be 0 records in memory");
    }
    
    @Test
    @DisplayName("Should save multiple times when size threshold is reached")
    void shouldSaveMultipleTimesWhenSizeThresholdIsReached() throws IOException {
        // Arrange
        File file = new File(currentTestFile);
        
        // Act - Create 5 clients (should not trigger save yet)
        for (int i = 0; i < 5; i++) {
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
        
        // Assert - File should not exist yet (less than 10)
        assertFalse(file.exists(), "The file should not exist before reaching size threshold");
        
        // Create 5 more clients to reach 10 (should trigger save)
        for (int i = 5; i < 10; i++) {
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
        
        // Act - Manual save (required in new architecture)
        repository.saveData();
        
        // Assert - File should exist after reaching threshold
        assertTrue(file.exists(), "The file should exist after reaching size threshold");
        assertTrue(file.length() > 0, "The file should have content");
        
        // Verify that the content contains all 10 clients
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("Client0"), "The JSON should contain the first client");
        assertTrue(content.contains("Client9"), "The JSON should contain the tenth client");
        
        assertEquals(10, repository.countRecords(), "There should be 10 records in memory");
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
    }
}
