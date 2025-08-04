package com.mibanco.dtoTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.dto.CardDTO;
import com.mibanco.dto.ClientDTO;
import com.mibanco.model.enums.CardType;

import static org.assertj.core.api.Assertions.assertThat;
import java.time.LocalDate;
import java.util.Optional;

@DisplayName("Tests for CardDTO class")
class CardDtoTest {

    Long number = 1234567890123456L; // numero
    ClientDTO holder; // titular
    String associatedAccountNumber = "1234567890"; // numeroCuentaAsociada
    CardType type = CardType.CREDIT; // tipo
    LocalDate expirationDate = LocalDate.now().plusYears(3); // fechaExpiracion
    boolean active = true; // activa
    CardDTO cardDto;

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

        cardDto = CardDTO.builder()
                .number(number)
                .holder(holder)
                .associatedAccountNumber(associatedAccountNumber)
                .type(type)
                .expirationDate(expirationDate)
                .active(active)
                .build();
    }

    @Test
    @DisplayName("Should create a CardDTO with valid data using builder")
    void shouldCreateCardDtoWithValidDataUsingBuilder() {
        assertThat(cardDto).isNotNull();
        assertThat(cardDto.getNumber()).isEqualTo(number);
        assertThat(cardDto.getHolder()).isEqualTo(holder);
        assertThat(cardDto.getAssociatedAccountNumber()).isEqualTo(associatedAccountNumber);
        assertThat(cardDto.getType()).isEqualTo(type);
        assertThat(cardDto.getExpirationDate()).isEqualTo(expirationDate);
        assertThat(cardDto.isActive()).isEqualTo(active);
    }

    @Test
    @DisplayName("Should create a CardDTO with valid data using factory of()")
    void shouldCreateCardDtoWithValidDataUsingFactoryOf() {
        CardDTO cardWithOptionals = CardDTO.of(
                number,
                Optional.of(holder),
                Optional.of(associatedAccountNumber),
                Optional.of(type),
                Optional.of(expirationDate),
                Optional.of(active)
        );

        assertThat(cardWithOptionals).isNotNull();
        assertThat(cardWithOptionals.getNumber()).isEqualTo(number);
        assertThat(cardWithOptionals.getHolder()).isEqualTo(holder);
        assertThat(cardWithOptionals.getAssociatedAccountNumber()).isEqualTo(associatedAccountNumber);
        assertThat(cardWithOptionals.getType()).isEqualTo(type);
        assertThat(cardWithOptionals.getExpirationDate()).isEqualTo(expirationDate);
        assertThat(cardWithOptionals.isActive()).isEqualTo(active);
    }

    @Test
    @DisplayName("Should create a CardDTO with null values using factory of()")
    void shouldCreateCardDtoWithNullValuesUsingFactoryOf() {
        CardDTO cardWithNulls = CardDTO.of(
                number,
                Optional.empty(), // holder
                Optional.empty(), // associatedAccountNumber
                Optional.empty(), // type
                Optional.empty(), // expirationDate
                Optional.empty()  // active
        );

        assertThat(cardWithNulls).isNotNull();
        assertThat(cardWithNulls.getNumber()).isEqualTo(number);
        assertThat(cardWithNulls.getHolder()).isNull();
        assertThat(cardWithNulls.getAssociatedAccountNumber()).isNull();
        assertThat(cardWithNulls.getType()).isNull();
        assertThat(cardWithNulls.getExpirationDate()).isNotNull(); // Se asigna por defecto
        assertThat(cardWithNulls.isActive()).isTrue(); // Valor por defecto
    }

    @Test
    @DisplayName("Should create a CardDTO with mixed values using factory of()")
    void shouldCreateCardDtoWithMixedValuesUsingFactoryOf() {
        CardDTO cardWithMixedValues = CardDTO.of(
                number,
                Optional.of(holder),        // holder con valor
                Optional.empty(),            // associatedAccountNumber vacío
                Optional.of(type),           // type con valor
                Optional.empty(),            // expirationDate vacío
                Optional.of(active)          // active con valor
        );

        assertThat(cardWithMixedValues).isNotNull();
        assertThat(cardWithMixedValues.getNumber()).isEqualTo(number);
        assertThat(cardWithMixedValues.getHolder()).isEqualTo(holder);
        assertThat(cardWithMixedValues.getAssociatedAccountNumber()).isNull();
        assertThat(cardWithMixedValues.getType()).isEqualTo(type);
        assertThat(cardWithMixedValues.getExpirationDate()).isNotNull(); // Se asigna por defecto
        assertThat(cardWithMixedValues.isActive()).isEqualTo(active);
    }

    @Test
    @DisplayName("Should update the expiration date immutably using withExpirationDate()")
    void shouldUpdateExpirationDateImmutablyUsingWithExpirationDate() {
        // Arrange (Preparar)
        LocalDate newDate = LocalDate.now().plusYears(5);

        // Act (Actuar)
        CardDTO updatedCard = cardDto.withExpirationDate(newDate);

        // Assert (Verificar)
        assertThat(updatedCard).isNotNull();
        assertThat(updatedCard.getExpirationDate()).isEqualTo(newDate);
        assertThat(updatedCard.getNumber()).isEqualTo(cardDto.getNumber());
        assertThat(updatedCard.getHolder()).isEqualTo(cardDto.getHolder());
        assertThat(updatedCard.getAssociatedAccountNumber()).isEqualTo(cardDto.getAssociatedAccountNumber());
        assertThat(updatedCard.getType()).isEqualTo(cardDto.getType());
        assertThat(updatedCard.isActive()).isEqualTo(cardDto.isActive());
        
        // Verificar inmutabilidad
        assertThat(cardDto.getExpirationDate()).isEqualTo(expirationDate);
        assertThat(updatedCard).isNotSameAs(cardDto);
    }

    @Test
    @DisplayName("Should update the active state immutably using withActive()")
    void shouldUpdateActiveStateImmutablyUsingWithActive() {
        // Arrange (Preparar)
        boolean newState = false;

        // Act (Actuar)
        CardDTO updatedCard = cardDto.withActive(newState);

        // Assert (Verificar)
        assertThat(updatedCard).isNotNull();
        assertThat(updatedCard.isActive()).isEqualTo(newState);
        assertThat(updatedCard.getNumber()).isEqualTo(cardDto.getNumber());
        assertThat(updatedCard.getHolder()).isEqualTo(cardDto.getHolder());
        assertThat(updatedCard.getAssociatedAccountNumber()).isEqualTo(cardDto.getAssociatedAccountNumber());
        assertThat(updatedCard.getType()).isEqualTo(cardDto.getType());
        assertThat(updatedCard.getExpirationDate()).isEqualTo(cardDto.getExpirationDate());
        
        // Verificar inmutabilidad
        assertThat(cardDto.isActive()).isEqualTo(active);
        assertThat(updatedCard).isNotSameAs(cardDto);
    }

    @Test
    @DisplayName("Should update the holder immutably using withHolder()")
    void shouldUpdateHolderImmutablyUsingWithHolder() {
        // Arrange (Preparar)
        ClientDTO newHolder = ClientDTO.builder()
                .id(2L)
                .firstName("Maria") // nombre
                .lastName("Garcia") // apellido
                .dni("0987654321")
                .birthDate(LocalDate.of(1985, 5, 15)) // fechaNacimiento
                .email("maria.garcia@gmail.com")
                .phone("0987654321") // telefono
                .address("Avenida 456, Ciudad, País") // direccion
                .build();

        // Act (Actuar)
        CardDTO updatedCard = cardDto.withHolder(newHolder);

        // Assert (Verificar)
        assertThat(updatedCard).isNotNull();
        assertThat(updatedCard.getHolder()).isEqualTo(newHolder);
        assertThat(updatedCard.getNumber()).isEqualTo(cardDto.getNumber());
        assertThat(updatedCard.getAssociatedAccountNumber()).isEqualTo(cardDto.getAssociatedAccountNumber());
        assertThat(updatedCard.getType()).isEqualTo(cardDto.getType());
        assertThat(updatedCard.getExpirationDate()).isEqualTo(cardDto.getExpirationDate());
        assertThat(updatedCard.isActive()).isEqualTo(cardDto.isActive());
        
        // Verificar inmutabilidad
        assertThat(cardDto.getHolder()).isEqualTo(holder);
        assertThat(updatedCard).isNotSameAs(cardDto);
    }

    @Test
    @DisplayName("Should update multiple fields immutably using withUpdates()")
    void shouldUpdateMultipleFieldsImmutablyUsingWithUpdates() {
        // Arrange (Preparar)
        LocalDate newDate = LocalDate.now().plusYears(4);
        boolean newState = false;

        // Act (Actuar)
        CardDTO updatedCard = cardDto.withUpdates(
                Optional.of(newDate),
                Optional.of(newState)
        );

        // Assert (Verificar)
        assertThat(updatedCard).isNotNull();
        assertThat(updatedCard.getExpirationDate()).isEqualTo(newDate);
        assertThat(updatedCard.isActive()).isEqualTo(newState);
        assertThat(updatedCard.getNumber()).isEqualTo(cardDto.getNumber());
        assertThat(updatedCard.getHolder()).isEqualTo(cardDto.getHolder());
        assertThat(updatedCard.getAssociatedAccountNumber()).isEqualTo(cardDto.getAssociatedAccountNumber());
        assertThat(updatedCard.getType()).isEqualTo(cardDto.getType());
        
        // Verificar inmutabilidad
        assertThat(cardDto.getExpirationDate()).isEqualTo(expirationDate);
        assertThat(cardDto.isActive()).isEqualTo(active);
        assertThat(updatedCard).isNotSameAs(cardDto);
    }

    @Test
    @DisplayName("Should handle empty Optional in multiple updates")
    void shouldHandleEmptyOptionalInMultipleUpdates() {
        // Act (Actuar)
        CardDTO updatedCard = cardDto.withUpdates(
                Optional.empty(), // expirationDate vacío
                Optional.empty()  // active vacío
        );

        // Assert (Verificar)
        assertThat(updatedCard).isNotNull();
        assertThat(updatedCard.getExpirationDate()).isEqualTo(expirationDate); // Mantiene valor original
        assertThat(updatedCard.isActive()).isEqualTo(active); // Mantiene valor original
        assertThat(updatedCard.getNumber()).isEqualTo(cardDto.getNumber());
        assertThat(updatedCard.getHolder()).isEqualTo(cardDto.getHolder());
        assertThat(updatedCard.getAssociatedAccountNumber()).isEqualTo(cardDto.getAssociatedAccountNumber());
        assertThat(updatedCard.getType()).isEqualTo(cardDto.getType());
    }

    @Test
    @DisplayName("Should maintain default values when using Optional.empty()")
    void shouldMaintainDefaultValuesWhenUsingOptionalEmpty() {
        // Act (Actuar)
        CardDTO cardWithDefaultValues = CardDTO.of(
                number,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty()
        );

        // Assert (Verificar)
        assertThat(cardWithDefaultValues.getExpirationDate()).isAfter(LocalDate.now()); // Fecha futura por defecto
        assertThat(cardWithDefaultValues.isActive()).isTrue(); // Activa por defecto
        assertThat(cardWithDefaultValues.getNumber()).isEqualTo(number);
        assertThat(cardWithDefaultValues.getHolder()).isNull();
        assertThat(cardWithDefaultValues.getAssociatedAccountNumber()).isNull();
        assertThat(cardWithDefaultValues.getType()).isNull();
    }

    @Test
    @DisplayName("Should verify total immutability of the DTO")
    void shouldVerifyTotalImmutabilityOfDto() {
        // Arrange (Preparar)
        LocalDate originalExpirationDate = cardDto.getExpirationDate();
        boolean originalActive = cardDto.isActive();
        ClientDTO originalHolder = cardDto.getHolder();

        // Act (Actuar) - Crear múltiples instancias modificadas
        CardDTO card1 = cardDto.withExpirationDate(LocalDate.now().plusYears(5));
        CardDTO card2 = cardDto.withActive(false);
        CardDTO card3 = cardDto.withHolder(null);

        // Assert (Verificar)
        // Verificar que el original no cambió
        assertThat(cardDto.getExpirationDate()).isEqualTo(originalExpirationDate);
        assertThat(cardDto.isActive()).isEqualTo(originalActive);
        assertThat(cardDto.getHolder()).isEqualTo(originalHolder);

        // Verificar que las nuevas instancias son diferentes
        assertThat(card1).isNotSameAs(cardDto);
        assertThat(card2).isNotSameAs(cardDto);
        assertThat(card3).isNotSameAs(cardDto);
        assertThat(card1).isNotSameAs(card2);
        assertThat(card2).isNotSameAs(card3);
    }
    
    @Test
    @DisplayName("Should handle 16-digit card numbers")
    void shouldHandle16DigitCardNumbers() {
        // Arrange (Preparar)
        Long number16Digits = 1234567890123456L;
        
        // Act (Actuar)
        CardDTO card16Digits = CardDTO.builder()
                .number(number16Digits)
                .holder(holder)
                .associatedAccountNumber(associatedAccountNumber)
                .type(type)
                .expirationDate(expirationDate)
                .active(active)
                .build();
        
        // Assert (Verificar)
        assertThat(card16Digits.getNumber()).isEqualTo(number16Digits);
        assertThat(String.valueOf(card16Digits.getNumber())).hasSize(16);
    }
}
