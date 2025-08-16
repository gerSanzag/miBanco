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
        // Note: Since AuditUtil now uses RepositoryFactory internally,
        // we test the actual behavior rather than mocking
        
        // Act (Act)
        AuditRecord<Client, ClientOperationType> result = AuditUtil.registerOperation(
            Optional.of(operationType),
            Optional.of(client),
            Optional.of(user)
        );
        
        // Assert (Verify)
        assertThat(result).isNotNull();
        assertThat(result.getOperationType()).isEqualTo(operationType);
        assertThat(result.getEntity()).isEqualTo(client);
        assertThat(result.getUser()).isEqualTo(user);
    }

    @Test
    @DisplayName("Should handle empty Optionals correctly")
    void shouldHandleEmptyOptionalsCorrectly() {
        // Act (Act)
        AuditRecord<Client, ClientOperationType> result = AuditUtil.registerOperation(
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
    }

    @Test
    @DisplayName("Should maintain immutability of input parameters")
    void shouldMaintainImmutabilityOfInputParameters() {
        // Arrange (Prepare)
        // Save original references to verify immutability
        Optional<ClientOperationType> originalOperationType = Optional.of(operationType);
        Optional<Client> originalClient = Optional.of(client);
        Optional<String> originalUser = Optional.of(user);
        
        // Act (Act)
        AuditRecord<Client, ClientOperationType> result = AuditUtil.registerOperation(
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
    }

    @Test
    @DisplayName("Should create audit record with correct values")
    void shouldCreateAuditRecordWithCorrectValues() {
        // Act (Act)
        AuditRecord<Client, ClientOperationType> result = AuditUtil.registerOperation(
            Optional.of(operationType),
            Optional.of(client),
            Optional.of(user)
        );
        
        // Assert (Verify)
        assertThat(result).isNotNull();
        assertThat(result.getOperationType()).isEqualTo(ClientOperationType.CREATE);
        assertThat(result.getEntity()).isEqualTo(client);
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getDateTime()).isNotNull();
    }

    @Test
    @DisplayName("Should register operation with simplified parameters")
    void shouldRegisterOperationWithSimplifiedParameters() {
        // Arrange (Prepare)
        String entityType = "Client";
        Long entityId = 123L;
        String operationType = "CREATE";
        String user = "test_user";
        
        // Act (Act) - This should not throw any exception
        AuditUtil.registerOperation(entityType, entityId, operationType, user);
        
        // Assert (Verify) - Since the method doesn't return anything, we just verify it doesn't throw
        // The method should execute without errors
    }

    @Test
    @DisplayName("Should handle simplified operation with null parameters")
    void shouldHandleSimplifiedOperationWithNullParameters() {
        // Arrange (Prepare)
        String entityType = null;
        Long entityId = null;
        String operationType = null;
        String user = "test_user";
        
        // Act (Act) - This should not throw any exception
        AuditUtil.registerOperation(entityType, entityId, operationType, user);
        
        // Assert (Verify) - The method should handle null parameters gracefully
    }
} 