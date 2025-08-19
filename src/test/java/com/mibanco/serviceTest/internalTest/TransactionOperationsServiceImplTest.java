package com.mibanco.serviceTest.internalTest;

import com.mibanco.dto.TransactionDTO;
import com.mibanco.model.enums.TransactionType;
import com.mibanco.service.TransactionOperationsService;
import com.mibanco.service.internal.ServiceFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for TransactionOperationsService
 */
class TransactionOperationsServiceImplTest {

    private TransactionOperationsService transactionOperationsService;

    @BeforeEach
    void setUp() {
        transactionOperationsService = ServiceFactory.getInstance().getTransactionOperationsService();
    }

    // ========== DEPOSIT TESTS ==========

    @Test
    @DisplayName("deposit: should return empty when accountId is empty")
    void depositShouldReturnEmptyWhenAccountIdIsEmpty() {
        Optional<TransactionDTO> result = transactionOperationsService.deposit(
            Optional.empty(), 
            Optional.of(BigDecimal.valueOf(100)), 
            Optional.of("Test deposit")
        );
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deposit: should return empty when amount is empty")
    void depositShouldReturnEmptyWhenAmountIsEmpty() {
        Optional<TransactionDTO> result = transactionOperationsService.deposit(
            Optional.of(1L), 
            Optional.empty(), 
            Optional.of("Test deposit")
        );
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("deposit: should return empty when account does not exist")
    void depositShouldReturnEmptyWhenAccountDoesNotExist() {
        Optional<TransactionDTO> result = transactionOperationsService.deposit(
            Optional.of(999999L), 
            Optional.of(BigDecimal.valueOf(100)), 
            Optional.of("Test deposit")
        );
        assertThat(result).isEmpty();
    }

    // ========== WITHDRAW TESTS ==========

    @Test
    @DisplayName("withdraw: should return empty when accountId is empty")
    void withdrawShouldReturnEmptyWhenAccountIdIsEmpty() {
        Optional<TransactionDTO> result = transactionOperationsService.withdraw(
            Optional.empty(), 
            Optional.of(BigDecimal.valueOf(100)), 
            Optional.of("Test withdrawal")
        );
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("withdraw: should return empty when amount is empty")
    void withdrawShouldReturnEmptyWhenAmountIsEmpty() {
        Optional<TransactionDTO> result = transactionOperationsService.withdraw(
            Optional.of(1L), 
            Optional.empty(), 
            Optional.of("Test withdrawal")
        );
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("withdraw: should return empty when account does not exist")
    void withdrawShouldReturnEmptyWhenAccountDoesNotExist() {
        Optional<TransactionDTO> result = transactionOperationsService.withdraw(
            Optional.of(999999L), 
            Optional.of(BigDecimal.valueOf(100)), 
            Optional.of("Test withdrawal")
        );
        assertThat(result).isEmpty();
    }

    // ========== TRANSFER TESTS ==========

    @Test
    @DisplayName("transfer: should return empty when sourceAccountId is empty")
    void transferShouldReturnEmptyWhenSourceAccountIdIsEmpty() {
        Optional<TransactionDTO> result = transactionOperationsService.transfer(
            Optional.empty(), 
            Optional.of(2L), 
            Optional.of(BigDecimal.valueOf(100)), 
            Optional.of("Test transfer")
        );
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("transfer: should return empty when destinationAccountId is empty")
    void transferShouldReturnEmptyWhenDestinationAccountIdIsEmpty() {
        Optional<TransactionDTO> result = transactionOperationsService.transfer(
            Optional.of(1L), 
            Optional.empty(), 
            Optional.of(BigDecimal.valueOf(100)), 
            Optional.of("Test transfer")
        );
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("transfer: should return empty when amount is empty")
    void transferShouldReturnEmptyWhenAmountIsEmpty() {
        Optional<TransactionDTO> result = transactionOperationsService.transfer(
            Optional.of(1L), 
            Optional.of(2L), 
            Optional.empty(), 
            Optional.of("Test transfer")
        );
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("transfer: should return empty when source account does not exist")
    void transferShouldReturnEmptyWhenSourceAccountDoesNotExist() {
        Optional<TransactionDTO> result = transactionOperationsService.transfer(
            Optional.of(999999L), 
            Optional.of(2L), 
            Optional.of(BigDecimal.valueOf(100)), 
            Optional.of("Test transfer")
        );
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("transfer: should return empty when destination account does not exist")
    void transferShouldReturnEmptyWhenDestinationAccountDoesNotExist() {
        Optional<TransactionDTO> result = transactionOperationsService.transfer(
            Optional.of(1L), 
            Optional.of(999999L), 
            Optional.of(BigDecimal.valueOf(100)), 
            Optional.of("Test transfer")
        );
        assertThat(result).isEmpty();
    }

    // ========== CANCEL TRANSACTION TESTS ==========

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is empty")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsEmpty() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.empty());
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transaction does not exist")
    void cancelTransactionShouldReturnEmptyWhenTransactionDoesNotExist() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(999999L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is negative")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsNegative() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(-1L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is zero")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsZero() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(0L));
        assertThat(result).isEmpty();
    }
}
