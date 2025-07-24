package com.mibanco.dtoEnglishTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.dtoEnglish.TransactionDTO;
import com.mibanco.modelEnglish.enums.TransactionType;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@DisplayName("Tests for TransactionDTO class")
class TransactionDtoTest {

    Long id = 1L; // id
    String accountNumber = "ES3412345678901234567890"; // numeroCuenta
    String destinationAccountNumber = "ES3498765432109876543210"; // numeroCuentaDestino
    TransactionType type = TransactionType.SENT_TRANSFER; // tipo
    BigDecimal amount = new BigDecimal("1000.50"); // monto
    LocalDateTime date = LocalDateTime.of(2024, 1, 15, 10, 30, 0); // fecha
    String description = "Transferencia entre cuentas"; // descripcion
    TransactionDTO transactionDto;

    @BeforeEach
    void setUp() {
        transactionDto = TransactionDTO.builder()
                .id(id)
                .accountNumber(accountNumber)
                .destinationAccountNumber(destinationAccountNumber)
                .type(type)
                .amount(amount)
                .date(date)
                .description(description)
                .build();
    }

    @Test
    @DisplayName("Should create a TransactionDTO with valid data using builder")
    void shouldCreateTransactionDtoWithValidDataUsingBuilder() {
        assertThat(transactionDto).isNotNull();
        assertThat(transactionDto.getId()).isEqualTo(id);
        assertThat(transactionDto.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(transactionDto.getDestinationAccountNumber()).isEqualTo(destinationAccountNumber);
        assertThat(transactionDto.getType()).isEqualTo(type);
        assertThat(transactionDto.getAmount()).isEqualTo(amount);
        assertThat(transactionDto.getDate()).isEqualTo(date);
        assertThat(transactionDto.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("Should create a TransactionDTO with valid data using factory of()")
    void shouldCreateTransactionDtoWithValidDataUsingFactoryOf() {
        TransactionDTO transactionWithOptionals = TransactionDTO.of(
                id,
                accountNumber,
                Optional.of(destinationAccountNumber),
                Optional.of(type),
                Optional.of(amount),
                Optional.of(date),
                Optional.of(description)
        );

        assertThat(transactionWithOptionals).isNotNull();
        assertThat(transactionWithOptionals.getId()).isEqualTo(id);
        assertThat(transactionWithOptionals.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(transactionWithOptionals.getDestinationAccountNumber()).isEqualTo(destinationAccountNumber);
        assertThat(transactionWithOptionals.getType()).isEqualTo(type);
        assertThat(transactionWithOptionals.getAmount()).isEqualTo(amount);
        assertThat(transactionWithOptionals.getDate()).isEqualTo(date);
        assertThat(transactionWithOptionals.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("Should create a TransactionDTO with null values using factory of()")
    void shouldCreateTransactionDtoWithNullValuesUsingFactoryOf() {
        TransactionDTO transactionWithNulls = TransactionDTO.of(
                id,
                accountNumber,
                Optional.empty(), // destinationAccountNumber
                Optional.empty(), // type
                Optional.empty(), // amount
                Optional.empty(), // date
                Optional.empty()  // description
        );

        assertThat(transactionWithNulls).isNotNull();
        assertThat(transactionWithNulls.getId()).isEqualTo(id);
        assertThat(transactionWithNulls.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(transactionWithNulls.getDestinationAccountNumber()).isNull();
        assertThat(transactionWithNulls.getType()).isNull();
        assertThat(transactionWithNulls.getAmount()).isNull();
        assertThat(transactionWithNulls.getDate()).isNotNull(); // Valor por defecto
        assertThat(transactionWithNulls.getDescription()).isEmpty(); // Valor por defecto
    }

    @Test
    @DisplayName("Should create a TransactionDTO with mixed values using factory of()")
    void shouldCreateTransactionDtoWithMixedValuesUsingFactoryOf() {
        TransactionDTO transactionWithMixedValues = TransactionDTO.of(
                id,
                accountNumber,
                Optional.of(destinationAccountNumber), // destinationAccountNumber con valor
                Optional.empty(),                 // type vacío
                Optional.of(amount),               // amount con valor
                Optional.empty(),                 // date vacío
                Optional.of(description)          // description con valor
        );

        assertThat(transactionWithMixedValues).isNotNull();
        assertThat(transactionWithMixedValues.getId()).isEqualTo(id);
        assertThat(transactionWithMixedValues.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(transactionWithMixedValues.getDestinationAccountNumber()).isEqualTo(destinationAccountNumber);
        assertThat(transactionWithMixedValues.getType()).isNull();
        assertThat(transactionWithMixedValues.getAmount()).isEqualTo(amount);
        assertThat(transactionWithMixedValues.getDate()).isNotNull(); // Valor por defecto
        assertThat(transactionWithMixedValues.getDescription()).isEqualTo(description);
    }

    @Test
    @DisplayName("Should verify total immutability of the DTO")
    void shouldVerifyTotalImmutabilityOfDto() {
        // Arrange (Preparar)
        BigDecimal amountOriginal = transactionDto.getAmount();
        String descriptionOriginal = transactionDto.getDescription();
        TransactionType typeOriginal = transactionDto.getType();
        String destinationAccountNumberOriginal = transactionDto.getDestinationAccountNumber();

        // Act (Actuar) - Crear nuevas instancias usando builder estático
        TransactionDTO transaction1 = TransactionDTO.builder()
                .id(transactionDto.getId())
                .accountNumber(transactionDto.getAccountNumber())
                .destinationAccountNumber(transactionDto.getDestinationAccountNumber())
                .type(transactionDto.getType())
                .amount(new BigDecimal("5000.00"))
                .date(transactionDto.getDate())
                .description(transactionDto.getDescription())
                .build();

        TransactionDTO transaction2 = TransactionDTO.builder()
                .id(transaction1.getId())
                .accountNumber(transaction1.getAccountNumber())
                .destinationAccountNumber(transaction1.getDestinationAccountNumber())
                .type(transaction1.getType())
                .amount(transaction1.getAmount())
                .date(transaction1.getDate())
                .description("Descripción actualizada")
                .build();

        TransactionDTO transaction3 = TransactionDTO.builder()
                .id(transaction2.getId())
                .accountNumber(transaction2.getAccountNumber())
                .destinationAccountNumber(transaction2.getDestinationAccountNumber())
                .type(TransactionType.DEPOSIT)
                .amount(transaction2.getAmount())
                .date(transaction2.getDate())
                .description(transaction2.getDescription())
                .build();

        TransactionDTO transaction4 = TransactionDTO.builder()
                .id(transaction3.getId())
                .accountNumber(transaction3.getAccountNumber())
                .destinationAccountNumber("ES3411111111111111111111")
                .type(transaction3.getType())
                .amount(transaction3.getAmount())
                .date(transaction3.getDate())
                .description(transaction3.getDescription())
                .build();

        // Assert (Verificar)
        // Verificar que cada actualización crea una nueva instancia
        assertThat(transaction1).isNotSameAs(transactionDto);
        assertThat(transaction2).isNotSameAs(transaction1);
        assertThat(transaction3).isNotSameAs(transaction2);
        assertThat(transaction4).isNotSameAs(transaction3);

        // Verificar que el original no cambió
        assertThat(transactionDto.getAmount()).isEqualTo(amountOriginal);
        assertThat(transactionDto.getDescription()).isEqualTo(descriptionOriginal);
        assertThat(transactionDto.getType()).isEqualTo(typeOriginal);
        assertThat(transactionDto.getDestinationAccountNumber()).isEqualTo(destinationAccountNumberOriginal);
    }
}
