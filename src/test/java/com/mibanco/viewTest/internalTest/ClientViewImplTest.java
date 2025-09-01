package com.mibanco.viewTest.internalTest;

import com.mibanco.view.internal.ClientViewImpl;
import com.mibanco.view.ConsoleIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Simple test for ClientViewImpl.captureDataClient()
 * Focuses on covering the if and catch blocks
 */
class ClientViewImplTest {

    private ClientViewImpl clientView;
    private ConsoleIO mockConsole;

    @BeforeEach
    void setUp() {
        mockConsole = mock(ConsoleIO.class);
        clientView = new ClientViewImpl(mockConsole);
    }

    @Test
    @DisplayName("Should handle user cancellation in captureDataClient")
    void shouldHandleUserCancellationInCaptureDataClient() {
        // Given - Simulate user input but cancel at confirmation
        when(mockConsole.readInput())
            .thenReturn("John")           // firstName
            .thenReturn("Doe")            // lastName
            .thenReturn("12345678A")      // dni
            .thenReturn("1990-01-01")     // birthDate
            .thenReturn("john@test.com")  // email
            .thenReturn("123456789")      // phone
            .thenReturn("123 Main St")    // address
            .thenReturn("n");             // cancel confirmation

        // When
        Optional<Map<String, String>> result = clientView.captureDataClient();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, times(8)).readInput();
    }

    @Test
    @DisplayName("Should handle successful confirmation in captureDataClient")
    void shouldHandleSuccessfulConfirmationInCaptureDataClient() {
        // Given - Simulate user input with confirmation
        when(mockConsole.readInput())
            .thenReturn("John")           // firstName
            .thenReturn("Doe")            // lastName
            .thenReturn("12345678A")      // dni
            .thenReturn("1990-01-01")     // birthDate
            .thenReturn("john@test.com")  // email
            .thenReturn("123456789")      // phone
            .thenReturn("123 Main St")    // address
            .thenReturn("s");             // confirm creation

        // When
        Optional<Map<String, String>> result = clientView.captureDataClient();

        // Then
        assertThat(result).isPresent();
        Map<String, String> clientData = result.get();
        assertThat(clientData).containsKeys("firstName", "lastName", "dni", "birthDate", "email", "phone", "address");
        verify(mockConsole, times(8)).readInput();
    }

    @Test
    @DisplayName("Should handle exception in captureDataClient")
    void shouldHandleExceptionInCaptureDataClient() {
        // Given - Simulate exception during input
        when(mockConsole.readInput()).thenThrow(new RuntimeException("Test exception"));

        // When
        Optional<Map<String, String>> result = clientView.captureDataClient();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole).readInput();
    }
    
    @Test
    @DisplayName("Should search client by ID successfully")
    void shouldSearchClientByIdSuccessfully() {
        // Given - Simulate user choosing ID search and entering valid ID
        when(mockConsole.readInput())
            .thenReturn("1")      // Choose ID search
            .thenReturn("123");   // Enter ID 123

        // When
        Optional<Map<String, String>> result = clientView.searchClient();

        // Then
        assertThat(result).isPresent();
        Map<String, String> searchCriteria = result.get();
        assertThat(searchCriteria).containsEntry("searchType", "id");
        assertThat(searchCriteria).containsEntry("searchValue", "123");
        verify(mockConsole, times(2)).readInput();
    }
    
    @Test
    @DisplayName("Should search client by DNI successfully")
    void shouldSearchClientByDniSuccessfully() {
        // Given - Simulate user choosing DNI search and entering valid DNI
        when(mockConsole.readInput())
            .thenReturn("2")              // Choose DNI search
            .thenReturn("12345678A");     // Enter DNI

        // When
        Optional<Map<String, String>> result = clientView.searchClient();

        // Then
        assertThat(result).isPresent();
        Map<String, String> searchCriteria = result.get();
        assertThat(searchCriteria).containsEntry("searchType", "dni");
        assertThat(searchCriteria).containsEntry("searchValue", "12345678A");
        verify(mockConsole, times(2)).readInput();
    }
    
    @Test
    @DisplayName("Should return to menu when option 3 is selected")
    void shouldReturnToMenuWhenOption3IsSelected() {
        // Given - Simulate user choosing to return to menu
        when(mockConsole.readInput()).thenReturn("3");

        // When
        Optional<Map<String, String>> result = clientView.searchClient();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole).readInput();
    }
    
    @Test
    @DisplayName("Should handle invalid option in searchClient")
    void shouldHandleInvalidOptionInSearchClient() {
        // Given - Simulate user entering invalid option
        when(mockConsole.readInput()).thenReturn("99");

        // When
        Optional<Map<String, String>> result = clientView.searchClient();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole).readInput();
    }
    
    @Test
    @DisplayName("Should handle NumberFormatException in searchClientById")
    void shouldHandleNumberFormatExceptionInSearchClientById() {
        // Given - Simulate user choosing ID search and entering invalid ID
        when(mockConsole.readInput())
            .thenReturn("1")      // Choose ID search
            .thenReturn("abc");   // Enter invalid ID (not a number)

        // When
        Optional<Map<String, String>> result = clientView.searchClient();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, times(2)).readInput();
    }
    
    @Test
    @DisplayName("Should handle empty DNI in searchClientByDni")
    void shouldHandleEmptyDniInSearchClientByDni() {
        // Given - Simulate user choosing DNI search and entering empty DNI
        when(mockConsole.readInput())
            .thenReturn("2")      // Choose DNI search
            .thenReturn("");      // Enter empty DNI

        // When
        Optional<Map<String, String>> result = clientView.searchClient();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, times(2)).readInput();
    }
    
    @Test
    @DisplayName("Should update client email successfully with confirmation")
    void shouldUpdateClientEmailSuccessfullyWithConfirmation() {
        // Given - Simulate user input for email update with confirmation
        when(mockConsole.readInput())
            .thenReturn("123")           // ID del cliente
            .thenReturn("nuevo@test.com") // Nuevo email
            .thenReturn("s");            // Confirmar actualización

        // When
        Optional<Map<String, String>> result = clientView.updateClientEmail();

        // Then
        assertThat(result).isPresent();
        Map<String, String> updateData = result.get();
        assertThat(updateData).containsEntry("id", "123");
        assertThat(updateData).containsEntry("newEmail", "nuevo@test.com");
        verify(mockConsole, times(3)).readInput();
    }
    
    @Test
    @DisplayName("Should cancel client email update")
    void shouldCancelClientEmailUpdate() {
        // Given - Simulate user input for email update but cancel confirmation
        when(mockConsole.readInput())
            .thenReturn("123")           // ID del cliente
            .thenReturn("nuevo@test.com") // Nuevo email
            .thenReturn("n");            // Cancelar actualización

        // When
        Optional<Map<String, String>> result = clientView.updateClientEmail();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, times(3)).readInput();
    }
    
    @Test
    @DisplayName("Should handle empty email in updateClientEmail")
    void shouldHandleEmptyEmailInUpdateClientEmail() {
        // Given - Simulate user input with empty email
        when(mockConsole.readInput())
            .thenReturn("123")           // ID del cliente
            .thenReturn("");             // Email vacío

        // When
        Optional<Map<String, String>> result = clientView.updateClientEmail();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, times(2)).readInput();
    }
    
    @Test
    @DisplayName("Should handle invalid ID format in updateClientEmail")
    void shouldHandleInvalidIdFormatInUpdateClientEmail() {
        // Given - Simulate user input with invalid ID format
        when(mockConsole.readInput())
            .thenReturn("abc")           // ID inválido (no numérico)
            .thenReturn("nuevo@test.com"); // Nuevo email

        // When
        Optional<Map<String, String>> result = clientView.updateClientEmail();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, times(2)).readInput();
    }
}
