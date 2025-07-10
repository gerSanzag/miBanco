package com.mibanco.dtoTest.mapeadorTest;

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

import com.mibanco.dto.TransaccionDTO;
import com.mibanco.dto.mapeador.TransaccionMapeador;
import com.mibanco.modelo.Transaccion;
import com.mibanco.modelo.enums.TipoTransaccion;

/**
 * Tests para TransaccionMapeador siguiendo la nueva arquitectura
 * donde los mappers son puros conversores sin generación de IDs
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TransaccionMapeador Tests")
class TransaccionMapeadorTest {

    private TransaccionMapeador transaccionMapeador;
    
    @BeforeEach
    void setUp() {
        transaccionMapeador = new TransaccionMapeador();
    }

    @Nested
    @DisplayName("Entity to DTO Tests")
    class EntityToDtoTests {
        
        @Test
        @DisplayName("Debería convertir Transaccion a TransaccionDTO correctamente")
        void deberiaConvertirTransaccionADto() {
            // Arrange
            Transaccion transaccion = Transaccion.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(67890L)
                .tipo(TipoTransaccion.TRANSFERENCIA_ENVIADA)
                .monto(new BigDecimal("500.00"))
                .fecha(LocalDateTime.of(2024, 1, 1, 10, 0))
                .descripcion("Transferencia a cuenta destino")
                .build();
            
            // Act
            Optional<TransaccionDTO> resultado = transaccionMapeador.aDto(Optional.of(transaccion));
            
            // Assert
            assertTrue(resultado.isPresent());
            TransaccionDTO dto = resultado.get();
            
            assertEquals(1L, dto.getId());
            assertEquals(12345L, dto.getNumeroCuenta());
            assertEquals(67890L, dto.getNumeroCuentaDestino());
            assertEquals(TipoTransaccion.TRANSFERENCIA_ENVIADA, dto.getTipo());
            assertEquals(new BigDecimal("500.00"), dto.getMonto());
            assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), dto.getFecha());
            assertEquals("Transferencia a cuenta destino", dto.getDescripcion());
        }
        
        @Test
        @DisplayName("Debería manejar transacción con cuenta destino nula")
        void deberiaManejarTransaccionConCuentaDestinoNula() {
            // Arrange
            Transaccion transaccion = Transaccion.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("1000.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Depósito en efectivo")
                .build();
            
            // Act
            Optional<TransaccionDTO> resultado = transaccionMapeador.aDto(Optional.of(transaccion));
            
            // Assert
            assertTrue(resultado.isPresent());
            TransaccionDTO dto = resultado.get();
            
            assertEquals(1L, dto.getId());
            assertEquals(12345L, dto.getNumeroCuenta());
            assertNull(dto.getNumeroCuentaDestino());
            assertEquals(TipoTransaccion.DEPOSITO, dto.getTipo());
            assertEquals(new BigDecimal("1000.00"), dto.getMonto());
            assertEquals("Depósito en efectivo", dto.getDescripcion());
        }
        
        @Test
        @DisplayName("Debería retornar Optional.empty() para transacción nula")
        void deberiaRetornarEmptyParaTransaccionNula() {
            // Act
            Optional<TransaccionDTO> resultado = transaccionMapeador.aDto(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería convertir lista de transacciones correctamente")
        void deberiaConvertirListaDeTransacciones() {
            // Arrange
            Transaccion transaccion1 = Transaccion.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(67890L)
                .tipo(TipoTransaccion.TRANSFERENCIA_ENVIADA)
                .monto(new BigDecimal("500.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Transferencia 1")
                .build();
                
            Transaccion transaccion2 = Transaccion.builder()
                .id(2L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.RETIRO)
                .monto(new BigDecimal("200.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Retiro en cajero")
                .build();
            
            List<Transaccion> transacciones = Arrays.asList(transaccion1, transaccion2);
            
            // Act
            Optional<List<TransaccionDTO>> resultado = transaccionMapeador.aListaDto(transacciones);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<TransaccionDTO> dtos = resultado.get();
            
            assertEquals(2, dtos.size());
            assertEquals(1L, dtos.get(0).getId());
            assertEquals(2L, dtos.get(1).getId());
            assertEquals(TipoTransaccion.TRANSFERENCIA_ENVIADA, dtos.get(0).getTipo());
            assertEquals(TipoTransaccion.RETIRO, dtos.get(1).getTipo());
        }
        
        @Test
        @DisplayName("Debería manejar transacción con descripción nula")
        void deberiaManejarTransaccionConDescripcionNula() {
            // Arrange
            Transaccion transaccion = Transaccion.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("1000.00"))
                .fecha(LocalDateTime.now())
                .descripcion(null)
                .build();
            
            // Act
            Optional<TransaccionDTO> resultado = transaccionMapeador.aDto(Optional.of(transaccion));
            
            // Assert
            assertTrue(resultado.isPresent());
            TransaccionDTO dto = resultado.get();
            
            assertEquals(1L, dto.getId());
            assertEquals(12345L, dto.getNumeroCuenta());
            assertEquals(TipoTransaccion.DEPOSITO, dto.getTipo());
            assertEquals(new BigDecimal("1000.00"), dto.getMonto());
            assertNull(dto.getDescripcion());
        }
    }

    @Nested
    @DisplayName("DTO to Entity Tests")
    class DtoToEntityTests {
        
        @Test
        @DisplayName("Debería convertir TransaccionDTO a Transaccion correctamente")
        void deberiaConvertirDtoATransaccion() {
            // Arrange
            TransaccionDTO dto = TransaccionDTO.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(67890L)
                .tipo(TipoTransaccion.TRANSFERENCIA_ENVIADA)
                .monto(new BigDecimal("500.00"))
                .fecha(LocalDateTime.of(2024, 1, 1, 10, 0))
                .descripcion("Transferencia a cuenta destino")
                .build();
            
            // Act
            Optional<Transaccion> resultado = transaccionMapeador.aEntidad(Optional.of(dto));
            
            // Assert
            assertTrue(resultado.isPresent());
            Transaccion transaccion = resultado.get();
            
            assertEquals(1L, transaccion.getId());
            assertEquals(12345L, transaccion.getNumeroCuenta());
            assertEquals(67890L, transaccion.getNumeroCuentaDestino());
            assertEquals(TipoTransaccion.TRANSFERENCIA_ENVIADA, transaccion.getTipo());
            assertEquals(new BigDecimal("500.00"), transaccion.getMonto());
            assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), transaccion.getFecha());
            assertEquals("Transferencia a cuenta destino", transaccion.getDescripcion());
        }
        
        @Test
        @DisplayName("Debería manejar DTO con cuenta destino nula")
        void deberiaManejarDtoConCuentaDestinoNula() {
            // Arrange
            TransaccionDTO dto = TransaccionDTO.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("1000.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Depósito en efectivo")
                .build();
            
            // Act
            Optional<Transaccion> resultado = transaccionMapeador.aEntidad(Optional.of(dto));
            
            // Assert
            assertTrue(resultado.isPresent());
            Transaccion transaccion = resultado.get();
            
            assertEquals(1L, transaccion.getId());
            assertEquals(12345L, transaccion.getNumeroCuenta());
            assertNull(transaccion.getNumeroCuentaDestino());
            assertEquals(TipoTransaccion.DEPOSITO, transaccion.getTipo());
            assertEquals(new BigDecimal("1000.00"), transaccion.getMonto());
            assertEquals("Depósito en efectivo", transaccion.getDescripcion());
        }
        
        @Test
        @DisplayName("Debería retornar Optional.empty() para DTO nulo")
        void deberiaRetornarEmptyParaDtoNulo() {
            // Act
            Optional<Transaccion> resultado = transaccionMapeador.aEntidad(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería convertir lista de DTOs correctamente")
        void deberiaConvertirListaDeDtos() {
            // Arrange
            TransaccionDTO dto1 = TransaccionDTO.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(67890L)
                .tipo(TipoTransaccion.TRANSFERENCIA_ENVIADA)
                .monto(new BigDecimal("500.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Transferencia 1")
                .build();
                
            TransaccionDTO dto2 = TransaccionDTO.builder()
                .id(2L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.RETIRO)
                .monto(new BigDecimal("200.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Retiro en cajero")
                .build();
            
            List<TransaccionDTO> dtos = Arrays.asList(dto1, dto2);
            
            // Act
            Optional<List<Transaccion>> resultado = transaccionMapeador.aListaEntidad(dtos);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Transaccion> transacciones = resultado.get();
            
            assertEquals(2, transacciones.size());
            assertEquals(1L, transacciones.get(0).getId());
            assertEquals(2L, transacciones.get(1).getId());
            assertEquals(TipoTransaccion.TRANSFERENCIA_ENVIADA, transacciones.get(0).getTipo());
            assertEquals(TipoTransaccion.RETIRO, transacciones.get(1).getTipo());
        }
        
        @Test
        @DisplayName("Debería manejar DTO con descripción nula")
        void deberiaManejarDtoConDescripcionNula() {
            // Arrange
            TransaccionDTO dto = TransaccionDTO.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("1000.00"))
                .fecha(LocalDateTime.now())
                .descripcion(null)
                .build();
            
            // Act
            Optional<Transaccion> resultado = transaccionMapeador.aEntidad(Optional.of(dto));
            
            // Assert
            assertTrue(resultado.isPresent());
            Transaccion transaccion = resultado.get();
            
            assertEquals(1L, transaccion.getId());
            assertEquals(12345L, transaccion.getNumeroCuenta());
            assertEquals(TipoTransaccion.DEPOSITO, transaccion.getTipo());
            assertEquals(new BigDecimal("1000.00"), transaccion.getMonto());
            assertNull(transaccion.getDescripcion());
        }
    }

    @Nested
    @DisplayName("Direct Method Tests")
    class DirectMethodTests {
        
        @Test
        @DisplayName("aDtoDirecto debería funcionar correctamente")
        void aDtoDirectoDeberiaFuncionar() {
            // Arrange
            Transaccion transaccion = Transaccion.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("1000.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Depósito")
                .build();
            
            // Act
            Optional<TransaccionDTO> resultado = transaccionMapeador.aDtoDirecto(transaccion);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(1L, resultado.get().getId());
            assertEquals(12345L, resultado.get().getNumeroCuenta());
            assertEquals(TipoTransaccion.DEPOSITO, resultado.get().getTipo());
        }
        
        @Test
        @DisplayName("aEntidadDirecta debería funcionar correctamente")
        void aEntidadDirectaDeberiaFuncionar() {
            // Arrange
            TransaccionDTO dto = TransaccionDTO.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("1000.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Depósito")
                .build();
            
            // Act
            Optional<Transaccion> resultado = transaccionMapeador.aEntidadDirecta(dto);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(1L, resultado.get().getId());
            assertEquals(12345L, resultado.get().getNumeroCuenta());
            assertEquals(TipoTransaccion.DEPOSITO, resultado.get().getTipo());
        }
        
        @Test
        @DisplayName("aDtoDirecto debería manejar transacción nula")
        void aDtoDirectoDeberiaManejarTransaccionNula() {
            // Act
            Optional<TransaccionDTO> resultado = transaccionMapeador.aDtoDirecto(null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("aEntidadDirecta debería manejar DTO nulo")
        void aEntidadDirectaDeberiaManejarDtoNulo() {
            // Act
            Optional<Transaccion> resultado = transaccionMapeador.aEntidadDirecta(null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }

    @Nested
    @DisplayName("Edge Cases Tests")
    class EdgeCasesTests {
        
        @Test
        @DisplayName("Debería manejar transacción con monto cero")
        void deberiaManejarTransaccionConMontoCero() {
            // Arrange
            Transaccion transaccion = Transaccion.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(BigDecimal.ZERO)
                .fecha(LocalDateTime.now())
                .descripcion("Depósito cero")
                .build();
            
            // Act
            Optional<TransaccionDTO> resultado = transaccionMapeador.aDto(Optional.of(transaccion));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(BigDecimal.ZERO, resultado.get().getMonto());
        }
        
        @Test
        @DisplayName("Debería manejar transacción con monto negativo")
        void deberiaManejarTransaccionConMontoNegativo() {
            // Arrange
            Transaccion transaccion = Transaccion.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.RETIRO)
                .monto(new BigDecimal("-500.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Retiro")
                .build();
            
            // Act
            Optional<TransaccionDTO> resultado = transaccionMapeador.aDto(Optional.of(transaccion));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(new BigDecimal("-500.00"), resultado.get().getMonto());
        }
        
        @Test
        @DisplayName("Debería manejar transacción con descripción vacía")
        void deberiaManejarTransaccionConDescripcionVacia() {
            // Arrange
            Transaccion transaccion = Transaccion.builder()
                .id(1L)
                .numeroCuenta(12345L)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("1000.00"))
                .fecha(LocalDateTime.now())
                .descripcion("")
                .build();
            
            // Act
            Optional<TransaccionDTO> resultado = transaccionMapeador.aDto(Optional.of(transaccion));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals("", resultado.get().getDescripcion());
        }
        
        @Test
        @DisplayName("Debería manejar lista vacía de transacciones")
        void deberiaManejarListaVaciaDeTransacciones() {
            // Arrange
            List<Transaccion> transacciones = Arrays.asList();
            
            // Act
            Optional<List<TransaccionDTO>> resultado = transaccionMapeador.aListaDto(transacciones);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get().isEmpty());
        }
        
        @Test
        @DisplayName("Debería manejar lista nula de transacciones")
        void deberiaManejarListaNulaDeTransacciones() {
            // Act
            Optional<List<TransaccionDTO>> resultado = transaccionMapeador.aListaDto((List<Transaccion>) null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
}
