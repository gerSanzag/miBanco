package com.mibanco.modelTest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.mibanco.model.AuditRecord;
import com.mibanco.model.Client;
import com.mibanco.model.enums.ClientOperationType;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Tests for AuditRecord class")
class AuditRecordTest {

    UUID id = UUID.randomUUID();
    ClientOperationType operationType = ClientOperationType.CREATE;  // tipoOperacion
    LocalDateTime dateTime = LocalDateTime.of(2024, 1, 15, 10, 30, 0);  // fechaHora
    Client entity;  // entidad
    String user = "admin";  // usuario
    Double amount = 1000.50;  // monto
    String details = "Cliente creado exitosamente";  // detalles
    AuditRecord<Client, ClientOperationType> record;

    @BeforeEach
    void setUp() {
        entity = Client.builder()
                .id(1L)
                .firstName("Juan")        // nombre
                .lastName("Perez")        // apellido
                .dni("1234567890")
                .build();
        
        record = AuditRecord.<Client, ClientOperationType>builder()
                .id(id)
                .operationType(operationType)
                .dateTime(dateTime)
                .entity(entity)
                .user(user)
                .amount(amount)
                .details(details)
                .build();
    }

    @Test
    @DisplayName("Should create an audit record with valid data using Builder")
    void shouldCreateAuditRecordWithValidDataBuilder() {
        // Assert (Verificar)
        assertThat(record).isNotNull();
        assertThat(record.getId()).isEqualTo(id);
        assertThat(record.getOperationType()).isEqualTo(operationType);
        assertThat(record.getDateTime()).isEqualTo(dateTime);
        assertThat(record.getEntity()).isEqualTo(entity);
        assertThat(record.getUser()).isEqualTo(user);
        assertThat(record.getAmount()).isEqualTo(amount);
        assertThat(record.getDetails()).isEqualTo(details);
    }

    @Test
    @DisplayName("Should create a basic record using the factory method of()")
    void shouldCreateBasicRecordUsingFactoryOf() {
        // Act (Actuar)
        AuditRecord<Client, ClientOperationType> basicRecord = 
                AuditRecord.of(Optional.of(operationType), Optional.of(entity), Optional.of(user));
        
        // Assert (Verificar)
        assertThat(basicRecord).isNotNull();
        assertThat(basicRecord.getId()).isNotNull();
        assertThat(basicRecord.getOperationType()).isEqualTo(operationType);
        assertThat(basicRecord.getEntity()).isEqualTo(entity);
        assertThat(basicRecord.getUser()).isEqualTo(user);
        assertThat(basicRecord.getAmount()).isNull();
        assertThat(basicRecord.getDetails()).isNull();
        
        // Verify that the date was set automatically
        assertThat(basicRecord.getDateTime()).isNotNull();
    }

    @Test
    @DisplayName("Should create a detailed record using the factory method ofDetailed()")
    void shouldCreateDetailedRecordUsingFactoryOfDetailed() {
        // Act (Actuar)
        AuditRecord<Client, ClientOperationType> detailedRecord = 
                AuditRecord.ofDetailed(Optional.of(operationType), Optional.of(entity), Optional.of(user), Optional.of(amount), Optional.of(details));
        
        // Assert (Verificar)
        assertThat(detailedRecord).isNotNull();
        assertThat(detailedRecord.getId()).isNotNull();
        assertThat(detailedRecord.getOperationType()).isEqualTo(operationType);
        assertThat(detailedRecord.getEntity()).isEqualTo(entity);
        assertThat(detailedRecord.getUser()).isEqualTo(user);
        assertThat(detailedRecord.getAmount()).isEqualTo(amount);
        assertThat(detailedRecord.getDetails()).isEqualTo(details);
        
        // Verify that the date was set automatically
        assertThat(detailedRecord.getDateTime()).isNotNull();
    }

    @Test
    @DisplayName("Should generate unique ID automatically in factory methods")
    void shouldGenerateUniqueIdAutomaticallyInFactoryMethods() {
        // Act (Actuar)
        AuditRecord<Client, ClientOperationType> record1 = 
                AuditRecord.of(Optional.of(operationType), Optional.of(entity), Optional.of(user));
        AuditRecord<Client, ClientOperationType> record2 = 
                AuditRecord.of(Optional.of(operationType), Optional.of(entity), Optional.of(user));
        
        // Assert (Verificar)
        assertThat(record1.getId()).isNotNull();
        assertThat(record2.getId()).isNotNull();
        assertThat(record1.getId()).isNotEqualTo(record2.getId()); // Different IDs
    }

    @Test
    @DisplayName("Should set current date automatically in factory methods")
    void shouldSetCurrentDateAutomaticallyInFactoryMethods() {
        // Arrange (Preparar)
        LocalDateTime before = LocalDateTime.now();
        
        // Act (Actuar)
        AuditRecord<Client, ClientOperationType> basicRecord = 
                AuditRecord.of(Optional.of(operationType), Optional.of(entity), Optional.of(user));
        LocalDateTime after = LocalDateTime.now();
        
        // Assert (Verificar)
        assertThat(basicRecord.getDateTime()).isNotNull();
        assertThat(basicRecord.getDateTime()).isBetween(before, after);
    }

    @Test
    @DisplayName("Should handle different operation types")
    void shouldHandleDifferentOperationTypes() {
        // Arrange (Preparar)
        AuditRecord<Client, ClientOperationType> createRecord = 
                AuditRecord.of(Optional.of(ClientOperationType.CREATE), Optional.of(entity), Optional.of(user));
        AuditRecord<Client, ClientOperationType> updateRecord = 
                AuditRecord.of(Optional.of(ClientOperationType.UPDATE), Optional.of(entity), Optional.of(user));
        AuditRecord<Client, ClientOperationType> deleteRecord = 
                AuditRecord.of(Optional.of(ClientOperationType.DELETE), Optional.of(entity), Optional.of(user));
        
        // Assert (Verificar)
        assertThat(createRecord.getOperationType()).isEqualTo(ClientOperationType.CREATE);
        assertThat(updateRecord.getOperationType()).isEqualTo(ClientOperationType.UPDATE);
        assertThat(deleteRecord.getOperationType()).isEqualTo(ClientOperationType.DELETE);
    }

    @Test
    @DisplayName("Should handle null and non-null amounts")
    void shouldHandleNullAndNonNullAmounts() {
        // Arrange (Preparar)
        Double zeroAmount = 0.0;
        Double negativeAmount = -100.0;
        
        // Act (Actuar)
        AuditRecord<Client, ClientOperationType> recordWithoutAmount = 
                AuditRecord.of(Optional.of(operationType), Optional.of(entity), Optional.of(user));
        AuditRecord<Client, ClientOperationType> recordWithZeroAmount = 
                AuditRecord.ofDetailed(Optional.of(operationType), Optional.of(entity), Optional.of(user), Optional.of(zeroAmount), Optional.of(details));
        AuditRecord<Client, ClientOperationType> recordWithNegativeAmount = 
                AuditRecord.ofDetailed(Optional.of(operationType), Optional.of(entity), Optional.of(user), Optional.of(negativeAmount), Optional.of(details));
        
        // Assert (Verificar)
        assertThat(recordWithoutAmount.getAmount()).isNull();
        assertThat(recordWithZeroAmount.getAmount()).isEqualTo(zeroAmount);
        assertThat(recordWithNegativeAmount.getAmount()).isEqualTo(negativeAmount);
    }

    @Test
    @DisplayName("Should handle null and non-null details")
    void shouldHandleNullAndNonNullDetails() {
        // Arrange (Preparar)
        String emptyDetails = "";
        String longDetails = "Este es un detalle muy largo que contiene mucha información sobre la operación realizada";
        
        // Act (Actuar)
        AuditRecord<Client, ClientOperationType> recordWithoutDetails = 
                AuditRecord.of(Optional.of(operationType), Optional.of(entity), Optional.of(user));
        AuditRecord<Client, ClientOperationType> recordWithDetails = 
                AuditRecord.ofDetailed(Optional.of(operationType), Optional.of(entity), Optional.of(user), Optional.of(amount), Optional.of(details));
        AuditRecord<Client, ClientOperationType> recordWithEmptyDetails = 
                AuditRecord.ofDetailed(Optional.of(operationType), Optional.of(entity), Optional.of(user), Optional.of(amount), Optional.of(emptyDetails));
        AuditRecord<Client, ClientOperationType> recordWithLongDetails = 
                AuditRecord.ofDetailed(Optional.of(operationType), Optional.of(entity), Optional.of(user), Optional.of(amount), Optional.of(longDetails));
        
        // Assert (Verificar)
        assertThat(recordWithoutDetails.getDetails()).isNull();
        assertThat(recordWithDetails.getDetails()).isEqualTo(details);
        assertThat(recordWithEmptyDetails.getDetails()).isEqualTo(emptyDetails);
        assertThat(recordWithLongDetails.getDetails()).isEqualTo(longDetails);
    }

    @Test
    @DisplayName("Should maintain immutability in all fields")
    void shouldMaintainImmutabilityInAllFields() {
        // Arrange (Preparar)
        AuditRecord<Client, ClientOperationType> originalRecord = record;
        
        // Act (Actuar) - Create a new record with different data
        AuditRecord<Client, ClientOperationType> newRecord = AuditRecord.of(
            Optional.of(ClientOperationType.UPDATE),
            Optional.of(entity),
            Optional.of("newuser")
        );
        
        // Assert (Verificar) - Original should not change
        assertThat(originalRecord.getId()).isEqualTo(id);
        assertThat(originalRecord.getOperationType()).isEqualTo(operationType);
        assertThat(originalRecord.getDateTime()).isEqualTo(dateTime);
        assertThat(originalRecord.getEntity()).isEqualTo(entity);
        assertThat(originalRecord.getUser()).isEqualTo(user);
        assertThat(originalRecord.getAmount()).isEqualTo(amount);
        assertThat(originalRecord.getDetails()).isEqualTo(details);
        
        // Verify that they are different objects
        assertThat(newRecord).isNotSameAs(originalRecord);
        assertThat(newRecord.getId()).isNotEqualTo(originalRecord.getId());
    }

    @Test
    @DisplayName("Should handle different entity types")
    void shouldHandleDifferentEntityTypes() {
        // This test demonstrates that AuditRecord can work with different entity types
        // We'll use Client as the entity type, but it could be any class that implements Identifiable
        
        // Arrange (Preparar)
        Client newClient = Client.builder()
                .id(2L)
                .firstName("Maria")       // nombre
                .lastName("Garcia")       // apellido
                .dni("0987654321")
                .build();
        
        // Act (Actuar)
        AuditRecord<Client, ClientOperationType> recordWithNewEntity = 
                AuditRecord.of(Optional.of(operationType), Optional.of(newClient), Optional.of(user));
        
        // Assert (Verificar)
        assertThat(recordWithNewEntity.getEntity()).isEqualTo(newClient);
        assertThat(recordWithNewEntity.getEntity()).isNotEqualTo(entity);
        assertThat(recordWithNewEntity.getEntity().getId()).isEqualTo(2L);
    }
} 