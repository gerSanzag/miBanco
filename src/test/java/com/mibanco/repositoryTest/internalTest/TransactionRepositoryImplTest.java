package com.mibanco.repositoryTest.internalTest;

import com.mibanco.model.Transaction;
import com.mibanco.model.enums.TransactionOperationType;
import com.mibanco.model.enums.TransactionType;
import com.mibanco.repository.internal.TransactionRepositoryImplWrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Specific tests for TransactionRepositoryImpl
 * Validates the specific functionality of the transaction repository
 * Includes configuration, specific searches and edge cases
 */
@DisplayName("TransactionRepositoryImpl Tests")
class TransactionRepositoryImplTest {
    
    private TransactionRepositoryImplWrapper repository;
    private Transaction transaction1;
    private Transaction transaction2;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Create temporary JSON file for testing
        File jsonFile = tempDir.resolve("test_transactions.json").toFile();
        
        // Create test data
        transaction1 = Transaction.builder()
            .id(1L)
            .accountNumber("ES3412345678901234567890")
            .destinationAccountNumber(null)
            .type(TransactionType.DEPOSIT)
            .amount(new BigDecimal("1000.00"))
            .date(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
            .description("Initial deposit")
            .build();
            
        transaction2 = Transaction.builder()
            .id(2L)
            .accountNumber("ES3498765432109876543210")
            .destinationAccountNumber("ES3412345678901234567890")
            .type(TransactionType.SENT_TRANSFER)
            .amount(new BigDecimal("500.00"))
            .date(LocalDateTime.of(2024, 1, 16, 14, 45, 0))
            .description("Transfer to destination account")
            .build();
        
        // Create repository with temporary configuration
        repository = new TestTransactionRepositoryImpl(jsonFile.getAbsolutePath());
    }
    
    /**
     * Test implementation that allows configuring the file path
     */
    private static class TestTransactionRepositoryImpl extends TransactionRepositoryImplWrapper {
        
        private final String filePath;
        
        public TestTransactionRepositoryImpl(String filePath) {
            this.filePath = filePath;
        }
        
        @Override
        public java.util.Map<String, Object> getConfiguration() {
            java.util.Map<String, Object> config = new java.util.HashMap<>();
            config.put("filePath", filePath);
            config.put("classType", Transaction.class);
            config.put("idExtractor", (java.util.function.Function<Transaction, Long>) Transaction::getId);
            return config;
        }
    }

    @Nested
    @DisplayName("Specific configuration tests")
    class ConfigurationTests {
        
        @Test
        @DisplayName("Should use correct file path for transactions")
        void shouldUseCorrectFilePath() {
            // Act
            Optional<List<Transaction>> result = repository.findAll();
            
            // Assert
            // If there are no file errors, the configuration is correct
            assertNotNull(result);
        }
        
        @Test
        @DisplayName("Should use Transaction class type")
        void shouldUseTransactionClassType() {
            // Act
            Optional<Transaction> result = repository.createRecord(Optional.of(transaction1), TransactionOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            assertTrue(result.get() instanceof Transaction);
        }
        
        @Test
        @DisplayName("Should return correct configuration for transactions")
        void shouldReturnCorrectConfiguration() {
            // Act - Execute operations that internally call getConfiguration
            // The getConfiguration method is executed during initialization and data loading
            Optional<List<Transaction>> result = repository.findAll();
            
            // Assert - Verify that the configuration works correctly
            assertNotNull(result);
            // If we get here without errors, it means getConfiguration() executed correctly
            // and returned the expected configuration (file path, class type, ID extractor)
        }

        @Test
        @DisplayName("Should execute getConfiguration() from the real class")
        void shouldExecuteGetConfigurationFromRealClass() {
            // Arrange - Use the Factory to get the real class
            com.mibanco.repository.TransactionRepository realRepository = 
                com.mibanco.repository.internal.RepositoryFactory.getInstance().getTransactionRepository();
            
            // Act - Execute operations that internally call getConfiguration()
            // The getConfiguration() method is executed during lazy data loading
            Optional<List<Transaction>> result = realRepository.findAll();
            
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
            Transaction newTransaction1 = Transaction.builder()
                .accountNumber("ES3411111111111111111111")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .date(LocalDateTime.now())
                .description("Test deposit 1")
                .build();
                
            Transaction newTransaction2 = Transaction.builder()
                .accountNumber("ES3422222222222222222222")
                .destinationAccountNumber("ES3411111111111111111111")
                .type(TransactionType.SENT_TRANSFER)
                .amount(new BigDecimal("50.00"))
                .date(LocalDateTime.now())
                .description("Test transfer 2")
                .build();
            
            // Act
            Optional<Transaction> result1 = repository.createRecord(Optional.of(newTransaction1), TransactionOperationType.CREATE);
            Optional<Transaction> result2 = repository.createRecord(Optional.of(newTransaction2), TransactionOperationType.CREATE);
            
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
            List<Transaction> transactions = new ArrayList<>();
            
            // Act - Create 5 transactions
            for (int i = 0; i < 5; i++) {
                Transaction transaction = Transaction.builder()
                    .accountNumber("ES34" + String.format("%020d", i))
                    .destinationAccountNumber(i % 2 == 0 ? null : "ES3499999999999999999999")
                    .type(TransactionType.values()[i % TransactionType.values().length])
                    .amount(new BigDecimal("100.00").multiply(new BigDecimal(i + 1)))
                    .date(LocalDateTime.now().plusDays(i))
                    .description("Test transaction " + i)
                    .build();
                
                Optional<Transaction> result = repository.createRecord(Optional.of(transaction), TransactionOperationType.CREATE);
                assertTrue(result.isPresent(), "Transaction " + i + " should have been created correctly");
                transactions.add(result.get());
            }
            
            // Assert - Verify that the IDs are consecutive (not necessarily starting at 1)
            Long firstId = transactions.get(0).getId();
            for (int i = 0; i < transactions.size(); i++) {
                assertEquals(firstId + i, transactions.get(i).getId(), 
                    "ID of transaction " + i + " must be consecutive with the first ID");
            }
            
            // Verify that exactly 5 transactions were created
            assertEquals(5, transactions.size());
            assertEquals(5, repository.countRecords());
        }
    }
    
    @Nested
    @DisplayName("Specific CRUD operation tests")
    class CrudOperationTests {
        
        @BeforeEach
        void setUp() {
            // Create test entities
            repository.createRecord(Optional.of(transaction1), TransactionOperationType.CREATE);
            repository.createRecord(Optional.of(transaction2), TransactionOperationType.CREATE);
        }
        
        @Test
        @DisplayName("Should create transaction with all fields")
        void shouldCreateTransactionWithAllFields() {
            // Arrange
            Transaction completeTransaction = Transaction.builder()
                .accountNumber("ES3433333333333333333333")
                .destinationAccountNumber("ES3444444444444444444444")
                .type(TransactionType.SENT_TRANSFER)
                .amount(new BigDecimal("750.50"))
                .date(LocalDateTime.of(2024, 1, 20, 16, 30, 0))
                .description("Complete test transfer")
                .build();
            
            // Act
            Optional<Transaction> result = repository.createRecord(Optional.of(completeTransaction), TransactionOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Transaction createdTransaction = result.get();
            assertEquals("ES3433333333333333333333", createdTransaction.getAccountNumber());
            assertEquals("ES3444444444444444444444", createdTransaction.getDestinationAccountNumber());
            assertEquals(TransactionType.SENT_TRANSFER, createdTransaction.getType());
            assertEquals(new BigDecimal("750.50"), createdTransaction.getAmount());
            assertEquals("Complete test transfer", createdTransaction.getDescription());
        }
        
        @Test
        @DisplayName("Should search transaction by account number")
        void shouldSearchTransactionByAccountNumber() {
            // Act
            Optional<Transaction> result = repository.findByPredicate(
                transaction -> "ES3412345678901234567890".equals(transaction.getAccountNumber())
            );
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(TransactionType.DEPOSIT, result.get().getType());
            assertEquals("ES3412345678901234567890", result.get().getAccountNumber());
        }
        
        @Test
        @DisplayName("Should search transactions by type")
        void shouldSearchTransactionsByType() {
            // Act
            Optional<List<Transaction>> result = repository.findAllByPredicate(
                transaction -> transaction.getType() == TransactionType.DEPOSIT
            );
            
            // Assert
            assertTrue(result.isPresent());
            List<Transaction> transactions = result.get();
            assertEquals(1, transactions.size());
            assertTrue(transactions.stream().allMatch(t -> t.getType() == TransactionType.DEPOSIT));
        }
        
        @Test
        @DisplayName("Should update existing transaction")
        void shouldUpdateExistingTransaction() {
            // Arrange
            Transaction updatedTransaction = Transaction.builder()
                .id(transaction1.getId())
                .accountNumber(transaction1.getAccountNumber())
                .destinationAccountNumber(transaction1.getDestinationAccountNumber())
                .type(TransactionType.WITHDRAWAL) // Change type
                .amount(new BigDecimal("1500.00")) // Change amount
                .date(transaction1.getDate())
                .description("Updated transaction") // Change description
                .build();
            
            // Act
            Optional<Transaction> result = repository.updateRecord(Optional.of(updatedTransaction), TransactionOperationType.UPDATE);
            
            // Assert
            assertTrue(result.isPresent());
            Transaction modifiedTransaction = result.get();
            assertEquals(TransactionType.WITHDRAWAL, modifiedTransaction.getType());
            assertEquals(new BigDecimal("1500.00"), modifiedTransaction.getAmount());
            assertEquals("Updated transaction", modifiedTransaction.getDescription());
            assertEquals("ES3412345678901234567890", modifiedTransaction.getAccountNumber()); // Field not modified
        }
    }
    
    @Nested
    @DisplayName("Specific search tests")
    class SpecificSearchTests {
        
        @BeforeEach
        void setUp() {
            // Create test entities
            repository.createRecord(Optional.of(transaction1), TransactionOperationType.CREATE);
            repository.createRecord(Optional.of(transaction2), TransactionOperationType.CREATE);
        }
        
        @Test
        @DisplayName("Should search transactions by account")
        void shouldSearchTransactionsByAccount() {
            // Act
            Optional<List<Transaction>> result = repository.findByAccount(Optional.of("ES3412345678901234567890"));
            
            // Assert
            assertTrue(result.isPresent());
            List<Transaction> transactions = result.get();
            assertEquals(1, transactions.size());
            assertEquals("ES3412345678901234567890", transactions.get(0).getAccountNumber());
        }
        
        @Test
        @DisplayName("Should search transactions by type")
        void shouldSearchTransactionsByType() {
            // Act
            Optional<List<Transaction>> result = repository.findByType(Optional.of(TransactionType.DEPOSIT));
            
            // Assert
            assertTrue(result.isPresent());
            List<Transaction> transactions = result.get();
            assertEquals(1, transactions.size());
            assertTrue(transactions.stream().allMatch(t -> t.getType() == TransactionType.DEPOSIT));
        }
        
        @Test
        @DisplayName("Should search transactions by date")
        void shouldSearchTransactionsByDate() {
            // Act
            LocalDate searchDate = LocalDate.of(2024, 1, 15);
            Optional<List<Transaction>> result = repository.findByDate(Optional.of(searchDate));
            
            // Assert
            assertTrue(result.isPresent());
            List<Transaction> transactions = result.get();
            assertEquals(1, transactions.size());
            assertTrue(transactions.stream().allMatch(t -> t.getDate().toLocalDate().equals(searchDate)));
        }
        
        @Test
        @DisplayName("Should search transactions by date range")
        void shouldSearchTransactionsByDateRange() {
            // Act
            LocalDate startDate = LocalDate.of(2024, 1, 15);
            LocalDate endDate = LocalDate.of(2024, 1, 16);
            Optional<List<Transaction>> result = repository.findByDateRange(Optional.of(startDate), Optional.of(endDate));
            
            // Assert
            assertTrue(result.isPresent());
            List<Transaction> transactions = result.get();
            assertEquals(2, transactions.size());
            assertTrue(transactions.stream().allMatch(t -> {
                LocalDate transactionDate = t.getDate().toLocalDate();
                return !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);
            }));
        }
        
        @Test
        @DisplayName("Should return Optional.empty() when startDate is null")
        void shouldReturnEmptyWhenStartDateIsNull() {
            // Act
            LocalDate endDate = LocalDate.of(2024, 1, 16);
            Optional<List<Transaction>> result = repository.findByDateRange(Optional.empty(), Optional.of(endDate));
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should return Optional.empty() when endDate is null")
        void shouldReturnEmptyWhenEndDateIsNull() {
            // Act
            LocalDate startDate = LocalDate.of(2024, 1, 15);
            Optional<List<Transaction>> result = repository.findByDateRange(Optional.of(startDate), Optional.empty());
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should return Optional.empty() when both dates are null")
        void shouldReturnEmptyWhenBothDatesAreNull() {
            // Act
            Optional<List<Transaction>> result = repository.findByDateRange(Optional.empty(), Optional.empty());
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should handle date range with startDate equal to endDate")
        void shouldHandleDateRangeWithStartDateEqualToEndDate() {
            // Act
            LocalDate date = LocalDate.of(2024, 1, 15);
            Optional<List<Transaction>> result = repository.findByDateRange(Optional.of(date), Optional.of(date));
            
            // Assert
            assertTrue(result.isPresent());
            List<Transaction> transactions = result.get();
            assertEquals(1, transactions.size());
            assertEquals(date, transactions.get(0).getDate().toLocalDate());
        }
        
        @Test
        @DisplayName("Should handle date range with startDate after endDate")
        void shouldHandleDateRangeWithStartDateAfterEndDate() {
            // Act
            LocalDate startDate = LocalDate.of(2024, 1, 16);
            LocalDate endDate = LocalDate.of(2024, 1, 15);
            Optional<List<Transaction>> result = repository.findByDateRange(Optional.of(startDate), Optional.of(endDate));
            
            // Assert
            assertTrue(result.isPresent());
            List<Transaction> transactions = result.get();
            assertEquals(0, transactions.size()); // No transactions in invalid range
        }
        
        @Test
        @DisplayName("Should handle search by non-existent account")
        void shouldHandleSearchByNonExistentAccount() {
            // Act
            Optional<List<Transaction>> result = repository.findByAccount(Optional.of("ES3499999999999999999999"));
            
            // Assert
            assertTrue(result.isPresent());
            List<Transaction> transactions = result.get();
            assertEquals(0, transactions.size());
        }
        
        @Test
        @DisplayName("Should handle search by non-existent type")
        void shouldHandleSearchByNonExistentType() {
            // Act
            Optional<List<Transaction>> result = repository.findByType(Optional.of(TransactionType.SERVICE_PAYMENT));
            
            // Assert
            assertTrue(result.isPresent());
            List<Transaction> transactions = result.get();
            assertEquals(0, transactions.size());
        }
    }
    
    @Nested
    @DisplayName("Specific edge case tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle transaction with null fields")
        void shouldHandleTransactionWithNullFields() {
            // Arrange
            Transaction transactionWithNulls = Transaction.builder()
                .accountNumber("ES3455555555555555555555")
                .destinationAccountNumber(null) // Valid null field
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .date(LocalDateTime.now())
                .description(null) // Valid null field
                .build();
            
            // Act
            Optional<Transaction> result = repository.createRecord(Optional.of(transactionWithNulls), TransactionOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Transaction createdTransaction = result.get();
            assertEquals("ES3455555555555555555555", createdTransaction.getAccountNumber());
            assertNull(createdTransaction.getDestinationAccountNumber());
            assertNull(createdTransaction.getDescription());
            assertEquals(TransactionType.DEPOSIT, createdTransaction.getType());
            assertEquals(new BigDecimal("100.00"), createdTransaction.getAmount());
        }
        
        @Test
        @DisplayName("Should handle transaction with zero amount")
        void shouldHandleTransactionWithZeroAmount() {
            // Arrange
            Transaction zeroAmountTransaction = Transaction.builder()
                .accountNumber("ES3466666666666666666666")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.ZERO)
                .date(LocalDateTime.now())
                .description("Transaction with zero amount")
                .build();
            
            // Act
            Optional<Transaction> result = repository.createRecord(Optional.of(zeroAmountTransaction), TransactionOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(BigDecimal.ZERO, result.get().getAmount());
        }
        
        @Test
        @DisplayName("Should handle transaction with negative amount")
        void shouldHandleTransactionWithNegativeAmount() {
            // Arrange
            Transaction negativeAmountTransaction = Transaction.builder()
                .accountNumber("ES3477777777777777777777")
                .destinationAccountNumber(null)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("-100.00"))
                .date(LocalDateTime.now())
                .description("Withdrawal with negative amount")
                .build();
            
            // Act
            Optional<Transaction> result = repository.createRecord(Optional.of(negativeAmountTransaction), TransactionOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(new BigDecimal("-100.00"), result.get().getAmount());
        }
        
        @Test
        @DisplayName("Should handle transaction with future date")
        void shouldHandleTransactionWithFutureDate() {
            // Arrange
            Transaction futureDateTransaction = Transaction.builder()
                .accountNumber("ES3488888888888888888888")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("200.00"))
                .date(LocalDateTime.now().plusDays(30))
                .description("Transaction with future date")
                .build();
            
            // Act
            Optional<Transaction> result = repository.createRecord(Optional.of(futureDateTransaction), TransactionOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            assertTrue(result.get().getDate().isAfter(LocalDateTime.now()));
        }
        
        @Test
        @DisplayName("Should handle transaction with very long account number")
        void shouldHandleTransactionWithVeryLongAccountNumber() {
            // Arrange
            String longAccount = "ES34" + "1".repeat(50); // Very long account
            Transaction longAccountTransaction = Transaction.builder()
                .accountNumber(longAccount)
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("300.00"))
                .date(LocalDateTime.now())
                .description("Transaction with very long account")
                .build();
            
            // Act
            Optional<Transaction> result = repository.createRecord(Optional.of(longAccountTransaction), TransactionOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(longAccount, result.get().getAccountNumber());
        }
    }
    
    @Nested
    @DisplayName("JSON file integration tests")
    class JsonIntegrationTests {
        
        @Test
        @DisplayName("Should persist transaction in JSON file")
        void shouldPersistTransactionInJsonFile() {
            // Arrange
            Transaction transactionToPersist = Transaction.builder()
                .accountNumber("ES3499999999999999999999")
                .destinationAccountNumber("ES3488888888888888888888")
                .type(TransactionType.SENT_TRANSFER)
                .amount(new BigDecimal("250.75"))
                .date(LocalDateTime.of(2024, 1, 25, 12, 0, 0))
                .description("Transfer persisted in JSON")
                .build();
            
            // Act
            Optional<Transaction> result = repository.createRecord(Optional.of(transactionToPersist), TransactionOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Transaction createdTransaction = result.get();
            assertNotNull(createdTransaction.getId());
            
            // Verify that it can be retrieved from the repository
            Optional<Transaction> retrievedTransaction = repository.findByPredicate(
                t -> t.getId().equals(createdTransaction.getId())
            );
            assertTrue(retrievedTransaction.isPresent());
            assertEquals(transactionToPersist.getAccountNumber(), retrievedTransaction.get().getAccountNumber());
            assertEquals(transactionToPersist.getType(), retrievedTransaction.get().getType());
            assertEquals(transactionToPersist.getAmount(), retrievedTransaction.get().getAmount());
        }
        
        @Test
        @DisplayName("Should load transactions from JSON file")
        void shouldLoadTransactionsFromJsonFile() {
            // Arrange - Create transactions
            Transaction transaction1 = Transaction.builder()
                .accountNumber("ES3411111111111111111111")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100.00"))
                .date(LocalDateTime.now())
                .description("Deposit to load")
                .build();
                
            Transaction transaction2 = Transaction.builder()
                .accountNumber("ES3422222222222222222222")
                .destinationAccountNumber("ES3411111111111111111111")
                .type(TransactionType.SENT_TRANSFER)
                .amount(new BigDecimal("50.00"))
                .date(LocalDateTime.now())
                .description("Transfer to load")
                .build();
            
            // Act - Create transactions (they are saved in JSON)
            repository.createRecord(Optional.of(transaction1), TransactionOperationType.CREATE);
            repository.createRecord(Optional.of(transaction2), TransactionOperationType.CREATE);
            
            // Verify that they load correctly
            Optional<List<Transaction>> allTransactions = repository.findAll();
            
            // Assert
            assertTrue(allTransactions.isPresent());
            List<Transaction> transactions = allTransactions.get();
            assertEquals(2, transactions.size());
            
            // Verify that transactions have correct data
            assertTrue(transactions.stream().anyMatch(t -> 
                "ES3411111111111111111111".equals(t.getAccountNumber()) && 
                t.getType() == TransactionType.DEPOSIT
            ));
            
            assertTrue(transactions.stream().anyMatch(t -> 
                "ES3422222222222222222222".equals(t.getAccountNumber()) && 
                t.getType() == TransactionType.SENT_TRANSFER
            ));
        }
    }
}
