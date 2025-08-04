package com.mibanco.modelTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.model.Transaction;
import com.mibanco.model.enums.TransactionType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for Transaction class")
class TransactionTest {

    Long id = 1L;
    String accountNumber = "ES3412345678901234567890";        // numeroCuenta
    String destinationAccountNumber = "ES3498765432109876543210";  // numeroCuentaDestino
    TransactionType type = TransactionType.SENT_TRANSFER;     // tipo
    BigDecimal amount = new BigDecimal("1000.50");            // monto
    LocalDateTime date = LocalDateTime.of(2024, 1, 15, 10, 30, 0);  // fecha
    String description = "Transferencia entre cuentas";       // descripcion
    Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = Transaction.of(id, accountNumber, destinationAccountNumber, type, amount, date, description);
    }

    @Test
    @DisplayName("Should create a transaction with valid data using Builder")
    void shouldCreateTransactionWithValidDataBuilder() {
        // Act (Actuar)
        Transaction transactionBuilder = Transaction.builder()
                .id(id)
                .accountNumber(accountNumber)
                .destinationAccountNumber(destinationAccountNumber)
                .type(type)
                .amount(amount)
                .date(date)
                .description(description)
                .build();

        // Assert (Verificar)
        assertThat(transactionBuilder).isNotNull();
        assertThat(transactionBuilder.getId()).isEqualTo(id);
        assertThat(transactionBuilder.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(transactionBuilder.getDestinationAccountNumber()).isEqualTo(destinationAccountNumber);
        assertThat(transactionBuilder.getType()).isEqualTo(type);
        assertThat(transactionBuilder.getAmount()).isEqualTo(amount);
        assertThat(transactionBuilder.getDate()).isEqualTo(date);
        assertThat(transactionBuilder.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("Should create a transaction with valid data using the factory method of()")
    void shouldCreateTransactionWithValidDataFactoryOf() {
        // Assert (Verificar)
        assertThat(transaction).isNotNull();
        assertThat(transaction.getId()).isEqualTo(id);
        assertThat(transaction.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(transaction.getDestinationAccountNumber()).isEqualTo(destinationAccountNumber);
        assertThat(transaction.getType()).isEqualTo(type);
        assertThat(transaction.getAmount()).isEqualTo(amount);
        assertThat(transaction.getDate()).isEqualTo(date);
        assertThat(transaction.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("Should use current date when date is null in factory method")
    void shouldUseCurrentDateWhenDateIsNull() {
        // Arrange (Preparar)
        LocalDateTime before = LocalDateTime.now();
        
        // Act (Actuar)
        Transaction transactionWithoutDate = Transaction.of(id, accountNumber, destinationAccountNumber, type, amount, null, description);
        LocalDateTime after = LocalDateTime.now();
        
        // Assert (Verificar)
        assertThat(transactionWithoutDate.getDate()).isNotNull();
        assertThat(transactionWithoutDate.getDate()).isBetween(before, after);
    }

    @Test
    @DisplayName("Should use empty description when description is null in factory method")
    void shouldUseEmptyDescriptionWhenDescriptionIsNull() {
        // Act (Actuar)
        Transaction transactionWithoutDescription = Transaction.of(id, accountNumber, destinationAccountNumber, type, amount, date, null);
        
        // Assert (Verificar)
        assertThat(transactionWithoutDescription.getDescription()).isEqualTo("");
    }

    @Test
    @DisplayName("Should return the transaction identifier")
    void shouldReturnTransactionIdentifier() {
        // Assert (Verificar)
        assertThat(transaction.getId()).isEqualTo(id);
        assertThat(transaction.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should return the transaction identifier when it is null")
    void shouldReturnTransactionIdentifierWhenItIsNull() {
        // Arrange (Preparar)
        Transaction transactionWithoutId = Transaction.builder()
                .id(null)
                .accountNumber(accountNumber)
                .destinationAccountNumber(destinationAccountNumber)
                .type(type)
                .amount(amount)
                .date(date)
                .description(description)
                .build();

        // Act (Actuar)
        Long id = transactionWithoutId.getId();

        // Assert (Verificar)
        assertThat(id).isNull();
    }

    @Test
    @DisplayName("Should handle different transaction types")
    void shouldHandleDifferentTransactionTypes() {
        // Arrange (Preparar)
        Transaction depositTransaction = Transaction.of(id, accountNumber, null, TransactionType.DEPOSIT, amount, date, "Depósito");
        Transaction withdrawalTransaction = Transaction.of(id, accountNumber, null, TransactionType.WITHDRAWAL, amount, date, "Retiro");
        Transaction paymentTransaction = Transaction.of(id, accountNumber, destinationAccountNumber, TransactionType.SERVICE_PAYMENT, amount, date, "Pago");
        
        // Assert (Verificar)
        assertThat(depositTransaction.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(withdrawalTransaction.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(paymentTransaction.getType()).isEqualTo(TransactionType.SERVICE_PAYMENT);
    }

    @Test
    @DisplayName("Should handle amounts with different precisions")
    void shouldHandleAmountsWithDifferentPrecisions() {
        // Arrange (Preparar)
        BigDecimal amountWithDecimals = new BigDecimal("1234.567");
        BigDecimal integerAmount = new BigDecimal("1000");
        BigDecimal zeroAmount = BigDecimal.ZERO;
        
        // Act (Actuar)
        Transaction transactionWithDecimals = Transaction.of(id, accountNumber, destinationAccountNumber, type, amountWithDecimals, date, description);
        Transaction transactionWithInteger = Transaction.of(id, accountNumber, destinationAccountNumber, type, integerAmount, date, description);
        Transaction transactionWithZero = Transaction.of(id, accountNumber, destinationAccountNumber, type, zeroAmount, date, description);
        
        // Assert (Verificar)
        assertThat(transactionWithDecimals.getAmount()).isEqualTo(amountWithDecimals);
        assertThat(transactionWithInteger.getAmount()).isEqualTo(integerAmount);
        assertThat(transactionWithZero.getAmount()).isEqualTo(zeroAmount);
    }

    @Test
    @DisplayName("Should handle transactions without destination account")
    void shouldHandleTransactionsWithoutDestinationAccount() {
        // Act (Actuar)
        Transaction transactionWithoutDestination = Transaction.of(id, accountNumber, null, TransactionType.DEPOSIT, amount, date, description);
        
        // Assert (Verificar)
        assertThat(transactionWithoutDestination.getDestinationAccountNumber()).isNull();
        assertThat(transactionWithoutDestination.getAccountNumber()).isEqualTo(accountNumber);
    }

    @Test
    @DisplayName("Should maintain immutability in all fields")
    void shouldMaintainImmutabilityInAllFields() {
        // Arrange (Preparar)
        Transaction originalTransaction = transaction;
        
        // Act (Actuar) - Create a new transaction with different data
        Transaction newTransaction = Transaction.of(999L, "ES3499999999999999999999", "ES3488888888888888888888", 
                TransactionType.DEPOSIT, new BigDecimal("9999.99"), LocalDateTime.now(), "Nueva descripción");
        
        // Assert (Verificar) - Original should not change
        assertThat(originalTransaction.getId()).isEqualTo(id);
        assertThat(originalTransaction.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(originalTransaction.getDestinationAccountNumber()).isEqualTo(destinationAccountNumber);
        assertThat(originalTransaction.getType()).isEqualTo(type);
        assertThat(originalTransaction.getAmount()).isEqualTo(amount);
        assertThat(originalTransaction.getDate()).isEqualTo(date);
        assertThat(originalTransaction.getDescription()).isEqualTo(description);
        
        // Verify that they are different objects
        assertThat(newTransaction).isNotSameAs(originalTransaction);
        assertThat(newTransaction.getId()).isNotEqualTo(originalTransaction.getId());
    }
} 