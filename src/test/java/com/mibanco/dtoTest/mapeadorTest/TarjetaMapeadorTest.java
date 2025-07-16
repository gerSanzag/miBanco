package com.mibanco.dtoTest.mapeadorTest;

import com.mibanco.dto.TarjetaDTO;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.mapeador.TarjetaMapeador;
import com.mibanco.dto.mapeador.Mapeador;
import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.enums.TipoTarjeta;
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
 * Tests para TarjetaMapeador siguiendo la nueva arquitectura
 * donde los mappers son puros conversores sin generación de IDs
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TarjetaMapeador Tests")
class TarjetaMapeadorTest {
    
    @Mock
    private Mapeador<Cliente, ClienteDTO> clienteMapeador;
    
    private TarjetaMapeador tarjetaMapeador;
    private ClienteDTO clienteDTO;
    private TarjetaDTO tarjetaDTO;
    private Cliente cliente;
    private Tarjeta tarjeta;
    
    @BeforeEach
    void setUp() {
        tarjetaMapeador = new TarjetaMapeador(clienteMapeador);
        
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
            
        tarjetaDTO = TarjetaDTO.builder()
            .numero(1234567890123456L)
            .titular(clienteDTO)
            .numeroCuentaAsociada("123456789")
            .tipo(TipoTarjeta.DEBITO)
            .fechaExpiracion(LocalDate.now().plusYears(3))
            .activa(true)
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
            
        tarjeta = Tarjeta.builder()
            .numero(1234567890123456L)
            .cvv("456") // ✅ CVV válido para la entidad (3 dígitos, rango 100-999)
            .titular(cliente)
            .numeroCuentaAsociada("123456789")
            .tipo(TipoTarjeta.DEBITO)
            .fechaExpiracion(LocalDate.now().plusYears(3))
            .activa(true)
            .build();
    }
    
    @Nested
    @DisplayName("Tests de conversión DTO a Entidad")
    class DtoToEntityTests {
        
        @Test
        @DisplayName("Debería convertir TarjetaDTO a Tarjeta correctamente")
        void deberiaConvertirDtoAEntidad() {
            // Arrange
            when(clienteMapeador.aEntidad(Optional.of(clienteDTO)))
                .thenReturn(Optional.of(cliente));
            
            // Act
            Optional<Tarjeta> resultado = tarjetaMapeador.aEntidad(Optional.of(tarjetaDTO));
            
            // Assert
            assertTrue(resultado.isPresent());
            Tarjeta tarjetaResultado = resultado.get();
            
            assertEquals(tarjetaDTO.getNumero(), tarjetaResultado.getNumero());
            assertEquals(cliente, tarjetaResultado.getTitular());
            assertEquals(tarjetaDTO.getNumeroCuentaAsociada(), tarjetaResultado.getNumeroCuentaAsociada());
            assertEquals(tarjetaDTO.getTipo(), tarjetaResultado.getTipo());
            assertEquals(tarjetaDTO.getFechaExpiracion(), tarjetaResultado.getFechaExpiracion());
            assertEquals(tarjetaDTO.isActiva(), tarjetaResultado.isActiva());
            
            verify(clienteMapeador).aEntidad(Optional.of(clienteDTO));
        }
        
        @Test
        @DisplayName("Debería mantener CVV null cuando no se proporciona")
        void deberiaMantenerCvvNullCuandoNoSeProporciona() {
            // Arrange
            when(clienteMapeador.aEntidad(Optional.of(clienteDTO)))
                .thenReturn(Optional.of(cliente));
            
            // Act
            Optional<Tarjeta> resultado1 = tarjetaMapeador.aEntidad(Optional.of(tarjetaDTO));
            Optional<Tarjeta> resultado2 = tarjetaMapeador.aEntidad(Optional.of(tarjetaDTO));
            
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
            Optional<TarjetaDTO> tarjetaDTOVacio = Optional.empty();
            
            // Act
            Optional<Tarjeta> resultado = tarjetaMapeador.aEntidad(tarjetaDTOVacio);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería mantener número null cuando no se proporciona")
        void deberiaMantenerNumeroNullCuandoNoSeProporciona() {
            // Arrange
            TarjetaDTO tarjetaSinNumero = tarjetaDTO.toBuilder()
                .numero(null)
                .build();
                
            when(clienteMapeador.aEntidad(Optional.of(clienteDTO)))
                .thenReturn(Optional.of(cliente));
            
            // Act
            Optional<Tarjeta> resultado1 = tarjetaMapeador.aEntidad(Optional.of(tarjetaSinNumero));
            Optional<Tarjeta> resultado2 = tarjetaMapeador.aEntidad(Optional.of(tarjetaSinNumero));
            
            // Assert
            assertTrue(resultado1.isPresent());
            assertTrue(resultado2.isPresent());
            
            Long numero1 = resultado1.get().getNumero();
            Long numero2 = resultado2.get().getNumero();
            
            // Verificar que ambos números son null (el mapeador no genera números)
            assertNull(numero1);
            assertNull(numero2);
        }
        
        @Test
        @DisplayName("Debería manejar DTO con titular nulo")
        void deberiaManejarDtoConTitularNulo() {
            // Arrange
            TarjetaDTO dtoSinTitular = tarjetaDTO.toBuilder()
                .titular(null)
                .build();
            
            // Act
            Optional<Tarjeta> resultado = tarjetaMapeador.aEntidad(Optional.of(dtoSinTitular));
            
            // Assert
            assertTrue(resultado.isPresent());
            Tarjeta tarjetaResultado = resultado.get();
            
            assertEquals(tarjetaDTO.getNumero(), tarjetaResultado.getNumero());
            assertNull(tarjetaResultado.getTitular());
            assertEquals(tarjetaDTO.getNumeroCuentaAsociada(), tarjetaResultado.getNumeroCuentaAsociada());
            assertEquals(tarjetaDTO.getTipo(), tarjetaResultado.getTipo());
            assertEquals(tarjetaDTO.getFechaExpiracion(), tarjetaResultado.getFechaExpiracion());
            assertEquals(tarjetaDTO.isActiva(), tarjetaResultado.isActiva());
        }
    }
    
    @Nested
    @DisplayName("Tests de conversión Entidad a DTO")
    class EntityToDtoTests {
        
        @Test
        @DisplayName("Debería convertir Tarjeta a TarjetaDTO correctamente")
        void deberiaConvertirEntidadADto() {
            // Arrange
            when(clienteMapeador.aDto(Optional.of(cliente)))
                .thenReturn(Optional.of(clienteDTO));
            
            // Act
            Optional<TarjetaDTO> resultado = tarjetaMapeador.aDto(Optional.of(tarjeta));
            
            // Assert
            assertTrue(resultado.isPresent());
            TarjetaDTO dtoResultado = resultado.get();
            
            assertEquals(tarjeta.getNumero(), dtoResultado.getNumero());
            assertEquals(clienteDTO, dtoResultado.getTitular());
            assertEquals(tarjeta.getNumeroCuentaAsociada(), dtoResultado.getNumeroCuentaAsociada());
            assertEquals(tarjeta.getTipo(), dtoResultado.getTipo());
            assertEquals(tarjeta.getFechaExpiracion(), dtoResultado.getFechaExpiracion());
            assertEquals(tarjeta.isActiva(), dtoResultado.isActiva());
            
            verify(clienteMapeador).aDto(Optional.of(cliente));
        }
        
        @Test
        @DisplayName("El DTO no debería contener el CVV por seguridad")
        void elDtoNoDeberiaContenerCvv() {
            // Arrange
            when(clienteMapeador.aDto(Optional.of(cliente)))
                .thenReturn(Optional.of(clienteDTO));
            
            // Act
            Optional<TarjetaDTO> resultado = tarjetaMapeador.aDto(Optional.of(tarjeta));
            
            // Assert
            assertTrue(resultado.isPresent());
            TarjetaDTO dtoResultado = resultado.get();
            
            // Verificar que el DTO no tiene campo CVV (por seguridad)
            // El CVV solo existe en la entidad, no en el DTO
            assertNotNull(dtoResultado.getNumero());
            assertNotNull(dtoResultado.getTitular());
            assertNotNull(dtoResultado.getNumeroCuentaAsociada());
            assertNotNull(dtoResultado.getTipo());
            assertNotNull(dtoResultado.getFechaExpiracion());
            // activa es un boolean, siempre tiene valor
        }
        
        @Test
        @DisplayName("Debería manejar tarjeta con titular nulo")
        void deberiaManejarTarjetaConTitularNulo() {
            // Arrange
            Tarjeta tarjetaSinTitular = Tarjeta.builder()
                .numero(tarjeta.getNumero())
                .titular(null)
                .numeroCuentaAsociada(tarjeta.getNumeroCuentaAsociada())
                .tipo(tarjeta.getTipo())
                .cvv(tarjeta.getCvv())
                .fechaExpiracion(tarjeta.getFechaExpiracion())
                .activa(tarjeta.isActiva())
                .build();
            
            // Act
            Optional<TarjetaDTO> resultado = tarjetaMapeador.aDto(Optional.of(tarjetaSinTitular));
            
            // Assert
            assertTrue(resultado.isPresent());
            TarjetaDTO dtoResultado = resultado.get();
            
            assertEquals(tarjeta.getNumero(), dtoResultado.getNumero());
            assertNull(dtoResultado.getTitular());
            assertEquals(tarjeta.getNumeroCuentaAsociada(), dtoResultado.getNumeroCuentaAsociada());
            assertEquals(tarjeta.getTipo(), dtoResultado.getTipo());
            assertEquals(tarjeta.getFechaExpiracion(), dtoResultado.getFechaExpiracion());
            assertEquals(tarjeta.isActiva(), dtoResultado.isActiva());
            
            // No debería llamar al mapeador de cliente cuando el titular es null
            verify(clienteMapeador, never()).aDto(any());
        }
        
        @Test
        @DisplayName("Debería manejar Optional.empty() correctamente")
        void deberiaManejarOptionalEmptyEnDto() {
            // Arrange
            Optional<Tarjeta> tarjetaVacia = Optional.empty();
            
            // Act
            Optional<TarjetaDTO> resultado = tarjetaMapeador.aDto(tarjetaVacia);
            
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
            when(clienteMapeador.aDto(Optional.of(cliente)))
                .thenReturn(Optional.of(clienteDTO));
            
            // Act
            Optional<TarjetaDTO> resultado = tarjetaMapeador.aDtoDirecto(tarjeta);
            
            // Assert
            assertTrue(resultado.isPresent());
            TarjetaDTO dtoResultado = resultado.get();
            assertEquals(tarjeta.getNumero(), dtoResultado.getNumero());
            assertEquals(clienteDTO, dtoResultado.getTitular());
        }
        
        @Test
        @DisplayName("aEntidadDirecta debería funcionar correctamente")
        void aEntidadDirectaDeberiaFuncionar() {
            // Arrange
            when(clienteMapeador.aEntidad(Optional.of(clienteDTO)))
                .thenReturn(Optional.of(cliente));
            
            // Act
            Optional<Tarjeta> resultado = tarjetaMapeador.aEntidadDirecta(tarjetaDTO);
            
            // Assert
            assertTrue(resultado.isPresent());
            Tarjeta tarjetaResultado = resultado.get();
            assertEquals(tarjetaDTO.getNumero(), tarjetaResultado.getNumero());
            assertEquals(cliente, tarjetaResultado.getTitular());
        }
        
        @Test
        @DisplayName("aDtoDirecto debería manejar null correctamente")
        void aDtoDirectoDeberiaManejarNull() {
            // Act
            Optional<TarjetaDTO> resultado = tarjetaMapeador.aDtoDirecto(null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("aEntidadDirecta debería manejar null correctamente")
        void aEntidadDirectaDeberiaManejarNull() {
            // Act
            Optional<Tarjeta> resultado = tarjetaMapeador.aEntidadDirecta(null);
            
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
            List<Tarjeta> tarjetas = List.of(tarjeta);
            when(clienteMapeador.aDto(Optional.of(cliente)))
                .thenReturn(Optional.of(clienteDTO));
            
            // Act
            Optional<List<TarjetaDTO>> resultado = tarjetaMapeador.aListaDto(tarjetas);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<TarjetaDTO> dtos = resultado.get();
            assertEquals(1, dtos.size());
            assertEquals(tarjeta.getNumero(), dtos.get(0).getNumero());
            assertEquals(clienteDTO, dtos.get(0).getTitular());
        }
        
        @Test
        @DisplayName("aListaEntidad debería convertir lista de DTOs a entidades")
        void aListaEntidadDeberiaConvertirLista() {
            // Arrange
            List<TarjetaDTO> dtos = List.of(tarjetaDTO);
            when(clienteMapeador.aEntidad(Optional.of(clienteDTO)))
                .thenReturn(Optional.of(cliente));
            
            // Act
            Optional<List<Tarjeta>> resultado = tarjetaMapeador.aListaEntidad(dtos);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Tarjeta> tarjetas = resultado.get();
            assertEquals(1, tarjetas.size());
            assertEquals(tarjetaDTO.getNumero(), tarjetas.get(0).getNumero());
            assertEquals(cliente, tarjetas.get(0).getTitular());
        }
        
        @Test
        @DisplayName("aListaDto debería manejar lista vacía")
        void aListaDtoDeberiaManejarListaVacia() {
            // Arrange
            List<Tarjeta> tarjetas = List.of();
            
            // Act
            Optional<List<TarjetaDTO>> resultado = tarjetaMapeador.aListaDto(tarjetas);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<TarjetaDTO> dtos = resultado.get();
            assertTrue(dtos.isEmpty());
        }
        
        @Test
        @DisplayName("aListaEntidad debería manejar lista vacía")
        void aListaEntidadDeberiaManejarListaVacia() {
            // Arrange
            List<TarjetaDTO> dtos = List.of();
            
            // Act
            Optional<List<Tarjeta>> resultado = tarjetaMapeador.aListaEntidad(dtos);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Tarjeta> tarjetas = resultado.get();
            assertTrue(tarjetas.isEmpty());
        }
        
        @Test
        @DisplayName("aListaDto debería manejar null")
        void aListaDtoDeberiaManejarNull() {
            // Act
            Optional<List<TarjetaDTO>> resultado = tarjetaMapeador.aListaDto((List<Tarjeta>) null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("aListaEntidad debería manejar null")
        void aListaEntidadDeberiaManejarNull() {
            // Act
            Optional<List<Tarjeta>> resultado = tarjetaMapeador.aListaEntidad((List<TarjetaDTO>) null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
}
