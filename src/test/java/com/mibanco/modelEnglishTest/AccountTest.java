package com.mibanco.modelEnglishTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.modelEnglish.Client;
import com.mibanco.modelEnglish.Account;
import com.mibanco.modelEnglish.enums.AccountType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for Account class")
class AccountTest {

    Client client;
    String accountNumber = "ES3412345678901234567890";  // numeroCuenta
    Client holder;           // titular
    AccountType type = AccountType.SAVINGS;  // tipo
    LocalDateTime creationDate = LocalDateTime.now();  // fechaCreacion
    BigDecimal initialBalance = BigDecimal.valueOf(1000.0);  // saldoInicial
    BigDecimal balance = initialBalance;  // saldo
    boolean active = true;   // activa
    Account account;

    @BeforeEach
    void setUp() {
        client = Client.builder()
                .id(1L)
                .firstName("Juan")        // nombre
                .lastName("Perez")        // apellido
                .dni("1234567890")
                .birthDate(LocalDate.of(1990, 1, 1))  // fechaNacimiento
                .email("juan.perez@gmail.com")
                .phone("1234567890")      // telefono
                .address("Calle 123, Ciudad, País")  // direccion
                .build();

        holder = client;
        account = Account.of(accountNumber, holder, type, initialBalance, creationDate, active);
    }

    @Test
    @DisplayName("Should create an account with valid data")
    void shouldCreateAccountWithValidData() {
        
         // Act (Actuar)
         Account accountBuilder = Account.builder()
                
         .accountNumber(accountNumber)
         .holder(holder)
         .type(type)
         .creationDate(creationDate)
         .initialBalance(initialBalance)
         .balance(balance)
         .active(active)
         .build();

        // Assert (Verificar)
        assertThat(accountBuilder).isNotNull();
        assertThat(accountBuilder.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(accountBuilder.getHolder()).isEqualTo(holder);
        assertThat(accountBuilder.getType()).isEqualTo(type);
        assertThat(accountBuilder.getCreationDate()).isEqualTo(creationDate);
        assertThat(accountBuilder.getInitialBalance()).isEqualTo(initialBalance);
        assertThat(accountBuilder.getBalance()).isEqualTo(balance);
        assertThat(accountBuilder.isActive()).isEqualTo(active);
       
    }


@Test
@DisplayName("Should create an account with valid data using the factory method of()")
void shouldCreateAccountWithValidDataFactoryOf() {
    // Arrange (Preparar)

    // Act (Actuar)
    

    // Assert (Verificar)
    assertThat(account).isNotNull();
    assertThat(account.getAccountNumber()).isEqualTo(accountNumber);
    assertThat(account.getHolder()).isEqualTo(holder);
    assertThat(account.getType()).isEqualTo(type);
    assertThat(account.getCreationDate()).isEqualTo(creationDate);
    assertThat(account.getInitialBalance()).isEqualTo(initialBalance);
    assertThat(account.isActive()).isEqualTo(active);
    
}

@Test
@DisplayName("Should validate the IBAN account number format")
void shouldValidateIBANAccountNumberFormat() {
    // Assert (Verificar)
    assertThat(account.getAccountNumber()).isNotNull();
    assertThat(account.getAccountNumber()).startsWith("ES34");
    assertThat(account.getAccountNumber()).hasSize(24);
    assertThat(account.getAccountNumber()).matches("ES34\\d{20}");
}

@Test
@DisplayName("Should handle null account number")
void shouldHandleNullAccountNumber() {
    // Arrange (Preparar)
    Account accountBuilder = Account.builder()
            .accountNumber(null)
            .holder(holder)
            .type(type)
            .creationDate(creationDate)
            .initialBalance(initialBalance)
            .balance(balance)
            .active(active)
            .build();
    
    // Assert (Verificar)
    assertThat(accountBuilder.getAccountNumber()).isNull();
}

@Test
@DisplayName("Should maintain total immutability")
void shouldMaintainTotalImmutability() {
    // Arrange (Preparar)
    BigDecimal originalBalance = account.getBalance();
    boolean originalActive = account.isActive();
    
    // Act (Actuar) - Create a new account with different values
    Account newAccount = Account.of(
        account.getAccountNumber() + "1",
        account.getHolder(),
        account.getType(),
        BigDecimal.valueOf(3000.0),
        account.getCreationDate(),
        false
    );
    
    // Assert (Verificar)
    assertThat(newAccount.getBalance()).isEqualTo(BigDecimal.valueOf(3000.0));
    assertThat(newAccount.isActive()).isFalse();
    
    // Verify that the original didn't change
    assertThat(account.getBalance()).isEqualTo(originalBalance);
    assertThat(account.isActive()).isEqualTo(originalActive);
    assertThat(newAccount).isNotSameAs(account);
}



@Test
@DisplayName("Should create account with different balance using factory method")
void shouldCreateAccountWithDifferentBalanceUsingFactoryMethod() {
    // Arrange
    BigDecimal originalBalance = account.getBalance();
    BigDecimal newBalance = BigDecimal.valueOf(2000.0);
    
    // Act
    Account accountWithNewBalance = Account.of(
        account.getAccountNumber(),
        account.getHolder(),
        account.getType(),
        newBalance,
        account.getCreationDate(),
        account.isActive()
    );
    
    // Assert
    assertThat(accountWithNewBalance.getBalance()).isEqualTo(newBalance);
    assertThat(account.getBalance()).isEqualTo(originalBalance); // ← Original doesn't change
    assertThat(accountWithNewBalance).isNotSameAs(account); // ← Are different objects
}   
@Test
@DisplayName("Should create account with different activation state using factory method")
void shouldCreateAccountWithDifferentActivationStateUsingFactoryMethod() {
    // Arrange (Preparar)
    boolean originalState = account.isActive();
    
    // Act (Actuar)
    Account accountWithNewState = Account.of(
        account.getAccountNumber(),
        account.getHolder(),
        account.getType(),
        account.getInitialBalance(),
        account.getCreationDate(),
        false
    );
    
    // Assert (Verificar)
    assertThat(accountWithNewState.isActive()).isFalse();
    assertThat(account.isActive()).isEqualTo(originalState); // Original doesn't change
    assertThat(accountWithNewState).isNotSameAs(account); // Are different objects
}
@Test
@DisplayName("Should create account with multiple changes using factory method")
void shouldCreateAccountWithMultipleChangesUsingFactoryMethod() {
    // Arrange (Preparar)
    BigDecimal originalBalance = account.getBalance();
    boolean originalActive = account.isActive();
    
    // Act (Actuar)
    Account accountWithNewBalanceAndState = Account.of(
        account.getAccountNumber(),
        account.getHolder(),
        account.getType(),
        BigDecimal.valueOf(2000.0),
        account.getCreationDate(),
        false
    );
    
    // Assert (Verificar)
    assertThat(accountWithNewBalanceAndState.getBalance()).isEqualTo(BigDecimal.valueOf(2000.0));
    assertThat(accountWithNewBalanceAndState.isActive()).isFalse();
    assertThat(account.getBalance()).isEqualTo(originalBalance); // Original doesn't change
    assertThat(account.isActive()).isEqualTo(originalActive); // Original doesn't change
    assertThat(accountWithNewBalanceAndState).isNotSameAs(account); // Are different objects
}
} 