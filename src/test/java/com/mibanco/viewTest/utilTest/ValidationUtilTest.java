package com.mibanco.viewTest.utilTest;

import com.mibanco.view.util.ValidationUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for ValidationUtil.
 * Tests all validation and input capture methods.
 */
class ValidationUtilTest {
    
    // ===== TESTS FOR VALIDATION METHODS =====
    
    @Test
    @DisplayName("Should return true when validateRequiredFields with Map has all required fields")
    void shouldReturnTrueWhenValidateRequiredFieldsWithMapHasAllRequiredFields() {
        // Given
        Map<String, String> fields = Map.of(
            "firstName", "Juan",
            "lastName", "Pérez",
            "email", "juan@test.com"
        );
        List<String> requiredFields = List.of("firstName", "lastName", "email");

        // When
        boolean result = ValidationUtil.validateRequiredFields(fields, requiredFields);

        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("Should return false when validateRequiredFields with Map has missing required field")
    void shouldReturnFalseWhenValidateRequiredFieldsWithMapHasMissingRequiredField() {
        // Given
        Map<String, String> fields = Map.of(
            "firstName", "Juan",
            "lastName", "Pérez"
            // email is missing
        );
        List<String> requiredFields = List.of("firstName", "lastName", "email");

        // When
        boolean result = ValidationUtil.validateRequiredFields(fields, requiredFields);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when validateRequiredFields with Map has empty required field")
    void shouldReturnFalseWhenValidateRequiredFieldsWithMapHasEmptyRequiredField() {
        // Given
        Map<String, String> fields = Map.of(
            "firstName", "Juan",
            "lastName", "Pérez",
            "email", ""  // Empty email
        );
        List<String> requiredFields = List.of("firstName", "lastName", "email");

        // When
        boolean result = ValidationUtil.validateRequiredFields(fields, requiredFields);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when validateRequiredFields with Map has null required field")
    void shouldReturnFalseWhenValidateRequiredFieldsWithMapHasNullRequiredField() {
        // Given
        Map<String, String> fields = new java.util.HashMap<>();
        fields.put("firstName", "Juan");
        fields.put("lastName", "Pérez");
        fields.put("email", null);  // Null email
        List<String> requiredFields = List.of("firstName", "lastName", "email");

        // When
        boolean result = ValidationUtil.validateRequiredFields(fields, requiredFields);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when validateRequiredFields with Map has whitespace-only required field")
    void shouldReturnFalseWhenValidateRequiredFieldsWithMapHasWhitespaceOnlyRequiredField() {
        // Given
        Map<String, String> fields = Map.of(
            "firstName", "Juan",
            "lastName", "Pérez",
            "email", "   "  // Whitespace-only email
        );
        List<String> requiredFields = List.of("firstName", "lastName", "email");

        // When
        boolean result = ValidationUtil.validateRequiredFields(fields, requiredFields);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when validateRequiredFields with Map is null")
    void shouldReturnFalseWhenValidateRequiredFieldsWithMapIsNull() {
        // Given
        Map<String, String> fields = null;
        List<String> requiredFields = List.of("firstName", "lastName");

        // When
        boolean result = ValidationUtil.validateRequiredFields(fields, requiredFields);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when validateRequiredFields with requiredFields is null")
    void shouldReturnFalseWhenValidateRequiredFieldsWithRequiredFieldsIsNull() {
        // Given
        Map<String, String> fields = Map.of("firstName", "Juan");
        List<String> requiredFields = null;

        // When
        boolean result = ValidationUtil.validateRequiredFields(fields, requiredFields);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return true when validateRequiredFields with varargs has all valid fields")
    void shouldReturnTrueWhenValidateRequiredFieldsWithVarargsHasAllValidFields() {
        // Given
        String field1 = "Juan";
        String field2 = "Pérez";
        String field3 = "juan@test.com";

        // When
        boolean result = ValidationUtil.validateRequiredFields(field1, field2, field3);

        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("Should return false when validateRequiredFields with varargs has empty field")
    void shouldReturnFalseWhenValidateRequiredFieldsWithVarargsHasEmptyField() {
        // Given
        String field1 = "Juan";
        String field2 = "";  // Empty field
        String field3 = "juan@test.com";

        // When
        boolean result = ValidationUtil.validateRequiredFields(field1, field2, field3);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when validateRequiredFields with varargs has null field")
    void shouldReturnFalseWhenValidateRequiredFieldsWithVarargsHasNullField() {
        // Given
        String field1 = "Juan";
        String field2 = null;  // Null field
        String field3 = "juan@test.com";

        // When
        boolean result = ValidationUtil.validateRequiredFields(field1, field2, field3);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when validateRequiredFields with varargs is null")
    void shouldReturnFalseWhenValidateRequiredFieldsWithVarargsIsNull() {
        // Given
        String[] fieldValues = null;

        // When
        boolean result = ValidationUtil.validateRequiredFields(fieldValues);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return true when validateRequiredFields with varargs has no fields")
    void shouldReturnTrueWhenValidateRequiredFieldsWithVarargsHasNoFields() {
        // Given
        String[] fieldValues = {};

        // When
        boolean result = ValidationUtil.validateRequiredFields(fieldValues);

        // Then
        assertThat(result).isTrue();
    }
    
    // ===== TESTS FOR EMAIL VALIDATION =====
    
    @Test
    @DisplayName("Should return true when isValidEmail has valid email format")
    void shouldReturnTrueWhenIsValidEmailHasValidEmailFormat() {
        // Given
        String validEmail = "juan.perez@test.com";

        // When
        boolean result = ValidationUtil.isValidEmail(validEmail);

        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("Should return true when isValidEmail has valid email with numbers")
    void shouldReturnTrueWhenIsValidEmailHasValidEmailWithNumbers() {
        // Given
        String validEmail = "juan123@test123.com";

        // When
        boolean result = ValidationUtil.isValidEmail(validEmail);

        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("Should return true when isValidEmail has valid email with special characters")
    void shouldReturnTrueWhenIsValidEmailHasValidEmailWithSpecialCharacters() {
        // Given
        String validEmail = "juan.perez+test@test-domain.com";

        // When
        boolean result = ValidationUtil.isValidEmail(validEmail);

        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("Should return false when isValidEmail has invalid email format")
    void shouldReturnFalseWhenIsValidEmailHasInvalidEmailFormat() {
        // Given
        String invalidEmail = "invalid-email";

        // When
        boolean result = ValidationUtil.isValidEmail(invalidEmail);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when isValidEmail has email without domain")
    void shouldReturnFalseWhenIsValidEmailHasEmailWithoutDomain() {
        // Given
        String invalidEmail = "juan@";

        // When
        boolean result = ValidationUtil.isValidEmail(invalidEmail);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when isValidEmail has email without @ symbol")
    void shouldReturnFalseWhenIsValidEmailHasEmailWithoutAtSymbol() {
        // Given
        String invalidEmail = "juantest.com";

        // When
        boolean result = ValidationUtil.isValidEmail(invalidEmail);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return true when isValidEmail has null email")
    void shouldReturnTrueWhenIsValidEmailHasNullEmail() {
        // Given
        String nullEmail = null;

        // When
        boolean result = ValidationUtil.isValidEmail(nullEmail);

        // Then
        assertThat(result).isTrue(); // Empty email is allowed
    }
    
    @Test
    @DisplayName("Should return true when isValidEmail has empty email")
    void shouldReturnTrueWhenIsValidEmailHasEmptyEmail() {
        // Given
        String emptyEmail = "";

        // When
        boolean result = ValidationUtil.isValidEmail(emptyEmail);

        // Then
        assertThat(result).isTrue(); // Empty email is allowed
    }
    
    @Test
    @DisplayName("Should return true when isValidEmail has whitespace-only email")
    void shouldReturnTrueWhenIsValidEmailHasWhitespaceOnlyEmail() {
        // Given
        String whitespaceEmail = "   ";

        // When
        boolean result = ValidationUtil.isValidEmail(whitespaceEmail);

        // Then
        assertThat(result).isTrue(); // Empty email is allowed
    }
    
    // ===== TESTS FOR STRING VALIDATION =====
    
    @Test
    @DisplayName("Should return true when isNotNullOrEmpty has valid string")
    void shouldReturnTrueWhenIsNotNullOrEmptyHasValidString() {
        // Given
        String validString = "Hello World";

        // When
        boolean result = ValidationUtil.isNotNullOrEmpty(validString);

        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("Should return false when isNotNullOrEmpty has null string")
    void shouldReturnFalseWhenIsNotNullOrEmptyHasNullString() {
        // Given
        String nullString = null;

        // When
        boolean result = ValidationUtil.isNotNullOrEmpty(nullString);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when isNotNullOrEmpty has empty string")
    void shouldReturnFalseWhenIsNotNullOrEmptyHasEmptyString() {
        // Given
        String emptyString = "";

        // When
        boolean result = ValidationUtil.isNotNullOrEmpty(emptyString);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when isNotNullOrEmpty has whitespace-only string")
    void shouldReturnFalseWhenIsNotNullOrEmptyHasWhitespaceOnlyString() {
        // Given
        String whitespaceString = "   ";

        // When
        boolean result = ValidationUtil.isNotNullOrEmpty(whitespaceString);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return true when hasMinimumLength has string with sufficient length")
    void shouldReturnTrueWhenHasMinimumLengthHasStringWithSufficientLength() {
        // Given
        String validString = "Hello";
        int minLength = 3;

        // When
        boolean result = ValidationUtil.hasMinimumLength(validString, minLength);

        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("Should return true when hasMinimumLength has string with exact minimum length")
    void shouldReturnTrueWhenHasMinimumLengthHasStringWithExactMinimumLength() {
        // Given
        String validString = "Hi";
        int minLength = 2;

        // When
        boolean result = ValidationUtil.hasMinimumLength(validString, minLength);

        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    @DisplayName("Should return false when hasMinimumLength has string with insufficient length")
    void shouldReturnFalseWhenHasMinimumLengthHasStringWithInsufficientLength() {
        // Given
        String shortString = "Hi";
        int minLength = 5;

        // When
        boolean result = ValidationUtil.hasMinimumLength(shortString, minLength);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when hasMinimumLength has null string")
    void shouldReturnFalseWhenHasMinimumLengthHasNullString() {
        // Given
        String nullString = null;
        int minLength = 3;

        // When
        boolean result = ValidationUtil.hasMinimumLength(nullString, minLength);

        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("Should return false when hasMinimumLength has empty string")
    void shouldReturnFalseWhenHasMinimumLengthHasEmptyString() {
        // Given
        String emptyString = "";
        int minLength = 3;

        // When
        boolean result = ValidationUtil.hasMinimumLength(emptyString, minLength);

        // Then
        assertThat(result).isFalse();
    }
    
    // ===== TESTS FOR INPUT CAPTURE METHODS =====
    
    @Test
    @DisplayName("Should capture required string input successfully with valid input")
    void shouldCaptureRequiredStringInputSuccessfullyWithValidInput() {
        // Given
        StringBuilder capturedMessages = new StringBuilder();
        Consumer<String> showMessage = capturedMessages::append;
        Supplier<String> getInput = () -> "Juan Pérez";
        String prompt = "Enter your name: ";
        String fieldName = "Nombre";

        // When
        String result = ValidationUtil.captureRequiredStringInput(prompt, showMessage, getInput, fieldName);

        // Then
        assertThat(result).isEqualTo("Juan Pérez");
        assertThat(capturedMessages.toString()).contains(prompt);
        assertThat(capturedMessages.toString()).doesNotContain("Error: Nombre es obligatorio.");
    }
    
    @Test
    @DisplayName("Should return null when captureRequiredStringInput gets empty input")
    void shouldReturnNullWhenCaptureRequiredStringInputGetsEmptyInput() {
        // Given
        StringBuilder capturedMessages = new StringBuilder();
        Consumer<String> showMessage = capturedMessages::append;
        Supplier<String> getInput = () -> "";
        String prompt = "Enter your name: ";
        String fieldName = "Nombre";

        // When
        String result = ValidationUtil.captureRequiredStringInput(prompt, showMessage, getInput, fieldName);

        // Then
        assertThat(result).isNull();
        assertThat(capturedMessages.toString()).contains(prompt);
        assertThat(capturedMessages.toString()).contains("Error: Nombre es obligatorio.");
    }
    
    @Test
    @DisplayName("Should return null when captureRequiredStringInput gets null input")
    void shouldReturnNullWhenCaptureRequiredStringInputGetsNullInput() {
        // Given
        StringBuilder capturedMessages = new StringBuilder();
        Consumer<String> showMessage = capturedMessages::append;
        Supplier<String> getInput = () -> null;
        String prompt = "Enter your name: ";
        String fieldName = "Nombre";

        // When
        String result = ValidationUtil.captureRequiredStringInput(prompt, showMessage, getInput, fieldName);

        // Then
        assertThat(result).isNull();
        assertThat(capturedMessages.toString()).contains(prompt);
        assertThat(capturedMessages.toString()).contains("Error: Nombre es obligatorio.");
    }
    
    @Test
    @DisplayName("Should return null when captureRequiredStringInput gets whitespace-only input")
    void shouldReturnNullWhenCaptureRequiredStringInputGetsWhitespaceOnlyInput() {
        // Given
        StringBuilder capturedMessages = new StringBuilder();
        Consumer<String> showMessage = capturedMessages::append;
        Supplier<String> getInput = () -> "   ";
        String prompt = "Enter your name: ";
        String fieldName = "Nombre";

        // When
        String result = ValidationUtil.captureRequiredStringInput(prompt, showMessage, getInput, fieldName);

        // Then
        assertThat(result).isNull();
        assertThat(capturedMessages.toString()).contains(prompt);
        assertThat(capturedMessages.toString()).contains("Error: Nombre es obligatorio.");
    }
    
    @Test
    @DisplayName("Should capture required string input with trimmed whitespace")
    void shouldCaptureRequiredStringInputWithTrimmedWhitespace() {
        // Given
        StringBuilder capturedMessages = new StringBuilder();
        Consumer<String> showMessage = capturedMessages::append;
        Supplier<String> getInput = () -> "  Juan Pérez  ";
        String prompt = "Enter your name: ";
        String fieldName = "Nombre";

        // When
        String result = ValidationUtil.captureRequiredStringInput(prompt, showMessage, getInput, fieldName);

        // Then
        assertThat(result).isEqualTo("Juan Pérez"); // Should be trimmed
        assertThat(capturedMessages.toString()).contains(prompt);
        assertThat(capturedMessages.toString()).doesNotContain("Error: Nombre es obligatorio.");
    }
    
    @Test
    @DisplayName("Should capture required string input with different field names")
    void shouldCaptureRequiredStringInputWithDifferentFieldNames() {
        // Given
        StringBuilder capturedMessages = new StringBuilder();
        Consumer<String> showMessage = capturedMessages::append;
        Supplier<String> getInput = () -> "";
        String prompt = "Enter your email: ";
        String fieldName = "Email";

        // When
        String result = ValidationUtil.captureRequiredStringInput(prompt, showMessage, getInput, fieldName);

        // Then
        assertThat(result).isNull();
        assertThat(capturedMessages.toString()).contains(prompt);
        assertThat(capturedMessages.toString()).contains("Error: Email es obligatorio.");
    }
}
