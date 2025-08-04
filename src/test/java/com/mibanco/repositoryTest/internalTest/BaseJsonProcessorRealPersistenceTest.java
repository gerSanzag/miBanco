package com.mibanco.repositoryTest.internalTest;

import com.mibanco.model.Client;
import com.mibanco.repository.internal.BaseJsonProcessorWrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Real persistence tests for BaseJsonProcessor
 * Uses real files in src/test/resources/data
 * Tests must be executed in specific order
 */
@DisplayName("BaseJsonProcessor - Real Persistence Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BaseJsonProcessorRealPersistenceTest {
    
    private BaseJsonProcessorWrapper<Client> jsonProcessor;
    private Client client1;
    private Client client2;
    
    // Real paths for test files
    private static final String BASE_PATH = "src/test/resources/data";
    private static final String CLIENTS_FILE = BASE_PATH + "/test_clientes.json";
    private static final String EMPTY_CLIENTS_FILE = BASE_PATH + "/test_clientes_vacio.json";
    
    @BeforeEach
    void setUp() {
        jsonProcessor = new BaseJsonProcessorWrapper<>();
        
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
            .id(5L)
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
    @Order(1)
    @DisplayName("Should create JSON file if it doesn't exist")
    void shouldCreateJsonFileIfItDoesntExist() throws IOException {
        // Arrange
        File file = new File(CLIENTS_FILE);
        
        // Clean file if it exists (for clean test)
        if (file.exists()) {
            file.delete();
        }
        
        List<Client> clients = List.of(client1, client2);
        
        // Act
        jsonProcessor.saveData(clients, CLIENTS_FILE);
        
        // Assert
        assertTrue(file.exists(), "The file should have been created");
        assertTrue(file.length() > 0, "The file should have content");
        
        // Verify that the content is valid JSON
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("Juan"), "The JSON should contain the client's name");
        assertTrue(content.contains("María"), "The JSON should contain the second client's name");
        assertTrue(content.startsWith("["), "The JSON should start with array");
        assertTrue(content.endsWith("]"), "The JSON should end with array");
    }
    
    @Test
    @Order(2)
    @DisplayName("Should load data from existing JSON file")
    void shouldLoadDataFromExistingFile() {
        // Arrange - The file already exists from the previous test
        File file = new File(CLIENTS_FILE);
        assertTrue(file.exists(), "The file must exist from the previous test");
        
        // Act
        List<Client> loadedClients = jsonProcessor.loadDataConditionally(
            CLIENTS_FILE, Client.class);
        
        // Assert
        assertNotNull(loadedClients, "The loaded list should not be null");
        assertEquals(2, loadedClients.size(), "2 clients should have been loaded");
        
        // Verify that the data is correct
        Client loadedClient1 = loadedClients.get(0);
        assertEquals(client1.getId(), loadedClient1.getId(), "The first client's ID should match");
        assertEquals(client1.getFirstName(), loadedClient1.getFirstName(), "The first client's name should match");
        assertEquals(client1.getDni(), loadedClient1.getDni(), "The first client's DNI should match");
        
        Client loadedClient2 = loadedClients.get(1);
        assertEquals(client2.getId(), loadedClient2.getId(), "The second client's ID should match");
        assertEquals(client2.getFirstName(), loadedClient2.getFirstName(), "The second client's name should match");
        assertEquals(client2.getDni(), loadedClient2.getDni(), "The second client's DNI should match");
    }
    
    @Test
    @Order(3)
    @DisplayName("Should update existing JSON file")
    void shouldUpdateExistingJsonFile() throws IOException {
        // Arrange - The file already exists from the previous test
        File file = new File(CLIENTS_FILE);
        assertTrue(file.exists(), "The file must exist from the previous test");
        
        // Get original size
        long originalSize = file.length();
        
        // Create new client to add
        Client client3 = Client.builder()
            .id(10L)
            .firstName("Carlos")
            .lastName("López")
            .dni("11111111")
            .email("carlos@test.com")
            .phone("555555555")
            .address("Calle Nueva 789")
            .birthDate(LocalDate.of(1995, 8, 20))
            .build();
        
        List<Client> updatedClients = List.of(client1, client2, client3);
        
        // Act
        jsonProcessor.saveData(updatedClients, CLIENTS_FILE);
        
        // Assert
        assertTrue(file.exists(), "The file should still exist");
        assertTrue(file.length() > originalSize, "The file should be larger");
        
        // Verify that the updated content is correct
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("Carlos"), "The updated JSON should contain the new client");
        assertTrue(content.contains("Juan"), "The updated JSON should contain the first client");
        assertTrue(content.contains("María"), "The updated JSON should contain the second client");
    }
    
    @Test
    @Order(4)
    @DisplayName("Should load updated data from JSON file")
    void shouldLoadUpdatedDataFromFile() {
        // Arrange - The file already exists with updated data from the previous test
        File file = new File(CLIENTS_FILE);
        assertTrue(file.exists(), "The file must exist from the previous test");
        
        // Act
        List<Client> loadedClients = jsonProcessor.loadDataConditionally(
            CLIENTS_FILE, Client.class);
        
        // Assert
        assertNotNull(loadedClients, "The loaded list should not be null");
        assertEquals(3, loadedClients.size(), "3 clients should have been loaded");
        
        // Verify that the third client is present
        boolean client3Found = loadedClients.stream()
            .anyMatch(c -> "Carlos".equals(c.getFirstName()) && "11111111".equals(c.getDni()));
        assertTrue(client3Found, "The third client should be present in the loaded data");
    }
    
    @Test
    @Order(5)
    @DisplayName("Should handle empty JSON file")
    void shouldHandleEmptyJsonFile() throws IOException {
        // Arrange
        File emptyFile = new File(EMPTY_CLIENTS_FILE);
        List<Client> emptyList = List.of();
        
        // Create empty file
        jsonProcessor.saveData(emptyList, EMPTY_CLIENTS_FILE);
        
        // Act
        List<Client> loadedClients = jsonProcessor.loadDataConditionally(
            EMPTY_CLIENTS_FILE, Client.class);
        
        // Assert
        assertNotNull(loadedClients, "The loaded list should not be null");
        assertTrue(loadedClients.isEmpty(), "The list should be empty");
        
        // Verify that the file exists and has valid JSON content
        assertTrue(emptyFile.exists(), "The empty file should exist");
        String content = Files.readString(emptyFile.toPath());
        assertEquals("[ ]", content.trim(), "The file should contain an empty JSON array");
    }
    
    @Test
    @Order(6)
    @DisplayName("Should return empty list for non-existent file")
    void shouldReturnEmptyListForNonExistentFile() {
        // Arrange
        String nonExistentPath = BASE_PATH + "/file_that_does_not_exist.json";
        File nonExistentFile = new File(nonExistentPath);
        
        // Ensure the file doesn't exist
        if (nonExistentFile.exists()) {
            nonExistentFile.delete();
        }
        
        // Act
        List<Client> loadedClients = jsonProcessor.loadDataConditionally(
            nonExistentPath, Client.class);
        
        // Assert
        assertNotNull(loadedClients, "The loaded list should not be null");
        assertTrue(loadedClients.isEmpty(), "The list should be empty for non-existent file");
    }
} 