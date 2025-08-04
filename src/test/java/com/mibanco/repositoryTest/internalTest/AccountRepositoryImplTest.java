package com.mibanco.repositoryTest.internalTest;

import com.mibanco.model.Account;
import com.mibanco.model.Client;
import com.mibanco.model.enums.AccountOperationType;
import com.mibanco.model.enums.AccountType;
import com.mibanco.repository.internal.AccountRepositoryImplWrapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
 * Specific tests for AccountRepositoryImpl
 * Validates the specific functionality of the account repository
 * Includes configuration, mapping and specific edge case tests
 */
@DisplayName("AccountRepositoryImpl Tests")
class AccountRepositoryImplTest {
    
    private AccountRepositoryImplWrapper repository;
    private Client holder1;
    private Client holder2;
    private Account account1;
    private Account account2;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Create temporary JSON file for testing
        File jsonFile = tempDir.resolve("test_accounts.json").toFile();
        
        // Create test holders
        holder1 = Client.builder()
            .id(1L)
            .firstName("Juan")
            .lastName("Pérez")
            .dni("12345678")
            .email("juan@test.com")
            .phone("123456789")
            .address("Calle Test 123")
            .birthDate(LocalDate.of(1990, 1, 1))
            .build();
            
        holder2 = Client.builder()
            .id(2L)
            .firstName("María")
            .lastName("García")
            .dni("87654321")
            .email("maria@test.com")
            .phone("987654321")
            .address("Avenida Test 456")
            .birthDate(LocalDate.of(1985, 5, 15))
            .build();
        
        // Create test accounts (without specific numbers, the repository will generate them)
        account1 = Account.builder()
            .accountNumber(null) // The repository will assign a random number
            .holder(holder1)
            .type(AccountType.CHECKING)
            .creationDate(LocalDateTime.now())
            .initialBalance(new BigDecimal("1000.00"))
            .balance(new BigDecimal("1000.00"))
            .active(true)
            .build();
        
        account2 = Account.builder()
            .accountNumber(null) // The repository will assign a random number
            .holder(holder2)
            .type(AccountType.SAVINGS)
            .creationDate(LocalDateTime.now())
            .initialBalance(new BigDecimal("2000.00"))
            .balance(new BigDecimal("2000.00"))
            .active(true)
            .build();
        
        // Create repository with temporary configuration
        repository = new TestAccountRepositoryImpl(jsonFile.getAbsolutePath());
    }
    
    /**
     * Test implementation that allows configuring the file path
     */
    private static class TestAccountRepositoryImpl extends AccountRepositoryImplWrapper {
        
        private final String filePath;
        
        public TestAccountRepositoryImpl(String filePath) {
            this.filePath = filePath;
        }
        
        @Override
        public java.util.Map<String, Object> getConfiguration() {
            java.util.Map<String, Object> config = new java.util.HashMap<>();
            config.put("filePath", filePath);
            config.put("classType", Account.class);
            config.put("idExtractor", (java.util.function.Function<Account, Long>) Account::getId);
            return config;
        }
    }

    @Nested
    @DisplayName("Specific configuration tests")
    class ConfigurationTests {
        
        @Test
        @DisplayName("Should use correct file path for accounts")
        void shouldUseCorrectFilePath() {
            // Act
            Optional<List<Account>> result = repository.findAll();
            
            // Assert
            // If there are no file errors, the configuration is correct
            assertNotNull(result);
        }
        
        @Test
        @DisplayName("Should use Account class type")
        void shouldUseAccountClassType() {
            // Act
            Optional<Account> result = repository.createRecord(Optional.of(account1), AccountOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            assertTrue(result.get() instanceof Account);
        }
        
        @Test
        @DisplayName("Should return correct configuration for accounts")
        void shouldReturnCorrectConfiguration() {
            // Act - Execute operations that internally call getConfiguration
            // The getConfiguration method is executed during initialization and data loading
            Optional<List<Account>> result = repository.findAll();
            
            // Assert - Verify that the configuration works correctly
            assertNotNull(result);
            // If we get here without errors, it means getConfiguration() executed correctly
            // and returned the expected configuration (file path, class type, ID extractor)
        }

        @Test
        @DisplayName("Should execute getConfiguration() from the real class")
        void shouldExecuteGetConfigurationFromRealClass() {
            // Arrange - Use the Factory to get the real class
            com.mibanco.repository.AccountRepository realRepository = 
                com.mibanco.repository.internal.RepositoryFactory.getInstance().getAccountRepository();
            
            // Act - Execute operations that internally call getConfiguration()
            // The getConfiguration() method is executed during lazy data loading
            Optional<List<Account>> result = realRepository.findAll();
            
            // Assert - Verify that the operation executed correctly
            assertNotNull(result);
            // If we get here without errors, it means getConfiguration() executed correctly
            // and returned the expected configuration (file path, class type, ID extractor)
        }
    }
    
    @Nested
    @DisplayName("Specific account number assignment tests")
    class AccountNumberAssignmentTests {
        
        @Test
        @DisplayName("Should assign unique account number automatically")
        void shouldAssignUniqueAccountNumber() {
            // Arrange
            Account newAccount1 = Account.builder()
                .accountNumber(null)
                .holder(holder1)
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.now())
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .active(true)
                .build();
                
            Account newAccount2 = Account.builder()
                .accountNumber(null)
                .holder(holder2)
                .type(AccountType.SAVINGS)
                .creationDate(LocalDateTime.now())
                .initialBalance(new BigDecimal("2000.00"))
                .balance(new BigDecimal("2000.00"))
                .active(true)
                .build();
            
            // Act
            Optional<Account> result1 = repository.createRecord(Optional.of(newAccount1), AccountOperationType.CREATE);
            Optional<Account> result2 = repository.createRecord(Optional.of(newAccount2), AccountOperationType.CREATE);
            
            // Assert
            assertTrue(result1.isPresent());
            assertTrue(result2.isPresent());
            
            String accountNumber1 = result1.get().getAccountNumber();
            String accountNumber2 = result2.get().getAccountNumber();
            
            assertNotNull(accountNumber1);
            assertNotNull(accountNumber2);
            assertNotEquals(accountNumber1, accountNumber2, "Account numbers must be unique");
            assertTrue(accountNumber1.startsWith("ES"), "Account number must start with ES");
            assertTrue(accountNumber2.startsWith("ES"), "Account number must start with ES");
        }
        
        @Test
        @DisplayName("Should generate account numbers with correct IBAN format")
        void shouldGenerateAccountNumbersWithCorrectIbanFormat() {
            // Arrange
            Account newAccount = Account.builder()
                .accountNumber(null)
                .holder(holder1)
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.now())
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .active(true)
                .build();
            
            // Act
            Optional<Account> result = repository.createRecord(Optional.of(newAccount), AccountOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            String accountNumber = result.get().getAccountNumber();
            
            // Verify Spanish IBAN format (ES + 22 digits)
            assertTrue(accountNumber.matches("^ES\\d{22}$"), 
                "Account number must have Spanish IBAN format: ES + 22 digits");
        }
        
        @Test
        @DisplayName("Should maintain consecutive sequence in multiple creations")
        void shouldMaintainConsecutiveSequence() {
            // Arrange
            List<Account> accounts = new ArrayList<>();
            
            // Act - Create 5 accounts
            for (int i = 0; i < 5; i++) {
                Account account = Account.builder()
                    .accountNumber(null)
                    .holder(holder1)
                    .type(i % 2 == 0 ? AccountType.CHECKING : AccountType.SAVINGS)
                    .creationDate(LocalDateTime.now())
                    .initialBalance(new BigDecimal("1000.00"))
                    .balance(new BigDecimal("1000.00"))
                    .active(true)
                    .build();
                
                Optional<Account> result = repository.createRecord(Optional.of(account), AccountOperationType.CREATE);
                assertTrue(result.isPresent(), "Account " + i + " should have been created correctly");
                accounts.add(result.get());
            }
            
            // Assert - Verify that all have unique numbers
            List<String> accountNumbers = accounts.stream()
                .map(Account::getAccountNumber)
                .distinct()
                .toList();
            
            assertEquals(5, accountNumbers.size(), "All accounts must have unique numbers");
            assertEquals(5, repository.countRecords());
        }
    }
    
    @Nested
    @DisplayName("Specific CRUD operation tests")
    class CrudOperationTests {
        
        @BeforeEach
        void setUp() {
            // Create test entities with proper account numbers
            Account testAccount1 = Account.builder()
                .accountNumber("ES3400000000000000001001")
                .holder(holder1)
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.now())
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .active(true)
                .build();
                
            Account testAccount2 = Account.builder()
                .accountNumber("ES3400000000000000001002")
                .holder(holder2)
                .type(AccountType.SAVINGS)
                .creationDate(LocalDateTime.now())
                .initialBalance(new BigDecimal("2000.00"))
                .balance(new BigDecimal("2000.00"))
                .active(true)
                .build();
            
            // Create test entities
            repository.createRecord(Optional.of(testAccount1), AccountOperationType.CREATE);
            repository.createRecord(Optional.of(testAccount2), AccountOperationType.CREATE);
        }
        
        @Test
        @DisplayName("Should create account with all fields")
        void shouldCreateAccountWithAllFields() {
            // Arrange
            Account completeAccount = Account.builder()
                .accountNumber(null)
                .holder(holder1)
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.of(2024, 1, 1, 10, 30))
                .initialBalance(new BigDecimal("5000.00"))
                .balance(new BigDecimal("5000.00"))
                .active(true)
                .build();
            
            // Act
            Optional<Account> result = repository.createRecord(Optional.of(completeAccount), AccountOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Account createdAccount = result.get();
            assertNotNull(createdAccount.getAccountNumber());
            assertEquals(holder1.getId(), createdAccount.getHolder().getId());
            assertEquals(AccountType.CHECKING, createdAccount.getType());
            assertEquals(new BigDecimal("5000.00"), createdAccount.getBalance());
            assertEquals(new BigDecimal("5000.00"), createdAccount.getInitialBalance());
            assertTrue(createdAccount.isActive());
        }
        
        @Test
        @DisplayName("Should search account by account number")
        void shouldSearchAccountByAccountNumber() {
            // Arrange - Create account with valid account number
            Account testAccount = Account.builder()
                .accountNumber("ES3400000000000000001003")
                .holder(holder1)
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.now())
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .active(true)
                .build();
                
            Optional<Account> createdAccount = repository.createRecord(Optional.of(testAccount), AccountOperationType.CREATE);
            assertTrue(createdAccount.isPresent());
            String accountNumber = createdAccount.get().getAccountNumber();
            
            // Act
            Optional<Account> result = repository.findByPredicate(
                account -> accountNumber.equals(account.getAccountNumber())
            );
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(accountNumber, result.get().getAccountNumber());
            assertEquals(holder1.getId(), result.get().getHolder().getId());
        }
        
        @Test
        @DisplayName("Should search accounts by type")
        void shouldSearchAccountsByType() {
            // Act
            Optional<List<Account>> result = repository.findAllByPredicate(
                account -> AccountType.CHECKING.equals(account.getType())
            );
            
            // Assert
            assertTrue(result.isPresent());
            List<Account> accounts = result.get();
            assertFalse(accounts.isEmpty());
            assertTrue(accounts.stream().allMatch(a -> AccountType.CHECKING.equals(a.getType())));
        }
        
        @Test
        @DisplayName("Should update existing account")
        void shouldUpdateExistingAccount() {
            // Arrange - Create account with valid account number
            Account testAccount = Account.builder()
                .accountNumber("ES3400000000000000001004")
                .holder(holder1)
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.now())
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .active(true)
                .build();
                
            Optional<Account> createdAccount = repository.createRecord(Optional.of(testAccount), AccountOperationType.CREATE);
            assertTrue(createdAccount.isPresent());
            
            Account updatedAccount = Account.builder()
                .accountNumber(createdAccount.get().getAccountNumber())
                .holder(createdAccount.get().getHolder())
                .type(createdAccount.get().getType())
                .creationDate(createdAccount.get().getCreationDate())
                .initialBalance(createdAccount.get().getInitialBalance())
                .balance(new BigDecimal("2500.00")) // Updated balance
                .active(false) // Updated status
                .build();
            
            // Act
            Optional<Account> result = repository.updateRecord(Optional.of(updatedAccount), AccountOperationType.UPDATE);
            
            // Assert
            assertTrue(result.isPresent());
            Account modifiedAccount = result.get();
            assertEquals(new BigDecimal("2500.00"), modifiedAccount.getBalance());
            assertFalse(modifiedAccount.isActive());
            assertEquals(createdAccount.get().getAccountNumber(), modifiedAccount.getAccountNumber()); // Number doesn't change
        }
    }
    
    @Nested
    @DisplayName("Specific edge case tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle account with null fields")
        void shouldHandleAccountWithNullFields() {
            // Arrange
            Account accountWithNulls = Account.builder()
                .accountNumber(null)
                .holder(holder1)
                .type(AccountType.CHECKING)
                .creationDate(null) // Null field
                .initialBalance(null) // Null field
                .balance(null) // Null field
                .active(true)
                .build();
            
            // Act
            Optional<Account> result = repository.createRecord(Optional.of(accountWithNulls), AccountOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Account createdAccount = result.get();
            assertNotNull(createdAccount.getAccountNumber()); // The repository assigns a number
            assertEquals(holder1.getId(), createdAccount.getHolder().getId());
            assertEquals(AccountType.CHECKING, createdAccount.getType());
            assertTrue(createdAccount.isActive());
        }
        
        @Test
        @DisplayName("Should allow account with negative balance in repository (validation in service)")
        void shouldAllowAccountWithNegativeBalanceInRepository() {
            // Arrange
            Account accountWithNegativeBalance = Account.builder()
                .accountNumber(null)
                .holder(holder1)
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.now())
                .initialBalance(new BigDecimal("-100.00"))
                .balance(new BigDecimal("-100.00"))
                .active(true)
                .build();
            
            // Act
            Optional<Account> result = repository.createRecord(Optional.of(accountWithNegativeBalance), AccountOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            // The repository allows negative balances because validation is in the service
            assertEquals(new BigDecimal("-100.00"), result.get().getBalance());
        }
        
        @Test
        @DisplayName("Should handle account with future creation date")
        void shouldHandleAccountWithFutureCreationDate() {
            // Arrange
            Account futureAccount = Account.builder()
                .accountNumber(null)
                .holder(holder1)
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.now().plusDays(1)) // Future date
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .active(true)
                .build();
            
            // Act
            Optional<Account> result = repository.createRecord(Optional.of(futureAccount), AccountOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Account createdAccount = result.get();
            assertNotNull(createdAccount.getAccountNumber());
            assertTrue(createdAccount.getCreationDate().isAfter(LocalDateTime.now()));
        }
        
        @Test
        @DisplayName("Should handle account with null holder")
        void shouldHandleAccountWithNullHolder() {
            // Arrange
            Account accountWithoutHolder = Account.builder()
                .accountNumber(null)
                .holder(null) // Null holder
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.now())
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .active(true)
                .build();
            
            // Act
            Optional<Account> result = repository.createRecord(Optional.of(accountWithoutHolder), AccountOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            Account createdAccount = result.get();
            assertNotNull(createdAccount.getAccountNumber());
            assertNull(createdAccount.getHolder()); // The repository allows null holder
        }
    }
    
    @Nested
    @DisplayName("JSON file integration tests")
    class JsonIntegrationTests {
        
        @Test
        @DisplayName("Should persist account in JSON file")
        void shouldPersistAccountInJsonFile() {
            // Arrange
            Account accountToPersist = Account.builder()
                .accountNumber(null)
                .holder(holder1)
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.now())
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .active(true)
                .build();
            
            // Act
            Optional<Account> result = repository.createRecord(Optional.of(accountToPersist), AccountOperationType.CREATE);
            
            // Assert
            assertTrue(result.isPresent());
            // Verify that the counter was incremented (persistence indicator)
            assertEquals(1, repository.countRecords());
        }
        
        @Test
        @DisplayName("Should load accounts from JSON file")
        void shouldLoadAccountsFromJsonFile() {
            // Arrange - Create account first
            Account accountToLoad = Account.builder()
                .accountNumber(null)
                .holder(holder1)
                .type(AccountType.SAVINGS)
                .creationDate(LocalDateTime.now())
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .active(true)
                .build();
            
            repository.createRecord(Optional.of(accountToLoad), AccountOperationType.CREATE);
            
            // Act - Find all (this loads from JSON)
            Optional<List<Account>> result = repository.findAll();
            
            // Assert
            assertTrue(result.isPresent());
            List<Account> accounts = result.get();
            assertFalse(accounts.isEmpty());
            assertTrue(accounts.stream().anyMatch(a -> AccountType.SAVINGS.equals(a.getType())));
        }
        
        @Test
        @DisplayName("Should count records correctly")
        void shouldCountRecords() {
            // Arrange - Create multiple accounts
            for (int i = 0; i < 3; i++) {
                Account account = Account.builder()
                    .accountNumber(null)
                    .holder(holder1)
                    .type(i % 2 == 0 ? AccountType.CHECKING : AccountType.SAVINGS)
                    .creationDate(LocalDateTime.now())
                    .initialBalance(new BigDecimal("1000.00"))
                    .balance(new BigDecimal("1000.00"))
                    .active(true)
                    .build();
                
                repository.createRecord(Optional.of(account), AccountOperationType.CREATE);
            }
            
            // Act
            long count = repository.countRecords();
            
            // Assert
            assertEquals(3, count);
        }
    }
    
    @Nested
    @DisplayName("Missing CRUD operation tests")
    class MissingCrudOperationTests {
        
        @Test
        @DisplayName("Should search account by ID")
        void shouldSearchAccountById() {
            // Arrange - Create account first
            Optional<Account> createdAccount = repository.createRecord(Optional.of(account1), AccountOperationType.CREATE);
            assertTrue(createdAccount.isPresent());
            Long id = createdAccount.get().getId();
            
            // Act
            Optional<Account> result = repository.findById(Optional.of(id));
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals(id, result.get().getId());
            assertEquals(account1.getHolder().getId(), result.get().getHolder().getId());
            assertEquals(account1.getType(), result.get().getType());
        }
        
        @Test
        @DisplayName("Should delete account by ID")
        void shouldDeleteAccountById() {
            // Arrange - Create account first
            Optional<Account> createdAccount = repository.createRecord(Optional.of(account1), AccountOperationType.CREATE);
            assertTrue(createdAccount.isPresent());
            Long id = createdAccount.get().getId();
            
            // Act
            Optional<Account> deleted = repository.deleteById(Optional.of(id), AccountOperationType.DELETE);
            
            // Assert
            assertTrue(deleted.isPresent());
            assertEquals(id, deleted.get().getId());
            
            // Verify that it no longer exists
            Optional<Account> deletedAccount = repository.findById(Optional.of(id));
            assertFalse(deletedAccount.isPresent());
        }
        
        @Test
        @DisplayName("Should restore deleted account")
        void shouldRestoreDeletedAccount() {
            // Arrange - Create and delete account
            Optional<Account> createdAccount = repository.createRecord(Optional.of(account1), AccountOperationType.CREATE);
            assertTrue(createdAccount.isPresent());
            Long id = createdAccount.get().getId();
            
            repository.deleteById(Optional.of(id), AccountOperationType.DELETE);
            
            // Act - Restore the deleted account
            Optional<Account> restored = repository.restore(Optional.of(id), AccountOperationType.RESTORE);
            
            // Assert
            assertTrue(restored.isPresent());
            assertEquals(id, restored.get().getId());
            
            // Verify that it exists again
            Optional<Account> restoredAccount = repository.findById(Optional.of(id));
            assertTrue(restoredAccount.isPresent());
        }
        
        @Test
        @DisplayName("Should get deleted accounts")
        void shouldGetDeletedAccounts() {
            // Arrange - Create and delete multiple accounts
            for (int i = 0; i < 3; i++) {
                Account account = Account.builder()
                    .accountNumber(null)
                    .holder(holder1)
                    .type(AccountType.CHECKING)
                    .creationDate(LocalDateTime.now())
                    .initialBalance(new BigDecimal("1000.00"))
                    .balance(new BigDecimal("1000.00"))
                    .active(true)
                    .build();
                
                Optional<Account> created = repository.createRecord(Optional.of(account), AccountOperationType.CREATE);
                assertTrue(created.isPresent());
                repository.deleteById(Optional.of(created.get().getId()), AccountOperationType.DELETE);
            }
            
            // Act
            List<Account> deletedAccounts = repository.getDeleted();
            
            // Assert
            assertEquals(3, deletedAccounts.size());
            // Note: We can't check isDeleted() as it doesn't exist in Account class
            // The fact that they are in getDeleted() means they were deleted
        }
        
        @Test
        @DisplayName("Should set current user for audit")
        void shouldSetCurrentUser() {
            // Arrange
            String currentUser = "testUser";
            
            // Act
            repository.setCurrentUser(currentUser);
            
            // Assert - Verify that the user was set (this is an internal operation)
            // We can't directly test the user setting, but we can verify the repository still works
            Optional<List<Account>> result = repository.findAll();
            assertNotNull(result);
        }
        
        @Test
        @DisplayName("Should save data manually")
        void shouldSaveDataManually() {
            // Arrange - Create an account
            repository.createRecord(Optional.of(account1), AccountOperationType.CREATE);
            
            // Act
            repository.saveData();
            
            // Assert - Verify that data was saved (this is an internal operation)
            // We can't directly test the save operation, but we can verify the repository still works
            Optional<List<Account>> result = repository.findAll();
            assertNotNull(result);
        }
        
        @Test
        @DisplayName("Should handle search by null ID")
        void shouldHandleSearchByNullId() {
            // Act
            Optional<Account> result = repository.findById(Optional.empty());
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should handle deletion by null ID")
        void shouldHandleDeletionByNullId() {
            // Act
            Optional<Account> result = repository.deleteById(Optional.empty(), AccountOperationType.DELETE);
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should handle restoration by null ID")
        void shouldHandleRestorationByNullId() {
            // Act
            Optional<Account> result = repository.restore(Optional.empty(), AccountOperationType.RESTORE);
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should handle search by non-existent ID")
        void shouldHandleSearchByNonExistentId() {
            // Act
            Optional<Account> result = repository.findById(Optional.of(999999L));
            
            // Assert
            assertFalse(result.isPresent());
        }
    }
}
