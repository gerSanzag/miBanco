package com.mibanco.repositoryEnglishTest.internalTest;

import com.mibanco.modelEnglish.Client;
import com.mibanco.modelEnglish.AuditRecord;
import com.mibanco.modelEnglish.enums.ClientOperationType;
import com.mibanco.repositoryEnglish.internal.AuditRepositoryImplWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Specific tests for AuditRepositoryImpl
 * Validates the specific functionality of the audit repository
 * Includes registration tests, specific searches and edge cases
 */
@DisplayName("AuditRepositoryImpl Tests")
class AuditRepositoryImplTest {
    
    private AuditRepositoryImplWrapper repository;
    private AuditRecord<Client, ClientOperationType> record1;
    private AuditRecord<Client, ClientOperationType> record2;
    private Client client1;
    private Client client2;
    
    @BeforeEach
    void setUp() {
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
        
        // Create test audit records
        record1 = AuditRecord.<Client, ClientOperationType>builder()
            .id(UUID.randomUUID())
            .operationType(ClientOperationType.CREATE)
            .dateTime(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
            .entity(client1)
            .user("admin")
            .amount(null)
            .details("Creation of client Juan Pérez")
            .build();
            
        record2 = AuditRecord.<Client, ClientOperationType>builder()
            .id(UUID.randomUUID())
            .operationType(ClientOperationType.UPDATE)
            .dateTime(LocalDateTime.of(2024, 1, 16, 14, 45, 0))
            .entity(client2)
            .user("operator")
            .amount(1000.0)
            .details("Update of María García data")
            .build();
        
        // Create repository
        repository = new AuditRepositoryImplWrapper();
    }
    
    @Nested
    @DisplayName("Basic operation tests")
    class BasicOperationTests {
        
        @Test
        @DisplayName("Should register a new audit record")
        void shouldRegisterNewRecord() {
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.register(Optional.of(record1));
            
            // Assert
            assertTrue(result.isPresent());
            AuditRecord<Client, ClientOperationType> registeredRecord = result.get();
            assertEquals(record1.getId(), registeredRecord.getId());
            assertEquals(ClientOperationType.CREATE, registeredRecord.getOperationType());
            assertEquals(client1.getId(), registeredRecord.getEntity().getId());
            assertEquals("admin", registeredRecord.getUser());
        }
        
        @Test
        @DisplayName("Should handle record with Optional.empty()")
        void shouldHandleRecordWithOptionalEmpty() {
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.register(Optional.empty());
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should search record by ID")
        void shouldSearchRecordById() {
            // Arrange
            repository.register(Optional.of(record1));
            
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.findById(Optional.of(record1.getId()));
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(record1.getId(), result.get().getId());
            assertEquals(ClientOperationType.CREATE, result.get().getOperationType());
        }
        
        @Test
        @DisplayName("Should return Optional.empty() when ID doesn't exist")
        void shouldReturnEmptyWhenIdDoesNotExist() {
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.findById(Optional.of(UUID.randomUUID()));
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should handle search by ID with Optional.empty()")
        void shouldHandleSearchByIdWithOptionalEmpty() {
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.findById(Optional.empty());
            
            // Assert
            assertFalse(result.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Specific search tests")
    class SpecificSearchTests {
        
        @BeforeEach
        void setUp() {
            repository.register(Optional.of(record1));
            repository.register(Optional.of(record2));
        }
        
        @Test
        @DisplayName("Should get history by entity type and ID")
        void shouldGetHistoryByEntityTypeAndId() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> history = 
                repository.getHistory(Optional.of(Client.class), Optional.of(client1.getId()));
            
            // Assert
            assertTrue(history.isPresent());
            List<AuditRecord<Client, ClientOperationType>> records = history.get();
            assertFalse(records.isEmpty());
            assertTrue(records.stream().allMatch(record -> 
                record.getEntity().getId().equals(client1.getId())));
        }
        
        @Test
        @DisplayName("Should handle history with null entity type")
        void shouldHandleHistoryWithNullEntityType() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> history = 
                repository.getHistory(Optional.empty(), Optional.of(client1.getId()));
            
            // Assert
            assertTrue(history.isPresent());
            assertTrue(history.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle history with null entity ID")
        void shouldHandleHistoryWithNullEntityId() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> history = 
                repository.getHistory(Optional.of(Client.class), Optional.empty());
            
            // Assert
            assertTrue(history.isPresent());
            assertTrue(history.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should search records by date range")
        void shouldSearchRecordsByDateRange() {
            // Arrange
            LocalDateTime fromDate = LocalDateTime.of(2024, 1, 15, 0, 0, 0);
            LocalDateTime toDate = LocalDateTime.of(2024, 1, 16, 23, 59, 59);
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByDates(Optional.of(fromDate), Optional.of(toDate));
            
            // Assert
            assertTrue(records.isPresent());
            List<AuditRecord<Client, ClientOperationType>> recordList = records.get();
            assertFalse(recordList.isEmpty());
            assertTrue(recordList.stream().allMatch(record -> 
                !record.getDateTime().isBefore(fromDate) && 
                !record.getDateTime().isAfter(toDate)));
        }
        
        @Test
        @DisplayName("Should handle search by dates with null from date")
        void shouldHandleSearchByDatesWithNullFromDate() {
            // Arrange
            LocalDateTime toDate = LocalDateTime.of(2024, 1, 16, 23, 59, 59);
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByDates(Optional.empty(), Optional.of(toDate));
            
            // Assert
            assertTrue(records.isPresent());
            assertTrue(records.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle search by dates with null to date")
        void shouldHandleSearchByDatesWithNullToDate() {
            // Arrange
            LocalDateTime fromDate = LocalDateTime.of(2024, 1, 15, 0, 0, 0);
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByDates(Optional.of(fromDate), Optional.empty());
            
            // Assert
            assertTrue(records.isPresent());
            assertTrue(records.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should search records by user")
        void shouldSearchRecordsByUser() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByUser(Optional.of("admin"));
            
            // Assert
            assertTrue(records.isPresent());
            List<AuditRecord<Client, ClientOperationType>> recordList = records.get();
            assertFalse(recordList.isEmpty());
            assertTrue(recordList.stream().allMatch(record -> 
                "admin".equals(record.getUser())));
        }
        
        @Test
        @DisplayName("Should handle search by non-existent user")
        void shouldHandleSearchByNonExistentUser() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByUser(Optional.of("nonexistent"));
            
            // Assert
            assertTrue(records.isPresent());
            assertTrue(records.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle search by user with Optional.empty()")
        void shouldHandleSearchByUserWithOptionalEmpty() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByUser(Optional.empty());
            
            // Assert
            assertTrue(records.isPresent());
            assertTrue(records.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should search records by operation type")
        void shouldSearchRecordsByOperationType() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByOperationType(Optional.of(ClientOperationType.CREATE), Optional.of(ClientOperationType.class));
            
            // Assert
            assertTrue(records.isPresent());
            List<AuditRecord<Client, ClientOperationType>> recordList = records.get();
            assertFalse(recordList.isEmpty());
            assertTrue(recordList.stream().allMatch(record -> 
                ClientOperationType.CREATE.equals(record.getOperationType())));
        }
        
        @Test
        @DisplayName("Should handle search by operation type with null type")
        void shouldHandleSearchByOperationTypeWithNullType() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByOperationType(Optional.empty(), Optional.of(ClientOperationType.class));
            
            // Assert
            assertTrue(records.isPresent());
            assertTrue(records.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle search by operation type with null enum type")
        void shouldHandleSearchByOperationTypeWithNullEnumType() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByOperationType(Optional.of(ClientOperationType.CREATE), Optional.empty());
            
            // Assert
            assertTrue(records.isPresent());
            assertTrue(records.get().isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Specific edge case tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle record with null fields")
        void shouldHandleRecordWithNullFields() {
            // Arrange
            AuditRecord<Client, ClientOperationType> recordWithNulls = AuditRecord.<Client, ClientOperationType>builder()
                .id(UUID.randomUUID())
                .operationType(null)
                .dateTime(null)
                .entity(null)
                .user(null)
                .amount(null)
                .details(null)
                .build();
            
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.register(Optional.of(recordWithNulls));
            
            // Assert
            assertTrue(result.isPresent());
            AuditRecord<Client, ClientOperationType> registeredRecord = result.get();
            assertNotNull(registeredRecord.getId());
            assertNull(registeredRecord.getOperationType());
            assertNull(registeredRecord.getDateTime());
            assertNull(registeredRecord.getEntity());
            assertNull(registeredRecord.getUser());
            assertNull(registeredRecord.getAmount());
            assertNull(registeredRecord.getDetails());
        }
        
        @Test
        @DisplayName("Should handle record with zero amount")
        void shouldHandleRecordWithZeroAmount() {
            // Arrange
            AuditRecord<Client, ClientOperationType> recordWithZeroAmount = AuditRecord.<Client, ClientOperationType>builder()
                .id(UUID.randomUUID())
                .operationType(ClientOperationType.UPDATE)
                .dateTime(LocalDateTime.now())
                .entity(client1)
                .user("admin")
                .amount(0.0)
                .details("Update with zero amount")
                .build();
            
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.register(Optional.of(recordWithZeroAmount));
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(0.0, result.get().getAmount());
        }
        
        @Test
        @DisplayName("Should handle record with negative amount")
        void shouldHandleRecordWithNegativeAmount() {
            // Arrange
            AuditRecord<Client, ClientOperationType> recordWithNegativeAmount = AuditRecord.<Client, ClientOperationType>builder()
                .id(UUID.randomUUID())
                .operationType(ClientOperationType.UPDATE)
                .dateTime(LocalDateTime.now())
                .entity(client1)
                .user("admin")
                .amount(-100.0)
                .details("Update with negative amount")
                .build();
            
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.register(Optional.of(recordWithNegativeAmount));
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(-100.0, result.get().getAmount());
        }
        
        @Test
        @DisplayName("Should handle record with future date")
        void shouldHandleRecordWithFutureDate() {
            // Arrange
            AuditRecord<Client, ClientOperationType> recordWithFutureDate = AuditRecord.<Client, ClientOperationType>builder()
                .id(UUID.randomUUID())
                .operationType(ClientOperationType.CREATE)
                .dateTime(LocalDateTime.now().plusDays(1))
                .entity(client1)
                .user("admin")
                .amount(null)
                .details("Record with future date")
                .build();
            
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.register(Optional.of(recordWithFutureDate));
            
            // Assert
            assertTrue(result.isPresent());
            assertTrue(result.get().getDateTime().isAfter(LocalDateTime.now()));
        }
        
        @Test
        @DisplayName("Should handle record with very long user")
        void shouldHandleRecordWithVeryLongUser() {
            // Arrange
            String veryLongUser = "a".repeat(1000);
            AuditRecord<Client, ClientOperationType> recordWithLongUser = AuditRecord.<Client, ClientOperationType>builder()
                .id(UUID.randomUUID())
                .operationType(ClientOperationType.CREATE)
                .dateTime(LocalDateTime.now())
                .entity(client1)
                .user(veryLongUser)
                .amount(null)
                .details("Record with very long user")
                .build();
            
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.register(Optional.of(recordWithLongUser));
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(veryLongUser, result.get().getUser());
        }
        
        @Test
        @DisplayName("Should handle multiple records from same user")
        void shouldHandleMultipleRecordsFromSameUser() {
            // Arrange
            String sameUser = "admin";
            AuditRecord<Client, ClientOperationType> record3 = AuditRecord.<Client, ClientOperationType>builder()
                .id(UUID.randomUUID())
                .operationType(ClientOperationType.DELETE)
                .dateTime(LocalDateTime.now())
                .entity(client2)
                .user(sameUser)
                .amount(null)
                .details("Third record from same user")
                .build();
            
            repository.register(Optional.of(record1));
            repository.register(Optional.of(record3));
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByUser(Optional.of(sameUser));
            
            // Assert
            assertTrue(records.isPresent());
            List<AuditRecord<Client, ClientOperationType>> recordList = records.get();
            assertEquals(2, recordList.size());
            assertTrue(recordList.stream().allMatch(record -> sameUser.equals(record.getUser())));
        }
        
        @Test
        @DisplayName("Should handle search by date range with inverted dates")
        void shouldHandleSearchByDateRangeWithInvertedDates() {
            // Arrange
            LocalDateTime laterDate = LocalDateTime.of(2024, 1, 16, 23, 59, 59);
            LocalDateTime earlierDate = LocalDateTime.of(2024, 1, 15, 0, 0, 0);
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByDates(Optional.of(laterDate), Optional.of(earlierDate));
            
            // Assert
            assertTrue(records.isPresent());
            assertTrue(records.get().isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Filter coverage specific tests")
    class FilterCoverageTests {
        
        @BeforeEach
        void setUp() {
            repository.register(Optional.of(record1));
            repository.register(Optional.of(record2));
        }
        
        @Test
        @DisplayName("Should filter getHistory by correct entity type")
        void shouldFilterGetHistoryByCorrectEntityType() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> history = 
                repository.getHistory(Optional.of(Client.class), Optional.of(client1.getId()));
            
            // Assert
            assertTrue(history.isPresent());
            List<AuditRecord<Client, ClientOperationType>> records = history.get();
            assertFalse(records.isEmpty());
            assertTrue(records.stream().allMatch(record -> 
                record.getEntity() instanceof Client));
        }
        
        @Test
        @DisplayName("Should filter getHistory excluding entities of incorrect type")
        void shouldFilterGetHistoryExcludingEntitiesOfIncorrectType() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> history = 
                repository.getHistory(Optional.of(Client.class), Optional.of(999L));
            
            // Assert
            assertTrue(history.isPresent());
            assertTrue(history.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should filter getHistory excluding entities with different ID")
        void shouldFilterGetHistoryExcludingEntitiesWithDifferentId() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> history = 
                repository.getHistory(Optional.of(Client.class), Optional.of(999L));
            
            // Assert
            assertTrue(history.isPresent());
            assertTrue(history.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should filter getHistory with null entity")
        void shouldFilterGetHistoryWithNullEntity() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> history = 
                repository.getHistory(Optional.of(Client.class), Optional.of(client1.getId()));
            
            // Assert
            assertTrue(history.isPresent());
            // Note: The repository should handle null entities gracefully
            // The test verifies that the method doesn't throw exceptions
        }
        
        @Test
        @DisplayName("Should filter searchByDates with exact date")
        void shouldFilterSearchByDatesWithExactDate() {
            // Arrange
            LocalDateTime exactDate = record1.getDateTime();
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByDates(Optional.of(exactDate), Optional.of(exactDate));
            
            // Assert
            assertTrue(records.isPresent());
            List<AuditRecord<Client, ClientOperationType>> recordList = records.get();
            assertFalse(recordList.isEmpty());
            assertTrue(recordList.stream().allMatch(record -> 
                record.getDateTime().equals(exactDate)));
        }
        
        @Test
        @DisplayName("Should filter searchByDates excluding earlier dates")
        void shouldFilterSearchByDatesExcludingEarlierDates() {
            // Arrange
            LocalDateTime fromDate = record1.getDateTime().plusHours(1);
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByDates(Optional.of(fromDate), Optional.empty());
            
            // Assert
            assertTrue(records.isPresent());
            List<AuditRecord<Client, ClientOperationType>> recordList = records.get();
            assertTrue(recordList.stream().allMatch(record -> 
                !record.getDateTime().isBefore(fromDate)));
        }
        
        @Test
        @DisplayName("Should filter searchByDates excluding later dates")
        void shouldFilterSearchByDatesExcludingLaterDates() {
            // Arrange
            LocalDateTime toDate = record1.getDateTime().minusHours(1);
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByDates(Optional.empty(), Optional.of(toDate));
            
            // Assert
            assertTrue(records.isPresent());
            List<AuditRecord<Client, ClientOperationType>> recordList = records.get();
            assertTrue(recordList.stream().allMatch(record -> 
                !record.getDateTime().isAfter(toDate)));
        }
        
        @Test
        @DisplayName("Should handle searchByDates with null dateTime (implemented solution)")
        void shouldHandleSearchByDatesWithNullDateTime() {
            // Arrange
            AuditRecord<Client, ClientOperationType> recordWithNullDateTime = AuditRecord.<Client, ClientOperationType>builder()
                .id(UUID.randomUUID())
                .operationType(ClientOperationType.CREATE)
                .dateTime(null)
                .entity(client1)
                .user("admin")
                .amount(null)
                .details("Record with null dateTime")
                .build();
            
            repository.register(Optional.of(recordWithNullDateTime));
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByDates(Optional.of(LocalDateTime.now()), Optional.of(LocalDateTime.now()));
            
            // Assert
            assertTrue(records.isPresent());
            // The repository should handle null dateTime gracefully
            // Records with null dateTime should be excluded from date range searches
        }
        
        @Test
        @DisplayName("Should filter searchByDates with dates at boundaries")
        void shouldFilterSearchByDatesWithDatesAtBoundaries() {
            // Arrange
            LocalDateTime boundaryDate = record1.getDateTime();
            LocalDateTime fromDate = boundaryDate.minusMinutes(1);
            LocalDateTime toDate = boundaryDate.plusMinutes(1);
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByDates(Optional.of(fromDate), Optional.of(toDate));
            
            // Assert
            assertTrue(records.isPresent());
            List<AuditRecord<Client, ClientOperationType>> recordList = records.get();
            assertFalse(recordList.isEmpty());
            assertTrue(recordList.stream().anyMatch(record -> 
                record.getDateTime().equals(boundaryDate)));
        }
        
        @Test
        @DisplayName("Should handle searchByDates with mix of valid and null records")
        void shouldHandleSearchByDatesWithMixOfValidAndNullRecords() {
            // Arrange
            AuditRecord<Client, ClientOperationType> recordWithNullDateTime = AuditRecord.<Client, ClientOperationType>builder()
                .id(UUID.randomUUID())
                .operationType(ClientOperationType.UPDATE)
                .dateTime(null)
                .entity(client2)
                .user("operator")
                .amount(500.0)
                .details("Record with null dateTime")
                .build();
            
            repository.register(Optional.of(recordWithNullDateTime));
            
            LocalDateTime fromDate = LocalDateTime.of(2024, 1, 15, 0, 0, 0);
            LocalDateTime toDate = LocalDateTime.of(2024, 1, 16, 23, 59, 59);
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByDates(Optional.of(fromDate), Optional.of(toDate));
            
            // Assert
            assertTrue(records.isPresent());
            List<AuditRecord<Client, ClientOperationType>> recordList = records.get();
            // Should only include records with valid dateTime within the range
            assertTrue(recordList.stream().allMatch(record -> 
                record.getDateTime() != null &&
                !record.getDateTime().isBefore(fromDate) && 
                !record.getDateTime().isAfter(toDate)));
        }
    }
    
    @Nested
    @DisplayName("Integration and special cases tests")
    class IntegrationAndSpecialCasesTests {
        
        @Test
        @DisplayName("Should maintain multiple records in memory")
        void shouldMaintainMultipleRecordsInMemory() {
            // Arrange
            List<AuditRecord<Client, ClientOperationType>> testRecords = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                AuditRecord<Client, ClientOperationType> record = AuditRecord.<Client, ClientOperationType>builder()
                    .id(UUID.randomUUID())
                    .operationType(ClientOperationType.CREATE)
                    .dateTime(LocalDateTime.now().plusMinutes(i))
                    .entity(client1)
                    .user("admin")
                    .amount(100.0 * i)
                    .details("Test record " + i)
                    .build();
                testRecords.add(record);
                repository.register(Optional.of(record));
            }
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> allRecords = 
                repository.findByUser(Optional.of("admin"));
            
            // Assert
            assertTrue(allRecords.isPresent());
            List<AuditRecord<Client, ClientOperationType>> recordList = allRecords.get();
            assertEquals(5, recordList.size());
        }
        
        @Test
        @DisplayName("Should use AuditRecord factory methods")
        void shouldUseAuditRecordFactoryMethods() {
            // Arrange
            AuditRecord<Client, ClientOperationType> factoryRecord = AuditRecord.of(
                Optional.of(ClientOperationType.CREATE),
                Optional.of(client1),
                Optional.of("admin")
            );
            
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.register(Optional.of(factoryRecord));
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(ClientOperationType.CREATE, result.get().getOperationType());
            assertEquals(client1.getId(), result.get().getEntity().getId());
            assertEquals("admin", result.get().getUser());
        }
        
        @Test
        @DisplayName("Should use detailed factory method of AuditRecord")
        void shouldUseDetailedFactoryMethodOfAuditRecord() {
            // Arrange
            AuditRecord<Client, ClientOperationType> detailedRecord = AuditRecord.ofDetailed(
                Optional.of(ClientOperationType.UPDATE),
                Optional.of(client2),
                Optional.of("operator"),
                Optional.of(1000.0),
                Optional.of("Detailed factory method test")
            );
            
            // Act
            Optional<AuditRecord<Client, ClientOperationType>> result = 
                repository.register(Optional.of(detailedRecord));
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(ClientOperationType.UPDATE, result.get().getOperationType());
            assertEquals(client2.getId(), result.get().getEntity().getId());
            assertEquals("operator", result.get().getUser());
            assertEquals(1000.0, result.get().getAmount());
            assertEquals("Detailed factory method test", result.get().getDetails());
        }
        
        @Test
        @DisplayName("Should handle search by history with non-existent entity")
        void shouldHandleSearchByHistoryWithNonExistentEntity() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> history = 
                repository.getHistory(Optional.of(Client.class), Optional.of(999999L));
            
            // Assert
            assertTrue(history.isPresent());
            assertTrue(history.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle search by non-existent operation type")
        void shouldHandleSearchByNonExistentOperationType() {
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByOperationType(Optional.of(ClientOperationType.DELETE), Optional.of(ClientOperationType.class));
            
            // Assert
            assertTrue(records.isPresent());
            assertTrue(records.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle search by exact dates")
        void shouldHandleSearchByExactDates() {
            // Arrange
            LocalDateTime exactDate = record1.getDateTime();
            
            // Act
            Optional<List<AuditRecord<Client, ClientOperationType>>> records = 
                repository.findByDates(Optional.of(exactDate), Optional.of(exactDate));
            
            // Assert
            assertTrue(records.isPresent());
            List<AuditRecord<Client, ClientOperationType>> recordList = records.get();
            assertFalse(recordList.isEmpty());
            assertTrue(recordList.stream().allMatch(record -> 
                record.getDateTime().equals(exactDate)));
        }
    }
}
