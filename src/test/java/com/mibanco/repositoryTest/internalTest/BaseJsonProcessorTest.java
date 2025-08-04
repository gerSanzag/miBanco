package com.mibanco.repositoryTest.internalTest;

import com.mibanco.model.Client;
import com.mibanco.repository.internal.BaseJsonProcessorWrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for BaseJsonProcessor using Test-Specific Subclass
 * Validates generic JSON file processing
 */
@DisplayName("BaseJsonProcessor Tests")
class BaseJsonProcessorTest {
    
    private BaseJsonProcessorWrapper<Client> jsonProcessor;
    private Client client1;
    private Client client2;
    
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
    
    @Nested
    @DisplayName("Tests for calculateMaxId")
    class CalculateMaxIdTests {
        
        @Test
        @DisplayName("Should calculate the maximum ID from a list with elements")
        void shouldCalculateMaxIdWithElements() {
            // Arrange
            List<Client> clients = List.of(client1, client2);
            Function<Client, Long> idExtractor = Client::getId;
            
            // Act
            Long maxId = jsonProcessor.calculateMaxId(clients, idExtractor);
            
            // Assert
            assertEquals(5L, maxId);
        }
        
        @Test
        @DisplayName("Should return 0 for empty list")
        void shouldReturnZeroForEmptyList() {
            // Arrange
            List<Client> emptyClients = List.of();
            Function<Client, Long> idExtractor = Client::getId;
            
            // Act
            Long maxId = jsonProcessor.calculateMaxId(emptyClients, idExtractor);
            
            // Assert
            assertEquals(0L, maxId);
        }
        
        @Test
        @DisplayName("Should handle list with single element")
        void shouldHandleListWithSingleElement() {
            // Arrange
            List<Client> singleClient = List.of(client1);
            Function<Client, Long> idExtractor = Client::getId;
            
            // Act
            Long maxId = jsonProcessor.calculateMaxId(singleClient, idExtractor);
            
            // Assert
            assertEquals(1L, maxId);
        }
    }
    
    @Nested
    @DisplayName("Tests for saveJson")
    class SaveJsonTests {
        
        @Test
        @DisplayName("Should save client list to JSON file")
        void shouldSaveListToJson(@TempDir Path tempDir) throws IOException {
            // Arrange
            List<Client> clients = List.of(client1, client2);
            File jsonFile = tempDir.resolve("clients_test.json").toFile();
            
            // Act
            jsonProcessor.saveData(clients, jsonFile.getAbsolutePath());
            
            // Assert
            assertTrue(jsonFile.exists());
            assertTrue(jsonFile.length() > 0);
        }
        
        @Test
        @DisplayName("Should save empty list to JSON file")
        void shouldSaveEmptyListToJson(@TempDir Path tempDir) throws IOException {
            // Arrange
            List<Client> emptyClients = List.of();
            File jsonFile = tempDir.resolve("empty_clients_test.json").toFile();
            
            // Act
            jsonProcessor.saveData(emptyClients, jsonFile.getAbsolutePath());
            
            // Assert
            assertTrue(jsonFile.exists());
        }
        
        @Test
        @DisplayName("Should create directory if it doesn't exist")
        void shouldCreateDirectoryIfNotExists(@TempDir Path tempDir) throws IOException {
            // Arrange
            List<Client> clients = List.of(client1);
            File newDirectory = tempDir.resolve("new_directory").toFile();
            File jsonFile = new File(newDirectory, "clients_test.json");
            
            // Act
            jsonProcessor.saveData(clients, jsonFile.getAbsolutePath());
            
            // Assert
            assertTrue(newDirectory.exists());
            assertTrue(jsonFile.exists());
        }
    }
    
    @Nested
    @DisplayName("Tests for loadDataConditionally")
    class LoadDataConditionallyTests {
        
        @Test
        @DisplayName("Should load data from existing JSON file")
        void shouldLoadDataFromExistingFile(@TempDir Path tempDir) throws IOException {
            // Arrange
            List<Client> originalClients = List.of(client1, client2);
            File jsonFile = tempDir.resolve("clients_load_test.json").toFile();
            
            // Save data first
            jsonProcessor.saveData(originalClients, jsonFile.getAbsolutePath());
            
            // Debug: Verify that the file was created and has content
            System.out.println("=== DEBUG INFO ===");
            System.out.println("File exists: " + jsonFile.exists());
            System.out.println("File size: " + jsonFile.length() + " bytes");
            System.out.println("File content:");
            String content = java.nio.file.Files.readString(jsonFile.toPath());
            System.out.println(content);
            System.out.println("==================");
            
            // Act
            List<Client> loadedClients = jsonProcessor.loadDataConditionally(
                jsonFile.getAbsolutePath(), Client.class);
            
            // Debug: Verify what was loaded
            System.out.println("Loaded clients: " + loadedClients.size());
            System.out.println("==================");
            
            // Assert
            assertNotNull(loadedClients);
            assertEquals(2, loadedClients.size());
            assertEquals(client1.getId(), loadedClients.get(0).getId());
            assertEquals(client2.getId(), loadedClients.get(1).getId());
        }
        
        @Test
        @DisplayName("Should return empty list for non-existent file")
        void shouldReturnEmptyListForNonExistentFile() {
            // Arrange
            String nonExistentPath = "/path/that/does/not/exist/file.json";
            
            // Act
            List<Client> loadedClients = jsonProcessor.loadDataConditionally(
                nonExistentPath, Client.class);
            
            // Assert
            assertNotNull(loadedClients);
            assertTrue(loadedClients.isEmpty());
        }
        
        @Test
        @DisplayName("Should load empty list from empty JSON file")
        void shouldLoadEmptyListFromEmptyFile(@TempDir Path tempDir) throws IOException {
            // Arrange
            List<Client> emptyClients = List.of();
            File jsonFile = tempDir.resolve("empty_clients_load_test.json").toFile();
            
            // Save empty list
            jsonProcessor.saveData(emptyClients, jsonFile.getAbsolutePath());
            
            // Act
            List<Client> loadedClients = jsonProcessor.loadDataConditionally(
                jsonFile.getAbsolutePath(), Client.class);
            
            // Assert
            assertNotNull(loadedClients);
            assertTrue(loadedClients.isEmpty());
        }
        
        @Test
        @DisplayName("Should handle JSON file with invalid format")
        void shouldHandleInvalidJsonFile(@TempDir Path tempDir) throws IOException {
            // Arrange
            File jsonFile = tempDir.resolve("invalid_file.json").toFile();
            
            // Create file with invalid content
            java.nio.file.Files.write(jsonFile.toPath(), "invalid content".getBytes());
            
            // Act
            List<Client> loadedClients = jsonProcessor.loadDataConditionally(
                jsonFile.getAbsolutePath(), Client.class);
            
            // Assert
            assertNotNull(loadedClients);
            assertTrue(loadedClients.isEmpty()); // Should return empty list in case of error
        }
    }
} 