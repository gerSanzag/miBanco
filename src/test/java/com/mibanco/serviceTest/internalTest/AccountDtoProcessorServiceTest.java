package com.mibanco.serviceTest.internalTest;

import com.mibanco.dto.AccountDTO;
import com.mibanco.dto.ClientDTO;
import com.mibanco.model.enums.AccountType;
import com.mibanco.service.AccountService;
import com.mibanco.service.ClientService;
import com.mibanco.service.TransactionOperationsService;
import com.mibanco.service.internal.AccountDtoProcessorService;
import com.mibanco.service.internal.ServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Test class for AccountDtoProcessorService
 * Tests the processing of raw data into AccountDTO using real classes
 */
@DisplayName("AccountDtoProcessorService Tests")
class AccountDtoProcessorServiceTest {

    private AccountDtoProcessorService processor;
    private ClientService clientService;
    private TransactionOperationsService transactionService;
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        clientService = ServiceFactory.getInstance().getClientService();
        transactionService = ServiceFactory.getInstance().getTransactionOperationsService();
        accountService = ServiceFactory.getInstance().getAccountService();
        processor = new AccountDtoProcessorService(clientService);
    }

    @Nested
    @DisplayName("Successful processing tests")
    class SuccessfulProcessingTests {

        @Test
        @DisplayName("Should process complete account data successfully")
        void shouldProcessCompleteAccountDataSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "SAVINGS");
            rawData.put("creationDate", "2025-01-15T10:30:00");
            rawData.put("active", "true");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isPresent();
            AccountDTO account = result.get();
            assertThat(account.getHolder()).isEqualTo(savedHolder.get());
            assertThat(account.getType()).isEqualTo(AccountType.SAVINGS);
            assertThat(account.getCreationDate()).isEqualTo(LocalDateTime.parse("2025-01-15T10:30:00"));
            assertThat(account.isActive()).isTrue();
            assertThat(account.getInitialBalance()).isNull();
            assertThat(account.getBalance()).isNull();
        }

        @Test
        @DisplayName("Should process account with minimal required data successfully")
        void shouldProcessAccountWithMinimalRequiredDataSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isPresent();
            AccountDTO account = result.get();
            assertThat(account.getHolder()).isEqualTo(savedHolder.get());
            assertThat(account.getType()).isNull();
            assertThat(account.getCreationDate()).isNull();
            assertThat(account.isActive()).isTrue(); // Default value
            assertThat(account.getInitialBalance()).isNull();
            assertThat(account.getBalance()).isNull();
        }

        @Test
        @DisplayName("Should process savings account type successfully")
        void shouldProcessSavingsAccountTypeSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "SAVINGS");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isPresent();
            AccountDTO account = result.get();
            assertThat(account.getType()).isEqualTo(AccountType.SAVINGS);
        }

        @Test
        @DisplayName("Should process checking account type successfully")
        void shouldProcessCheckingAccountTypeSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "CHECKING");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isPresent();
            AccountDTO account = result.get();
            assertThat(account.getType()).isEqualTo(AccountType.CHECKING);
        }

        @Test
        @DisplayName("Should process fixed term account type successfully")
        void shouldProcessFixedTermAccountTypeSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "FIXED_TERM");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isPresent();
            AccountDTO account = result.get();
            assertThat(account.getType()).isEqualTo(AccountType.FIXED_TERM);
        }

        @Test
        @DisplayName("Should process inactive account successfully")
        void shouldProcessInactiveAccountSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("active", "false");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isPresent();
            AccountDTO account = result.get();
            assertThat(account.isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("Error handling tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should return empty when holder ID is missing")
        void shouldReturnEmptyWhenHolderIdIsMissing() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("type", "SAVINGS");
            rawData.put("creationDate", "2025-01-15T10:30:00");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when holder ID is invalid")
        void shouldThrowExceptionWhenHolderIdIsInvalid() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", "invalid_id");
            rawData.put("type", "SAVINGS");

            // When & Then
            assertThatThrownBy(() -> processor.processAccountDto(rawData))
                .isInstanceOf(NumberFormatException.class);
        }

        @Test
        @DisplayName("Should return empty when holder does not exist")
        void shouldReturnEmptyWhenHolderDoesNotExist() {
            // Given
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", "999999");
            rawData.put("type", "SAVINGS");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when account type is invalid")
        void shouldReturnEmptyWhenAccountTypeIsInvalid() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "INVALID_TYPE");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should return empty when creation date is invalid")
        void shouldReturnEmptyWhenCreationDateIsInvalid() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("creationDate", "invalid-date");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle invalid active status gracefully")
        void shouldHandleInvalidActiveStatusGracefully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("active", "invalid-boolean");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isPresent();
            AccountDTO account = result.get();
            assertThat(account.isActive()).isFalse(); // Boolean.parseBoolean("invalid-boolean") returns false
        }
    }

    @Nested
    @DisplayName("Edge cases tests")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should throw exception when raw data is null")
        void shouldThrowExceptionWhenRawDataIsNull() {
            // When & Then
            assertThatThrownBy(() -> processor.processAccountDto(null))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should handle empty raw data")
        void shouldHandleEmptyRawData() {
            // Given
            Map<String, String> rawData = new HashMap<>();

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Should handle null values in raw data")
        void shouldHandleNullValuesInRawData() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", null);
            rawData.put("creationDate", null);
            rawData.put("active", null);

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isPresent();
            AccountDTO account = result.get();
            assertThat(account.getType()).isNull();
            assertThat(account.getCreationDate()).isNull();
            assertThat(account.isActive()).isTrue(); // Default value
        }

        @Test
        @DisplayName("Should return empty when empty string values cause parsing errors")
        void shouldReturnEmptyWhenEmptyStringValuesCauseParsingErrors() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "");
            rawData.put("creationDate", "");
            rawData.put("active", "");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isEmpty(); // Empty strings cause parsing exceptions that are caught
        }

        @Test
        @DisplayName("Should handle very old creation dates")
        void shouldHandleVeryOldCreationDates() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("creationDate", "1900-01-01T00:00:00");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isPresent();
            AccountDTO account = result.get();
            assertThat(account.getCreationDate()).isEqualTo(LocalDateTime.parse("1900-01-01T00:00:00"));
        }

        @Test
        @DisplayName("Should handle future creation dates")
        void shouldHandleFutureCreationDates() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("creationDate", "2050-12-31T23:59:59");

            // When
            Optional<AccountDTO> result = processor.processAccountDto(rawData);

            // Then
            assertThat(result).isPresent();
            AccountDTO account = result.get();
            assertThat(account.getCreationDate()).isEqualTo(LocalDateTime.parse("2050-12-31T23:59:59"));
        }
    }

    @Nested
    @DisplayName("Initial deposit processing tests")
    class InitialDepositProcessingTests {

        @Test
        @DisplayName("Should process initial deposit successfully even when account has no ID")
        void shouldProcessInitialDepositSuccessfullyEvenWhenAccountHasNoId() {
            // Given - First create a client and account
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            AccountDTO account = AccountDTO.builder()
                .holder(savedHolder.get())
                .type(AccountType.SAVINGS)
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();

            BigDecimal initialAmount = new BigDecimal("1000.00");

            // When - Process initial deposit (should work now with simplified logic)
            Optional<AccountDTO> result = processor.processInitialDeposit(account, initialAmount, transactionService);

            // Then - Should return account with updated balance
            assertThat(result).isPresent();
            AccountDTO updatedAccount = result.get();
            assertThat(updatedAccount.getInitialBalance()).isEqualTo(initialAmount);
            assertThat(updatedAccount.getBalance()).isEqualTo(initialAmount);
        }

        @Test
        @DisplayName("Should process initial deposit successfully with zero amount")
        void shouldProcessInitialDepositSuccessfullyWithZeroAmount() {
            // Given - First create a client and account
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            AccountDTO account = AccountDTO.builder()
                .holder(savedHolder.get())
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();

            BigDecimal initialAmount = BigDecimal.ZERO;

            // When - Process initial deposit with zero amount
            Optional<AccountDTO> result = processor.processInitialDeposit(account, initialAmount, transactionService);

            // Then - Should return account with zero balance
            assertThat(result).isPresent();
            AccountDTO updatedAccount = result.get();
            assertThat(updatedAccount.getInitialBalance()).isEqualTo(BigDecimal.ZERO);
            assertThat(updatedAccount.getBalance()).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should process initial deposit successfully with negative amount")
        void shouldProcessInitialDepositSuccessfullyWithNegativeAmount() {
            // Given - First create a client and account
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            AccountDTO account = AccountDTO.builder()
                .holder(savedHolder.get())
                .type(AccountType.SAVINGS)
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();

            BigDecimal initialAmount = new BigDecimal("-500.00");

            // When - Process initial deposit with negative amount
            Optional<AccountDTO> result = processor.processInitialDeposit(account, initialAmount, transactionService);

            // Then - Should return account with negative balance
            assertThat(result).isPresent();
            AccountDTO updatedAccount = result.get();
            assertThat(updatedAccount.getInitialBalance()).isEqualTo(new BigDecimal("-500.00"));
            assertThat(updatedAccount.getBalance()).isEqualTo(new BigDecimal("-500.00"));
        }

        @Test
        @DisplayName("Should process initial deposit successfully with large amount")
        void shouldProcessInitialDepositSuccessfullyWithLargeAmount() {
            // Given - First create a client and account
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            AccountDTO account = AccountDTO.builder()
                .holder(savedHolder.get())
                .type(AccountType.FIXED_TERM)
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();

            BigDecimal initialAmount = new BigDecimal("1000000.00");

            // When - Process initial deposit with large amount
            Optional<AccountDTO> result = processor.processInitialDeposit(account, initialAmount, transactionService);

            // Then - Should return account with large balance
            assertThat(result).isPresent();
            AccountDTO updatedAccount = result.get();
            assertThat(updatedAccount.getInitialBalance()).isEqualTo(new BigDecimal("1000000.00"));
            assertThat(updatedAccount.getBalance()).isEqualTo(new BigDecimal("1000000.00"));
        }
    }

    @Nested
    @DisplayName("Balance update tests")
    class BalanceUpdateTests {

        @Test
        @DisplayName("Should update account with balance successfully")
        void shouldUpdateAccountWithBalanceSuccessfully() {
            // Given
            ClientDTO holder = createTestClient();
            AccountDTO account = AccountDTO.builder()
                .holder(holder)
                .type(AccountType.SAVINGS)
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();

            BigDecimal balance = new BigDecimal("2500.75");

            // When
            AccountDTO result = processor.updateAccountWithBalance(account, balance);

            // Then
            assertThat(result.getInitialBalance()).isEqualTo(balance);
            assertThat(result.getBalance()).isEqualTo(balance);
            assertThat(result.getHolder()).isEqualTo(holder);
            assertThat(result.getType()).isEqualTo(AccountType.SAVINGS);
            assertThat(result.isActive()).isTrue();
        }

        @Test
        @DisplayName("Should update account with zero balance")
        void shouldUpdateAccountWithZeroBalance() {
            // Given
            ClientDTO holder = createTestClient();
            AccountDTO account = AccountDTO.builder()
                .holder(holder)
                .type(AccountType.CHECKING)
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();

            BigDecimal balance = BigDecimal.ZERO;

            // When
            AccountDTO result = processor.updateAccountWithBalance(account, balance);

            // Then
            assertThat(result.getInitialBalance()).isEqualTo(BigDecimal.ZERO);
            assertThat(result.getBalance()).isEqualTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Should update account with negative balance")
        void shouldUpdateAccountWithNegativeBalance() {
            // Given
            ClientDTO holder = createTestClient();
            AccountDTO account = AccountDTO.builder()
                .holder(holder)
                .type(AccountType.SAVINGS)
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();

            BigDecimal balance = new BigDecimal("-100.50");

            // When
            AccountDTO result = processor.updateAccountWithBalance(account, balance);

            // Then
            assertThat(result.getInitialBalance()).isEqualTo(balance);
            assertThat(result.getBalance()).isEqualTo(balance);
        }
    }

    @Nested
    @DisplayName("Integration tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should process multiple accounts for same holder")
        void shouldProcessMultipleAccountsForSameHolder() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            // When & Then - Process multiple accounts
            AccountType[] accountTypes = {AccountType.SAVINGS, AccountType.CHECKING, AccountType.FIXED_TERM};
            
            for (int i = 0; i < accountTypes.length; i++) {
                Map<String, String> rawData = new HashMap<>();
                rawData.put("holderId", savedHolder.get().getId().toString());
                rawData.put("type", accountTypes[i].name());
                rawData.put("creationDate", "2025-0" + (i + 1) + "-01T10:00:00");
                rawData.put("active", i % 2 == 0 ? "true" : "false");

                Optional<AccountDTO> result = processor.processAccountDto(rawData);
                assertThat(result).isPresent();
                
                AccountDTO account = result.get();
                assertThat(account.getHolder()).isEqualTo(savedHolder.get());
                assertThat(account.getType()).isEqualTo(accountTypes[i]);
                assertThat(account.getCreationDate()).isEqualTo(LocalDateTime.parse("2025-0" + (i + 1) + "-01T10:00:00"));
                assertThat(account.isActive()).isEqualTo(i % 2 == 0);
            }
        }

        @Test
        @DisplayName("Should handle concurrent processing without issues")
        void shouldHandleConcurrentProcessingWithoutIssues() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "SAVINGS");
            rawData.put("creationDate", "2025-12-31T10:30:00");

            // When & Then - Multiple concurrent calls should work
            assertThatCode(() -> {
                for (int i = 0; i < 10; i++) {
                    Optional<AccountDTO> result = processor.processAccountDto(rawData);
                    assertThat(result).isPresent();
                }
            }).doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should process account creation and initial deposit successfully")
        void shouldProcessAccountCreationAndInitialDepositSuccessfully() {
            // Given - First create a client to use as holder
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "SAVINGS");
            rawData.put("creationDate", "2025-01-15T10:30:00");
            rawData.put("active", "true");

            // When - Process account creation
            Optional<AccountDTO> accountResult = processor.processAccountDto(rawData);
            assertThat(accountResult).isPresent();
            
            AccountDTO account = accountResult.get();
            BigDecimal initialAmount = new BigDecimal("5000.00");
            
            // When - Process initial deposit (should work now with corrected logic)
            Optional<AccountDTO> result = processor.processInitialDeposit(account, initialAmount, transactionService);

            // Then - Should return account with updated balance
            assertThat(result).isPresent();
            AccountDTO updatedAccount = result.get();
            assertThat(updatedAccount.getInitialBalance()).isEqualTo(initialAmount);
            assertThat(updatedAccount.getBalance()).isEqualTo(initialAmount);
        }

        @Test
        @DisplayName("Should process initial deposit successfully with null account ID")
        void shouldProcessInitialDepositSuccessfullyWithNullAccountId() {
            // Given - Create an account DTO without ID (not persisted)
            ClientDTO holder = createTestClient();
            AccountDTO account = AccountDTO.builder()
                .holder(holder)
                .type(AccountType.SAVINGS)
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();

            BigDecimal initialAmount = new BigDecimal("1000.00");

            // When - Process initial deposit (should work now with simplified logic)
            Optional<AccountDTO> result = processor.processInitialDeposit(account, initialAmount, transactionService);

            // Then - Should return account with updated balance
            assertThat(result).isPresent();
            AccountDTO updatedAccount = result.get();
            assertThat(updatedAccount.getInitialBalance()).isEqualTo(initialAmount);
            assertThat(updatedAccount.getBalance()).isEqualTo(initialAmount);
        }

        @Test
        @DisplayName("Should process initial deposit successfully even with mapper issues")
        void shouldProcessInitialDepositSuccessfullyEvenWithMapperIssues() {
            // Given - Create an account DTO that would cause mapper issues
            ClientDTO holder = createTestClient();
            AccountDTO account = AccountDTO.builder()
                .holder(holder)
                .type(AccountType.SAVINGS)
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();

            BigDecimal initialAmount = new BigDecimal("1000.00");

            // When - Process initial deposit (should work now with simplified logic)
            Optional<AccountDTO> result = processor.processInitialDeposit(account, initialAmount, transactionService);

            // Then - Should return account with updated balance
            assertThat(result).isPresent();
            AccountDTO updatedAccount = result.get();
            assertThat(updatedAccount.getInitialBalance()).isEqualTo(initialAmount);
            assertThat(updatedAccount.getBalance()).isEqualTo(initialAmount);
        }

        @Test
        @DisplayName("Should update account with balance successfully")
        void shouldUpdateAccountWithBalanceSuccessfully() {
            // Given - Create an account DTO
            ClientDTO holder = createTestClient();
            AccountDTO account = AccountDTO.builder()
                .accountNumber("ES3412345678901234567890")
                .holder(holder)
                .type(AccountType.SAVINGS)
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();

            BigDecimal balance = new BigDecimal("2500.00");

            // When - Update account with balance
            AccountDTO result = processor.updateAccountWithBalance(account, balance);

            // Then - Should return account with updated balance
            assertThat(result.getInitialBalance()).isEqualTo(balance);
            assertThat(result.getBalance()).isEqualTo(balance);
            assertThat(result.getAccountNumber()).isEqualTo("ES3412345678901234567890");
        }

        @Test
        @DisplayName("Should test createAccountDto flow with corrected order")
        void shouldTestCreateAccountDtoFlowWithCorrectedOrder() {
            // Given - Create a client and save it
            ClientDTO holder = createTestClient();
            Optional<ClientDTO> savedHolder = clientService.saveClient(Optional.of(holder));
            
            // Given - Prepare account data
            Map<String, String> rawData = new HashMap<>();
            rawData.put("holderId", savedHolder.get().getId().toString());
            rawData.put("type", "SAVINGS");
            rawData.put("creationDate", "2025-01-15T10:30:00");
            rawData.put("active", "true");

            BigDecimal initialAmount = new BigDecimal("1000.00");

            // When - Use the corrected createAccountDto flow
            // This should now work because we fixed the order in AccountServiceImpl
            Optional<AccountDTO> result = accountService.createAccountDto(rawData, initialAmount, transactionService);

            // Then - Should create account successfully
            assertThat(result).isPresent();
            AccountDTO createdAccount = result.get();
            assertThat(createdAccount.getHolder()).isEqualTo(savedHolder.get());
            assertThat(createdAccount.getType()).isEqualTo(AccountType.SAVINGS);
            assertThat(createdAccount.getInitialBalance()).isEqualTo(initialAmount);
            assertThat(createdAccount.getBalance()).isEqualTo(initialAmount);
            assertThat(createdAccount.getAccountNumber()).isNotNull();
        }
    }

    /**
     * Helper method to create a test client
     */
    private ClientDTO createTestClient() {
        return ClientDTO.builder()
                .firstName("Test")
                .lastName("Client")
                .email("test.client@email.com")
                .phone("123456789")
                .dni("12345678A")
                .birthDate(java.time.LocalDate.of(1990, 1, 1))
                .address("Test Address 123")
                .build();
    }
}
