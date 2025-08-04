package com.mibanco.repositoryTest.internalTest;

import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Client;
import com.mibanco.model.enums.ClientOperationType;
import com.mibanco.repository.internal.BaseRepositoryImplWrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify automatic saving based on list size
 */
@DisplayName("BaseRepository Size-Based Saving Tests")
class BaseRepositorySizeBasedSavingTest {
    
    private TestRepositoryWithSizeBasedSaving repository;
    private Client client1;
    private Client client2;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        String filePath = tempDir.resolve("test_clients_size.json").toString();
        repository = new TestRepositoryWithSizeBasedSaving(filePath);
        
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
    @DisplayName("Should save automatically when size reaches 10")
    void shouldSaveAutomaticallyAtSize10() {
        // Create 10 clients to trigger automatic saving
        for (int i = 0; i < 10; i++) {
            Client client = Client.builder()
                .firstName("Client" + i)
                .lastName("Test" + i)
                .dni("DNI" + i)
                .email("client" + i + "@test.com")
                .phone("123456789")
                .address("Address " + i)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
                
            Optional<Client> result = repository.createRecord(Optional.of(client), ClientOperationType.CREATE);
            assertTrue(result.isPresent());
        }
        
        // Verify that it was saved automatically (file should exist)
        assertTrue(repository.getFileExists());
        assertEquals(10, repository.countRecords());
    }
    
    @Test
    @DisplayName("Should save automatically when size reaches 20")
    void shouldSaveAutomaticallyAtSize20() {
        // Create 20 clients to trigger second automatic saving
        for (int i = 0; i < 20; i++) {
            Client client = Client.builder()
                .firstName("Client" + i)
                .lastName("Test" + i)
                .dni("DNI" + i)
                .email("client" + i + "@test.com")
                .phone("123456789")
                .address("Address " + i)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
                
            Optional<Client> result = repository.createRecord(Optional.of(client), ClientOperationType.CREATE);
            assertTrue(result.isPresent());
        }
        
        // Verify that it was saved automatically
        assertTrue(repository.getFileExists());
        assertEquals(20, repository.countRecords());
    }
    
    @Test
    @DisplayName("Should not save automatically at sizes that are not multiples of 10")
    void shouldNotSaveAutomaticallyAtNonMultipleSizes() {
        // Create 9 clients (not multiple of 10)
        for (int i = 0; i < 9; i++) {
            Client client = Client.builder()
                .firstName("Client" + i)
                .lastName("Test" + i)
                .dni("DNI" + i)
                .email("client" + i + "@test.com")
                .phone("123456789")
                .address("Address " + i)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
                
            Optional<Client> result = repository.createRecord(Optional.of(client), ClientOperationType.CREATE);
            assertTrue(result.isPresent());
        }
        
        // Verify that it was NOT saved automatically
        assertFalse(repository.getFileExists());
        assertEquals(9, repository.countRecords());
    }
    
    @Test
    @DisplayName("Should allow manual saving with saveData()")
    void shouldAllowManualSaving() {
        // Create 5 clients (not multiple of 10)
        for (int i = 0; i < 5; i++) {
            Client client = Client.builder()
                .firstName("Client" + i)
                .lastName("Test" + i)
                .dni("DNI" + i)
                .email("client" + i + "@test.com")
                .phone("123456789")
                .address("Address " + i)
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();
                
            Optional<Client> result = repository.createRecord(Optional.of(client), ClientOperationType.CREATE);
            assertTrue(result.isPresent());
        }
        
        // Verify that it was NOT saved automatically
        assertFalse(repository.getFileExists());
        
        // Force manual saving
        repository.saveData();
        
        // Verify that the file now exists
        assertTrue(repository.getFileExists());
        assertEquals(5, repository.countRecords());
    }
    
    @Test
    @DisplayName("Should not save if the list is empty")
    void shouldNotSaveIfListIsEmpty() {
        // Don't create any clients
        assertEquals(0, repository.countRecords());
        
        // Force manual saving
        repository.saveData();
        
        // Verify that the file was NOT created because the list is empty
        assertFalse(repository.getFileExists());
    }
    
    /**
     * Test class that extends BaseRepositoryImpl for testing
     */
    private static class TestRepositoryWithSizeBasedSaving extends BaseRepositoryImplWrapper<Client, Long, ClientOperationType> {
        
        private final String filePath;
        
        public TestRepositoryWithSizeBasedSaving(String filePath) {
            this.filePath = filePath;
        }
        
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
            config.put("filePath", filePath);
            config.put("classType", Client.class);
            config.put("idExtractor", (Function<Client, Long>) Client::getId);
            return config;
        }
        
        public boolean getFileExists() {
            return new File(filePath).exists();
        }
    }
} 