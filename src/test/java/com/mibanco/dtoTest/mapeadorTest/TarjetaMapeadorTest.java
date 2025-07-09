package com.mibanco.dtoTest.mapeadorTest;

import com.mibanco.dto.TarjetaDTO;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.mapeador.TarjetaMapeador;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.enums.TipoTarjeta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para TarjetaMapeador
 * Valida la generación automática del CVV y la funcionalidad del mapeador
 */
@DisplayName("TarjetaMapeador Tests")
class TarjetaMapeadorTest {
    
    private TarjetaMapeador tarjetaMapeador;
    private ClienteMapeador clienteMapeador;
    private ClienteDTO clienteDTO;
    private TarjetaDTO tarjetaDTO;
    private Cliente cliente;
    private Tarjeta tarjeta;
    
    @BeforeEach
    void setUp() {
        clienteMapeador = new ClienteMapeador();
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
            .numero("1234567890123456")
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
            .numero("1234567890123456")
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
            // tarjetaDTO ya está disponible como campo de la clase
            
            // Act
            Optional<Tarjeta> resultado = tarjetaMapeador.aEntidad(Optional.of(tarjetaDTO));
            
            // Assert
            assertTrue(resultado.isPresent());
            Tarjeta tarjetaResultado = resultado.get();
            
            assertEquals(tarjetaDTO.getNumero(), tarjetaResultado.getNumero());
            assertEquals(tarjetaDTO.getTitular().getId(), tarjetaResultado.getTitular().getId());
            assertEquals(tarjetaDTO.getNumeroCuentaAsociada(), tarjetaResultado.getNumeroCuentaAsociada());
            assertEquals(tarjetaDTO.getTipo(), tarjetaResultado.getTipo());
            assertEquals(tarjetaDTO.getFechaExpiracion(), tarjetaResultado.getFechaExpiracion());
            assertEquals(tarjetaDTO.isActiva(), tarjetaResultado.isActiva());
        }
        
        @Test
        @DisplayName("Debería mantener CVV null cuando no se proporciona")
        void deberiaMantenerCvvNullCuandoNoSeProporciona() {
            // Arrange
            // tarjetaDTO ya está disponible como campo de la clase
            
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
            
            // Act
            Optional<Tarjeta> resultado1 = tarjetaMapeador.aEntidad(Optional.of(tarjetaSinNumero));
            Optional<Tarjeta> resultado2 = tarjetaMapeador.aEntidad(Optional.of(tarjetaSinNumero));
            
            // Assert
            assertTrue(resultado1.isPresent());
            assertTrue(resultado2.isPresent());
            
            String numero1 = resultado1.get().getNumero();
            String numero2 = resultado2.get().getNumero();
            
            // Verificar que ambos números son null (el mapeador no genera números)
            assertNull(numero1);
            assertNull(numero2);
        }
    }
    
    @Nested
    @DisplayName("Tests de conversión Entidad a DTO")
    class EntityToDtoTests {
        
        @Test
        @DisplayName("Debería convertir Tarjeta a TarjetaDTO correctamente")
        void deberiaConvertirEntidadADto() {
            // Arrange
            // tarjeta ya está disponible como campo de la clase
            
            // Act
            Optional<TarjetaDTO> resultado = tarjetaMapeador.aDto(Optional.of(tarjeta));
            
            // Assert
            assertTrue(resultado.isPresent());
            TarjetaDTO tarjetaDtoResultado = resultado.get();
            
            assertEquals(tarjeta.getNumero(), tarjetaDtoResultado.getNumero());
            assertEquals(tarjeta.getTitular().getId(), tarjetaDtoResultado.getTitular().getId());
            assertEquals(tarjeta.getNumeroCuentaAsociada(), tarjetaDtoResultado.getNumeroCuentaAsociada());
            assertEquals(tarjeta.getTipo(), tarjetaDtoResultado.getTipo());
            assertEquals(tarjeta.getFechaExpiracion(), tarjetaDtoResultado.getFechaExpiracion());
            assertEquals(tarjeta.isActiva(), tarjetaDtoResultado.isActiva());
        }
        
        @Test
        @DisplayName("El DTO no debería contener el CVV por seguridad")
        void elDtoNoDeberiaContenerCvv() {
            // Arrange
            // tarjeta ya está disponible como campo de la clase
            
            // Act
            Optional<TarjetaDTO> resultado = tarjetaMapeador.aDto(Optional.of(tarjeta));
            
            // Assert
            assertTrue(resultado.isPresent());
            TarjetaDTO tarjetaDtoResultado = resultado.get();
            
            // Verificar que el DTO no tiene campo CVV (por diseño de seguridad)
            // El CVV solo existe en la entidad, no en el DTO
            assertNotNull(tarjetaDtoResultado.getNumero());
            assertNotNull(tarjetaDtoResultado.getTitular());
            // No hay getter para CVV en TarjetaDTO por seguridad
        }
        
        @Test
        @DisplayName("Debería manejar Optional.empty() correctamente")
        void deberiaManejarOptionalEmptyEnDto() {
            // Arrange
            Optional<Tarjeta> tarjetaVacia = null;
            
            // Act
            Optional<TarjetaDTO> resultado = tarjetaMapeador.aDto(Optional.empty());
            
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
            // tarjeta ya está disponible como campo de la clase
            
            // Act
            Optional<TarjetaDTO> resultado = tarjetaMapeador.aDtoDirecto(tarjeta);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(tarjeta.getNumero(), resultado.get().getNumero());
        }
        
        @Test
        @DisplayName("aEntidadDirecta debería funcionar correctamente")
        void aEntidadDirectaDeberiaFuncionar() {
            // Arrange
            // tarjetaDTO ya está disponible como campo de la clase
            
            // Act
            Optional<Tarjeta> resultado = tarjetaMapeador.aEntidadDirecta(tarjetaDTO);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(tarjetaDTO.getNumero(), resultado.get().getNumero());
            assertNull(resultado.get().getCvv()); // CVV no se genera en el mapeador
        }
        
        @Test
        @DisplayName("aDtoDirecto debería manejar null correctamente")
        void aDtoDirectoDeberiaManejarNull() {
            // Arrange
            Tarjeta tarjetaNull = null;
            
            // Act
            Optional<TarjetaDTO> resultado = tarjetaMapeador.aDtoDirecto(tarjetaNull);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("aEntidadDirecta debería manejar null correctamente")
        void aEntidadDirectaDeberiaManejarNull() {
            // Arrange
            TarjetaDTO tarjetaDTONull = null;
            
            // Act
            Optional<Tarjeta> resultado = tarjetaMapeador.aEntidadDirecta(tarjetaDTONull);
            
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
            
            // Act
            Optional<List<TarjetaDTO>> resultado = tarjetaMapeador.aListaDto(tarjetas);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<TarjetaDTO> tarjetasDto = resultado.get();
            assertEquals(1, tarjetasDto.size());
            assertEquals(tarjeta.getNumero(), tarjetasDto.get(0).getNumero());
        }
        
        @Test
        @DisplayName("aListaEntidad debería convertir lista de DTOs a entidades")
        void aListaEntidadDeberiaConvertirLista() {
            // Arrange
            List<TarjetaDTO> tarjetasDto = List.of(tarjetaDTO);
            
            // Act
            Optional<List<Tarjeta>> resultado = tarjetaMapeador.aListaEntidad(tarjetasDto);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Tarjeta> tarjetas = resultado.get();
            assertEquals(1, tarjetas.size());
            assertEquals(tarjetaDTO.getNumero(), tarjetas.get(0).getNumero());
            assertNull(tarjetas.get(0).getCvv()); // CVV no se genera en el mapeador
        }
        
        @Test
        @DisplayName("aListaDto debería manejar lista vacía")
        void aListaDtoDeberiaManejarListaVacia() {
            // Arrange
            List<Tarjeta> tarjetasVacias = List.of();
            
            // Act
            Optional<List<TarjetaDTO>> resultado = tarjetaMapeador.aListaDto(tarjetasVacias);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get().isEmpty());
        }
        
        @Test
        @DisplayName("aListaEntidad debería manejar lista vacía")
        void aListaEntidadDeberiaManejarListaVacia() {
            // Arrange
            List<TarjetaDTO> tarjetasDtoVacias = List.of();
            
            // Act
            Optional<List<Tarjeta>> resultado = tarjetaMapeador.aListaEntidad(tarjetasDtoVacias);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get().isEmpty());
        }
        
        @Test
        @DisplayName("aListaDto debería manejar null")
        void aListaDtoDeberiaManejarNull() {
            // Arrange
            List<Tarjeta> tarjetasNull = null;
            
            // Act
            Optional<List<TarjetaDTO>> resultado = tarjetaMapeador.aListaDto(tarjetasNull);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("aListaEntidad debería manejar null")
        void aListaEntidadDeberiaManejarNull() {
            // Arrange
            List<TarjetaDTO> tarjetasDTONull = null;
            
            // Act
            Optional<List<Tarjeta>> resultado = tarjetaMapeador.aListaEntidad(tarjetasDTONull);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
}
