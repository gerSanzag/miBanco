package com.mibanco.controllerTest.internalTest;

import com.mibanco.controller.internal.ClientControllerImpl;
import com.mibanco.dto.ClientDTO;
import com.mibanco.service.ClientService;
import com.mibanco.view.ClientView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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

    @Test
    @DisplayName("Should return true when all other methods are called (hardcoded for now)")
    void shouldReturnTrueWhenAllOtherMethodsAreCalled() {
        // Given - All methods are hardcoded to return true for now
        
        // When & Then
        // searchClient and updateClientEmail now have real logic, so we test them separately
        assertThat(controller.updateClientPhone()).isTrue();
        assertThat(controller.updateClientAddress()).isTrue();
        assertThat(controller.updateClientMultipleFields()).isTrue();
        assertThat(controller.deleteClient()).isTrue();
        assertThat(controller.restoreClient()).isTrue();
        assertThat(controller.listAllClients()).isTrue();
    }
    
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
        Map<String, String> updateData = Map.of("id", "123", "newEmail", "nuevo@test.com");
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
        verify(mockClientView, times(2)).displayClient(any()); // Called twice: current client + updated client
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
    @DisplayName("Should return false when user cancels confirmation in processClientUpdate")
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
    @DisplayName("Should return false when client not found in processClientUpdate")
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
        Map<String, String> updateData = Map.of("id", "123", "newEmail", "nuevo@test.com");
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
    
}
