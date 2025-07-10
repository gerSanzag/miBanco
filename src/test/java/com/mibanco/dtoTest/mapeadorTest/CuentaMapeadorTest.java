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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.mapeador.CuentaMapeador;
import com.mibanco.dto.mapeador.Mapeador;
import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.enums.TipoCuenta;

/**
 * Tests para CuentaMapeador siguiendo la nueva arquitectura
 * donde los mappers son puros conversores sin generación de IDs
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CuentaMapeador Tests")
class CuentaMapeadorTest {

    @Mock
    private Mapeador<Cliente, ClienteDTO> clienteMapeador;
    
    private CuentaMapeador cuentaMapeador;
    
    @BeforeEach
    void setUp() {
        cuentaMapeador = new CuentaMapeador(clienteMapeador);
    }

    @Nested
    @DisplayName("Entity to DTO Tests")
    class EntityToDtoTests {
        
        @Test
        @DisplayName("Debería convertir Cuenta a CuentaDTO correctamente")
        void deberiaConvertirCuentaADto() {
            // Arrange
            Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .email("juan@email.com")
                .build();
                
            Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(12345L)
                .titular(cliente)
                .tipo(TipoCuenta.CORRIENTE)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1500.00"))
                .fechaCreacion(LocalDateTime.of(2024, 1, 1, 10, 0))
                .activa(true)
                .build();
                
            ClienteDTO clienteDTO = ClienteDTO.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .email("juan@email.com")
                .build();
                
            when(clienteMapeador.aDto(Optional.of(cliente)))
                .thenReturn(Optional.of(clienteDTO));
            
            // Act
            Optional<CuentaDTO> resultado = cuentaMapeador.aDto(Optional.of(cuenta));
            
            // Assert
            assertTrue(resultado.isPresent());
            CuentaDTO dto = resultado.get();
            
            assertEquals(12345L, dto.getNumeroCuenta());
            assertEquals(clienteDTO, dto.getTitular());
            assertEquals(TipoCuenta.CORRIENTE, dto.getTipo());
            assertEquals(new BigDecimal("1000.00"), dto.getSaldoInicial());
            assertEquals(new BigDecimal("1500.00"), dto.getSaldo());
            assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), dto.getFechaCreacion());
            assertTrue(dto.isActiva());
            
            verify(clienteMapeador).aDto(Optional.of(cliente));
        }
        
        @Test
        @DisplayName("Debería manejar cuenta con titular nulo")
        void deberiaManejarCuentaConTitularNulo() {
            // Arrange
            Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(12345L)
                .titular(null)
                .tipo(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("500.00"))
                .saldo(new BigDecimal("500.00"))
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .build();
            
            // Act
            Optional<CuentaDTO> resultado = cuentaMapeador.aDto(Optional.of(cuenta));
            
            // Assert
            assertTrue(resultado.isPresent());
            CuentaDTO dto = resultado.get();
            
            assertEquals(12345L, dto.getNumeroCuenta());
            assertNull(dto.getTitular());
            assertEquals(TipoCuenta.AHORRO, dto.getTipo());
            assertTrue(dto.isActiva());
        }
        
        @Test
        @DisplayName("Debería retornar Optional.empty() para cuenta nula")
        void deberiaRetornarEmptyParaCuentaNula() {
            // Act
            Optional<CuentaDTO> resultado = cuentaMapeador.aDto(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería convertir lista de cuentas correctamente")
        void deberiaConvertirListaDeCuentas() {
            // Arrange
            Cliente cliente1 = Cliente.builder().id(1L).nombre("Cliente 1").build();
            Cliente cliente2 = Cliente.builder().id(2L).nombre("Cliente 2").build();
            
            Cuenta cuenta1 = Cuenta.builder()
                .numeroCuenta(1L)
                .titular(cliente1)
                .tipo(TipoCuenta.CORRIENTE)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1000.00"))
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .build();
                
            Cuenta cuenta2 = Cuenta.builder()
                .numeroCuenta(2L)
                .titular(cliente2)
                .tipo(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("500.00"))
                .saldo(new BigDecimal("500.00"))
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .build();
                
            ClienteDTO clienteDTO1 = ClienteDTO.builder().id(1L).nombre("Cliente 1").build();
            ClienteDTO clienteDTO2 = ClienteDTO.builder().id(2L).nombre("Cliente 2").build();
            
            when(clienteMapeador.aDto(Optional.of(cliente1)))
                .thenReturn(Optional.of(clienteDTO1));
            when(clienteMapeador.aDto(Optional.of(cliente2)))
                .thenReturn(Optional.of(clienteDTO2));
            
            List<Cuenta> cuentas = Arrays.asList(cuenta1, cuenta2);
            
            // Act
            Optional<List<CuentaDTO>> resultado = cuentaMapeador.aListaDto(cuentas);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<CuentaDTO> dtos = resultado.get();
            
            assertEquals(2, dtos.size());
            assertEquals(1L, dtos.get(0).getNumeroCuenta());
            assertEquals(2L, dtos.get(1).getNumeroCuenta());
        }
    }

    @Nested
    @DisplayName("DTO to Entity Tests")
    class DtoToEntityTests {
        
        @Test
        @DisplayName("Debería convertir CuentaDTO a Cuenta correctamente")
        void deberiaConvertirDtoACuenta() {
            // Arrange
            ClienteDTO clienteDTO = ClienteDTO.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .email("juan@email.com")
                .build();
                
            CuentaDTO dto = CuentaDTO.builder()
                .numeroCuenta(12345L)
                .titular(clienteDTO)
                .tipo(TipoCuenta.CORRIENTE)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1500.00"))
                .fechaCreacion(LocalDateTime.of(2024, 1, 1, 10, 0))
                .activa(true)
                .build();
                
            Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .email("juan@email.com")
                .build();
                
            when(clienteMapeador.aEntidad(Optional.of(clienteDTO)))
                .thenReturn(Optional.of(cliente));
            
            // Act
            Optional<Cuenta> resultado = cuentaMapeador.aEntidad(Optional.of(dto));
            
            // Assert
            assertTrue(resultado.isPresent());
            Cuenta cuenta = resultado.get();
            
            assertEquals(12345L, cuenta.getNumeroCuenta());
            assertEquals(cliente, cuenta.getTitular());
            assertEquals(TipoCuenta.CORRIENTE, cuenta.getTipo());
            assertEquals(new BigDecimal("1000.00"), cuenta.getSaldoInicial());
            assertEquals(new BigDecimal("1500.00"), cuenta.getSaldo());
            assertEquals(LocalDateTime.of(2024, 1, 1, 10, 0), cuenta.getFechaCreacion());
            assertTrue(cuenta.isActiva());
            
            verify(clienteMapeador).aEntidad(Optional.of(clienteDTO));
        }
        
        @Test
        @DisplayName("Debería manejar DTO con titular nulo")
        void deberiaManejarDtoConTitularNulo() {
            // Arrange
            CuentaDTO dto = CuentaDTO.builder()
                .numeroCuenta(12345L)
                .titular(null)
                .tipo(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("500.00"))
                .saldo(new BigDecimal("500.00"))
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .build();
            
            // Act
            Optional<Cuenta> resultado = cuentaMapeador.aEntidad(Optional.of(dto));
            
            // Assert
            assertTrue(resultado.isPresent());
            Cuenta cuenta = resultado.get();
            
            assertEquals(12345L, cuenta.getNumeroCuenta());
            assertNull(cuenta.getTitular());
            assertEquals(TipoCuenta.AHORRO, cuenta.getTipo());
            assertTrue(cuenta.isActiva());
        }
        
        @Test
        @DisplayName("Debería retornar Optional.empty() para DTO nulo")
        void deberiaRetornarEmptyParaDtoNulo() {
            // Act
            Optional<Cuenta> resultado = cuentaMapeador.aEntidad(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería convertir lista de DTOs correctamente")
        void deberiaConvertirListaDeDtos() {
            // Arrange
            ClienteDTO clienteDTO1 = ClienteDTO.builder().id(1L).nombre("Cliente 1").build();
            ClienteDTO clienteDTO2 = ClienteDTO.builder().id(2L).nombre("Cliente 2").build();
            
            CuentaDTO dto1 = CuentaDTO.builder()
                .numeroCuenta(1L)
                .titular(clienteDTO1)
                .tipo(TipoCuenta.CORRIENTE)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1000.00"))
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .build();
                
            CuentaDTO dto2 = CuentaDTO.builder()
                .numeroCuenta(2L)
                .titular(clienteDTO2)
                .tipo(TipoCuenta.AHORRO)
                .saldoInicial(new BigDecimal("500.00"))
                .saldo(new BigDecimal("500.00"))
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .build();
                
            Cliente cliente1 = Cliente.builder().id(1L).nombre("Cliente 1").build();
            Cliente cliente2 = Cliente.builder().id(2L).nombre("Cliente 2").build();
            
            when(clienteMapeador.aEntidad(Optional.of(clienteDTO1)))
                .thenReturn(Optional.of(cliente1));
            when(clienteMapeador.aEntidad(Optional.of(clienteDTO2)))
                .thenReturn(Optional.of(cliente2));
            
            List<CuentaDTO> dtos = Arrays.asList(dto1, dto2);
            
            // Act
            Optional<List<Cuenta>> resultado = cuentaMapeador.aListaEntidad(dtos);
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Cuenta> cuentas = resultado.get();
            
            assertEquals(2, cuentas.size());
            assertEquals(1L, cuentas.get(0).getNumeroCuenta());
            assertEquals(2L, cuentas.get(1).getNumeroCuenta());
        }
    }

    @Nested
    @DisplayName("Direct Method Tests")
    class DirectMethodTests {
        
        @Test
        @DisplayName("aDtoDirecto debería funcionar correctamente")
        void aDtoDirectoDeberiaFuncionar() {
            // Arrange
            Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(12345L)
                .titular(null)
                .tipo(TipoCuenta.CORRIENTE)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1000.00"))
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .build();
            
            // Act
            Optional<CuentaDTO> resultado = cuentaMapeador.aDtoDirecto(cuenta);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(12345L, resultado.get().getNumeroCuenta());
        }
        
        @Test
        @DisplayName("aEntidadDirecta debería funcionar correctamente")
        void aEntidadDirectaDeberiaFuncionar() {
            // Arrange
            CuentaDTO dto = CuentaDTO.builder()
                .numeroCuenta(12345L)
                .titular(null)
                .tipo(TipoCuenta.CORRIENTE)
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1000.00"))
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .build();
            
            // Act
            Optional<Cuenta> resultado = cuentaMapeador.aEntidadDirecta(dto);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(12345L, resultado.get().getNumeroCuenta());
        }
        
        @Test
        @DisplayName("aDtoDirecto debería manejar cuenta nula")
        void aDtoDirectoDeberiaManejarCuentaNula() {
            // Act
            Optional<CuentaDTO> resultado = cuentaMapeador.aDtoDirecto(null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("aEntidadDirecta debería manejar DTO nulo")
        void aEntidadDirectaDeberiaManejarDtoNulo() {
            // Act
            Optional<Cuenta> resultado = cuentaMapeador.aEntidadDirecta(null);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
}
