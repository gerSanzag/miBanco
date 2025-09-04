package com.mibanco.controllerTest.internalTest;

import com.mibanco.controller.internal.ClientControllerImpl;
import com.mibanco.dto.ClientDTO;
import com.mibanco.model.Client;
import com.mibanco.service.ClientService;
import com.mibanco.view.ClientView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Test class for ClientControllerImpl
 * Focuses on testing the orchestration between View and Service layers
 */
class ClientControllerImplTest {

    private ClientControllerImpl controller;
    private ClientService mockClientService;
    private ClientView mockClientView;

    @BeforeEach
    void setUp() {
        mockClientService = mock(ClientService.class);
        mockClientView = mock(ClientView.class);
        controller = new ClientControllerImpl(mockClientService, mockClientView);
    }

    @Test
    @DisplayName("Should return true when createClient succeeds with valid data")
    void shouldReturnTrueWhenCreateClientSucceedsWithValidData() {
        // Given
        Map<String, String> validClientData = Map.of(
            "firstName", "John",
            "lastName", "Doe",
            "dni", "12345678A",
            "birthDate", "1990-01-01",
            "email", "john.doe@example.com",
            "phone", "123456789",
            "address", "123 Main St"
        );

        ClientDTO expectedClientDto = ClientDTO.builder()
            .firstName("John")
            .lastName("Doe")
            .dni("12345678A")
            .birthDate(LocalDate.of(1990, 1, 1))
            .email("john.doe@example.com")
            .phone("123456789")
            .address("123 Main St")
            .build();

        when(mockClientView.captureDataClient()).thenReturn(Optional.of(validClientData));
        when(mockClientService.createClientDto(validClientData)).thenReturn(Optional.of(expectedClientDto));

        // When
        boolean result = controller.createClient();

        // Then
        assertThat(result).isTrue();
        verify(mockClientView).captureDataClient();
        verify(mockClientService).createClientDto(validClientData);
    }

    @Test
    @DisplayName("Should return false when view returns empty data")
    void shouldReturnFalseWhenViewReturnsEmptyData() {
        // Given
        when(mockClientView.captureDataClient()).thenReturn(Optional.empty());

        // When
        boolean result = controller.createClient();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).captureDataClient();
        verify(mockClientService, never()).createClientDto(any());
    }

    @Test
    @DisplayName("Should return false when service returns empty DTO")
    void shouldReturnFalseWhenServiceReturnsEmptyDto() {
        // Given
        Map<String, String> validClientData = Map.of(
            "firstName", "John",
            "lastName", "Doe",
            "dni", "12345678A",
            "birthDate", "1990-01-01",
            "email", "john.doe@example.com",
            "phone", "123456789",
            "address", "123 Main St"
        );

        when(mockClientView.captureDataClient()).thenReturn(Optional.of(validClientData));
        when(mockClientService.createClientDto(validClientData)).thenReturn(Optional.empty());

        // When
        boolean result = controller.createClient();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).captureDataClient();
        verify(mockClientService).createClientDto(validClientData);
    }

    @Test
    @DisplayName("Should return false when data is missing required fields")
    void shouldReturnFalseWhenDataIsMissingRequiredFields() {
        // Given - Missing required fields (firstName, lastName, dni)
        Map<String, String> invalidClientData = Map.of(
            "email", "john.doe@example.com",
            "phone", "123456789",
            "address", "123 Main St"
            // Missing: firstName, lastName, dni, birthDate
        );

        when(mockClientView.captureDataClient()).thenReturn(Optional.of(invalidClientData));

        // When
        boolean result = controller.createClient();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).captureDataClient();
        verify(mockClientService, never()).createClientDto(any());
    }

    @Test
    @DisplayName("Should return false when data has empty required fields")
    void shouldReturnFalseWhenDataHasEmptyRequiredFields() {
        // Given - Required fields are empty
        Map<String, String> invalidClientData = Map.of(
            "firstName", "",
            "lastName", "",
            "dni", "",
            "birthDate", "1990-01-01",
            "email", "john.doe@example.com",
            "phone", "123456789",
            "address", "123 Main St"
        );

        when(mockClientView.captureDataClient()).thenReturn(Optional.of(invalidClientData));

        // When
        boolean result = controller.createClient();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).captureDataClient();
        verify(mockClientService, never()).createClientDto(any());
    }

    @Test
    @DisplayName("Should return false when birthDate has invalid format")
    void shouldReturnFalseWhenBirthDateHasInvalidFormat() {
        // Given - Invalid date format
        Map<String, String> invalidClientData = Map.of(
            "firstName", "John",
            "lastName", "Doe",
            "dni", "12345678A",
            "birthDate", "invalid-date",
            "email", "john.doe@example.com",
            "phone", "123456789",
            "address", "123 Main St"
        );

        when(mockClientView.captureDataClient()).thenReturn(Optional.of(invalidClientData));

        // When
        boolean result = controller.createClient();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).captureDataClient();
        verify(mockClientService, never()).createClientDto(any());
    }

    // OBSOLETE TEST - Methods now have real logic, tested individually below
    // @Test
    // @DisplayName("Should return true when all other methods are called (hardcoded for now)")
    // void shouldReturnTrueWhenAllOtherMethodsAreCalled() {
    //     // This test is obsolete because methods now have real implementation
    //     // Each method is tested individually with proper mocking
    // }
    
    @Test
    @DisplayName("Should return true when searching client by ID successfully")
    void shouldReturnTrueWhenSearchingClientByIdSuccessfully() {
        // Given - Valid search criteria for ID
        Map<String, String> searchCriteria = Map.of("searchType", "id", "searchValue", "1");
        ClientDTO foundClient = ClientDTO.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .dni("12345678A")
            .build();
        
        when(mockClientView.searchClient()).thenReturn(Optional.of(searchCriteria));
        when(mockClientService.getClientById(Optional.of(1L))).thenReturn(Optional.of(foundClient));
        
        // When
        boolean result = controller.searchClient();
        
        // Then
        assertThat(result).isTrue();
        verify(mockClientView).searchClient();
        verify(mockClientService).getClientById(Optional.of(1L));
    }
    
    @Test
    @DisplayName("Should return true when searching client by DNI successfully")
    void shouldReturnTrueWhenSearchingClientByDniSuccessfully() {
        // Given - Valid search criteria for DNI
        Map<String, String> searchCriteria = Map.of("searchType", "dni", "searchValue", "12345678A");
        ClientDTO foundClient = ClientDTO.builder()
            .id(1L)
            .firstName("John")
            .lastName("Doe")
            .dni("12345678A")
            .build();
        
        when(mockClientView.searchClient()).thenReturn(Optional.of(searchCriteria));
        when(mockClientService.getClientByDni(Optional.of("12345678A"))).thenReturn(Optional.of(foundClient));
        
        // When
        boolean result = controller.searchClient();
        
        // Then
        assertThat(result).isTrue();
        verify(mockClientView).searchClient();
        verify(mockClientService).getClientByDni(Optional.of("12345678A"));
    }
    
    @Test
    @DisplayName("Should return false when search is cancelled")
    void shouldReturnFalseWhenSearchIsCancelled() {
        // Given - Search is cancelled (returns empty)
        when(mockClientView.searchClient()).thenReturn(Optional.empty());
        
        // When
        boolean result = controller.searchClient();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).searchClient();
        verify(mockClientService, never()).getClientById(any());
        verify(mockClientService, never()).getClientByDni(any());
    }
    
    @Test
    @DisplayName("Should return false when client is not found")
    void shouldReturnFalseWhenClientIsNotFound() {
        // Given - Valid search criteria but client not found
        Map<String, String> searchCriteria = Map.of("searchType", "id", "searchValue", "999");
        
        when(mockClientView.searchClient()).thenReturn(Optional.of(searchCriteria));
        when(mockClientService.getClientById(Optional.of(999L))).thenReturn(Optional.empty());
        
        // When
        boolean result = controller.searchClient();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).searchClient();
        verify(mockClientService).getClientById(Optional.of(999L));
    }
    
    @Test
    @DisplayName("Should return false when searchType is invalid")
    void shouldReturnFalseWhenSearchTypeIsInvalid() {
        // Given - Invalid search type
        Map<String, String> searchCriteria = Map.of("searchType", "invalid", "searchValue", "test");
        
        when(mockClientView.searchClient()).thenReturn(Optional.of(searchCriteria));
        
        // When
        boolean result = controller.searchClient();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).searchClient();
        verify(mockClientService, never()).getClientById(any());
        verify(mockClientService, never()).getClientByDni(any());
    }
    
    @Test
    @DisplayName("Should return false when NumberFormatException occurs in ID search")
    void shouldReturnFalseWhenNumberFormatExceptionOccursInIdSearch() {
        // Given - Invalid ID format that will cause NumberFormatException
        Map<String, String> searchCriteria = Map.of("searchType", "id", "searchValue", "abc");
        
        when(mockClientView.searchClient()).thenReturn(Optional.of(searchCriteria));
        
        // When
        boolean result = controller.searchClient();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).searchClient();
        verify(mockClientService, never()).getClientById(any());
        verify(mockClientService, never()).getClientByDni(any());
    }
    
    @Test
    @DisplayName("Should update client email successfully with confirmation")
    void shouldUpdateClientEmailSuccessfullyWithConfirmation() {
        // Given - Valid update data and confirmation flow
        Map<String, String> updateData = Map.of("id", "123", "newValue", "nuevo@test.com");
        ClientDTO currentClient = ClientDTO.builder()
            .id(123L)
            .firstName("John")
            .lastName("Doe")
            .email("old@test.com")
            .build();
        ClientDTO updatedClient = ClientDTO.builder()
            .id(123L)
            .firstName("John")
            .lastName("Doe")
            .email("nuevo@test.com")
            .build();
        
        when(mockClientView.updateClientEmail()).thenReturn(Optional.of(updateData));
        when(mockClientService.getClientById(Optional.of(123L))).thenReturn(Optional.of(currentClient));
        when(mockClientView.displayClient(any())).thenReturn(true); // User confirms
        when(mockClientService.updateClientEmail(123L, Optional.of("nuevo@test.com")))
            .thenReturn(Optional.of(updatedClient));
        
        // When
        boolean result = controller.updateClientEmail();
        
        // Then
        assertThat(result).isTrue();
        verify(mockClientView).updateClientEmail();
        verify(mockClientService).getClientById(Optional.of(123L));
        verify(mockClientView, times(2)).displayClient(any()); // Called twice: confirmation + result
        verify(mockClientService).updateClientEmail(123L, Optional.of("nuevo@test.com"));
    }
    
    @Test
    @DisplayName("Should return false when updateClientEmail is cancelled")
    void shouldReturnFalseWhenUpdateClientEmailIsCancelled() {
        // Given - Update is cancelled (returns empty)
        when(mockClientView.updateClientEmail()).thenReturn(Optional.empty());
        
        // When
        boolean result = controller.updateClientEmail();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientEmail();
        verify(mockClientService, never()).getClientById(any());
        verify(mockClientService, never()).updateClientEmail(any(), any());
    }
    
    @Test
    @DisplayName("Should return false when user cancels confirmation in showClientAndConfirm")
    void shouldReturnFalseWhenUserCancelsConfirmationInProcessClientUpdate() {
        // Given - Valid update data but user cancels confirmation
        Map<String, String> updateData = Map.of("id", "123", "newEmail", "nuevo@test.com");
        ClientDTO currentClient = ClientDTO.builder()
            .id(123L)
            .firstName("John")
            .lastName("Doe")
            .email("old@test.com")
            .build();
        
        when(mockClientView.updateClientEmail()).thenReturn(Optional.of(updateData));
        when(mockClientService.getClientById(Optional.of(123L))).thenReturn(Optional.of(currentClient));
        when(mockClientView.displayClient(any())).thenReturn(false); // User cancels
        
        // When
        boolean result = controller.updateClientEmail();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientEmail();
        verify(mockClientService).getClientById(Optional.of(123L));
        verify(mockClientView).displayClient(any());
        verify(mockClientService, never()).updateClientEmail(any(), any());
    }
    
    @Test
    @DisplayName("Should return false when client not found in showClientAndConfirm")
    void shouldReturnFalseWhenClientNotFoundInProcessClientUpdate() {
        // Given - Valid update data but client not found
        Map<String, String> updateData = Map.of("id", "123", "newEmail", "nuevo@test.com");
        
        when(mockClientView.updateClientEmail()).thenReturn(Optional.of(updateData));
        when(mockClientService.getClientById(Optional.of(123L))).thenReturn(Optional.empty());
        
        // When
        boolean result = controller.updateClientEmail();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientEmail();
        verify(mockClientService).getClientById(Optional.of(123L));
        verify(mockClientView, never()).displayClient(any());
        verify(mockClientService, never()).updateClientEmail(any(), any());
    }
    
    @Test
    @DisplayName("Should return false when NumberFormatException occurs in updateClientEmail")
    void shouldReturnFalseWhenNumberFormatExceptionOccursInUpdateClientEmail() {
        // Given - Invalid ID format that will cause NumberFormatException
        Map<String, String> updateData = Map.of("id", "abc", "newEmail", "nuevo@test.com");
        
        when(mockClientView.updateClientEmail()).thenReturn(Optional.of(updateData));
        
        // When
        boolean result = controller.updateClientEmail();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientEmail();
        verify(mockClientService, never()).getClientById(any());
        verify(mockClientService, never()).updateClientEmail(any(), any());
    }
    
    @Test
    @DisplayName("Should return false when service update fails")
    void shouldReturnFalseWhenServiceUpdateFails() {
        // Given - Valid update data and confirmation but service update fails
        Map<String, String> updateData = Map.of("id", "123", "newValue", "nuevo@test.com");
        ClientDTO currentClient = ClientDTO.builder()
            .id(123L)
            .firstName("John")
            .lastName("Doe")
            .email("old@test.com")
            .build();
        
        when(mockClientView.updateClientEmail()).thenReturn(Optional.of(updateData));
        when(mockClientService.getClientById(Optional.of(123L))).thenReturn(Optional.of(currentClient));
        when(mockClientView.displayClient(any())).thenReturn(true); // User confirms
        when(mockClientService.updateClientEmail(123L, Optional.of("nuevo@test.com")))
            .thenReturn(Optional.empty()); // Service update fails
        
        // When
        boolean result = controller.updateClientEmail();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientEmail();
        verify(mockClientService).getClientById(Optional.of(123L));
        verify(mockClientView).displayClient(any());
        verify(mockClientService).updateClientEmail(123L, Optional.of("nuevo@test.com"));
    }
    
    // ===== TESTS FOR updateClientPhone =====
    
    @Test
    @DisplayName("Should update client phone successfully with confirmation")
    void shouldUpdateClientPhoneSuccessfullyWithConfirmation() {
        // Given - Valid update data and confirmation flow
        Map<String, String> updateData = Map.of("id", "123", "newValue", "987654321");
        ClientDTO updatedClient = ClientDTO.builder()
            .id(123L)
            .firstName("John")
            .lastName("Doe")
            .phone("987654321")
            .build();
        
        when(mockClientView.updateClientPhone()).thenReturn(Optional.of(updateData));
        when(mockClientService.getClientById(Optional.of(123L))).thenReturn(Optional.of(updatedClient));
        when(mockClientView.displayClient(any())).thenReturn(true); // User confirms
        when(mockClientService.updateClientPhone(123L, Optional.of("987654321")))
            .thenReturn(Optional.of(updatedClient));
        
        // When
        boolean result = controller.updateClientPhone();
        
        // Then
        assertThat(result).isTrue();
        verify(mockClientView).updateClientPhone();
        verify(mockClientService).getClientById(Optional.of(123L));
        verify(mockClientView, times(2)).displayClient(any()); // Called twice: confirmation + result
        verify(mockClientService).updateClientPhone(123L, Optional.of("987654321"));
    }
    
    @Test
    @DisplayName("Should return false when updateClientPhone is cancelled")
    void shouldReturnFalseWhenUpdateClientPhoneIsCancelled() {
        // Given - Update is cancelled (returns empty)
        when(mockClientView.updateClientPhone()).thenReturn(Optional.empty());
        
        // When
        boolean result = controller.updateClientPhone();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientPhone();
        verify(mockClientService, never()).updateClientPhone(any(), any());
    }
    
    @Test
    @DisplayName("Should return false when NumberFormatException occurs in updateClientPhone")
    void shouldReturnFalseWhenNumberFormatExceptionOccursInUpdateClientPhone() {
        // Given - Invalid ID format that will cause NumberFormatException
        Map<String, String> updateData = Map.of("id", "abc", "newPhone", "987654321");
        
        when(mockClientView.updateClientPhone()).thenReturn(Optional.of(updateData));
        
        // When
        boolean result = controller.updateClientPhone();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientPhone();
        verify(mockClientService, never()).updateClientPhone(any(), any());
    }
    
    @Test
    @DisplayName("Should return false when service update fails in updateClientPhone")
    void shouldReturnFalseWhenServiceUpdateFailsInUpdateClientPhone() {
        // Given - Valid update data but service update fails
        Map<String, String> updateData = Map.of("id", "123", "newValue", "987654321");
        
        ClientDTO currentClient = ClientDTO.builder()
            .id(123L)
            .firstName("John")
            .lastName("Doe")
            .phone("123456789")
            .build();
            
        when(mockClientView.updateClientPhone()).thenReturn(Optional.of(updateData));
        when(mockClientService.getClientById(Optional.of(123L))).thenReturn(Optional.of(currentClient));
        when(mockClientView.displayClient(any())).thenReturn(true); // User confirms
        when(mockClientService.updateClientPhone(123L, Optional.of("987654321")))
            .thenReturn(Optional.empty()); // Service update fails
        
        // When
        boolean result = controller.updateClientPhone();
        
        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientPhone();
        verify(mockClientService).getClientById(Optional.of(123L));
        verify(mockClientView).displayClient(any()); // Called once: confirmation only (service fails)
        verify(mockClientService).updateClientPhone(123L, Optional.of("987654321"));
    }
    
    // ===== TESTS FOR NEW METHODS IMPLEMENTED =====
    
    @Test
    @DisplayName("Should return true when deleteClient succeeds with valid data")
    void shouldReturnTrueWhenDeleteClientSucceedsWithValidData() {
        // Given
        String clientId = "123";
        ClientDTO clientDto = ClientDTO.builder()
            .id(123L)
            .firstName("John")
            .lastName("Doe")
            .dni("12345678A")
            .email("john.doe@example.com")
            .phone("123456789")
            .address("123 Main St")
            .build();

        when(mockClientView.deleteClient()).thenReturn(Optional.of(clientId));
        when(mockClientService.getClientById(Optional.of(123L))).thenReturn(Optional.of(clientDto));
        when(mockClientView.displayClient(any(Client.class))).thenReturn(true);
        when(mockClientService.deleteClient(Optional.of(123L))).thenReturn(true);

        // When
        boolean result = controller.deleteClient();

        // Then
        assertThat(result).isTrue();
        verify(mockClientView).deleteClient();
        verify(mockClientService).getClientById(Optional.of(123L));
        verify(mockClientView).displayClient(any(Client.class));
        verify(mockClientService).deleteClient(Optional.of(123L));
        verify(mockClientView).showMessage("Cliente eliminado exitosamente.");
    }
    
    @Test
    @DisplayName("Should return false when deleteClient view returns empty")
    void shouldReturnFalseWhenDeleteClientViewReturnsEmpty() {
        // Given
        when(mockClientView.deleteClient()).thenReturn(Optional.empty());

        // When
        boolean result = controller.deleteClient();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).deleteClient();
        verify(mockClientService, never()).getClientById(any());
        verify(mockClientService, never()).deleteClient(any());
    }
    
    @Test
    @DisplayName("Should return false when deleteClient has invalid ID")
    void shouldReturnFalseWhenDeleteClientHasInvalidId() {
        // Given
        String invalidId = "invalid";
        when(mockClientView.deleteClient()).thenReturn(Optional.of(invalidId));

        // When
        boolean result = controller.deleteClient();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).deleteClient();
        verify(mockClientView).showMessage("Error: ID de cliente inválido.");
        verify(mockClientService, never()).getClientById(any());
        verify(mockClientService, never()).deleteClient(any());
    }
    
    @Test
    @DisplayName("Should return false when user cancels deleteClient confirmation")
    void shouldReturnFalseWhenUserCancelsDeleteClientConfirmation() {
        // Given
        String clientId = "123";
        ClientDTO clientDto = ClientDTO.builder()
            .id(123L)
            .firstName("John")
            .lastName("Doe")
            .dni("12345678A")
            .email("john.doe@example.com")
            .phone("123456789")
            .address("123 Main St")
            .build();

        when(mockClientView.deleteClient()).thenReturn(Optional.of(clientId));
        when(mockClientService.getClientById(Optional.of(123L))).thenReturn(Optional.of(clientDto));
        when(mockClientView.displayClient(any(Client.class))).thenReturn(false); // User cancels

        // When
        boolean result = controller.deleteClient();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).deleteClient();
        verify(mockClientService).getClientById(Optional.of(123L));
        verify(mockClientView).displayClient(any(Client.class));
        verify(mockClientView).showMessage("Eliminación cancelada por el usuario.");
        verify(mockClientService, never()).deleteClient(any());
    }
    
    @Test
    @DisplayName("Should return true when restoreClient succeeds with valid data")
    void shouldReturnTrueWhenRestoreClientSucceedsWithValidData() {
        // Given
        String clientId = "123";
        ClientDTO clientDto = ClientDTO.builder()
            .id(123L)
            .firstName("John")
            .lastName("Doe")
            .dni("12345678A")
            .email("john.doe@example.com")
            .phone("123456789")
            .address("123 Main St")
            .build();

        when(mockClientView.restoreClient()).thenReturn(Optional.of(clientId));
        when(mockClientService.getClientById(Optional.of(123L))).thenReturn(Optional.of(clientDto));
        when(mockClientView.displayClient(any(Client.class))).thenReturn(true);
        when(mockClientService.restoreClient(Optional.of(123L))).thenReturn(Optional.of(clientDto));

        // When
        boolean result = controller.restoreClient();

        // Then
        assertThat(result).isTrue();
        verify(mockClientView).restoreClient();
        verify(mockClientService).getClientById(Optional.of(123L));
        verify(mockClientView, times(2)).displayClient(any(Client.class)); // Shows client for confirmation + restored client
        verify(mockClientService).restoreClient(Optional.of(123L));
        verify(mockClientView).showMessage("Cliente restaurado exitosamente.");
    }
    
    @Test
    @DisplayName("Should return false when restoreClient view returns empty")
    void shouldReturnFalseWhenRestoreClientViewReturnsEmpty() {
        // Given
        when(mockClientView.restoreClient()).thenReturn(Optional.empty());

        // When
        boolean result = controller.restoreClient();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).restoreClient();
        verify(mockClientService, never()).getClientById(any());
        verify(mockClientService, never()).restoreClient(any());
    }
    
    @Test
    @DisplayName("Should return true when listAllClients succeeds with data")
    void shouldReturnTrueWhenListAllClientsSucceedsWithData() {
        // Given
        List<ClientDTO> clientList = List.of(
            ClientDTO.builder().id(1L).firstName("John").lastName("Doe").dni("12345678A").email("john@test.com").build(),
            ClientDTO.builder().id(2L).firstName("Jane").lastName("Smith").dni("87654321B").email("jane@test.com").build()
        );

        when(mockClientService.getAllClients()).thenReturn(Optional.of(clientList));

        // When
        boolean result = controller.listAllClients();

        // Then
        assertThat(result).isTrue();
        verify(mockClientService).getAllClients();
        verify(mockClientView).listAllClients(clientList);
    }
    
    @Test
    @DisplayName("Should return false when listAllClients service returns empty")
    void shouldReturnFalseWhenListAllClientsServiceReturnsEmpty() {
        // Given
        when(mockClientService.getAllClients()).thenReturn(Optional.empty());

        // When
        boolean result = controller.listAllClients();

        // Then
        assertThat(result).isFalse();
        verify(mockClientService).getAllClients();
        verify(mockClientView, never()).listAllClients(any());
    }
    
    // ===== TESTS FOR updateClientMultipleFields METHOD =====
    
    @Test
    @DisplayName("Should return true when updateClientMultipleFields succeeds with valid data")
    void shouldReturnTrueWhenUpdateClientMultipleFieldsSucceedsWithValidData() {
        // Given
        Map<String, Object> updateData = Map.of(
            "id", "123",
            "email", "nuevo@test.com",
            "phone", "987654321",
            "address", "Nueva Dirección 456"
        );
        
        ClientDTO updatedClient = ClientDTO.builder()
            .id(123L)
            .firstName("John")
            .lastName("Doe")
            .dni("12345678A")
            .email("nuevo@test.com")
            .phone("987654321")
            .address("Nueva Dirección 456")
            .build();
        
        when(mockClientView.updateClientMultipleFields()).thenReturn(Optional.of(updateData));
        when(mockClientService.updateMultipleFields(123L, Map.of(
            "email", "nuevo@test.com",
            "phone", "987654321",
            "address", "Nueva Dirección 456"
        ))).thenReturn(Optional.of(updatedClient));

        // When
        boolean result = controller.updateClientMultipleFields();

        // Then
        assertThat(result).isTrue();
        verify(mockClientView).updateClientMultipleFields();
        verify(mockClientService).updateMultipleFields(123L, Map.of(
            "email", "nuevo@test.com",
            "phone", "987654321",
            "address", "Nueva Dirección 456"
        ));
        verify(mockClientView).displayClient(any(Client.class));
    }
    
    @Test
    @DisplayName("Should return false when updateClientMultipleFields view returns empty")
    void shouldReturnFalseWhenUpdateClientMultipleFieldsViewReturnsEmpty() {
        // Given
        when(mockClientView.updateClientMultipleFields()).thenReturn(Optional.empty());

        // When
        boolean result = controller.updateClientMultipleFields();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientMultipleFields();
        verify(mockClientService, never()).updateMultipleFields(anyLong(), any());
        verify(mockClientView, never()).displayClient(any());
    }
    
    @Test
    @DisplayName("Should return false when updateClientMultipleFields has null ID")
    void shouldReturnFalseWhenUpdateClientMultipleFieldsHasNullId() {
        // Given
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("id", null);
        updateData.put("email", "nuevo@test.com");
        
        when(mockClientView.updateClientMultipleFields()).thenReturn(Optional.of(updateData));

        // When
        boolean result = controller.updateClientMultipleFields();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientMultipleFields();
        verify(mockClientService, never()).updateMultipleFields(anyLong(), any());
        verify(mockClientView, never()).displayClient(any());
    }
    
    @Test
    @DisplayName("Should return false when updateClientMultipleFields has invalid ID type")
    void shouldReturnFalseWhenUpdateClientMultipleFieldsHasInvalidIdType() {
        // Given
        Map<String, Object> updateData = Map.of(
            "id", 123.45, // Invalid type (Double instead of Long or String)
            "email", "nuevo@test.com"
        );
        
        when(mockClientView.updateClientMultipleFields()).thenReturn(Optional.of(updateData));

        // When
        boolean result = controller.updateClientMultipleFields();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientMultipleFields();
        verify(mockClientService, never()).updateMultipleFields(anyLong(), any());
        verify(mockClientView, never()).displayClient(any());
    }
    
    @Test
    @DisplayName("Should return false when updateClientMultipleFields has invalid ID string")
    void shouldReturnFalseWhenUpdateClientMultipleFieldsHasInvalidIdString() {
        // Given
        Map<String, Object> updateData = Map.of(
            "id", "invalid_id", // Invalid string that can't be parsed to Long
            "email", "nuevo@test.com"
        );
        
        when(mockClientView.updateClientMultipleFields()).thenReturn(Optional.of(updateData));

        // When
        boolean result = controller.updateClientMultipleFields();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientMultipleFields();
        verify(mockClientService, never()).updateMultipleFields(anyLong(), any());
        verify(mockClientView, never()).displayClient(any());
    }
    
    @Test
    @DisplayName("Should return false when updateClientMultipleFields service returns empty")
    void shouldReturnFalseWhenUpdateClientMultipleFieldsServiceReturnsEmpty() {
        // Given
        Map<String, Object> updateData = Map.of(
            "id", "123",
            "email", "nuevo@test.com"
        );
        
        when(mockClientView.updateClientMultipleFields()).thenReturn(Optional.of(updateData));
        when(mockClientService.updateMultipleFields(123L, Map.of("email", "nuevo@test.com")))
            .thenReturn(Optional.empty());

        // When
        boolean result = controller.updateClientMultipleFields();

        // Then
        assertThat(result).isFalse();
        verify(mockClientView).updateClientMultipleFields();
        verify(mockClientService).updateMultipleFields(123L, Map.of("email", "nuevo@test.com"));
        verify(mockClientView, never()).displayClient(any());
    }
    
    // ===== TESTS FOR MENU METHODS =====
    
    @Test
    @DisplayName("Should handle showClientMenu with valid option 1 (Create Client)")
    void shouldHandleShowClientMenuWithValidOption1() {
        // Given
        when(mockClientView.showClientMenu())
            .thenReturn(Optional.of("1"))  // First call returns option 1
            .thenReturn(Optional.empty()); // Second call returns empty to exit
        
        when(mockClientView.captureDataClient()).thenReturn(Optional.empty()); // Create client returns empty

        // When
        controller.showClientMenu();

        // Then
        verify(mockClientView, times(2)).showClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientView).captureDataClient(); // Should call createClient
    }
    
    @Test
    @DisplayName("Should handle showClientMenu with valid option 2 (Search Client)")
    void shouldHandleShowClientMenuWithValidOption2() {
        // Given
        when(mockClientView.showClientMenu())
            .thenReturn(Optional.of("2"))  // First call returns option 2
            .thenReturn(Optional.empty()); // Second call returns empty to exit
        
        when(mockClientView.searchClient()).thenReturn(Optional.empty()); // Search returns empty

        // When
        controller.showClientMenu();

        // Then
        verify(mockClientView, times(2)).showClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientView).searchClient(); // Should call searchClient
    }
    
    @Test
    @DisplayName("Should handle showClientMenu with valid option 3 (Update Client Menu)")
    void shouldHandleShowClientMenuWithValidOption3() {
        // Given
        when(mockClientView.showClientMenu())
            .thenReturn(Optional.of("3"))  // First call returns option 3
            .thenReturn(Optional.empty()); // Second call returns empty to exit
        
        when(mockClientView.showUpdateClientMenu())
            .thenReturn(Optional.empty()); // Update menu returns empty immediately

        // When
        controller.showClientMenu();

        // Then
        verify(mockClientView, times(2)).showClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientView).showUpdateClientMenu(); // Should call showUpdateClientMenu
    }
    
    @Test
    @DisplayName("Should handle showClientMenu with valid option 4 (Delete Client)")
    void shouldHandleShowClientMenuWithValidOption4() {
        // Given
        when(mockClientView.showClientMenu())
            .thenReturn(Optional.of("4"))  // First call returns option 4
            .thenReturn(Optional.empty()); // Second call returns empty to exit
        
        when(mockClientView.deleteClient()).thenReturn(Optional.empty()); // Delete returns empty

        // When
        controller.showClientMenu();

        // Then
        verify(mockClientView, times(2)).showClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientView).deleteClient(); // Should call deleteClient
    }
    
    @Test
    @DisplayName("Should handle showClientMenu with valid option 5 (Restore Client)")
    void shouldHandleShowClientMenuWithValidOption5() {
        // Given
        when(mockClientView.showClientMenu())
            .thenReturn(Optional.of("5"))  // First call returns option 5
            .thenReturn(Optional.empty()); // Second call returns empty to exit
        
        when(mockClientView.restoreClient()).thenReturn(Optional.empty()); // Restore returns empty

        // When
        controller.showClientMenu();

        // Then
        verify(mockClientView, times(2)).showClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientView).restoreClient(); // Should call restoreClient
    }
    
    @Test
    @DisplayName("Should handle showClientMenu with valid option 6 (List All Clients)")
    void shouldHandleShowClientMenuWithValidOption6() {
        // Given
        when(mockClientView.showClientMenu())
            .thenReturn(Optional.of("6"))  // First call returns option 6
            .thenReturn(Optional.empty()); // Second call returns empty to exit
        
        when(mockClientService.getAllClients()).thenReturn(Optional.empty()); // List returns empty

        // When
        controller.showClientMenu();

        // Then
        verify(mockClientView, times(2)).showClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientService).getAllClients(); // Should call getAllClients
    }
    
    @Test
    @DisplayName("Should handle showClientMenu with invalid option")
    void shouldHandleShowClientMenuWithInvalidOption() {
        // Given
        when(mockClientView.showClientMenu())
            .thenReturn(Optional.of("99")) // First call returns invalid option
            .thenReturn(Optional.empty()); // Second call returns empty to exit

        // When
        controller.showClientMenu();

        // Then
        verify(mockClientView, times(2)).showClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientView).showMessage("Opción inválida. Por favor, elige una opción del 1 al 7.");
    }
    
    @Test
    @DisplayName("Should handle showClientMenu with immediate exit")
    void shouldHandleShowClientMenuWithImmediateExit() {
        // Given
        when(mockClientView.showClientMenu()).thenReturn(Optional.empty()); // Immediate exit

        // When
        controller.showClientMenu();

        // Then
        verify(mockClientView).showClientMenu(); // Called once
        verify(mockClientView, never()).showMessage(anyString()); // No error message
    }
    
    @Test
    @DisplayName("Should handle showUpdateClientMenu with valid option 1 (Update Email)")
    void shouldHandleShowUpdateClientMenuWithValidOption1() {
        // Given
        when(mockClientView.showUpdateClientMenu())
            .thenReturn(Optional.of("1"))  // First call returns option 1
            .thenReturn(Optional.empty()); // Second call returns empty to exit
        
        when(mockClientView.updateClientEmail()).thenReturn(Optional.empty()); // Update email returns empty

        // When
        controller.showUpdateClientMenu();

        // Then
        verify(mockClientView, times(2)).showUpdateClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientView).updateClientEmail(); // Should call updateClientEmail
    }
    
    @Test
    @DisplayName("Should handle showUpdateClientMenu with valid option 2 (Update Phone)")
    void shouldHandleShowUpdateClientMenuWithValidOption2() {
        // Given
        when(mockClientView.showUpdateClientMenu())
            .thenReturn(Optional.of("2"))  // First call returns option 2
            .thenReturn(Optional.empty()); // Second call returns empty to exit
        
        when(mockClientView.updateClientPhone()).thenReturn(Optional.empty()); // Update phone returns empty

        // When
        controller.showUpdateClientMenu();

        // Then
        verify(mockClientView, times(2)).showUpdateClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientView).updateClientPhone(); // Should call updateClientPhone
    }
    
    @Test
    @DisplayName("Should handle showUpdateClientMenu with valid option 3 (Update Address)")
    void shouldHandleShowUpdateClientMenuWithValidOption3() {
        // Given
        when(mockClientView.showUpdateClientMenu())
            .thenReturn(Optional.of("3"))  // First call returns option 3
            .thenReturn(Optional.empty()); // Second call returns empty to exit
        
        when(mockClientView.updateClientAddress()).thenReturn(Optional.empty()); // Update address returns empty

        // When
        controller.showUpdateClientMenu();

        // Then
        verify(mockClientView, times(2)).showUpdateClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientView).updateClientAddress(); // Should call updateClientAddress
    }
    
    @Test
    @DisplayName("Should handle showUpdateClientMenu with valid option 4 (Update Multiple Fields)")
    void shouldHandleShowUpdateClientMenuWithValidOption4() {
        // Given
        when(mockClientView.showUpdateClientMenu())
            .thenReturn(Optional.of("4"))  // First call returns option 4
            .thenReturn(Optional.empty()); // Second call returns empty to exit
        
        when(mockClientView.updateClientMultipleFields()).thenReturn(Optional.empty()); // Update multiple returns empty

        // When
        controller.showUpdateClientMenu();

        // Then
        verify(mockClientView, times(2)).showUpdateClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientView).updateClientMultipleFields(); // Should call updateClientMultipleFields
    }
    
    @Test
    @DisplayName("Should handle showUpdateClientMenu with invalid option")
    void shouldHandleShowUpdateClientMenuWithInvalidOption() {
        // Given
        when(mockClientView.showUpdateClientMenu())
            .thenReturn(Optional.of("99")) // First call returns invalid option
            .thenReturn(Optional.empty()); // Second call returns empty to exit

        // When
        controller.showUpdateClientMenu();

        // Then
        verify(mockClientView, times(2)).showUpdateClientMenu(); // Called twice: once for option, once for exit
        verify(mockClientView).showMessage("Opción inválida. Por favor, elige una opción del 1 al 5.");
    }
    
    @Test
    @DisplayName("Should handle showUpdateClientMenu with immediate exit")
    void shouldHandleShowUpdateClientMenuWithImmediateExit() {
        // Given
        when(mockClientView.showUpdateClientMenu()).thenReturn(Optional.empty()); // Immediate exit

        // When
        controller.showUpdateClientMenu();

        // Then
        verify(mockClientView).showUpdateClientMenu(); // Called once
        verify(mockClientView, never()).showMessage(anyString()); // No error message
    }
    
}
