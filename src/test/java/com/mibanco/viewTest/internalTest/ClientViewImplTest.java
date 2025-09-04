package com.mibanco.viewTest.internalTest;

import com.mibanco.view.internal.ClientViewImpl;
import com.mibanco.view.ConsoleIO;
import com.mibanco.dto.ClientDTO;
import com.mibanco.model.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
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
        assertThat(updateData).containsEntry("newValue", "nuevo@test.com");
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
    
    // ===== TESTS FOR NEW METHODS IMPLEMENTED =====
    
    @Test
    @DisplayName("Should return client ID when deleteClient captures valid input")
    void shouldReturnClientIdWhenDeleteClientCapturesValidInput() {
        // Given
        when(mockConsole.readInput()).thenReturn("123");

        // When
        Optional<String> result = clientView.deleteClient();

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("123");
        verify(mockConsole, times(3)).writeOutput(anyString()); // Header messages + ValidationUtil
        verify(mockConsole).readInput();
    }
    
    @Test
    @DisplayName("Should return empty when deleteClient captures empty input")
    void shouldReturnEmptyWhenDeleteClientCapturesEmptyInput() {
        // Given
        when(mockConsole.readInput()).thenReturn("");

        // When
        Optional<String> result = clientView.deleteClient();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, times(3)).writeOutput(anyString()); // Header messages + ValidationUtil
        verify(mockConsole).readInput();
    }
    
    @Test
    @DisplayName("Should return client ID when restoreClient captures valid input")
    void shouldReturnClientIdWhenRestoreClientCapturesValidInput() {
        // Given
        when(mockConsole.readInput()).thenReturn("456");

        // When
        Optional<String> result = clientView.restoreClient();

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("456");
        verify(mockConsole, times(3)).writeOutput(anyString()); // Header messages + ValidationUtil
        verify(mockConsole).readInput();
    }
    
    @Test
    @DisplayName("Should return empty when restoreClient captures empty input")
    void shouldReturnEmptyWhenRestoreClientCapturesEmptyInput() {
        // Given
        when(mockConsole.readInput()).thenReturn("");

        // When
        Optional<String> result = clientView.restoreClient();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, times(3)).writeOutput(anyString()); // Header messages + ValidationUtil
        verify(mockConsole).readInput();
    }
    
    @Test
    @DisplayName("Should display client list when listAllClients receives valid data")
    void shouldDisplayClientListWhenListAllClientsReceivesValidData() {
        // Given
        List<ClientDTO> clientList = List.of(
            ClientDTO.builder().id(1L).firstName("John").lastName("Doe").dni("12345678A").email("john@test.com").build(),
            ClientDTO.builder().id(2L).firstName("Jane").lastName("Smith").dni("87654321B").email("jane@test.com").build()
        );

        // When
        clientView.listAllClients(clientList);

        // Then
        verify(mockConsole, atLeast(5)).writeOutput(anyString()); // Header + 2 clients + total + empty lines
        verify(mockConsole).writeOutput("=== LISTA DE CLIENTES ===");
        verify(mockConsole).writeOutput("Total de clientes: 2");
    }
    
    @Test
    @DisplayName("Should display empty message when listAllClients receives empty list")
    void shouldDisplayEmptyMessageWhenListAllClientsReceivesEmptyList() {
        // Given
        List<ClientDTO> emptyList = List.of();

        // When
        clientView.listAllClients(emptyList);

        // Then
        verify(mockConsole, atLeast(3)).writeOutput(anyString()); // Header + empty message + empty lines
        verify(mockConsole).writeOutput("=== LISTA DE CLIENTES ===");
        verify(mockConsole).writeOutput("No hay clientes para mostrar.");
    }
    
    @Test
    @DisplayName("Should display empty message when listAllClients receives null list")
    void shouldDisplayEmptyMessageWhenListAllClientsReceivesNullList() {
        // When
        clientView.listAllClients(null);

        // Then
        verify(mockConsole, atLeast(3)).writeOutput(anyString()); // Header + empty message + empty lines
        verify(mockConsole).writeOutput("=== LISTA DE CLIENTES ===");
        verify(mockConsole).writeOutput("No hay clientes para mostrar.");
    }
    
    @Test
    @DisplayName("Should show message when showMessage is called")
    void shouldShowMessageWhenShowMessageIsCalled() {
        // Given
        String testMessage = "Test message";

        // When
        clientView.showMessage(testMessage);

        // Then
        verify(mockConsole).writeOutput(testMessage);
    }
    
    // ===== TESTS FOR MENU METHODS =====
    
    @Test
    @DisplayName("Should show main client menu correctly")
    void shouldShowMainClientMenuCorrectly() {
        // Given - Mock to exit immediately
        when(mockConsole.readInput()).thenReturn("7"); // Exit option

        // When
        clientView.showClientMenu();

        // Then - Verify menu options are displayed
        verify(mockConsole, atLeast(8)).writeOutput(anyString()); // Header + 7 options + prompt
        verify(mockConsole).writeOutput("=== GESTIÓN DE CLIENTES ===");
        verify(mockConsole).writeOutput("1. Crear Cliente");
        verify(mockConsole).writeOutput("2. Buscar Cliente");
        verify(mockConsole).writeOutput("3. Actualizar Cliente");
        verify(mockConsole).writeOutput("4. Eliminar Cliente");
        verify(mockConsole).writeOutput("5. Restaurar Cliente");
        verify(mockConsole).writeOutput("6. Listar Clientes");
        verify(mockConsole).writeOutput("7. Volver al menú principal");
        verify(mockConsole).writeOutput("Elige una opción:");
        verify(mockConsole).readInput(); // Should read input once
    }
    
    @Test
    @DisplayName("Should show update client menu correctly")
    void shouldShowUpdateClientMenuCorrectly() {
        // Given - Mock to exit immediately
        when(mockConsole.readInput()).thenReturn("5"); // Exit option

        // When
        clientView.showUpdateClientMenu();

        // Then - Verify menu options are displayed
        verify(mockConsole, atLeast(6)).writeOutput(anyString()); // Header + 5 options + prompt
        verify(mockConsole).writeOutput("=== ACTUALIZAR CLIENTE ===");
        verify(mockConsole).writeOutput("1. Actualizar Email");
        verify(mockConsole).writeOutput("2. Actualizar Teléfono");
        verify(mockConsole).writeOutput("3. Actualizar Dirección");
        verify(mockConsole).writeOutput("4. Actualizar Múltiples Campos");
        verify(mockConsole).writeOutput("5. Volver al menú de clientes");
        verify(mockConsole).writeOutput("Elige una opción:");
        verify(mockConsole).readInput(); // Should read input once
    }
    
    @Test
    @DisplayName("Should handle main client menu option 6 (List Clients) - simple test")
    void shouldHandleMainClientMenuOption6() {
        // Given - Mock to select option 6
        when(mockConsole.readInput()).thenReturn("6"); // Select option 6

        // When
        Optional<String> result = clientView.showClientMenu();

        // Then
        verify(mockConsole).readInput(); // Should read input once
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("6");
    }
    
    @Test
    @DisplayName("Should handle invalid main client menu option")
    void shouldHandleInvalidMainClientMenuOption() {
        // Given - Mock to select invalid option
        when(mockConsole.readInput()).thenReturn("99"); // Invalid option

        // When
        Optional<String> result = clientView.showClientMenu();

        // Then
        verify(mockConsole).readInput(); // Should read input once
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("99");
    }
    
    @Test
    @DisplayName("Should handle invalid update client menu option")
    void shouldHandleInvalidUpdateClientMenuOption() {
        // Given - Mock to select invalid option
        when(mockConsole.readInput()).thenReturn("99"); // Invalid option

        // When
        Optional<String> result = clientView.showUpdateClientMenu();

        // Then
        verify(mockConsole).readInput(); // Should read input once
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo("99");
    }
    
    // ===== TESTS FOR UPDATE METHODS =====
    
    @Test
    @DisplayName("Should update client phone successfully with valid data")
    void shouldUpdateClientPhoneSuccessfullyWithValidData() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("123")           // Client ID
            .thenReturn("987654321")     // New phone
            .thenReturn("s");            // Confirmation

        // When
        Optional<Map<String, String>> result = clientView.updateClientPhone();

        // Then
        assertThat(result).isPresent();
        Map<String, String> updateData = result.get();
        assertThat(updateData).containsEntry("id", "123");
        assertThat(updateData).containsEntry("newValue", "987654321");
        verify(mockConsole, atLeast(4)).writeOutput(anyString()); // Header + prompts + ValidationUtil
        verify(mockConsole, times(3)).readInput();
    }
    
    @Test
    @DisplayName("Should return empty when updateClientPhone has invalid ID")
    void shouldReturnEmptyWhenUpdateClientPhoneHasInvalidId() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("invalid")       // Invalid ID
            .thenReturn("987654321");    // New phone

        // When
        Optional<Map<String, String>> result = clientView.updateClientPhone();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, atLeast(4)).writeOutput(anyString()); // Header + prompts + ValidationUtil + error
        verify(mockConsole, times(2)).readInput();
    }
    
    @Test
    @DisplayName("Should return empty when updateClientPhone is cancelled")
    void shouldReturnEmptyWhenUpdateClientPhoneIsCancelled() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("123")           // Client ID
            .thenReturn("987654321")     // New phone
            .thenReturn("n");            // Cancel confirmation

        // When
        Optional<Map<String, String>> result = clientView.updateClientPhone();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, atLeast(4)).writeOutput(anyString()); // Header + prompts + ValidationUtil
        verify(mockConsole, times(3)).readInput();
    }
    
    @Test
    @DisplayName("Should update client address successfully with valid data")
    void shouldUpdateClientAddressSuccessfullyWithValidData() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("123")                    // Client ID
            .thenReturn("Nueva Dirección 456")    // New address
            .thenReturn("s");                     // Confirmation

        // When
        Optional<Map<String, String>> result = clientView.updateClientAddress();

        // Then
        assertThat(result).isPresent();
        Map<String, String> updateData = result.get();
        assertThat(updateData).containsEntry("id", "123");
        assertThat(updateData).containsEntry("newValue", "Nueva Dirección 456");
        verify(mockConsole, atLeast(4)).writeOutput(anyString()); // Header + prompts + ValidationUtil
        verify(mockConsole, times(3)).readInput();
    }
    
    @Test
    @DisplayName("Should return empty when updateClientAddress has invalid ID")
    void shouldReturnEmptyWhenUpdateClientAddressHasInvalidId() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("invalid")                // Invalid ID
            .thenReturn("Nueva Dirección 456");   // New address

        // When
        Optional<Map<String, String>> result = clientView.updateClientAddress();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, atLeast(4)).writeOutput(anyString()); // Header + prompts + ValidationUtil + error
        verify(mockConsole, times(2)).readInput();
    }
    
    @Test
    @DisplayName("Should return empty when updateClientAddress is cancelled")
    void shouldReturnEmptyWhenUpdateClientAddressIsCancelled() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("123")                    // Client ID
            .thenReturn("Nueva Dirección 456")    // New address
            .thenReturn("n");                     // Cancel confirmation

        // When
        Optional<Map<String, String>> result = clientView.updateClientAddress();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, atLeast(4)).writeOutput(anyString()); // Header + prompts + ValidationUtil
        verify(mockConsole, times(3)).readInput();
    }
    
    @Test
    @DisplayName("Should update client multiple fields successfully with valid data")
    void shouldUpdateClientMultipleFieldsSuccessfullyWithValidData() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("123")                    // Client ID
            .thenReturn("nuevo@test.com")         // New email
            .thenReturn("987654321")              // New phone
            .thenReturn("Nueva Dirección 789")    // New address
            .thenReturn("s");                     // Confirmation

        // When
        Optional<Map<String, Object>> result = clientView.updateClientMultipleFields();

        // Then
        assertThat(result).isPresent();
        Map<String, Object> updateData = result.get();
        assertThat(updateData).containsEntry("id", 123L);
        assertThat(updateData).containsEntry("email", "nuevo@test.com");
        assertThat(updateData).containsEntry("phone", "987654321");
        assertThat(updateData).containsEntry("address", "Nueva Dirección 789");
        verify(mockConsole, atLeast(6)).writeOutput(anyString()); // Header + prompts + ValidationUtil + summary
        verify(mockConsole, times(5)).readInput();
    }
    
    @Test
    @DisplayName("Should update client multiple fields with partial data")
    void shouldUpdateClientMultipleFieldsWithPartialData() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("123")                    // Client ID
            .thenReturn("nuevo@test.com")         // New email
            .thenReturn("")                       // Empty phone (skip)
            .thenReturn("Nueva Dirección 789")    // New address
            .thenReturn("s");                     // Confirmation

        // When
        Optional<Map<String, Object>> result = clientView.updateClientMultipleFields();

        // Then
        assertThat(result).isPresent();
        Map<String, Object> updateData = result.get();
        assertThat(updateData).containsEntry("id", 123L);
        assertThat(updateData).containsEntry("email", "nuevo@test.com");
        assertThat(updateData).containsEntry("address", "Nueva Dirección 789");
        assertThat(updateData).doesNotContainKey("phone"); // Should not include empty phone
        verify(mockConsole, atLeast(6)).writeOutput(anyString()); // Header + prompts + ValidationUtil + summary
        verify(mockConsole, times(5)).readInput();
    }
    
    @Test
    @DisplayName("Should return empty when updateClientMultipleFields has invalid ID")
    void shouldReturnEmptyWhenUpdateClientMultipleFieldsHasInvalidId() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("invalid");               // Invalid ID

        // When
        Optional<Map<String, Object>> result = clientView.updateClientMultipleFields();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, atLeast(4)).writeOutput(anyString()); // Header + prompts + ValidationUtil + error
        verify(mockConsole, times(1)).readInput();
    }
    
    @Test
    @DisplayName("Should return empty when updateClientMultipleFields has no fields to update")
    void shouldReturnEmptyWhenUpdateClientMultipleFieldsHasNoFieldsToUpdate() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("123")                    // Client ID
            .thenReturn("")                       // Empty email
            .thenReturn("")                       // Empty phone
            .thenReturn("");                      // Empty address

        // When
        Optional<Map<String, Object>> result = clientView.updateClientMultipleFields();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, atLeast(5)).writeOutput(anyString()); // Header + prompts + ValidationUtil + error
        verify(mockConsole, times(5)).readInput();
    }
    
    @Test
    @DisplayName("Should return empty when updateClientMultipleFields is cancelled")
    void shouldReturnEmptyWhenUpdateClientMultipleFieldsIsCancelled() {
        // Given
        when(mockConsole.readInput())
            .thenReturn("123")                    // Client ID
            .thenReturn("nuevo@test.com")         // New email
            .thenReturn("987654321")              // New phone
            .thenReturn("Nueva Dirección 789")    // New address
            .thenReturn("n");                     // Cancel confirmation

        // When
        Optional<Map<String, Object>> result = clientView.updateClientMultipleFields();

        // Then
        assertThat(result).isEmpty();
        verify(mockConsole, atLeast(6)).writeOutput(anyString()); // Header + prompts + ValidationUtil + summary
        verify(mockConsole, times(5)).readInput();
    }
    
    // ===== TESTS FOR DISPLAY CLIENT METHOD =====
    
    @Test
    @DisplayName("Should display client information and return true when confirmed")
    void shouldDisplayClientInformationAndReturnTrueWhenConfirmed() {
        // Given
        Client testClient = Client.builder()
            .id(123L)
            .firstName("Juan")
            .lastName("Pérez")
            .dni("12345678A")
            .birthDate(LocalDate.of(1990, 1, 1))
            .email("juan@test.com")
            .phone("123456789")
            .address("Calle Test 123")
            .build();
        
        when(mockConsole.readInput()).thenReturn("s"); // Confirmation

        // When
        boolean result = clientView.displayClient(testClient);

        // Then
        assertThat(result).isTrue();
        verify(mockConsole, atLeast(8)).writeOutput(anyString()); // Header + client info + prompt + ValidationUtil
        verify(mockConsole).readInput();
        
        // Verify specific client information is displayed
        verify(mockConsole).writeOutput("ID: 123");
        verify(mockConsole).writeOutput("Nombre: Juan Pérez");
        verify(mockConsole).writeOutput("DNI: 12345678A");
        verify(mockConsole).writeOutput("Email: juan@test.com");
        verify(mockConsole).writeOutput("Teléfono: 123456789");
        verify(mockConsole).writeOutput("Dirección: Calle Test 123");
    }
    
    @Test
    @DisplayName("Should display client information and return false when not confirmed")
    void shouldDisplayClientInformationAndReturnFalseWhenNotConfirmed() {
        // Given
        Client testClient = Client.builder()
            .id(456L)
            .firstName("María")
            .lastName("García")
            .dni("87654321B")
            .birthDate(LocalDate.of(1985, 5, 15))
            .email("maria@test.com")
            .phone("987654321")
            .address("Avenida Test 456")
            .build();
        
        when(mockConsole.readInput()).thenReturn("n"); // No confirmation

        // When
        boolean result = clientView.displayClient(testClient);

        // Then
        assertThat(result).isFalse();
        verify(mockConsole, atLeast(8)).writeOutput(anyString()); // Header + client info + prompt + ValidationUtil
        verify(mockConsole).readInput();
        
        // Verify specific client information is displayed
        verify(mockConsole).writeOutput("ID: 456");
        verify(mockConsole).writeOutput("Nombre: María García");
        verify(mockConsole).writeOutput("DNI: 87654321B");
        verify(mockConsole).writeOutput("Email: maria@test.com");
        verify(mockConsole).writeOutput("Teléfono: 987654321");
        verify(mockConsole).writeOutput("Dirección: Avenida Test 456");
    }
    
    @Test
    @DisplayName("Should display client information and return false when confirmation is case insensitive")
    void shouldDisplayClientInformationAndReturnFalseWhenConfirmationIsCaseInsensitive() {
        // Given
        Client testClient = Client.builder()
            .id(789L)
            .firstName("Carlos")
            .lastName("López")
            .dni("11223344C")
            .birthDate(LocalDate.of(1992, 12, 25))
            .email("carlos@test.com")
            .phone("555666777")
            .address("Plaza Test 789")
            .build();
        
        when(mockConsole.readInput()).thenReturn("S"); // Uppercase confirmation

        // When
        boolean result = clientView.displayClient(testClient);

        // Then
        assertThat(result).isTrue();
        verify(mockConsole, atLeast(8)).writeOutput(anyString()); // Header + client info + prompt + ValidationUtil
        verify(mockConsole).readInput();
    }
}
