package com.mibanco.dtoEnglishTest.mapperTest;

import com.mibanco.dtoEnglish.CardDTO;
import com.mibanco.dtoEnglish.ClientDTO;
import com.mibanco.dtoEnglish.mappers.CardMapper;
import com.mibanco.dtoEnglish.mappers.Mapper;
import com.mibanco.modelEnglish.Card;
import com.mibanco.modelEnglish.Client;
import com.mibanco.modelEnglish.enums.CardType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for CardMapper following the new architecture
 * where mappers are pure converters without ID generation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CardMapper Tests")
class CardMapperTest {
    
    @Mock
    private Mapper<Client, ClientDTO> clientMapper;
    
    private CardMapper cardMapper;
    private ClientDTO clientDTO;
    private CardDTO cardDTO;
    private Client client;
    private Card card;
    
    @BeforeEach
    void setUp() {
        cardMapper = new CardMapper(clientMapper);
        
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
            
        cardDTO = CardDTO.builder()
            .number(1234567890123456L) // numero
            .holder(clientDTO) // titular
            .associatedAccountNumber("123456789") // numeroCuentaAsociada
            .type(CardType.DEBIT) // tipo
            .expirationDate(LocalDate.now().plusYears(3)) // fechaExpiracion
            .active(true) // activa
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
            
        card = Card.builder()
            .number(1234567890123456L) // numero
            .cvv("456") // CVV válido para la entidad (3 dígitos, rango 100-999)
            .holder(client) // titular
            .associatedAccountNumber("123456789") // numeroCuentaAsociada
            .type(CardType.DEBIT) // tipo
            .expirationDate(LocalDate.now().plusYears(3)) // fechaExpiracion
            .active(true) // activa
            .build();
    }
    
    @Nested
    @DisplayName("Tests de conversión DTO a Entidad")
    class DtoToEntityTests {
        
        @Test
        @DisplayName("Debería convertir CardDTO a Card correctamente")
        void deberiaConvertirDtoAEntidad() {
            // Arrange
            when(clientMapper.toEntity(Optional.of(clientDTO)))
                .thenReturn(Optional.of(client));
            
            // Act
            Optional<Card> resultado = cardMapper.toEntity(Optional.of(cardDTO));
            
            // Assert
            assertTrue(resultado.isPresent());
            Card cardResultado = resultado.get();
            
            assertEquals(cardDTO.getNumber(), cardResultado.getNumber());
            assertEquals(client, cardResultado.getHolder());
            assertEquals(cardDTO.getAssociatedAccountNumber(), cardResultado.getAssociatedAccountNumber());
            assertEquals(cardDTO.getType(), cardResultado.getType());
            assertEquals(cardDTO.getExpirationDate(), cardResultado.getExpirationDate());
            assertEquals(cardDTO.isActive(), cardResultado.isActive());
            
            verify(clientMapper).toEntity(Optional.of(clientDTO));
        }
        
        @Test
        @DisplayName("Debería mantener CVV null cuando no se proporciona")
        void deberiaMantenerCvvNullCuandoNoSeProporciona() {
            // Arrange
            when(clientMapper.toEntity(Optional.of(clientDTO)))
                .thenReturn(Optional.of(client));
            
            // Act
            Optional<Card> resultado1 = cardMapper.toEntity(Optional.of(cardDTO));
            Optional<Card> resultado2 = cardMapper.toEntity(Optional.of(cardDTO));
            
            // Assert
            assertTrue(resultado1.isPresent());
            assertTrue(resultado2.isPresent());
            
            String cvv1 = resultado1.get().getCvv();
            String cvv2 = resultado2.get().getCvv();
            
            // Verificar que ambos CVVs son null (el mapeador no genera CVV)
            assertNull(cvv1);
            assertNull(cvv2);
        }
        
        @Test
        @DisplayName("Debería manejar Optional.empty() correctamente")
        void deberiaManejarOptionalEmpty() {
            // Arrange
            Optional<CardDTO> cardDTOVacio = Optional.empty();
            
            // Act
            Optional<Card> resultado = cardMapper.toEntity(cardDTOVacio);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería mantener número null cuando no se proporciona")
        void deberiaMantenerNumeroNullCuandoNoSeProporciona() {
            // Arrange
            CardDTO cardSinNumero = cardDTO.toBuilder()
                .number(null)
                .build();
                
            when(clientMapper.toEntity(Optional.of(clientDTO)))
                .thenReturn(Optional.of(client));
            
            // Act
            Optional<Card> resultado1 = cardMapper.toEntity(Optional.of(cardSinNumero));
            Optional<Card> resultado2 = cardMapper.toEntity(Optional.of(cardSinNumero));
            
            // Assert
            assertTrue(resultado1.isPresent());
            assertTrue(resultado2.isPresent());
            
            Long numero1 = resultado1.get().getNumber();
            Long numero2 = resultado2.get().getNumber();
            
            // Verificar que ambos números son null (el mapeador no genera números)
            assertNull(numero1);
            assertNull(numero2);
        }
        
        @Test
        @DisplayName("Debería manejar DTO con titular nulo")
        void deberiaManejarDtoConTitularNulo() {
            // Arrange
            CardDTO dtoSinTitular = cardDTO.toBuilder()
                .holder(null)
                .build();
            
            // Act
            Optional<Card> resultado = cardMapper.toEntity(Optional.of(dtoSinTitular));
            
            // Assert
            assertTrue(resultado.isPresent());
            Card cardResultado = resultado.get();
            
            assertEquals(cardDTO.getNumber(), cardResultado.getNumber());
            assertNull(cardResultado.getHolder());
            assertEquals(cardDTO.getAssociatedAccountNumber(), cardResultado.getAssociatedAccountNumber());
            assertEquals(cardDTO.getType(), cardResultado.getType());
            assertEquals(cardDTO.getExpirationDate(), cardResultado.getExpirationDate());
            assertEquals(cardDTO.isActive(), cardResultado.isActive());
        }
    }
    
    @Nested
    @DisplayName("Tests de conversión Entidad a DTO")
    class EntityToDtoTests {
        
        @Test
        @DisplayName("Debería convertir Card a CardDTO correctamente")
        void deberiaConvertirEntidadADto() {
            // Arrange
            when(clientMapper.toDto(Optional.of(client)))
                .thenReturn(Optional.of(clientDTO));
            
            // Act
            Optional<CardDTO> resultado = cardMapper.toDto(Optional.of(card));
            
            // Assert
            assertTrue(resultado.isPresent());
            CardDTO dtoResultado = resultado.get();
            
            assertEquals(card.getNumber(), dtoResultado.getNumber());
            assertEquals(clientDTO, dtoResultado.getHolder());
            assertEquals(card.getAssociatedAccountNumber(), dtoResultado.getAssociatedAccountNumber());
            assertEquals(card.getType(), dtoResultado.getType());
            assertEquals(card.getExpirationDate(), dtoResultado.getExpirationDate());
            assertEquals(card.isActive(), dtoResultado.isActive());
            
            verify(clientMapper).toDto(Optional.of(client));
        }
        
        @Test
        @DisplayName("El DTO no debería contener el CVV por seguridad")
        void elDtoNoDeberiaContenerCvv() {
            // Arrange
            when(clientMapper.toDto(Optional.of(client)))
                .thenReturn(Optional.of(clientDTO));
            
            // Act
            Optional<CardDTO> resultado = cardMapper.toDto(Optional.of(card));
            
            // Assert
            assertTrue(resultado.isPresent());
            CardDTO dtoResultado = resultado.get();
            
            // Verificar que el DTO no tiene campo CVV (por seguridad)
            // El CVV solo existe en la entidad, no en el DTO
            assertNotNull(dtoResultado.getNumber());
            assertNotNull(dtoResultado.getHolder());
            assertNotNull(dtoResultado.getAssociatedAccountNumber());
            assertNotNull(dtoResultado.getType());
            assertNotNull(dtoResultado.getExpirationDate());
            // active es un boolean, siempre tiene valor
        }
        
        @Test
        @DisplayName("Debería manejar card con titular nulo")
        void deberiaManejarCardConTitularNulo() {
            // Arrange
            Card cardSinTitular = Card.builder()
                .number(card.getNumber())
                .holder(null)
                .associatedAccountNumber(card.getAssociatedAccountNumber())
                .type(card.getType())
                .cvv(card.getCvv())
                .expirationDate(card.getExpirationDate())
                .active(card.isActive())
                .build();
            
            // Act
            Optional<CardDTO> resultado = cardMapper.toDto(Optional.of(cardSinTitular));
            
            // Assert
            assertTrue(resultado.isPresent());
            CardDTO dtoResultado = resultado.get();
            
            assertEquals(card.getNumber(), dtoResultado.getNumber());
            assertNull(dtoResultado.getHolder());
            assertEquals(card.getAssociatedAccountNumber(), dtoResultado.getAssociatedAccountNumber());
            assertEquals(card.getType(), dtoResultado.getType());
            assertEquals(card.getExpirationDate(), dtoResultado.getExpirationDate());
            assertEquals(card.isActive(), dtoResultado.isActive());
            
            // No debería llamar al mapeador de cliente cuando el titular es null
            verify(clientMapper, never()).toDto(any());
        }
        
        @Test
        @DisplayName("Debería manejar Optional.empty() correctamente")
        void deberiaManejarOptionalEmptyEnDto() {
            // Arrange
            Optional<Card> cardVacia = Optional.empty();
            
            // Act
            Optional<CardDTO> resultado = cardMapper.toDto(cardVacia);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Tests de métodos directos")
    class MetodosDirectosTests {
        
        @Test
        @DisplayName("toDtoDirecto debería funcionar correctamente")
        void toDtoDirectoDeberiaFuncionar() {
            // Arrange
            when(clientMapper.toDto(Optional.of(client)))
                .thenReturn(Optional.of(clientDTO));
            
            // Act
            Optional<CardDTO> resultado = cardMapper.toDtoDirect(card);
            
            // Assert
            assertTrue(resultado.isPresent());
            CardDTO dtoResultado = resultado.get();
            assertEquals(card.getNumber(), dtoResultado.getNumber());
            assertEquals(clientDTO, dtoResultado.getHolder());
        }
        
        @Test
        @DisplayName("toEntityDirecta debería funcionar correctamente")
        void toEntityDirectaDeberiaFuncionar() {
            // Arrange
            when(clientMapper.toEntity(Optional.of(clientDTO)))
                .thenReturn(Optional.of(client));
            
            // Act
            Optional<Card> resultado = cardMapper.toEntityDirect(cardDTO);
            
            // Assert
            assertTrue(resultado.isPresent());
            Card cardResultado = resultado.get();
            assertEquals(cardDTO.getNumber(), cardResultado.getNumber());
            assertEquals(client, cardResultado.getHolder());
        }
        
        @Test
        @DisplayName("toDtoDirecto debería manejar null correctamente")
        void toDtoDirectoDeberiaManejarNull() {
            // Act
            Optional<CardDTO> resultado = cardMapper.toDtoDirect(null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("toEntityDirecta debería manejar null correctamente")
        void toEntityDirectaDeberiaManejarNull() {
            // Act
            Optional<Card> resultado = cardMapper.toEntityDirect(null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Tests de listas")
    class ListasTests {
        
        @Test
        @DisplayName("toListaDto debería convertir lista de entidades a DTOs")
        void toListaDtoDeberiaConvertirLista() {
            // Arrange
            List<Card> cards = List.of(card);
            when(clientMapper.toDto(Optional.of(client)))
                .thenReturn(Optional.of(clientDTO));
            
            // Act
            Optional<List<CardDTO>> resultado = cardMapper.toDtoList(cards);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<CardDTO> dtos = resultado.get();
            assertEquals(1, dtos.size());
            assertEquals(card.getNumber(), dtos.get(0).getNumber());
            assertEquals(clientDTO, dtos.get(0).getHolder());
        }
        
        @Test
        @DisplayName("toListaEntidad debería convertir lista de DTOs a entidades")
        void toListaEntidadDeberiaConvertirLista() {
            // Arrange
            List<CardDTO> dtos = List.of(cardDTO);
            when(clientMapper.toEntity(Optional.of(clientDTO)))
                .thenReturn(Optional.of(client));
            
            // Act
            Optional<List<Card>> resultado = cardMapper.toEntityList(dtos);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Card> cards = resultado.get();
            assertEquals(1, cards.size());
            assertEquals(cardDTO.getNumber(), cards.get(0).getNumber());
            assertEquals(client, cards.get(0).getHolder());
        }
        
        @Test
        @DisplayName("toListaDto debería manejar lista vacía")
        void toListaDtoDeberiaManejarListaVacia() {
            // Arrange
            List<Card> cards = List.of();
            
            // Act
            Optional<List<CardDTO>> resultado = cardMapper.toDtoList(cards);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<CardDTO> dtos = resultado.get();
            assertTrue(dtos.isEmpty());
        }
        
        @Test
        @DisplayName("toListaEntidad debería manejar lista vacía")
        void toListaEntidadDeberiaManejarListaVacia() {
            // Arrange
            List<CardDTO> dtos = List.of();
            
            // Act
            Optional<List<Card>> resultado = cardMapper.toEntityList(dtos);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Card> cards = resultado.get();
            assertTrue(cards.isEmpty());
        }
        
        @Test
        @DisplayName("toListaDto debería manejar null")
        void toListaDtoDeberiaManejarNull() {
            // Act
            Optional<List<CardDTO>> resultado = cardMapper.toDtoList((List<Card>) null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("toListaEntidad debería manejar null")
        void toListaEntidadDeberiaManejarNull() {
            // Act
            Optional<List<Card>> resultado = cardMapper.toEntityList((List<CardDTO>) null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
}
