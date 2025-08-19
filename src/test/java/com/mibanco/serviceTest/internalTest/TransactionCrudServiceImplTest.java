package com.mibanco.serviceTest.internalTest;

import com.mibanco.dto.TransactionDTO;
import com.mibanco.model.enums.TransactionType;
import com.mibanco.BaseTest;
import com.mibanco.service.TransactionCrudService;
import com.mibanco.service.internal.ServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for TransactionCrudServiceImpl
 * Tests all CRUD operations for transactions
 */
class TransactionCrudServiceImplTest extends BaseTest {

    private TransactionCrudService transactionCrudService;

    @BeforeEach
    void setUp() {
        transactionCrudService = ServiceFactory.getInstance().getTransactionCrudService();
        transactionCrudService.setCurrentUser("testUser");
    }

    @Test
    @DisplayName("Should create transaction successfully")
    void shouldCreateTransactionSuccessfully() {
        // Given - Valid transaction data
        Map<String, String> transactionData = new HashMap<>();
        transactionData.put("accountNumber", "ES3412345678901234567890");
        transactionData.put("destinationAccountNumber", "ES3498765432109876543210");
        transactionData.put("type", "DEPOSIT");
        transactionData.put("amount", "1000.00");
        transactionData.put("description", "Test deposit");

        // When - Create transaction
        Optional<TransactionDTO> result = transactionCrudService.createTransaction(transactionData);

        // Then - Should be created successfully
        assertThat(result).isPresent();
        TransactionDTO createdTransaction = result.get();
        assertThat(createdTransaction.getAccountNumber()).isEqualTo("ES3412345678901234567890");
        assertThat(createdTransaction.getDestinationAccountNumber()).isEqualTo("ES3498765432109876543210");
        assertThat(createdTransaction.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(createdTransaction.getAmount()).isEqualTo(new BigDecimal("1000.00"));
        assertThat(createdTransaction.getDescription()).isEqualTo("Test deposit");
        assertThat(createdTransaction.getId()).isNotNull();
        assertThat(createdTransaction.getDate()).isNotNull();
    }

    @Test
    @DisplayName("Should create transaction with minimal data")
    void shouldCreateTransactionWithMinimalData() {
        // Given - Minimal transaction data
        Map<String, String> transactionData = new HashMap<>();
        transactionData.put("accountNumber", "ES3412345678901234567890");
        transactionData.put("type", "WITHDRAWAL");
        transactionData.put("amount", "500.00");

        // When - Create transaction
        Optional<TransactionDTO> result = transactionCrudService.createTransaction(transactionData);

        // Then - Should be created successfully
        assertThat(result).isPresent();
        TransactionDTO createdTransaction = result.get();
        assertThat(createdTransaction.getAccountNumber()).isEqualTo("ES3412345678901234567890");
        assertThat(createdTransaction.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(createdTransaction.getAmount()).isEqualTo(new BigDecimal("500.00"));
        assertThat(createdTransaction.getId()).isNotNull();
        assertThat(createdTransaction.getDate()).isNotNull();
    }

    @Test
    @DisplayName("Should handle create transaction with invalid data")
    void shouldHandleCreateTransactionWithInvalidData() {
        // Given - Invalid transaction data (missing required fields)
        Map<String, String> transactionData = new HashMap<>();
        transactionData.put("accountNumber", "ES3412345678901234567890");
        // Missing type and amount

        // When - Create transaction
        Optional<TransactionDTO> result = transactionCrudService.createTransaction(transactionData);

        // Then - Should return empty due to validation failure
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should get transaction by ID successfully")
    void shouldGetTransactionByIdSuccessfully() {
        // Given - Create a transaction first
        Map<String, String> transactionData = new HashMap<>();
        transactionData.put("accountNumber", "ES3412345678901234567890");
        transactionData.put("type", "DEPOSIT");
        transactionData.put("amount", "1000.00");
        transactionData.put("description", "Test transaction");

        Optional<TransactionDTO> createdTransaction = transactionCrudService.createTransaction(transactionData);
        assertThat(createdTransaction).isPresent();
        Long transactionId = createdTransaction.get().getId();

        // When - Get transaction by ID
        Optional<TransactionDTO> result = transactionCrudService.getTransactionById(Optional.of(transactionId));

        // Then - Should return the transaction
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(transactionId);
        assertThat(result.get().getAccountNumber()).isEqualTo("ES3412345678901234567890");
    }

    @Test
    @DisplayName("Should return empty when getting non-existent transaction")
    void shouldReturnEmptyWhenGettingNonExistentTransaction() {
        // When - Get transaction with non-existent ID
        Optional<TransactionDTO> result = transactionCrudService.getTransactionById(Optional.of(999999L));

        // Then - Should return empty
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle get transaction by null ID")
    void shouldHandleGetTransactionByNullId() {
        // When - Get transaction with null ID
        Optional<TransactionDTO> result = transactionCrudService.getTransactionById(Optional.empty());

        // Then - Should return empty
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should get all transactions")
    void shouldGetAllTransactions() {
        // Given - Create some transactions
        Map<String, String> transactionData1 = new HashMap<>();
        transactionData1.put("accountNumber", "ES3412345678901234567890");
        transactionData1.put("type", "DEPOSIT");
        transactionData1.put("amount", "1000.00");

        Map<String, String> transactionData2 = new HashMap<>();
        transactionData2.put("accountNumber", "ES3498765432109876543210");
        transactionData2.put("type", "WITHDRAWAL");
        transactionData2.put("amount", "500.00");

        transactionCrudService.createTransaction(transactionData1);
        transactionCrudService.createTransaction(transactionData2);

        // When - Get all transactions
        Optional<List<TransactionDTO>> result = transactionCrudService.getAllTransactions();

        // Then - Should return list of transactions
        assertThat(result).isPresent();
        List<TransactionDTO> transactions = result.get();
        assertThat(transactions).isNotEmpty();
        assertThat(transactions.size()).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Should search transactions by account number")
    void shouldSearchTransactionsByAccountNumber() {
        // Given - Create transactions with specific account
        String accountNumber = "ES3412345678901234567890";
        Map<String, String> transactionData1 = new HashMap<>();
        transactionData1.put("accountNumber", accountNumber);
        transactionData1.put("type", "DEPOSIT");
        transactionData1.put("amount", "1000.00");

        Map<String, String> transactionData2 = new HashMap<>();
        transactionData2.put("accountNumber", accountNumber);
        transactionData2.put("type", "WITHDRAWAL");
        transactionData2.put("amount", "500.00");

        transactionCrudService.createTransaction(transactionData1);
        transactionCrudService.createTransaction(transactionData2);

        // When - Search by account number
        Optional<List<TransactionDTO>> result = transactionCrudService.searchByAccount(Optional.of(accountNumber));

        // Then - Should return transactions for that account
        assertThat(result).isPresent();
        List<TransactionDTO> transactions = result.get();
        assertThat(transactions).isNotEmpty();
        assertThat(transactions).allMatch(t -> t.getAccountNumber().equals(accountNumber));
    }

    @Test
    @DisplayName("Should return empty list when searching by non-existent account")
    void shouldReturnEmptyListWhenSearchingByNonExistentAccount() {
        // When - Search by non-existent account
        Optional<List<TransactionDTO>> result = transactionCrudService.searchByAccount(Optional.of("ES3499999999999999999999"));

        // Then - Should return empty list (not empty Optional)
        assertThat(result).isPresent();
        assertThat(result.get()).isEmpty();
    }

    @Test
    @DisplayName("Should search transactions by type")
    void shouldSearchTransactionsByType() {
        // Given - Create transactions with specific type
        Map<String, String> transactionData1 = new HashMap<>();
        transactionData1.put("accountNumber", "ES3412345678901234567890");
        transactionData1.put("type", "DEPOSIT");
        transactionData1.put("amount", "1000.00");

        Map<String, String> transactionData2 = new HashMap<>();
        transactionData2.put("accountNumber", "ES3498765432109876543210");
        transactionData2.put("type", "DEPOSIT");
        transactionData2.put("amount", "500.00");

        transactionCrudService.createTransaction(transactionData1);
        transactionCrudService.createTransaction(transactionData2);

        // When - Search by type
        Optional<List<TransactionDTO>> result = transactionCrudService.searchByType(Optional.of(TransactionType.DEPOSIT));

        // Then - Should return transactions of that type
        assertThat(result).isPresent();
        List<TransactionDTO> transactions = result.get();
        assertThat(transactions).isNotEmpty();
        assertThat(transactions).allMatch(t -> t.getType() == TransactionType.DEPOSIT);
    }

    @Test
    @DisplayName("Should search transactions by date")
    void shouldSearchTransactionsByDate() {
        // Given - Create a transaction
        Map<String, String> transactionData = new HashMap<>();
        transactionData.put("accountNumber", "ES3412345678901234567890");
        transactionData.put("type", "DEPOSIT");
        transactionData.put("amount", "1000.00");

        Optional<TransactionDTO> createdTransaction = transactionCrudService.createTransaction(transactionData);
        assertThat(createdTransaction).isPresent();

        LocalDate transactionDate = createdTransaction.get().getDate().toLocalDate();

        // When - Search by date
        Optional<List<TransactionDTO>> result = transactionCrudService.searchByDate(Optional.of(transactionDate));

        // Then - Should return transactions for that date
        assertThat(result).isPresent();
        List<TransactionDTO> transactions = result.get();
        assertThat(transactions).isNotEmpty();
        assertThat(transactions).allMatch(t -> t.getDate().toLocalDate().equals(transactionDate));
    }

    @Test
    @DisplayName("Should search transactions by date range")
    void shouldSearchTransactionsByDateRange() {
        // Given - Create transactions
        Map<String, String> transactionData1 = new HashMap<>();
        transactionData1.put("accountNumber", "ES3412345678901234567890");
        transactionData1.put("type", "DEPOSIT");
        transactionData1.put("amount", "1000.00");

        Map<String, String> transactionData2 = new HashMap<>();
        transactionData2.put("accountNumber", "ES3498765432109876543210");
        transactionData2.put("type", "WITHDRAWAL");
        transactionData2.put("amount", "500.00");

        transactionCrudService.createTransaction(transactionData1);
        transactionCrudService.createTransaction(transactionData2);

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(1);
        LocalDate endDate = today.plusDays(1);

        // When - Search by date range
        Optional<List<TransactionDTO>> result = transactionCrudService.searchByDateRange(Optional.of(startDate), Optional.of(endDate));

        // Then - Should return transactions in the range
        assertThat(result).isPresent();
        List<TransactionDTO> transactions = result.get();
        assertThat(transactions).isNotEmpty();
        assertThat(transactions).allMatch(t -> {
            LocalDate transactionDate = t.getDate().toLocalDate();
            return !transactionDate.isBefore(startDate) && !transactionDate.isAfter(endDate);
        });
    }

    @Test
    @DisplayName("Should delete transaction successfully")
    void shouldDeleteTransactionSuccessfully() {
        // Given - Create a transaction
        Map<String, String> transactionData = new HashMap<>();
        transactionData.put("accountNumber", "ES3412345678901234567890");
        transactionData.put("type", "DEPOSIT");
        transactionData.put("amount", "1000.00");

        Optional<TransactionDTO> createdTransaction = transactionCrudService.createTransaction(transactionData);
        assertThat(createdTransaction).isPresent();
        Long transactionId = createdTransaction.get().getId();

        // When - Delete transaction
        boolean result = transactionCrudService.deleteTransaction(Optional.of(transactionId));

        // Then - Should be deleted successfully
        assertThat(result).isTrue();

        // Verify it's no longer retrievable
        Optional<TransactionDTO> retrievedTransaction = transactionCrudService.getTransactionById(Optional.of(transactionId));
        assertThat(retrievedTransaction).isEmpty();
    }

    @Test
    @DisplayName("Should return false when deleting non-existent transaction")
    void shouldReturnFalseWhenDeletingNonExistentTransaction() {
        // When - Delete non-existent transaction
        boolean result = transactionCrudService.deleteTransaction(Optional.of(999999L));

        // Then - Should return false
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should handle delete transaction with null ID")
    void shouldHandleDeleteTransactionWithNullId() {
        // When - Delete transaction with null ID
        boolean result = transactionCrudService.deleteTransaction(Optional.empty());

        // Then - Should return false
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should count transactions correctly")
    void shouldCountTransactionsCorrectly() {
        // Given - Get initial count
        long initialCount = transactionCrudService.countTransactions();

        // When - Create a new transaction
        Map<String, String> transactionData = new HashMap<>();
        transactionData.put("accountNumber", "ES3412345678901234567890");
        transactionData.put("type", "DEPOSIT");
        transactionData.put("amount", "1000.00");

        transactionCrudService.createTransaction(transactionData);

        // Then - Count should increase
        long finalCount = transactionCrudService.countTransactions();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should handle search by account with null account number")
    void shouldHandleSearchByAccountWithNullAccountNumber() {
        // When - Search with null account number
        Optional<List<TransactionDTO>> result = transactionCrudService.searchByAccount(Optional.empty());

        // Then - Should return empty
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle search by type with null type")
    void shouldHandleSearchByTypeWithNullType() {
        // When - Search with null type
        Optional<List<TransactionDTO>> result = transactionCrudService.searchByType(Optional.empty());

        // Then - Should return empty
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle search by date with null date")
    void shouldHandleSearchByDateWithNullDate() {
        // When - Search with null date
        Optional<List<TransactionDTO>> result = transactionCrudService.searchByDate(Optional.empty());

        // Then - Should return empty
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should handle search by date range with null dates")
    void shouldHandleSearchByDateRangeWithNullDates() {
        // When - Search with null start date
        Optional<List<TransactionDTO>> result1 = transactionCrudService.searchByDateRange(Optional.empty(), Optional.of(LocalDate.now()));

        // Then - Should return empty
        assertThat(result1).isEmpty();

        // When - Search with null end date
        Optional<List<TransactionDTO>> result2 = transactionCrudService.searchByDateRange(Optional.of(LocalDate.now()), Optional.empty());

        // Then - Should return empty
        assertThat(result2).isEmpty();
    }

    @Test
    @DisplayName("Should create transaction with all transaction types")
    void shouldCreateTransactionWithAllTransactionTypes() {
        // Test all transaction types
        TransactionType[] types = TransactionType.values();
        
        for (TransactionType type : types) {
            // Given - Transaction data with specific type
            Map<String, String> transactionData = new HashMap<>();
            transactionData.put("accountNumber", "ES3412345678901234567890");
            transactionData.put("type", type.name());
            transactionData.put("amount", "100.00");
            transactionData.put("description", "Test " + type.name());

            // When - Create transaction
            Optional<TransactionDTO> result = transactionCrudService.createTransaction(transactionData);

            // Then - Should be created successfully
            assertThat(result).isPresent();
            assertThat(result.get().getType()).isEqualTo(type);
        }
    }

    @Test
    @DisplayName("Should create transaction with different amount formats")
    void shouldCreateTransactionWithDifferentAmountFormats() {
        // Test different amount formats
        String[] amounts = {"100", "100.50", "0.01", "999999.99"};

        for (String amount : amounts) {
            // Given - Transaction data with specific amount
            Map<String, String> transactionData = new HashMap<>();
            transactionData.put("accountNumber", "ES3412345678901234567890");
            transactionData.put("type", "DEPOSIT");
            transactionData.put("amount", amount);

            // When - Create transaction
            Optional<TransactionDTO> result = transactionCrudService.createTransaction(transactionData);

            // Then - Should be created successfully
            assertThat(result).isPresent();
            assertThat(result.get().getAmount()).isEqualTo(new BigDecimal(amount));
        }
    }
}
