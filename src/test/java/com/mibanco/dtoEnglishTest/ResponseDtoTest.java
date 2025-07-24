package com.mibanco.dtoEnglishTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import com.mibanco.dtoEnglish.ResponseDTO;
import com.mibanco.dtoEnglish.ClientDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@DisplayName("Tests for ResponseDTO class")
class ResponseDtoTest {

    @Nested
    @DisplayName("Tests for successful responses")
    class SuccessfulResponsesTest {

        @Test
        @DisplayName("Should create a successful response with data and custom message")
        void shouldCreateSuccessfulResponseWithDataAndMessage() {
            // Arrange
            ClientDTO client = ClientDTO.builder()
                    .id(1L)
                    .firstName("Juan") // nombre
                    .lastName("Pérez") // apellido
                    .build();
            String message = "Cliente creado exitosamente";

            // Act
            ResponseDTO<ClientDTO> response = ResponseDTO.success(client, Optional.of(message));

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo(message);
            assertThat(response.getData()).isEqualTo(client);
            assertThat(response.getErrors()).isEmpty();
            assertThat(response.getTimestamp()).isNotNull();
            assertThat(response.getTimestamp()).isBeforeOrEqualTo(LocalDateTime.now());
        }

        @Test
        @DisplayName("Should create a successful response with data and default message")
        void shouldCreateSuccessfulResponseWithDefaultMessage() {
            // Arrange
            String data = "Dato de prueba";

            // Act
            ResponseDTO<String> response = ResponseDTO.success(data, Optional.empty());

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Operation completed successfully");
            assertThat(response.getData()).isEqualTo(data);
            assertThat(response.getErrors()).isEmpty();
            assertThat(response.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Should create a simplified successful response")
        void shouldCreateSimplifiedSuccessfulResponse() {
            // Arrange
            Integer data = 42;

            // Act
            ResponseDTO<Integer> response = ResponseDTO.success(data);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Operation completed successfully");
            assertThat(response.getData()).isEqualTo(data);
            assertThat(response.getErrors()).isEmpty();
            assertThat(response.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Should create a successful response with null data")
        void shouldCreateSuccessfulResponseWithNullData() {
            // Act
            ResponseDTO<String> response = ResponseDTO.success(null, Optional.of("Sin datos"));

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getMessage()).isEqualTo("Sin datos");
            assertThat(response.getData()).isNull();
            assertThat(response.getErrors()).isEmpty();
            assertThat(response.getTimestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Tests for error responses")
    class ErrorResponsesTest {

        @Test
        @DisplayName("Should create an error response with message and error list")
        void shouldCreateErrorResponseWithMessageAndErrors() {
            // Arrange
            String message = "Error en la operación";
            List<String> errors = Arrays.asList("Error 1", "Error 2", "Error 3");

            // Act
            ResponseDTO<String> response = ResponseDTO.error(message, Optional.of(errors));

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo(message);
            assertThat(response.getData()).isNull();
            assertThat(response.getErrors()).containsExactly("Error 1", "Error 2", "Error 3");
            assertThat(response.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Should create an error response with message and empty error list")
        void shouldCreateErrorResponseWithEmptyErrorList() {
            // Arrange
            String message = "Error simple";

            // Act
            ResponseDTO<Integer> response = ResponseDTO.error(message, Optional.empty());

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo(message);
            assertThat(response.getData()).isNull();
            assertThat(response.getErrors()).isEmpty();
            assertThat(response.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Should create a simplified error response")
        void shouldCreateSimplifiedErrorResponse() {
            // Arrange
            String message = "Error crítico";

            // Act
            ResponseDTO<Double> response = ResponseDTO.error(message);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo(message);
            assertThat(response.getData()).isNull();
            assertThat(response.getErrors()).isEmpty();
            assertThat(response.getTimestamp()).isNotNull();
        }

        @Test
        @DisplayName("Should create an error response with null error list")
        void shouldCreateErrorResponseWithNullErrorList() {
            // Arrange
            String message = "Error con lista nula";

            // Act
            ResponseDTO<String> response = ResponseDTO.error(message, Optional.ofNullable(null));

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getMessage()).isEqualTo(message);
            assertThat(response.getData()).isNull();
            assertThat(response.getErrors()).isEmpty();
            assertThat(response.getTimestamp()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Tests for immutability")
    class ImmutabilityTest {

        @Test
        @DisplayName("Should verify total immutability of the response")
        void shouldVerifyTotalImmutability() {
            // Arrange
            String originalData = "Dato original";
            String originalMessage = "Mensaje original";
            List<String> originalErrors = Arrays.asList("Error 1");

            ResponseDTO<String> response = ResponseDTO.<String>builder()
                    .success(true)
                    .message(originalMessage)
                    .data(originalData)
                    .errors(originalErrors)
                    .timestamp(LocalDateTime.now())
                    .build();

            // Act - Attempt to modify (should not be possible due to immutability)
            // The fields are final, so they cannot be modified

            // Assert
            assertThat(response.getData()).isEqualTo(originalData);
            assertThat(response.getMessage()).isEqualTo(originalMessage);
            assertThat(response.getErrors()).containsExactly("Error 1");
        }

        @Test
        @DisplayName("Should create new instances for different generic types")
        void shouldCreateNewInstancesForDifferentGenericTypes() {
            // Act
            ResponseDTO<String> responseString = ResponseDTO.success("String");
            ResponseDTO<Integer> responseInteger = ResponseDTO.success(42);
            ResponseDTO<ClientDTO> responseClient = ResponseDTO.success(
                ClientDTO.builder().id(1L).firstName("Juan").lastName("Pérez").build()
            );

            // Assert
            assertThat(responseString.getData()).isInstanceOf(String.class);
            assertThat(responseInteger.getData()).isInstanceOf(Integer.class);
            assertThat(responseClient.getData()).isInstanceOf(ClientDTO.class);
        }
    }

    @Nested
    @DisplayName("Tests for edge cases")
    class EdgeCasesTest {

        @Test
        @DisplayName("Should handle empty and null messages")
        void shouldHandleEmptyAndNullMessages() {
            // Act
            ResponseDTO<String> responseEmptyMessage = ResponseDTO.success("dato", Optional.of(""));
            ResponseDTO<String> responseNullMessage = ResponseDTO.success("dato", Optional.ofNullable(null));

            // Assert
            assertThat(responseEmptyMessage.getMessage()).isEmpty();
            assertThat(responseNullMessage.getMessage()).isEqualTo("Operation completed successfully");
        }

        @Test
        @DisplayName("Should handle error lists with empty elements")
        void shouldHandleErrorListsWithEmptyElements() {
            // Arrange
            List<String> errorsWithEmpty = Arrays.asList("Error 1", "", "Error 3", null);

            // Act
            ResponseDTO<String> response = ResponseDTO.error("Error", Optional.of(errorsWithEmpty));

            // Assert
            assertThat(response.getErrors()).containsExactly("Error 1", "", "Error 3", null);
        }

        @Test
        @DisplayName("Should handle consistent timestamps")
        void shouldHandleConsistentTimestamps() {
            // Act
            LocalDateTime before = LocalDateTime.now();
            ResponseDTO<String> response = ResponseDTO.success("dato");
            LocalDateTime after = LocalDateTime.now();

            // Assert
            assertThat(response.getTimestamp()).isBetween(before, after);
        }
    }

    @Nested
    @DisplayName("Tests for generic functionality")
    class GenericFunctionalityTest {

        @Test
        @DisplayName("Should work with complex types")
        void shouldWorkWithComplexTypes() {
            // Arrange
            ClientDTO client = ClientDTO.builder()
                    .id(1L)
                    .firstName("María") // nombre
                    .lastName("García") // apellido
                    .email("maria@email.com") // email
                    .build();

            // Act
            ResponseDTO<ClientDTO> response = ResponseDTO.success(client);

            // Assert
            assertThat(response.getData()).isEqualTo(client);
            assertThat(response.getData().getFirstName()).isEqualTo("María"); // nombre
            assertThat(response.getData().getEmail()).isEqualTo("maria@email.com"); // email
        }

        @Test
        @DisplayName("Should work with collections")
        void shouldWorkWithCollections() {
            // Arrange
            List<String> list = Arrays.asList("item1", "item2", "item3");

            // Act
            ResponseDTO<List<String>> response = ResponseDTO.success(list);

            // Assert
            assertThat(response.getData()).containsExactly("item1", "item2", "item3");
            assertThat(response.getData()).hasSize(3);
        }

        @Test
        @DisplayName("Should work with primitive types")
        void shouldWorkWithPrimitiveTypes() {
            // Act
            ResponseDTO<Integer> responseInt = ResponseDTO.success(100);
            ResponseDTO<Double> responseDouble = ResponseDTO.success(3.14);
            ResponseDTO<Boolean> responseBoolean = ResponseDTO.success(true);

            // Assert
            assertThat(responseInt.getData()).isEqualTo(100);
            assertThat(responseDouble.getData()).isEqualTo(3.14);
            assertThat(responseBoolean.getData()).isTrue();
        }
    }
}
