package com.mibanco.modelTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.model.Card;
import com.mibanco.model.Client;
import com.mibanco.model.enums.CardType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for Card class")
class CardTest {

    Client client;
    Long number = 1234567890123456L;  // numero
    Client holder;                     // titular
    String associatedAccountNumber = "1234567890";  // numeroCuentaAsociada
    CardType type = CardType.CREDIT;   // tipo
    LocalDate expirationDate = LocalDate.now().plusYears(3);  // fechaExpiracion
    String cvv = "123";
    boolean active = true;             // activa
    Card card;

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
        card = Card.of(number, holder, associatedAccountNumber, type, expirationDate, cvv, active);
    }

    @Test
    @DisplayName("Should create a card with valid data")
    void shouldCreateCardWithValidData() {
        // Act (Actuar)
        Card cardBuilder = Card.builder()
                .number(number)
                .holder(holder)
                .associatedAccountNumber(associatedAccountNumber)
                .type(type)
                .expirationDate(expirationDate)
                .cvv(cvv)
                .active(active)
                .build();

        // Assert (Verificar)
        assertThat(cardBuilder).isNotNull();
        assertThat(cardBuilder.getNumber()).isEqualTo(number);
        assertThat(cardBuilder.getHolder()).isEqualTo(holder);
        assertThat(cardBuilder.getAssociatedAccountNumber()).isEqualTo(associatedAccountNumber);
        assertThat(cardBuilder.getType()).isEqualTo(type);
        assertThat(cardBuilder.getExpirationDate()).isEqualTo(expirationDate);
        assertThat(cardBuilder.getCvv()).isEqualTo(cvv);
        assertThat(cardBuilder.isActive()).isEqualTo(active);
    }

    @Test
    @DisplayName("Should create a card with valid data using the factory method of()")
    void shouldCreateCardWithValidDataFactoryOf() {
        // Assert (Verificar)
        assertThat(card).isNotNull();
        assertThat(card.getNumber()).isEqualTo(number);
        assertThat(card.getHolder()).isEqualTo(holder);
        assertThat(card.getAssociatedAccountNumber()).isEqualTo(associatedAccountNumber);
        assertThat(card.getType()).isEqualTo(type);
        assertThat(card.getExpirationDate()).isEqualTo(expirationDate);
        assertThat(card.getCvv()).isEqualTo(cvv);
        assertThat(card.isActive()).isEqualTo(active);
    }

    @Test
    @DisplayName("Should return the card identifier")
    void shouldReturnCardIdentifier() {
        // Assert (Verificar)
        assertThat(card.getId()).isEqualTo(number);
        assertThat(card.getId()).isNotNull();
    }
    
    @Test
    @DisplayName("Should return the card identifier when it is null")
    void shouldReturnCardIdentifierWhenItIsNull() {
        // Arrange (Preparar)
        Card cardBuilder = Card.builder()
        .number(null)
        .holder(holder)
        .associatedAccountNumber(associatedAccountNumber)
        .type(type)
        .expirationDate(expirationDate)
        .cvv(cvv)
        .active(active)
        .build();
        // Act (Actuar)
        Long id = cardBuilder.getId();
        // Assert (Verificar)
        assertThat(id).isNull();
    }

    @Test
    @DisplayName("Should create card with different expiration date using factory method")
    void shouldCreateCardWithDifferentExpirationDateUsingFactoryMethod() {
        // Arrange (Preparar)
        LocalDate newDate = LocalDate.now().plusYears(5);
        
        // Act (Actuar)
        Card updatedCard = Card.of(
            card.getNumber(),
            card.getHolder(),
            card.getAssociatedAccountNumber(),
            card.getType(),
            newDate,
            card.getCvv(),
            card.isActive()
        );
        
        // Assert (Verificar)
        assertThat(updatedCard.getExpirationDate()).isEqualTo(newDate);
        assertThat(card.getExpirationDate()).isEqualTo(expirationDate); // Original doesn't change
        assertThat(updatedCard).isNotSameAs(card); // Are different objects
    }

    @Test
    @DisplayName("Should create card with different active state using factory method")
    void shouldCreateCardWithDifferentActiveStateUsingFactoryMethod() {
        // Arrange (Preparar)
        boolean newState = false;
        
        // Act (Actuar)
        Card updatedCard = Card.of(
            card.getNumber(),
            card.getHolder(),
            card.getAssociatedAccountNumber(),
            card.getType(),
            card.getExpirationDate(),
            card.getCvv(),
            newState
        );
        
        // Assert (Verificar)
        assertThat(updatedCard.isActive()).isEqualTo(newState);
        assertThat(card.isActive()).isEqualTo(active); // Original doesn't change
        assertThat(updatedCard).isNotSameAs(card); // Are different objects
    }

    @Test
    @DisplayName("Should create card with multiple changes using factory method")
    void shouldCreateCardWithMultipleChangesUsingFactoryMethod() {
        // Arrange (Preparar)
        LocalDate newDate = LocalDate.now().plusYears(4);
        boolean newState = false;
        
        // Act (Actuar)
        Card updatedCard = Card.of(
            card.getNumber(),
            card.getHolder(),
            card.getAssociatedAccountNumber(),
            card.getType(),
            newDate,
            card.getCvv(),
            newState
        );
        
        // Assert (Verificar)
        assertThat(updatedCard.getExpirationDate()).isEqualTo(newDate);
        assertThat(updatedCard.isActive()).isEqualTo(newState);
        
        // Verify that the original didn't change
        assertThat(card.getExpirationDate()).isEqualTo(expirationDate);
        assertThat(card.isActive()).isEqualTo(active);
    }

    @Test
    @DisplayName("Should maintain total immutability")
    void shouldMaintainTotalImmutability() {
        // Arrange (Preparar)
        LocalDate originalDate = card.getExpirationDate();
        boolean originalActive = card.isActive();
        
        // Act (Actuar) - Create new instance with some changes
        Card newCard = Card.of(
            card.getNumber(),
            card.getHolder(),
            card.getAssociatedAccountNumber(),
            card.getType(),
            LocalDate.now().plusYears(4),
            card.getCvv(),
            card.isActive()
        );
        
        // Assert (Verificar)
        assertThat(newCard.getExpirationDate()).isEqualTo(LocalDate.now().plusYears(4));
        assertThat(newCard.isActive()).isEqualTo(originalActive);
        
        // Verify that the original didn't change
        assertThat(card.getExpirationDate()).isEqualTo(originalDate);
        assertThat(card.isActive()).isEqualTo(originalActive);
        assertThat(newCard).isNotSameAs(card);
    }

    @Test
    @DisplayName("Should generate 16-digit card numbers")
    void shouldGenerate16DigitCardNumbers() {
        // Arrange (Preparar)
        Long cardNumber = 1234567890123456L;
        
        // Act (Actuar)
        Card cardWithNumber = Card.of(
            cardNumber,
            card.getHolder(),
            card.getAssociatedAccountNumber(),
            card.getType(),
            card.getExpirationDate(),
            card.getCvv(),
            card.isActive()
        );
        
        // Assert (Verificar)
        assertThat(cardWithNumber.getNumber()).isEqualTo(cardNumber);
        assertThat(cardWithNumber.getId()).isEqualTo(cardNumber);
        assertThat(String.valueOf(cardNumber)).hasSize(16);
    }

    @Test
    @DisplayName("Should handle different card types")
    void shouldHandleDifferentCardTypes() {
        // Arrange (Preparar)
        CardType debitType = CardType.DEBIT;
        
        // Act (Actuar)
        Card debitCard = Card.of(
            card.getNumber(),
            card.getHolder(),
            card.getAssociatedAccountNumber(),
            debitType,
            card.getExpirationDate(),
            card.getCvv(),
            card.isActive()
        );
        
        // Assert (Verificar)
        assertThat(debitCard.getType()).isEqualTo(debitType);
        assertThat(card.getType()).isEqualTo(type); // Original doesn't change
        assertThat(debitCard).isNotSameAs(card); // Are different objects
    }
} 