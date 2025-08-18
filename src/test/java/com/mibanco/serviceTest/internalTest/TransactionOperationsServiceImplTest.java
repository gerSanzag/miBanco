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
 * Tests for TransactionOperationsService.cancelTransaction
 */
class TransactionOperationsServiceImplTest {

    private TransactionOperationsService transactionOperationsService;

    @BeforeEach
    void setUp() {
        transactionOperationsService = ServiceFactory.getInstance().getTransactionOperationsService();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is null")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsNull() {
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

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is very large")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsVeryLarge() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(999999999999L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is null and wrapped in Optional")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsNullWrapped() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.ofNullable(null));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 1")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsOne() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(1L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 2")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsTwo() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(2L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 100")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsHundred() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(100L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 1000")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsThousand() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(1000L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 50000")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsFiftyThousand() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(50000L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 100000")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsHundredThousand() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(100000L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 500000")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsFiveHundredThousand() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(500000L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 999999")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsNineHundredNinetyNineThousand() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(999999L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 1000000")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsOneMillion() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(1000000L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 9999999")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsNineMillion() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(9999999L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 10000000")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsTenMillion() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(10000000L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 99999999")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsNinetyNineMillion() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(99999999L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 100000000")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsHundredMillion() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(100000000L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 999999999")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsNineHundredNinetyNineMillion() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(999999999L));
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("cancelTransaction: should return empty when transactionId is 1000000000")
    void cancelTransactionShouldReturnEmptyWhenTransactionIdIsOneBillion() {
        Optional<TransactionDTO> result = transactionOperationsService.cancelTransaction(Optional.of(1000000000L));
        assertThat(result).isEmpty();
    }
}
