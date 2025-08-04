package com.mibanco.dtoTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.dto.AccountDTO;
import com.mibanco.dto.ClientDTO;
import com.mibanco.model.enums.AccountType;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@DisplayName("Tests for AccountDTO class")
class AccountDtoTest {

    String accountNumber = "ES3412345678901234567890"; // numeroCuenta
    ClientDTO holder; // titular
    AccountType type = AccountType.SAVINGS; // tipo
    LocalDateTime creationDate = LocalDateTime.now(); // fechaCreacion
    BigDecimal initialBalance = BigDecimal.valueOf(1000.0); // saldoInicial
    BigDecimal balance = initialBalance; // saldo
    boolean active = true; // activa
    AccountDTO accountDto;

    @BeforeEach
    void setUp() {
        holder = ClientDTO.builder()
                .id(1L)
                .firstName("Juan") // nombre
                .lastName("Perez") // apellido
                .dni("1234567890")
                .birthDate(LocalDate.of(1990, 1, 1)) // fechaNacimiento
                .email("juan.perez@gmail.com")
                .phone("1234567890") // telefono
                .address("Calle 123, Ciudad, País") // direccion
                .build();

        accountDto = AccountDTO.builder()
                .accountNumber(accountNumber)
                .holder(holder)
                .type(type)
                .creationDate(creationDate)
                .initialBalance(initialBalance)
                .balance(balance)
                .active(active)
                .build();
    }

    @Test
    @DisplayName("Should create an AccountDTO with valid data using Builder")
    void shouldCreateAccountDtoWithValidDataBuilder() {
        assertThat(accountDto).isNotNull();
        assertThat(accountDto.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(accountDto.getHolder()).isEqualTo(holder);
        assertThat(accountDto.getType()).isEqualTo(type);
        assertThat(accountDto.getCreationDate()).isEqualTo(creationDate);
        assertThat(accountDto.getInitialBalance()).isEqualTo(initialBalance);
        assertThat(accountDto.getBalance()).isEqualTo(balance);
        assertThat(accountDto.isActive()).isEqualTo(active);
    }

    @Test
    @DisplayName("Should create an AccountDTO using factory method of() with optional values")
    void shouldCreateAccountDtoUsingFactoryOfWithOptionalValues() {
        AccountDTO accountWithOptionals = AccountDTO.of(
                accountNumber,
                Optional.of(holder),
                Optional.of(type),
                initialBalance,
                Optional.of(creationDate),
                Optional.of(active)
        );

        assertThat(accountWithOptionals).isNotNull();
        assertThat(accountWithOptionals.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(accountWithOptionals.getHolder()).isEqualTo(holder);
        assertThat(accountWithOptionals.getType()).isEqualTo(type);
        assertThat(accountWithOptionals.getInitialBalance()).isEqualTo(initialBalance);
        assertThat(accountWithOptionals.getBalance()).isEqualTo(initialBalance);
        assertThat(accountWithOptionals.getCreationDate()).isEqualTo(creationDate);
        assertThat(accountWithOptionals.isActive()).isEqualTo(active);
    }

    @Test
    @DisplayName("Should create an AccountDTO using factory method of() with null values")
    void shouldCreateAccountDtoUsingFactoryOfWithNullValues() {
        AccountDTO accountWithNulls = AccountDTO.of(
                accountNumber,
                Optional.empty(), // holder
                Optional.empty(), // type
                null, // initialBalance
                Optional.empty(), // creationDate
                Optional.empty() // active
        );

        assertThat(accountWithNulls).isNotNull();
        assertThat(accountWithNulls.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(accountWithNulls.getHolder()).isNull();
        assertThat(accountWithNulls.getType()).isNull();
        assertThat(accountWithNulls.getInitialBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(accountWithNulls.getBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(accountWithNulls.getCreationDate()).isNotNull(); // Se establece automáticamente
        assertThat(accountWithNulls.isActive()).isTrue(); // Valor por defecto
    }

    @Test
    @DisplayName("Should update the balance immutably using withBalance()")
    void shouldUpdateBalanceImmutablyUsingWithBalance() {
        // Arrange (Preparar)
        BigDecimal newBalance = BigDecimal.valueOf(2000.0);

        // Act (Actuar)
        AccountDTO updatedAccount = accountDto.withBalance(newBalance);

        // Assert (Verificar)
        assertThat(updatedAccount).isNotNull();
        assertThat(updatedAccount.getBalance()).isEqualTo(newBalance);
        assertThat(updatedAccount.getAccountNumber()).isEqualTo(accountDto.getAccountNumber());
        assertThat(updatedAccount.getHolder()).isEqualTo(accountDto.getHolder());
        assertThat(updatedAccount.getType()).isEqualTo(accountDto.getType());
        assertThat(updatedAccount.getCreationDate()).isEqualTo(accountDto.getCreationDate());
        assertThat(updatedAccount.getInitialBalance()).isEqualTo(accountDto.getInitialBalance());
        assertThat(updatedAccount.isActive()).isEqualTo(accountDto.isActive());

        // Verificar que el original no cambió
        assertThat(accountDto.getBalance()).isEqualTo(balance);
        assertThat(updatedAccount).isNotSameAs(accountDto);
    }

    @Test
    @DisplayName("Should update the active status immutably using withActive()")
    void shouldUpdateActiveStatusImmutablyUsingWithActive() {
        // Arrange (Preparar)
        boolean newStatus = false;

        // Act (Actuar)
        AccountDTO updatedAccount = accountDto.withActive(newStatus);

        // Assert (Verificar)
        assertThat(updatedAccount).isNotNull();
        assertThat(updatedAccount.isActive()).isEqualTo(newStatus);
        assertThat(updatedAccount.getAccountNumber()).isEqualTo(accountDto.getAccountNumber());
        assertThat(updatedAccount.getHolder()).isEqualTo(accountDto.getHolder());
        assertThat(updatedAccount.getType()).isEqualTo(accountDto.getType());
        assertThat(updatedAccount.getCreationDate()).isEqualTo(accountDto.getCreationDate());
        assertThat(updatedAccount.getInitialBalance()).isEqualTo(accountDto.getInitialBalance());
        assertThat(updatedAccount.getBalance()).isEqualTo(accountDto.getBalance());

        // Verificar que el original no cambió
        assertThat(accountDto.isActive()).isEqualTo(active);
        assertThat(updatedAccount).isNotSameAs(accountDto);
    }

    @Test
    @DisplayName("Should update the holder immutably using withHolder()")
    void shouldUpdateHolderImmutablyUsingWithHolder() {
        // Arrange (Preparar)
        ClientDTO newHolder = ClientDTO.builder()
                .id(2L)
                .firstName("María") // nombre
                .lastName("García") // apellido
                .dni("9876543210")
                .birthDate(LocalDate.of(1985, 6, 20)) // fechaNacimiento
                .email("maria.garcia@gmail.com")
                .phone("9876543210") // telefono
                .address("Avenida Principal 456, Ciudad, País") // direccion
                .build();

        // Act (Actuar)
        AccountDTO updatedAccount = accountDto.withHolder(newHolder);

        // Assert (Verificar)
        assertThat(updatedAccount).isNotNull();
        assertThat(updatedAccount.getHolder()).isEqualTo(newHolder);
        assertThat(updatedAccount.getHolder().getId()).isEqualTo(2L);
        assertThat(updatedAccount.getHolder().getFirstName()).isEqualTo("María");
        assertThat(updatedAccount.getHolder().getLastName()).isEqualTo("García");
        assertThat(updatedAccount.getAccountNumber()).isEqualTo(accountDto.getAccountNumber());
        assertThat(updatedAccount.getType()).isEqualTo(accountDto.getType());
        assertThat(updatedAccount.getCreationDate()).isEqualTo(accountDto.getCreationDate());
        assertThat(updatedAccount.getInitialBalance()).isEqualTo(accountDto.getInitialBalance());
        assertThat(updatedAccount.getBalance()).isEqualTo(accountDto.getBalance());
        assertThat(updatedAccount.isActive()).isEqualTo(accountDto.isActive());

        // Verificar que el original no cambió
        assertThat(accountDto.getHolder()).isEqualTo(holder);
        assertThat(accountDto.getHolder().getId()).isEqualTo(1L);
        assertThat(accountDto.getHolder().getFirstName()).isEqualTo("Juan");
        assertThat(updatedAccount).isNotSameAs(accountDto);
    }

    @Test
    @DisplayName("Should update multiple fields using withUpdates()")
    void shouldUpdateMultipleFieldsUsingWithUpdates() {
        // Arrange (Preparar)
        BigDecimal newBalance = BigDecimal.valueOf(3000.0);
        boolean newStatus = false;

        // Act (Actuar)
        AccountDTO updatedAccount = accountDto.withUpdates(
                Optional.of(newBalance),
                Optional.of(newStatus)
        );

        // Assert (Verificar)
        assertThat(updatedAccount).isNotNull();
        assertThat(updatedAccount.getBalance()).isEqualTo(newBalance);
        assertThat(updatedAccount.isActive()).isEqualTo(newStatus);
        assertThat(updatedAccount.getAccountNumber()).isEqualTo(accountDto.getAccountNumber());
        assertThat(updatedAccount.getHolder()).isEqualTo(accountDto.getHolder());
        assertThat(updatedAccount.getType()).isEqualTo(accountDto.getType());
        assertThat(updatedAccount.getCreationDate()).isEqualTo(accountDto.getCreationDate());
        assertThat(updatedAccount.getInitialBalance()).isEqualTo(accountDto.getInitialBalance());

        // Verificar que el original no cambió
        assertThat(accountDto.getBalance()).isEqualTo(balance);
        assertThat(accountDto.isActive()).isEqualTo(active);
        assertThat(updatedAccount).isNotSameAs(accountDto);
    }

    @Test
    @DisplayName("Should maintain original values when passing empty Optionals in withUpdates()")
    void shouldMaintainOriginalValuesWhenPassingEmptyOptionalsInWithUpdates() {
        // Act (Actuar)
        AccountDTO updatedAccount = accountDto.withUpdates(
                Optional.empty(), // keep original balance
                Optional.empty()  // keep original status
        );

        // Assert (Verificar)
        assertThat(updatedAccount).isNotNull();
        assertThat(updatedAccount.getBalance()).isEqualTo(accountDto.getBalance());
        assertThat(updatedAccount.isActive()).isEqualTo(accountDto.isActive());
        assertThat(updatedAccount.getAccountNumber()).isEqualTo(accountDto.getAccountNumber());
        assertThat(updatedAccount.getHolder()).isEqualTo(accountDto.getHolder());
        assertThat(updatedAccount.getType()).isEqualTo(accountDto.getType());
        assertThat(updatedAccount.getCreationDate()).isEqualTo(accountDto.getCreationDate());
        assertThat(updatedAccount.getInitialBalance()).isEqualTo(accountDto.getInitialBalance());
    }

    @Test
    @DisplayName("Should update only some fields when passing mixed Optionals in withUpdates()")
    void shouldUpdateOnlySomeFieldsWhenPassingMixedOptionalsInWithUpdates() {
        // Arrange (Preparar)
        BigDecimal newBalance = BigDecimal.valueOf(5000.0);

        // Act (Actuar)
        AccountDTO updatedAccount = accountDto.withUpdates(
                Optional.of(newBalance), // update balance
                Optional.empty()         // keep original status
        );

        // Assert (Verificar)
        assertThat(updatedAccount).isNotNull();
        assertThat(updatedAccount.getBalance()).isEqualTo(newBalance);
        assertThat(updatedAccount.isActive()).isEqualTo(accountDto.isActive()); // Mantiene original
        assertThat(updatedAccount.getAccountNumber()).isEqualTo(accountDto.getAccountNumber());
        assertThat(updatedAccount.getHolder()).isEqualTo(accountDto.getHolder());
        assertThat(updatedAccount.getType()).isEqualTo(accountDto.getType());
        assertThat(updatedAccount.getCreationDate()).isEqualTo(accountDto.getCreationDate());
        assertThat(updatedAccount.getInitialBalance()).isEqualTo(accountDto.getInitialBalance());

        // Verificar que el original no cambió
        assertThat(accountDto.getBalance()).isEqualTo(balance);
        assertThat(accountDto.isActive()).isEqualTo(active);
    }

    @Test
    @DisplayName("Should handle different account types")
    void shouldHandleDifferentAccountTypes() {
        // Arrange (Preparar)
        AccountDTO currentAccount = AccountDTO.of(
                accountNumber + 1L,
                Optional.of(holder),
                Optional.of(AccountType.CHECKING),
                initialBalance,
                Optional.of(creationDate),
                Optional.of(active)
        );

        AccountDTO fixedTermAccount = AccountDTO.of(
                accountNumber + 2L,
                Optional.of(holder),
                Optional.of(AccountType.FIXED_TERM),
                initialBalance,
                Optional.of(creationDate),
                Optional.of(active)
        );

        // Assert (Verificar)
        assertThat(currentAccount.getType()).isEqualTo(AccountType.CHECKING);
        assertThat(fixedTermAccount.getType()).isEqualTo(AccountType.FIXED_TERM);
        assertThat(accountDto.getType()).isEqualTo(AccountType.SAVINGS);
    }

    @Test
    @DisplayName("Should handle null balances and with values")
    void shouldHandleNullBalancesAndWithValues() {
        // Arrange (Preparar)
        BigDecimal balanceZero = BigDecimal.ZERO;
        BigDecimal balanceNegative = BigDecimal.valueOf(-100.0);

        // Act (Actuar)
        AccountDTO accountWithoutBalance = AccountDTO.of(
                accountNumber + 1L,
                Optional.of(holder),
                Optional.of(type),
                null, // initialBalance nulo
                Optional.of(creationDate),
                Optional.of(active)
        );

        AccountDTO accountWithBalanceZero = AccountDTO.of(
                accountNumber + 2L,
                Optional.of(holder),
                Optional.of(type),
                balanceZero,
                Optional.of(creationDate),
                Optional.of(active)
        );

        AccountDTO accountWithBalanceNegative = AccountDTO.of(
                accountNumber + 3L,
                Optional.of(holder),
                Optional.of(type),
                balanceNegative,
                Optional.of(creationDate),
                Optional.of(active)
        );

        // Assert (Verificar)
        assertThat(accountWithoutBalance.getInitialBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(accountWithoutBalance.getBalance()).isEqualTo(BigDecimal.ZERO);
        assertThat(accountWithBalanceZero.getInitialBalance()).isEqualTo(balanceZero);
        assertThat(accountWithBalanceZero.getBalance()).isEqualTo(balanceZero);
        assertThat(accountWithBalanceNegative.getInitialBalance()).isEqualTo(balanceNegative);
        assertThat(accountWithBalanceNegative.getBalance()).isEqualTo(balanceNegative);
    }

    @Test
    @DisplayName("Should maintain immutability in all fields")
    void shouldMaintainImmutabilityInAllFields() {
        // Arrange (Preparar)
        AccountDTO originalAccount = accountDto;

        // Act (Actuar) - Create a new account with different data
        AccountDTO newAccount = AccountDTO.of(
                accountNumber + 1L,
                Optional.of(holder),
                Optional.of(AccountType.CHECKING),
                BigDecimal.valueOf(9999.99),
                Optional.of(creationDate.plusDays(1)),
                Optional.of(false)
        );

        // Assert (Verificar) - Original should not change
        assertThat(originalAccount.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(originalAccount.getHolder()).isEqualTo(holder);
        assertThat(originalAccount.getType()).isEqualTo(type);
        assertThat(originalAccount.getCreationDate()).isEqualTo(creationDate);
        assertThat(originalAccount.getInitialBalance()).isEqualTo(initialBalance);
        assertThat(originalAccount.getBalance()).isEqualTo(balance);
        assertThat(originalAccount.isActive()).isEqualTo(active);

        // Verify that they are different objects
        assertThat(newAccount).isNotSameAs(originalAccount);
        assertThat(newAccount.getAccountNumber()).isNotEqualTo(originalAccount.getAccountNumber());
        assertThat(newAccount.getType()).isNotEqualTo(originalAccount.getType());
        assertThat(newAccount.getInitialBalance()).isNotEqualTo(originalAccount.getInitialBalance());
        assertThat(newAccount.isActive()).isNotEqualTo(originalAccount.isActive());
    }
}
