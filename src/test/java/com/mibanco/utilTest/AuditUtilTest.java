package com.mibanco.utilTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mibanco.model.AuditRecord;
import com.mibanco.model.Client;
import com.mibanco.model.enums.ClientOperationType;
import com.mibanco.repository.AuditRepository;
import com.mibanco.util.AuditUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests for AuditUtil class")
class AuditUtilTest {

    @Mock
    private AuditRepository mockAuditRepository;
    
    private Client client;
    private ClientOperationType operationType;
    private String user;
    private AuditRecord<Client, ClientOperationType> expectedRecord;

    @BeforeEach
    void setUp() {
        // Common test data
        client = Client.of(
            1L, "John", "Doe", "12345678A", 
            LocalDate.of(1990, 5, 15), 
            "john.doe@email.com", "123456789", "Main Street 123"
        );
        
        operationType = ClientOperationType.CREATE;
        user = "test_user";
        
        // Create expected record
        expectedRecord = AuditRecord.of(
            Optional.of(operationType), 
            Optional.of(client), 
            Optional.of(user)
        );
    }

    @Test
    @DisplayName("Should register operation successfully")
    void shouldRegisterOperationSuccessfully() {
        // Arrange (Prepare)
        // Configure mock: "when register is called with any parameter, return expected record"
        when(mockAuditRepository.register(any(Optional.class))).thenReturn(Optional.of(expectedRecord));
        
        // Act (Act)
        AuditRecord<Client, ClientOperationType> result = AuditUtil.registerOperation(
            mockAuditRepository,
            Optional.of(operationType),
            Optional.of(client),
            Optional.of(user)
        );
        
        // Assert (Verify)
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedRecord);
        
        // Verify that the repository's register method was called
        verify(mockAuditRepository).register(any());
    }

    @Test
    @DisplayName("Should return original record when repository fails")
    void shouldReturnOriginalRecordWhenRepositoryFails() {
        // Arrange (Prepare)
        // Configure mock: "when register is called, return empty (simulates failure)"
        when(mockAuditRepository.register(any(Optional.class))).thenReturn(Optional.empty());
        
        // Act (Act)
        AuditRecord<Client, ClientOperationType> result = AuditUtil.registerOperation(
            mockAuditRepository,
            Optional.of(operationType),
            Optional.of(client),
            Optional.of(user)
        );
        
        // Assert (Verify)
        assertThat(result).isNotNull();
        // Getters return direct values, not Optional
        assertThat(result.getOperationType()).isEqualTo(operationType);
        assertThat(result.getEntity()).isEqualTo(client);
        assertThat(result.getUser()).isEqualTo(user);
        
        // Verify that the repository's register method was called
        verify(mockAuditRepository).register(any());
    }

    @Test
    @DisplayName("Should handle empty Optionals correctly")
    void shouldHandleEmptyOptionalsCorrectly() {
        // Arrange (Prepare)
        // Configure mock to return a record with empty Optionals
        AuditRecord<Client, ClientOperationType> recordWithEmpties = AuditRecord.of(
            Optional.<ClientOperationType>empty(), 
            Optional.<Client>empty(), 
            Optional.<String>empty()
        );
        when(mockAuditRepository.register(any(Optional.class))).thenReturn(Optional.of(recordWithEmpties));
        
        // Act (Act)
        AuditRecord<Client, ClientOperationType> result = AuditUtil.registerOperation(
            mockAuditRepository,
            Optional.<ClientOperationType>empty(), // empty operationType
            Optional.<Client>empty(), // empty entity
            Optional.<String>empty()  // empty user
        );
        
        // Assert (Verify)
        assertThat(result).isNotNull();
        // Getters return null when Optionals are empty
        assertThat(result.getOperationType()).isNull();
        assertThat(result.getEntity()).isNull();
        assertThat(result.getUser()).isNull();
        
        // Verify that the repository's register method was called
        verify(mockAuditRepository).register(any());
    }

    @Test
    @DisplayName("Should maintain immutability of input parameters")
    void shouldMaintainImmutabilityOfInputParameters() {
        // Arrange (Prepare)
        // Save original references to verify immutability
        Optional<ClientOperationType> originalOperationType = Optional.of(operationType);
        Optional<Client> originalClient = Optional.of(client);
        Optional<String> originalUser = Optional.of(user);
        
        when(mockAuditRepository.register(any(Optional.class))).thenReturn(Optional.of(expectedRecord));
        
        // Act (Act)
        AuditRecord<Client, ClientOperationType> result = AuditUtil.registerOperation(
            mockAuditRepository,
            originalOperationType,
            originalClient,
            originalUser
        );
        
        // Assert (Verify)
        assertThat(result).isNotNull();
        
        // Verify that original parameters didn't change
        assertThat(originalOperationType).isEqualTo(Optional.of(operationType));
        assertThat(originalClient).isEqualTo(Optional.of(client));
        assertThat(originalUser).isEqualTo(Optional.of(user));
        
        // Verify that the repository's register method was called
        verify(mockAuditRepository).register(any());
    }

} 