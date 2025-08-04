package com.mibanco.dtoTest.mapperTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mibanco.dto.TransactionDTO;
import com.mibanco.dto.mappers.TransactionMapper;
import com.mibanco.model.Transaction;
import com.mibanco.model.enums.TransactionType;

/**
 * Tests for TransactionMapper following the new architecture
 * where mappers are pure converters without ID generation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionMapper Tests")
class TransactionMapperTest {

    private TransactionMapper transactionMapper;
    
    @BeforeEach
    void setUp() {
        transactionMapper = new TransactionMapper();
    }

    @Nested
    @DisplayName("Entity to DTO Tests")
    class EntityToDtoTests {
        
        @Test
        @DisplayName("Should convert Transaction to TransactionDTO correctly")
        void deberiaConvertirTransaccionADto() {
            // Arrange
            Transaction transaction = Transaction.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber("ES3498765432109876543210")
                .type(TransactionType.SENT_TRANSFER)
                .amount(new BigDecimal("500.00"))
                .date(LocalDateTime.of(2024, 1, 1, 10, 0))
                .description("Transfer to destination account")
                .build();
            
            // Act
            Optional<TransactionDTO> resultado = transactionMapper.toDto(Optional.of(transaction));
            
            // Assert
            assertTrue(resultado.isPresent());
            TransactionDTO dto = resultado.get();
            
            assertEquals(1L, dto.getId());
            assertEquals("ES3412345678901234567890", dto.getAccountNumber());
            assertEquals("ES3498765432109876543210", dto.getDestinationAccountNumber());
            assertEquals(TransactionType.SENT_TRANSFER, dto.getType());
            assertEquals(new BigDecimal("500.00"), dto.getAmount());
            assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), dto.getDate());
            assertEquals("Transfer to destination account", dto.getDescription());
        }
        
        @Test
        @DisplayName("Should handle transaction with null destination account number")
        void deberiaManejarTransaccionConCuentaDestinoNula() {
            // Arrange
            Transaction transaction = Transaction.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("1000.00"))
                .date(LocalDateTime.now())
                .description("Cash deposit")
                .build();
            
            // Act
            Optional<TransactionDTO> resultado = transactionMapper.toDto(Optional.of(transaction));
            
            // Assert
            assertTrue(resultado.isPresent());
            TransactionDTO dto = resultado.get();
            
            assertEquals(1L, dto.getId());
            assertEquals("ES3412345678901234567890", dto.getAccountNumber());
            assertNull(dto.getDestinationAccountNumber());
            assertEquals(TransactionType.DEPOSIT, dto.getType());
            assertEquals(new BigDecimal("1000.00"), dto.getAmount());
            assertEquals("Cash deposit", dto.getDescription());
        }
        
        @Test
        @DisplayName("Should return Optional.empty() for null transaction")
        void deberiaRetornarEmptyParaTransaccionNula() {
            // Act
            Optional<TransactionDTO> resultado = transactionMapper.toDto(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Should convert list of transactions correctly")
        void deberiaConvertirListaDeTransacciones() {
            // Arrange
            Transaction transaction1 = Transaction.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber("ES3498765432109876543210")
                .type(TransactionType.SENT_TRANSFER)
                .amount(new BigDecimal("500.00"))
                .date(LocalDateTime.now())
                .description("Transfer 1")
                .build();
                
            Transaction transaction2 = Transaction.builder()
                .id(2L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber(null)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("200.00"))
                .date(LocalDateTime.now())
                .description("Withdrawal at ATM")
                .build();
            
            List<Transaction> transactions = Arrays.asList(transaction1, transaction2);
            
            // Act
            Optional<List<TransactionDTO>> resultado = transactionMapper.toDtoList(transactions);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<TransactionDTO> dtos = resultado.get();
            
            assertEquals(2, dtos.size());
            assertEquals(1L, dtos.get(0).getId());
            assertEquals(2L, dtos.get(1).getId());
            assertEquals(TransactionType.SENT_TRANSFER, dtos.get(0).getType());
            assertEquals(TransactionType.WITHDRAWAL, dtos.get(1).getType());
        }
        
        @Test
        @DisplayName("Should handle transaction with null description")
        void deberiaManejarTransaccionConDescripcionNula() {
            // Arrange
            Transaction transaction = Transaction.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("1000.00"))
                .date(LocalDateTime.now())
                .description(null)
                .build();
            
            // Act
            Optional<TransactionDTO> resultado = transactionMapper.toDto(Optional.of(transaction));
            
            // Assert
            assertTrue(resultado.isPresent());
            TransactionDTO dto = resultado.get();
            
            assertEquals(1L, dto.getId());
            assertEquals("ES3412345678901234567890", dto.getAccountNumber());
            assertEquals(TransactionType.DEPOSIT, dto.getType());
            assertEquals(new BigDecimal("1000.00"), dto.getAmount());
            assertNull(dto.getDescription());
        }
    }

    @Nested
    @DisplayName("DTO to Entity Tests")
    class DtoToEntityTests {
        
        @Test
        @DisplayName("Should convert TransactionDTO to Transaction correctly")
        void deberiaConvertirDtoATransaccion() {
            // Arrange
            TransactionDTO dto = TransactionDTO.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber("ES3498765432109876543210")
                .type(TransactionType.SENT_TRANSFER)
                .amount(new BigDecimal("500.00"))
                .date(LocalDateTime.of(2024, 1, 1, 10, 0))
                .description("Transfer to destination account")
                .build();
            
            // Act
            Optional<Transaction> resultado = transactionMapper.toEntity(Optional.of(dto));
            
            // Assert
            assertTrue(resultado.isPresent());
            Transaction transaction = resultado.get();
            
            assertEquals(1L, transaction.getId());
            assertEquals("ES3412345678901234567890", transaction.getAccountNumber());
            assertEquals("ES3498765432109876543210", transaction.getDestinationAccountNumber());
            assertEquals(TransactionType.SENT_TRANSFER, transaction.getType());
            assertEquals(new BigDecimal("500.00"), transaction.getAmount());
            assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), transaction.getDate());
            assertEquals("Transfer to destination account", transaction.getDescription());
        }
        
        @Test
        @DisplayName("Should handle DTO with null destination account number")
        void deberiaManejarDtoConCuentaDestinoNula() {
            // Arrange
            TransactionDTO dto = TransactionDTO.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("1000.00"))
                .date(LocalDateTime.now())
                .description("Cash deposit")
                .build();
            
            // Act
            Optional<Transaction> resultado = transactionMapper.toEntity(Optional.of(dto));
            
            // Assert
            assertTrue(resultado.isPresent());
            Transaction transaction = resultado.get();
            
            assertEquals(1L, transaction.getId());
            assertEquals("ES3412345678901234567890", transaction.getAccountNumber());
            assertNull(transaction.getDestinationAccountNumber());
            assertEquals(TransactionType.DEPOSIT, transaction.getType());
            assertEquals(new BigDecimal("1000.00"), transaction.getAmount());
            assertEquals("Cash deposit", transaction.getDescription());
        }
        
        @Test
        @DisplayName("Should return Optional.empty() for null DTO")
        void deberiaRetornarEmptyParaDtoNulo() {
            // Act
            Optional<Transaction> resultado = transactionMapper.toEntity(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Should convert list of DTOs correctly")
        void deberiaConvertirListaDeDtos() {
            // Arrange
            TransactionDTO dto1 = TransactionDTO.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber("ES3498765432109876543210")
                .type(TransactionType.SENT_TRANSFER)
                .amount(new BigDecimal("500.00"))
                .date(LocalDateTime.now())
                .description("Transfer 1")
                .build();
                
            TransactionDTO dto2 = TransactionDTO.builder()
                .id(2L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber(null)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("200.00"))
                .date(LocalDateTime.now())
                .description("Withdrawal at ATM")
                .build();
            
            List<TransactionDTO> dtos = Arrays.asList(dto1, dto2);
            
            // Act
            Optional<List<Transaction>> resultado = transactionMapper.toEntityList(dtos);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Transaction> transactions = resultado.get();
            
            assertEquals(2, transactions.size());
            assertEquals(1L, transactions.get(0).getId());
            assertEquals(2L, transactions.get(1).getId());
            assertEquals(TransactionType.SENT_TRANSFER, transactions.get(0).getType());
            assertEquals(TransactionType.WITHDRAWAL, transactions.get(1).getType());
        }
        
        @Test
        @DisplayName("Should handle DTO with null description")
        void deberiaManejarDtoConDescripcionNula() {
            // Arrange
            TransactionDTO dto = TransactionDTO.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("1000.00"))
                .date(LocalDateTime.now())
                .description(null)
                .build();
            
            // Act
            Optional<Transaction> resultado = transactionMapper.toEntity(Optional.of(dto));
            
            // Assert
            assertTrue(resultado.isPresent());
            Transaction transaction = resultado.get();
            
            assertEquals(1L, transaction.getId());
            assertEquals("ES3412345678901234567890", transaction.getAccountNumber());
            assertEquals(TransactionType.DEPOSIT, transaction.getType());
            assertEquals(new BigDecimal("1000.00"), transaction.getAmount());
            assertNull(transaction.getDescription());
        }
    }

    @Nested
    @DisplayName("Direct Method Tests")
    class DirectMethodTests {
        
        @Test
        @DisplayName("toDtoDirecto should work correctly")
        void aDtoDirectoDeberiaFuncionar() {
            // Arrange
            Transaction transaction = Transaction.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("1000.00"))
                .date(LocalDateTime.now())
                .description("Deposit")
                .build();
            
            // Act
            Optional<TransactionDTO> resultado = transactionMapper.toDtoDirect(transaction);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(1L, resultado.get().getId());
            assertEquals("ES3412345678901234567890", resultado.get().getAccountNumber());
            assertEquals(TransactionType.DEPOSIT, resultado.get().getType());
        }
        
        @Test
        @DisplayName("toEntityDirecta should work correctly")
        void aEntidadDirectaDeberiaFuncionar() {
            // Arrange
            TransactionDTO dto = TransactionDTO.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("1000.00"))
                .date(LocalDateTime.now())
                .description("Deposit")
                .build();
            
            // Act
            Optional<Transaction> resultado = transactionMapper.toEntityDirect(dto);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(1L, resultado.get().getId());
            assertEquals("ES3412345678901234567890", resultado.get().getAccountNumber());
            assertEquals(TransactionType.DEPOSIT, resultado.get().getType());
        }
        
        @Test
        @DisplayName("toDtoDirecto should handle null transaction")
        void aDtoDirectoDeberiaManejarTransaccionNula() {
            // Act
            Optional<TransactionDTO> resultado = transactionMapper.toDtoDirect(null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("toEntityDirecta should handle null DTO")
        void aEntidadDirectaDeberiaManejarDtoNulo() {
            // Act
            Optional<Transaction> resultado = transactionMapper.toEntityDirect(null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {
        
        @Test
        @DisplayName("Should handle transaction with zero amount")
        void deberiaManejarTransaccionConMontoCero() {
            // Arrange
            Transaction transaction = Transaction.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.ZERO)
                .date(LocalDateTime.now())
                .description("Zero deposit")
                .build();
            
            // Act
            Optional<TransactionDTO> resultado = transactionMapper.toDto(Optional.of(transaction));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(BigDecimal.ZERO, resultado.get().getAmount());
        }
        
        @Test
        @DisplayName("Should handle transaction with negative amount")
        void deberiaManejarTransaccionConMontoNegativo() {
            // Arrange
            Transaction transaction = Transaction.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber(null)
                .type(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("-500.00"))
                .date(LocalDateTime.now())
                .description("Withdrawal")
                .build();
            
            // Act
            Optional<TransactionDTO> resultado = transactionMapper.toDto(Optional.of(transaction));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(new BigDecimal("-500.00"), resultado.get().getAmount());
        }
        
        @Test
        @DisplayName("Should handle transaction with empty description")
        void deberiaManejarTransaccionConDescripcionVacia() {
            // Arrange
            Transaction transaction = Transaction.builder()
                .id(1L)
                .accountNumber("ES3412345678901234567890")
                .destinationAccountNumber(null)
                .type(TransactionType.DEPOSIT)
                .amount(new BigDecimal("1000.00"))
                .date(LocalDateTime.now())
                .description("")
                .build();
            
            // Act
            Optional<TransactionDTO> resultado = transactionMapper.toDto(Optional.of(transaction));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals("", resultado.get().getDescription());
        }
        
        @Test
        @DisplayName("Should handle empty list of transactions")
        void deberiaManejarListaVaciaDeTransacciones() {
            // Arrange
            List<Transaction> transactions = Arrays.asList();
            
            // Act
            Optional<List<TransactionDTO>> resultado = transactionMapper.toDtoList(transactions);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle null list of transactions")
        void deberiaManejarListaNulaDeTransacciones() {
            // Act
            Optional<List<TransactionDTO>> resultado = transactionMapper.toDtoList((List<Transaction>) null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
}
