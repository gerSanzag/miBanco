package com.mibanco.utilTest;

import com.mibanco.util.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for ValidationException class
 */
class ValidationExceptionTest {

    @Test
    @DisplayName("ValidationException: should create exception with message")
    void validationExceptionShouldCreateExceptionWithMessage() {
        String message = "Test validation error";
        ValidationException exception = new ValidationException(message);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("ValidationException: should create exception with null message")
    void validationExceptionShouldCreateExceptionWithNullMessage() {
        ValidationException exception = new ValidationException(null);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isNull();
    }

    @Test
    @DisplayName("ValidationException: should create exception with empty message")
    void validationExceptionShouldCreateExceptionWithEmptyMessage() {
        String message = "";
        ValidationException exception = new ValidationException(message);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("ValidationException: should create exception with message and cause")
    void validationExceptionShouldCreateExceptionWithMessageAndCause() {
        String message = "Test validation error with cause";
        Throwable cause = new RuntimeException("Original cause");
        ValidationException exception = new ValidationException(message, cause);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("ValidationException: should create exception with null message and cause")
    void validationExceptionShouldCreateExceptionWithNullMessageAndCause() {
        Throwable cause = new RuntimeException("Original cause");
        ValidationException exception = new ValidationException(null, cause);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("ValidationException: should create exception with message and null cause")
    void validationExceptionShouldCreateExceptionWithMessageAndNullCause() {
        String message = "Test validation error with null cause";
        ValidationException exception = new ValidationException(message, null);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("ValidationException: should create exception with null message and null cause")
    void validationExceptionShouldCreateExceptionWithNullMessageAndNullCause() {
        ValidationException exception = new ValidationException(null, null);
        
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isNull();
        assertThat(exception.getCause()).isNull();
    }

    @Test
    @DisplayName("ValidationException: should be throwable")
    void validationExceptionShouldBeThrowable() {
        String message = "Test throwable validation error";
        
        assertThatThrownBy(() -> {
            throw new ValidationException(message);
        })
        .isInstanceOf(ValidationException.class)
        .hasMessage(message);
    }

    @Test
    @DisplayName("ValidationException: should be throwable with cause")
    void validationExceptionShouldBeThrowableWithCause() {
        String message = "Test throwable validation error with cause";
        Throwable cause = new IllegalArgumentException("Invalid argument");
        
        assertThatThrownBy(() -> {
            throw new ValidationException(message, cause);
        })
        .isInstanceOf(ValidationException.class)
        .hasMessage(message)
        .hasCause(cause);
    }

    @Test
    @DisplayName("ValidationException: should extend RuntimeException")
    void validationExceptionShouldExtendRuntimeException() {
        ValidationException exception = new ValidationException("Test");
        
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("ValidationException: should have correct inheritance hierarchy")
    void validationExceptionShouldHaveCorrectInheritanceHierarchy() {
        ValidationException exception = new ValidationException("Test");
        
        assertThat(exception).isInstanceOf(ValidationException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception).isInstanceOf(Exception.class);
        assertThat(exception).isInstanceOf(Throwable.class);
    }

    @Test
    @DisplayName("ValidationException: should preserve stack trace")
    void validationExceptionShouldPreserveStackTrace() {
        ValidationException exception = new ValidationException("Test");
        
        assertThat(exception.getStackTrace()).isNotNull();
        assertThat(exception.getStackTrace().length).isGreaterThan(0);
    }

    @Test
    @DisplayName("ValidationException: should preserve stack trace with cause")
    void validationExceptionShouldPreserveStackTraceWithCause() {
        Throwable cause = new RuntimeException("Cause");
        ValidationException exception = new ValidationException("Test", cause);
        
        assertThat(exception.getStackTrace()).isNotNull();
        assertThat(exception.getStackTrace().length).isGreaterThan(0);
        assertThat(exception.getCause().getStackTrace()).isNotNull();
    }
}
