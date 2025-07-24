package com.mibanco.dtoEnglishTest.mapperTest;

import com.mibanco.dtoEnglish.ClientDTO;
import com.mibanco.dtoEnglish.mappers.ClientMapper;
import com.mibanco.modelEnglish.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ClientMapper
 * Validates the correct mapping between ClientDTO and Client
 */
@DisplayName("ClientMapper Tests")
class ClientMapperTest {
    
    private ClientMapper clientMapper;
    private ClientDTO clientDTO; // clienteDTO
    private Client client; // cliente
    
    @BeforeEach
    void setUp() {
        clientMapper = new ClientMapper();
        
        // Test data
        clientDTO = ClientDTO.builder()
            .id(1L)
            .firstName("Juan") // nombre
            .lastName("Pérez") // apellido
            .dni("12345678")
            .email("juan@test.com")
            .phone("123456789") // telefono
            .address("Calle Test 123") // direccion
            .birthDate(LocalDate.of(1990, 1, 1)) // fechaNacimiento
            .build();
            
        client = Client.builder()
            .id(1L)
            .firstName("Juan") // nombre
            .lastName("Pérez") // apellido
            .dni("12345678")
            .email("juan@test.com")
            .phone("123456789") // telefono
            .address("Calle Test 123") // direccion
            .birthDate(LocalDate.of(1990, 1, 1)) // fechaNacimiento
            .build();
    }
    
    @Nested
    @DisplayName("DTO to Entity conversion tests")
    class DtoToEntityTests {
        
        @Test
        @DisplayName("Should convert ClientDTO to Client correctly")
        void shouldConvertDtoToEntity() {
            Optional<Client> result = clientMapper.toEntity(Optional.of(clientDTO));
            assertTrue(result.isPresent());
            Client clientResult = result.get();
            assertEquals(clientDTO.getId(), clientResult.getId());
            assertEquals(clientDTO.getFirstName(), clientResult.getFirstName());
            assertEquals(clientDTO.getLastName(), clientResult.getLastName());
            assertEquals(clientDTO.getDni(), clientResult.getDni());
            assertEquals(clientDTO.getEmail(), clientResult.getEmail());
            assertEquals(clientDTO.getPhone(), clientResult.getPhone());
            assertEquals(clientDTO.getAddress(), clientResult.getAddress());
            assertEquals(clientDTO.getBirthDate(), clientResult.getBirthDate());
        }
        
        @Test
        @DisplayName("Should keep ID null when DTO has null ID")
        void shouldKeepIdNullIfNull() {
            ClientDTO clientWithoutId = clientDTO.toBuilder().id(null).build();
            Optional<Client> result = clientMapper.toEntity(Optional.of(clientWithoutId));
            assertTrue(result.isPresent());
            Client clientResult = result.get();
            assertNull(clientResult.getId());
        }
        
        @Test
        @DisplayName("Should keep ID if not null")
        void shouldKeepIdIfNotNull() {
            Long expectedId = 999L;
            ClientDTO clientWithId = clientDTO.toBuilder().id(expectedId).build();
            Optional<Client> result = clientMapper.toEntity(Optional.of(clientWithId));
            assertTrue(result.isPresent());
            Client clientResult = result.get();
            assertEquals(expectedId, clientResult.getId());
        }
        
        @Test
        @DisplayName("Should keep IDs null in each conversion")
        void shouldKeepIdsNullInEachConversion() {
            ClientDTO clientWithoutId = clientDTO.toBuilder().id(null).build();
            Optional<Client> result1 = clientMapper.toEntity(Optional.of(clientWithoutId));
            Optional<Client> result2 = clientMapper.toEntity(Optional.of(clientWithoutId));
            assertTrue(result1.isPresent());
            assertTrue(result2.isPresent());
            Long id1 = result1.get().getId();
            Long id2 = result2.get().getId();
            assertNull(id1);
            assertNull(id2);
        }
        
        @Test
        @DisplayName("Should handle Optional.empty() correctly")
        void shouldHandleOptionalEmpty() {
            Optional<Client> result = clientMapper.toEntity(Optional.empty());
            assertFalse(result.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Entity to DTO conversion tests")
    class EntityToDtoTests {
        @Test
        @DisplayName("Should convert Client to ClientDTO correctly")
        void shouldConvertEntityToDto() {
            Optional<ClientDTO> result = clientMapper.toDto(Optional.of(client));
            assertTrue(result.isPresent());
            ClientDTO dtoResult = result.get();
            assertEquals(client.getId(), dtoResult.getId());
            assertEquals(client.getFirstName(), dtoResult.getFirstName());
            assertEquals(client.getLastName(), dtoResult.getLastName());
            assertEquals(client.getDni(), dtoResult.getDni());
            assertEquals(client.getEmail(), dtoResult.getEmail());
            assertEquals(client.getPhone(), dtoResult.getPhone());
            assertEquals(client.getAddress(), dtoResult.getAddress());
            assertEquals(client.getBirthDate(), dtoResult.getBirthDate());
        }
        @Test
        @DisplayName("Should handle Optional.empty() correctly")
        void shouldHandleOptionalEmptyInDto() {
            Optional<ClientDTO> result = clientMapper.toDto(Optional.empty());
            assertFalse(result.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Direct methods tests")
    class DirectMethodsTests {
        @Test
        @DisplayName("toDtoDirect should work correctly")
        void toDtoDirectShouldWork() {
            Optional<ClientDTO> result = clientMapper.toDtoDirect(client);
            assertTrue(result.isPresent());
            assertEquals(client.getId(), result.get().getId());
            assertEquals(client.getFirstName(), result.get().getFirstName());
        }
        @Test
        @DisplayName("toEntityDirect should work correctly")
        void toEntityDirectShouldWork() {
            Optional<Client> result = clientMapper.toEntityDirect(clientDTO);
            assertTrue(result.isPresent());
            assertEquals(clientDTO.getId(), result.get().getId());
            assertEquals(clientDTO.getFirstName(), result.get().getFirstName());
        }
        @Test
        @DisplayName("toDtoDirect should handle null correctly")
        void toDtoDirectShouldHandleNull() {
            Client clientNull = null;
            Optional<ClientDTO> result = clientMapper.toDtoDirect(clientNull);
            assertFalse(result.isPresent());
        }
        @Test
        @DisplayName("toEntityDirect should handle null correctly")
        void toEntityDirectShouldHandleNull() {
            ClientDTO clientDTONull = null;
            Optional<Client> result = clientMapper.toEntityDirect(clientDTONull);
            assertFalse(result.isPresent());
        }
    }
    
    @Nested
    @DisplayName("List methods tests")
    class ListMethodsTests {
        @Test
        @DisplayName("toDtoList should convert list of entities to DTOs")
        void toDtoListShouldConvertList() {
            List<Client> clients = List.of(client);
            Optional<List<ClientDTO>> result = clientMapper.toDtoList(clients);
            assertTrue(result.isPresent());
            List<ClientDTO> clientsDto = result.get();
            assertEquals(1, clientsDto.size());
            assertEquals(client.getId(), clientsDto.get(0).getId());
            assertEquals(client.getFirstName(), clientsDto.get(0).getFirstName());
        }
        @Test
        @DisplayName("toEntityList should convert list of DTOs to entities")
        void toEntityListShouldConvertList() {
            List<ClientDTO> clientsDto = List.of(clientDTO);
            Optional<List<Client>> result = clientMapper.toEntityList(clientsDto);
            assertTrue(result.isPresent());
            List<Client> clients = result.get();
            assertEquals(1, clients.size());
            assertEquals(clientDTO.getId(), clients.get(0).getId());
            assertEquals(clientDTO.getFirstName(), clients.get(0).getFirstName());
        }
        @Test
        @DisplayName("toDtoList should handle empty list")
        void toDtoListShouldHandleEmptyList() {
            List<Client> emptyClients = List.of();
            Optional<List<ClientDTO>> result = clientMapper.toDtoList(emptyClients);
            assertTrue(result.isPresent());
            assertTrue(result.get().isEmpty());
        }
        @Test
        @DisplayName("toEntityList should handle empty list")
        void toEntityListShouldHandleEmptyList() {
            List<ClientDTO> emptyClientsDto = List.of();
            Optional<List<Client>> result = clientMapper.toEntityList(emptyClientsDto);
            assertTrue(result.isPresent());
            assertTrue(result.get().isEmpty());
        }
        @Test
        @DisplayName("toDtoList should handle null")
        void toDtoListShouldHandleNull() {
            List<Client> nullClients = null;
            Optional<List<ClientDTO>> result = clientMapper.toDtoList(nullClients);
            assertFalse(result.isPresent());
        }
        @Test
        @DisplayName("toEntityList should handle null")
        void toEntityListShouldHandleNull() {
            List<ClientDTO> nullClientsDto = null;
            Optional<List<Client>> result = clientMapper.toEntityList(nullClientsDto);
            assertFalse(result.isPresent());
        }
    }
}
