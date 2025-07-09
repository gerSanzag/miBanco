package com.mibanco.dtoTest.mapeadorTest;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para ClienteMapeador
 * Valida la generación automática de IDs y la funcionalidad del mapeador
 */
@DisplayName("ClienteMapeador Tests")
class ClienteMapeadorTest {
    
    private ClienteMapeador clienteMapeador;
    private ClienteDTO clienteDTO;
    private Cliente cliente;
    
    @BeforeEach
    void setUp() {
        clienteMapeador = new ClienteMapeador();
        
        // Crear datos de prueba
        clienteDTO = ClienteDTO.builder()
            .id(1L)
            .nombre("Juan")
            .apellido("Pérez")
            .dni("12345678")
            .email("juan@test.com")
            .telefono("123456789")
            .direccion("Calle Test 123")
            .fechaNacimiento(LocalDate.of(1990, 1, 1))
            .build();
            
        cliente = Cliente.builder()
            .id(1L)
            .nombre("Juan")
            .apellido("Pérez")
            .dni("12345678")
            .email("juan@test.com")
            .telefono("123456789")
            .direccion("Calle Test 123")
            .fechaNacimiento(LocalDate.of(1990, 1, 1))
            .build();
    }
    
    @Nested
    @DisplayName("Tests de conversión DTO a Entidad")
    class DtoToEntityTests {
        
        @Test
        @DisplayName("Debería convertir ClienteDTO a Cliente correctamente")
        void deberiaConvertirDtoAEntidad() {
            // Arrange
            // clienteDTO ya está disponible como campo de la clase
            
            // Act
            Optional<Cliente> resultado = clienteMapeador.aEntidad(Optional.of(clienteDTO));
            
            // Assert
            assertTrue(resultado.isPresent());
            Cliente clienteResultado = resultado.get();
            
            assertEquals(clienteDTO.getId(), clienteResultado.getId());
            assertEquals(clienteDTO.getNombre(), clienteResultado.getNombre());
            assertEquals(clienteDTO.getApellido(), clienteResultado.getApellido());
            assertEquals(clienteDTO.getDni(), clienteResultado.getDni());
            assertEquals(clienteDTO.getEmail(), clienteResultado.getEmail());
            assertEquals(clienteDTO.getTelefono(), clienteResultado.getTelefono());
            assertEquals(clienteDTO.getDireccion(), clienteResultado.getDireccion());
            assertEquals(clienteDTO.getFechaNacimiento(), clienteResultado.getFechaNacimiento());
        }
        
        @Test
        @DisplayName("Debería mantener ID null cuando el DTO tiene ID null")
        void deberiaMantenerIdNullSiEsNull() {
            // Arrange
            ClienteDTO clienteSinId = clienteDTO.toBuilder()
                .id(null)
                .build();
            
            // Act
            Optional<Cliente> resultado = clienteMapeador.aEntidad(Optional.of(clienteSinId));
            
            // Assert
            assertTrue(resultado.isPresent());
            Cliente clienteResultado = resultado.get();
            
            // Verificar que el ID sigue siendo null (el mapeador no genera IDs)
            assertNull(clienteResultado.getId());
        }
        
        @Test
        @DisplayName("Debería mantener el ID del DTO si no es null")
        void deberiaMantenerIdSiNoEsNull() {
            // Arrange
            Long idEsperado = 999L;
            ClienteDTO clienteConId = clienteDTO.toBuilder()
                .id(idEsperado)
                .build();
            
            // Act
            Optional<Cliente> resultado = clienteMapeador.aEntidad(Optional.of(clienteConId));
            
            // Assert
            assertTrue(resultado.isPresent());
            Cliente clienteResultado = resultado.get();
            
            assertEquals(idEsperado, clienteResultado.getId());
        }
        
        @Test
        @DisplayName("Debería mantener IDs null en cada conversión")
        void deberiaMantenerIdsNullEnCadaConversion() {
            // Arrange
            ClienteDTO clienteSinId = clienteDTO.toBuilder()
                .id(null)
                .build();
            
            // Act
            Optional<Cliente> resultado1 = clienteMapeador.aEntidad(Optional.of(clienteSinId));
            Optional<Cliente> resultado2 = clienteMapeador.aEntidad(Optional.of(clienteSinId));
            
            // Assert
            assertTrue(resultado1.isPresent());
            assertTrue(resultado2.isPresent());
            
            Long id1 = resultado1.get().getId();
            Long id2 = resultado2.get().getId();
            
            // Los IDs deberían ser null en ambos casos (el mapeador no genera IDs)
            assertNull(id1);
            assertNull(id2);
        }
        
        @Test
        @DisplayName("Debería manejar Optional.empty() correctamente")
        void deberiaManejarOptionalEmpty() {
            // Arrange
            Optional<ClienteDTO> clienteDTOVacio = Optional.empty();
            
            // Act
            Optional<Cliente> resultado = clienteMapeador.aEntidad(clienteDTOVacio);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Tests de conversión Entidad a DTO")
    class EntityToDtoTests {
        
        @Test
        @DisplayName("Debería convertir Cliente a ClienteDTO correctamente")
        void deberiaConvertirEntidadADto() {
            // Arrange
            // cliente ya está disponible como campo de la clase
            
            // Act
            Optional<ClienteDTO> resultado = clienteMapeador.aDto(Optional.of(cliente));
            
            // Assert
            assertTrue(resultado.isPresent());
            ClienteDTO clienteDtoResultado = resultado.get();
            
            assertEquals(cliente.getId(), clienteDtoResultado.getId());
            assertEquals(cliente.getNombre(), clienteDtoResultado.getNombre());
            assertEquals(cliente.getApellido(), clienteDtoResultado.getApellido());
            assertEquals(cliente.getDni(), clienteDtoResultado.getDni());
            assertEquals(cliente.getEmail(), clienteDtoResultado.getEmail());
            assertEquals(cliente.getTelefono(), clienteDtoResultado.getTelefono());
            assertEquals(cliente.getDireccion(), clienteDtoResultado.getDireccion());
            assertEquals(cliente.getFechaNacimiento(), clienteDtoResultado.getFechaNacimiento());
        }
        
        @Test
        @DisplayName("Debería manejar Optional.empty() correctamente")
        void deberiaManejarOptionalEmptyEnDto() {
            // Arrange
            Optional<Cliente> clienteVacio = null;
            
            // Act
            Optional<ClienteDTO> resultado = clienteMapeador.aDto(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Tests de métodos directos")
    class MetodosDirectosTests {
        
        @Test
        @DisplayName("aDtoDirecto debería funcionar correctamente")
        void aDtoDirectoDeberiaFuncionar() {
            // Arrange
            // cliente ya está disponible como campo de la clase
            
            // Act
            Optional<ClienteDTO> resultado = clienteMapeador.aDtoDirecto(cliente);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(cliente.getId(), resultado.get().getId());
            assertEquals(cliente.getNombre(), resultado.get().getNombre());
        }
        
        @Test
        @DisplayName("aEntidadDirecta debería funcionar correctamente")
        void aEntidadDirectaDeberiaFuncionar() {
            // Arrange
            // clienteDTO ya está disponible como campo de la clase
            
            // Act
            Optional<Cliente> resultado = clienteMapeador.aEntidadDirecta(clienteDTO);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(clienteDTO.getId(), resultado.get().getId());
            assertEquals(clienteDTO.getNombre(), resultado.get().getNombre());
        }
        
        @Test
        @DisplayName("aDtoDirecto debería manejar null correctamente")
        void aDtoDirectoDeberiaManejarNull() {
            // Arrange
            Cliente clienteNull = null;
            
            // Act
            Optional<ClienteDTO> resultado = clienteMapeador.aDtoDirecto(clienteNull);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("aEntidadDirecta debería manejar null correctamente")
        void aEntidadDirectaDeberiaManejarNull() {
            // Arrange
            ClienteDTO clienteDTONull = null;
            
            // Act
            Optional<Cliente> resultado = clienteMapeador.aEntidadDirecta(clienteDTONull);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Tests de listas")
    class ListasTests {
        
        @Test
        @DisplayName("aListaDto debería convertir lista de entidades a DTOs")
        void aListaDtoDeberiaConvertirLista() {
            // Arrange
            List<Cliente> clientes = List.of(cliente);
            
            // Act
            Optional<List<ClienteDTO>> resultado = clienteMapeador.aListaDto(clientes);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<ClienteDTO> clientesDto = resultado.get();
            assertEquals(1, clientesDto.size());
            assertEquals(cliente.getId(), clientesDto.get(0).getId());
            assertEquals(cliente.getNombre(), clientesDto.get(0).getNombre());
        }
        
        @Test
        @DisplayName("aListaEntidad debería convertir lista de DTOs a entidades")
        void aListaEntidadDeberiaConvertirLista() {
            // Arrange
            List<ClienteDTO> clientesDto = List.of(clienteDTO);
            
            // Act
            Optional<List<Cliente>> resultado = clienteMapeador.aListaEntidad(clientesDto);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Cliente> clientes = resultado.get();
            assertEquals(1, clientes.size());
            assertEquals(clienteDTO.getId(), clientes.get(0).getId());
            assertEquals(clienteDTO.getNombre(), clientes.get(0).getNombre());
        }
        
        @Test
        @DisplayName("aListaDto debería manejar lista vacía")
        void aListaDtoDeberiaManejarListaVacia() {
            // Arrange
            List<Cliente> clientesVacios = List.of();
            
            // Act
            Optional<List<ClienteDTO>> resultado = clienteMapeador.aListaDto(clientesVacios);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get().isEmpty());
        }
        
        @Test
        @DisplayName("aListaEntidad debería manejar lista vacía")
        void aListaEntidadDeberiaManejarListaVacia() {
            // Arrange
            List<ClienteDTO> clientesDTOVacios = List.of();
            
            // Act
            Optional<List<Cliente>> resultado = clienteMapeador.aListaEntidad(clientesDTOVacios);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get().isEmpty());
        }
        
        @Test
        @DisplayName("aListaDto debería manejar null")
        void aListaDtoDeberiaManejarNull() {
            // Arrange
            List<Cliente> clientesNull = null;
            
            // Act
            Optional<List<ClienteDTO>> resultado = clienteMapeador.aListaDto(clientesNull);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("aListaEntidad debería manejar null")
        void aListaEntidadDeberiaManejarNull() {
            // Arrange
            List<ClienteDTO> clientesDTONull = null;
            
            // Act
            Optional<List<Cliente>> resultado = clienteMapeador.aListaEntidad(clientesDTONull);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
    
}
