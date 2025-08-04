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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mibanco.dto.AccountDTO;
import com.mibanco.dto.ClientDTO;
import com.mibanco.dto.mappers.AccountMapper;
import com.mibanco.dto.mappers.Mapper;
import com.mibanco.model.Account;
import com.mibanco.model.Client;
import com.mibanco.model.enums.AccountType;

/**
 * Tests for AccountMapper following the new architecture
 * where mappers are pure converters without ID generation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountMapper Tests")
class AccountMapperTest {

    @Mock
    private Mapper<Client, ClientDTO> clientMapper;
    
    private AccountMapper accountMapper;
    
    @BeforeEach
    void setUp() {
        accountMapper = new AccountMapper(clientMapper);
    }

    @Nested
    @DisplayName("Entity to DTO Tests")
    class EntityToDtoTests {
        
        @Test
        @DisplayName("Should convert Account to AccountDTO correctly")
        void shouldConvertAccountToDto() {
            // Arrange
            Client client = Client.builder()
                .id(1L)
                .firstName("Juan Pérez")
                .email("juan@email.com")
                .build();
                
            Account account = Account.builder()
                .accountNumber("ES3412345678901234567890")
                .holder(client)
                .type(AccountType.CHECKING)
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1500.00"))
                .creationDate(LocalDateTime.of(2024, 1, 1, 10, 0))
                .active(true)
                .build();
                
            ClientDTO clientDTO = ClientDTO.builder()
                .id(1L)
                .firstName("Juan Pérez")
                .email("juan@email.com")
                .build();
                
            when(clientMapper.toDto(Optional.of(client)))
                .thenReturn(Optional.of(clientDTO));
            
            // Act
            Optional<AccountDTO> result = accountMapper.toDto(Optional.of(account));
            
            // Assert
            assertTrue(result.isPresent());
            AccountDTO dto = result.get();
            
            assertEquals("ES3412345678901234567890", dto.getAccountNumber());
            assertEquals(clientDTO, dto.getHolder());
            assertEquals(AccountType.CHECKING, dto.getType());
            assertEquals(new BigDecimal("1000.00"), dto.getInitialBalance());
            assertEquals(new BigDecimal("1500.00"), dto.getBalance());
            assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), dto.getCreationDate());
            assertTrue(dto.isActive());
            
            verify(clientMapper).toDto(Optional.of(client));
        }
        
        @Test
        @DisplayName("Should handle account with null holder")
        void shouldHandleAccountWithNullHolder() {
            // Arrange
            Account account = Account.builder()
                .accountNumber("ES3412345678901234567890")
                .holder(null)
                .type(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("500.00"))
                .balance(new BigDecimal("500.00"))
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();
            
            // Act
            Optional<AccountDTO> result = accountMapper.toDto(Optional.of(account));
            
            // Assert
            assertTrue(result.isPresent());
            AccountDTO dto = result.get();
            
            assertEquals("ES3412345678901234567890", dto.getAccountNumber());
            assertNull(dto.getHolder());
            assertEquals(AccountType.SAVINGS, dto.getType());
            assertTrue(dto.isActive());
        }
        
        @Test
        @DisplayName("Should return Optional.empty() for null account")
        void shouldReturnEmptyForNullAccount() {
            // Act
            Optional<AccountDTO> result = accountMapper.toDto(Optional.empty());
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should convert list of accounts correctly")
        void shouldConvertListOfAccounts() {
            // Arrange
            Client client1 = Client.builder().id(1L).firstName("Client 1").build();
            Client client2 = Client.builder().id(2L).firstName("Client 2").build();
            
            Account account1 = Account.builder()
                .accountNumber("ES3400000000000000000001")
                .holder(client1)
                .type(AccountType.CHECKING)
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();
                
            Account account2 = Account.builder()
                .accountNumber("ES3400000000000000000002")
                .holder(client2)
                .type(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("500.00"))
                .balance(new BigDecimal("500.00"))
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();
                
            ClientDTO clientDTO1 = ClientDTO.builder().id(1L).firstName("Client 1").build();
            ClientDTO clientDTO2 = ClientDTO.builder().id(2L).firstName("Client 2").build();
            
            when(clientMapper.toDto(Optional.of(client1)))
                .thenReturn(Optional.of(clientDTO1));
            when(clientMapper.toDto(Optional.of(client2)))
                .thenReturn(Optional.of(clientDTO2));
            
            List<Account> accounts = Arrays.asList(account1, account2);
            
            // Act
            Optional<List<AccountDTO>> result = accountMapper.toDtoList(accounts);
            
            // Assert
            assertTrue(result.isPresent());
            List<AccountDTO> dtos = result.get();
            
            assertEquals(2, dtos.size());
            assertEquals("ES3400000000000000000001", dtos.get(0).getAccountNumber());
            assertEquals("ES3400000000000000000002", dtos.get(1).getAccountNumber());
        }
    }

    @Nested
    @DisplayName("DTO to Entity Tests")
    class DtoToEntityTests {
        
        @Test
        @DisplayName("Should convert AccountDTO to Account correctly")
        void shouldConvertDtoToAccount() {
            // Arrange
            ClientDTO clientDTO = ClientDTO.builder()
                .id(1L)
                .firstName("Juan Pérez")
                .email("juan@email.com")
                .build();
                
            AccountDTO dto = AccountDTO.builder()
                .accountNumber("ES3412345678901234567890")
                .holder(clientDTO)
                .type(AccountType.CHECKING)
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1500.00"))
                .creationDate(LocalDateTime.of(2024, 1, 1, 10, 0))
                .active(true)
                .build();
                
            Client client = Client.builder()
                .id(1L)
                .firstName("Juan Pérez")
                .email("juan@email.com")
                .build();
                
            when(clientMapper.toEntity(Optional.of(clientDTO)))
                .thenReturn(Optional.of(client));
            
            // Act
            Optional<Account> result = accountMapper.toEntity(Optional.of(dto));
            
            // Assert
            assertTrue(result.isPresent());
            Account account = result.get();
            
            assertEquals("ES3412345678901234567890", account.getAccountNumber());
            assertEquals(client, account.getHolder());
            assertEquals(AccountType.CHECKING, account.getType());
            assertEquals(new BigDecimal("1000.00"), account.getInitialBalance());
            assertEquals(new BigDecimal("1500.00"), account.getBalance());
            assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), account.getCreationDate());
            assertTrue(account.isActive());
            
            verify(clientMapper).toEntity(Optional.of(clientDTO));
        }
        
        @Test
        @DisplayName("Should handle DTO with null holder")
        void shouldHandleDtoWithNullHolder() {
            // Arrange
            AccountDTO dto = AccountDTO.builder()
                .accountNumber("ES3412345678901234567890")
                .holder(null)
                .type(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("500.00"))
                .balance(new BigDecimal("500.00"))
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();
            
            // Act
            Optional<Account> result = accountMapper.toEntity(Optional.of(dto));
            
            // Assert
            assertTrue(result.isPresent());
            Account account = result.get();
            
            assertEquals("ES3412345678901234567890", account.getAccountNumber());
            assertNull(account.getHolder());
            assertEquals(AccountType.SAVINGS, account.getType());
            assertTrue(account.isActive());
        }
        
        @Test
        @DisplayName("Should return Optional.empty() for null DTO")
        void shouldReturnEmptyForNullDto() {
            // Act
            Optional<Account> result = accountMapper.toEntity(Optional.empty());
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("Should convert list of DTOs correctly")
        void shouldConvertListOfDtos() {
            // Arrange
            ClientDTO clientDTO1 = ClientDTO.builder().id(1L).firstName("Client 1").build();
            ClientDTO clientDTO2 = ClientDTO.builder().id(2L).firstName("Client 2").build();
            
            AccountDTO dto1 = AccountDTO.builder()
                .accountNumber("ES3400000000000000000001")
                .holder(clientDTO1)
                .type(AccountType.CHECKING)
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();
                
            AccountDTO dto2 = AccountDTO.builder()
                .accountNumber("ES3400000000000000000002")
                .holder(clientDTO2)
                .type(AccountType.SAVINGS)
                .initialBalance(new BigDecimal("500.00"))
                .balance(new BigDecimal("500.00"))
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();
                
            Client client1 = Client.builder().id(1L).firstName("Client 1").build();
            Client client2 = Client.builder().id(2L).firstName("Client 2").build();
            
            when(clientMapper.toEntity(Optional.of(clientDTO1)))
                .thenReturn(Optional.of(client1));
            when(clientMapper.toEntity(Optional.of(clientDTO2)))
                .thenReturn(Optional.of(client2));
            
            List<AccountDTO> dtos = Arrays.asList(dto1, dto2);
            
            // Act
            Optional<List<Account>> result = accountMapper.toEntityList(dtos);
            
            // Assert
            assertTrue(result.isPresent());
            List<Account> accounts = result.get();
            
            assertEquals(2, accounts.size());
            assertEquals("ES3400000000000000000001", accounts.get(0).getAccountNumber());
            assertEquals("ES3400000000000000000002", accounts.get(1).getAccountNumber());
        }
    }

    @Nested
    @DisplayName("Direct Method Tests")
    class DirectMethodTests {
        
        @Test
        @DisplayName("toDtoDirect should work correctly")
        void toDtoDirectShouldWork() {
            // Arrange
            Account account = Account.builder()
                .accountNumber("ES3412345678901234567890")
                .holder(null)
                .type(AccountType.CHECKING)
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();
            
            // Act
            Optional<AccountDTO> result = accountMapper.toDtoDirect(account);
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals("ES3412345678901234567890", result.get().getAccountNumber());
        }
        
        @Test
        @DisplayName("toEntityDirect should work correctly")
        void toEntityDirectShouldWork() {
            // Arrange
            AccountDTO dto = AccountDTO.builder()
                .accountNumber("ES3412345678901234567890")
                .holder(null)
                .type(AccountType.CHECKING)
                .initialBalance(new BigDecimal("1000.00"))
                .balance(new BigDecimal("1000.00"))
                .creationDate(LocalDateTime.now())
                .active(true)
                .build();
            
            // Act
            Optional<Account> result = accountMapper.toEntityDirect(dto);
            
            // Assert
            assertTrue(result.isPresent());
            assertEquals("ES3412345678901234567890", result.get().getAccountNumber());
        }
        
        @Test
        @DisplayName("toDtoDirect should handle null account")
        void toDtoDirectShouldHandleNullAccount() {
            // Act
            Optional<AccountDTO> result = accountMapper.toDtoDirect(null);
            
            // Assert
            assertFalse(result.isPresent());
        }
        
        @Test
        @DisplayName("toEntityDirect should handle null DTO")
        void toEntityDirectShouldHandleNullDto() {
            // Act
            Optional<Account> result = accountMapper.toEntityDirect(null);
            
            // Assert
            assertFalse(result.isPresent());
        }
    }
}
