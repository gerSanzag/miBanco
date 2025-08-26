package com.mibanco.viewTest.utilTest;

import com.mibanco.view.util.ValidationUtil;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ValidationUtil.
 * Tests validation and input capture methods.
 */
class ValidationUtilTest {

    // ===== VALIDATION METHODS TESTS =====

    @Test
    void testValidateRequiredFieldsWithMap_AllFieldsPresent() {
        // Given: Map with all required fields
        Map<String, String> fields = new HashMap<>();
        fields.put("firstName", "John");
        fields.put("lastName", "Doe");
        fields.put("dni", "12345678");
        
        List<String> requiredFields = List.of("firstName", "lastName", "dni");
        
        // When: Validate required fields
        boolean result = ValidationUtil.validateRequiredFields(fields, requiredFields);
        
        // Then: Should be valid
        assertTrue(result);
    }

    @Test
    void testValidateRequiredFieldsWithMap_MissingField() {
        // Given: Map with missing required field
        Map<String, String> fields = new HashMap<>();
        fields.put("firstName", "John");
        fields.put("lastName", "Doe");
        // dni is missing
        
        List<String> requiredFields = List.of("firstName", "lastName", "dni");
        
        // When: Validate required fields
        boolean result = ValidationUtil.validateRequiredFields(fields, requiredFields);
        
        // Then: Should be invalid
        assertFalse(result);
    }

    @Test
    void testValidateRequiredFieldsWithMap_EmptyField() {
        // Given: Map with empty required field
        Map<String, String> fields = new HashMap<>();
        fields.put("firstName", "John");
        fields.put("lastName", "");
        fields.put("dni", "12345678");
        
        List<String> requiredFields = List.of("firstName", "lastName", "dni");
        
        // When: Validate required fields
        boolean result = ValidationUtil.validateRequiredFields(fields, requiredFields);
        
        // Then: Should be invalid
        assertFalse(result);
    }

    @Test
    void testValidateRequiredFieldsWithMap_NullField() {
        // Given: Map with null required field
        Map<String, String> fields = new HashMap<>();
        fields.put("firstName", "John");
        fields.put("lastName", null);
        fields.put("dni", "12345678");
        
        List<String> requiredFields = List.of("firstName", "lastName", "dni");
        
        // When: Validate required fields
        boolean result = ValidationUtil.validateRequiredFields(fields, requiredFields);
        
        // Then: Should be invalid
        assertFalse(result);
    }

    @Test
    void testValidateRequiredFieldsWithMap_NullMap() {
        // Given: Null map
        List<String> requiredFields = List.of("firstName", "lastName", "dni");
        
        // When: Validate required fields
        boolean result = ValidationUtil.validateRequiredFields(null, requiredFields);
        
        // Then: Should be invalid
        assertFalse(result);
    }

    @Test
    void testValidateRequiredFieldsWithMap_NullRequiredFields() {
        // Given: Null required fields list
        Map<String, String> fields = new HashMap<>();
        fields.put("firstName", "John");
        
        // When: Validate required fields
        boolean result = ValidationUtil.validateRequiredFields(fields, null);
        
        // Then: Should be invalid
        assertFalse(result);
    }

    @Test
    void testValidateRequiredFieldsWithVarargs_AllValid() {
        // Given: All valid field values
        String firstName = "John";
        String lastName = "Doe";
        String dni = "12345678";
        
        // When: Validate required fields
        boolean result = ValidationUtil.validateRequiredFields(firstName, lastName, dni);
        
        // Then: Should be valid
        assertTrue(result);
    }

    @Test
    void testValidateRequiredFieldsWithVarargs_OneEmpty() {
        // Given: One empty field
        String firstName = "John";
        String lastName = "";
        String dni = "12345678";
        
        // When: Validate required fields
        boolean result = ValidationUtil.validateRequiredFields(firstName, lastName, dni);
        
        // Then: Should be invalid
        assertFalse(result);
    }

    @Test
    void testValidateRequiredFieldsWithVarargs_OneNull() {
        // Given: One null field
        String firstName = "John";
        String lastName = null;
        String dni = "12345678";
        
        // When: Validate required fields
        boolean result = ValidationUtil.validateRequiredFields(firstName, lastName, dni);
        
        // Then: Should be invalid
        assertFalse(result);
    }

    @Test
    void testValidateRequiredFieldsWithVarargs_NullArray() {
        // When: Validate required fields with null array
        boolean result = ValidationUtil.validateRequiredFields((String[]) null);
        
        // Then: Should be invalid
        assertFalse(result);
    }

    @Test
    void testIsValidEmail_ValidEmail() {
        // Given: Valid email
        String email = "john.doe@example.com";
        
        // When: Validate email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Then: Should be valid
        assertTrue(result);
    }

    @Test
    void testIsValidEmail_InvalidEmail() {
        // Given: Invalid email
        String email = "invalid-email";
        
        // When: Validate email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Then: Should be invalid
        assertFalse(result);
    }

    @Test
    void testIsValidEmail_EmptyEmail() {
        // Given: Empty email
        String email = "";
        
        // When: Validate email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Then: Should be valid (empty is allowed)
        assertTrue(result);
    }

    @Test
    void testIsValidEmail_NullEmail() {
        // Given: Null email
        String email = null;
        
        // When: Validate email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Then: Should be valid (null is allowed)
        assertTrue(result);
    }

    @Test
    void testIsValidEmail_ComplexValidEmail() {
        // Given: Complex valid email
        String email = "user.name+tag@domain.co.uk";
        
        // When: Validate email
        boolean result = ValidationUtil.isValidEmail(email);
        
        // Then: Should be valid
        assertTrue(result);
    }

    @Test
    void testIsNotNullOrEmpty_ValidString() {
        // Given: Valid string
        String value = "test";
        
        // When: Check if not null or empty
        boolean result = ValidationUtil.isNotNullOrEmpty(value);
        
        // Then: Should be true
        assertTrue(result);
    }

    @Test
    void testIsNotNullOrEmpty_EmptyString() {
        // Given: Empty string
        String value = "";
        
        // When: Check if not null or empty
        boolean result = ValidationUtil.isNotNullOrEmpty(value);
        
        // Then: Should be false
        assertFalse(result);
    }

    @Test
    void testIsNotNullOrEmpty_WhitespaceString() {
        // Given: Whitespace string
        String value = "   ";
        
        // When: Check if not null or empty
        boolean result = ValidationUtil.isNotNullOrEmpty(value);
        
        // Then: Should be false
        assertFalse(result);
    }

    @Test
    void testIsNotNullOrEmpty_NullString() {
        // Given: Null string
        String value = null;
        
        // When: Check if not null or empty
        boolean result = ValidationUtil.isNotNullOrEmpty(value);
        
        // Then: Should be false
        assertFalse(result);
    }

    @Test
    void testHasMinimumLength_ValidLength() {
        // Given: String with valid length
        String value = "test";
        int minLength = 3;
        
        // When: Check minimum length
        boolean result = ValidationUtil.hasMinimumLength(value, minLength);
        
        // Then: Should be true
        assertTrue(result);
    }

    @Test
    void testHasMinimumLength_ExactLength() {
        // Given: String with exact length
        String value = "test";
        int minLength = 4;
        
        // When: Check minimum length
        boolean result = ValidationUtil.hasMinimumLength(value, minLength);
        
        // Then: Should be true
        assertTrue(result);
    }

    @Test
    void testHasMinimumLength_TooShort() {
        // Given: String too short
        String value = "test";
        int minLength = 5;
        
        // When: Check minimum length
        boolean result = ValidationUtil.hasMinimumLength(value, minLength);
        
        // Then: Should be false
        assertFalse(result);
    }

    @Test
    void testHasMinimumLength_EmptyString() {
        // Given: Empty string
        String value = "";
        int minLength = 1;
        
        // When: Check minimum length
        boolean result = ValidationUtil.hasMinimumLength(value, minLength);
        
        // Then: Should be false
        assertFalse(result);
    }

    @Test
    void testHasMinimumLength_NullString() {
        // Given: Null string
        String value = null;
        int minLength = 1;
        
        // When: Check minimum length
        boolean result = ValidationUtil.hasMinimumLength(value, minLength);
        
        // Then: Should be false
        assertFalse(result);
    }

    // ===== INPUT CAPTURE METHODS TESTS =====

    @Test
    void testCaptureStringInput() {
        // Given: Mock input and output
        String expectedInput = "test input";
        StringBuilder output = new StringBuilder();
        
        Consumer<String> showMessage = output::append;
        Supplier<String> getInput = () -> expectedInput;
        
        // When: Capture string input
        String result = ValidationUtil.captureStringInput("Enter text: ", showMessage, getInput);
        
        // Then: Should return trimmed input
        assertEquals(expectedInput, result);
        assertTrue(output.toString().contains("Enter text: "));
    }

    @Test
    void testCaptureStringInput_TrimsWhitespace() {
        // Given: Input with whitespace
        String inputWithWhitespace = "  test input  ";
        StringBuilder output = new StringBuilder();
        
        Consumer<String> showMessage = output::append;
        Supplier<String> getInput = () -> inputWithWhitespace;
        
        // When: Capture string input
        String result = ValidationUtil.captureStringInput("Enter text: ", showMessage, getInput);
        
        // Then: Should return trimmed input
        assertEquals("test input", result);
    }

    @Test
    void testCaptureRequiredStringInput_ValidInput() {
        // Given: Valid input
        String validInput = "test";
        StringBuilder output = new StringBuilder();
        
        Consumer<String> showMessage = output::append;
        Supplier<String> getInput = () -> validInput;
        
        // When: Capture required string input
        String result = ValidationUtil.captureRequiredStringInput("Enter name: ", showMessage, getInput, "name");
        
        // Then: Should return input
        assertEquals(validInput, result);
        assertTrue(output.toString().contains("Enter name: "));
    }

    @Test
    void testCaptureRequiredStringInput_EmptyInput() {
        // Given: Empty input
        String emptyInput = "";
        StringBuilder output = new StringBuilder();
        
        Consumer<String> showMessage = output::append;
        Supplier<String> getInput = () -> emptyInput;
        
        // When: Capture required string input
        String result = ValidationUtil.captureRequiredStringInput("Enter name: ", showMessage, getInput, "name");
        
        // Then: Should return null and show error
        assertNull(result);
        assertTrue(output.toString().contains("Error: name es obligatorio."));
    }

    @Test
    void testCaptureRequiredStringInput_NullInput() {
        // Given: Null input
        String nullInput = null;
        StringBuilder output = new StringBuilder();
        
        Consumer<String> showMessage = output::append;
        Supplier<String> getInput = () -> nullInput;
        
        // When: Capture required string input
        String result = ValidationUtil.captureRequiredStringInput("Enter name: ", showMessage, getInput, "name");
        
        // Then: Should return null and show error
        assertNull(result);
        assertTrue(output.toString().contains("Error: name es obligatorio."));
    }

    @Test
    void testCaptureEmailInput_ValidEmail() {
        // Given: Valid email
        String validEmail = "test@example.com";
        StringBuilder output = new StringBuilder();
        
        Consumer<String> showMessage = output::append;
        Supplier<String> getInput = () -> validEmail;
        
        // When: Capture email input
        String result = ValidationUtil.captureEmailInput("Enter email: ", showMessage, getInput);
        
        // Then: Should return email
        assertEquals(validEmail, result);
    }

    @Test
    void testCaptureEmailInput_EmptyEmail() {
        // Given: Empty email
        String emptyEmail = "";
        StringBuilder output = new StringBuilder();
        
        Consumer<String> showMessage = output::append;
        Supplier<String> getInput = () -> emptyEmail;
        
        // When: Capture email input
        String result = ValidationUtil.captureEmailInput("Enter email: ", showMessage, getInput);
        
        // Then: Should return empty email (allowed)
        assertEquals(emptyEmail, result);
    }

    @Test
    void testCaptureEmailInput_InvalidEmail() {
        // Given: Invalid email
        String invalidEmail = "invalid-email";
        StringBuilder output = new StringBuilder();
        
        Consumer<String> showMessage = output::append;
        Supplier<String> getInput = () -> invalidEmail;
        
        // When: Capture email input
        String result = ValidationUtil.captureEmailInput("Enter email: ", showMessage, getInput);
        
        // Then: Should return null and show error
        assertNull(result);
        assertTrue(output.toString().contains("Error: Formato de email inválido."));
    }
}
