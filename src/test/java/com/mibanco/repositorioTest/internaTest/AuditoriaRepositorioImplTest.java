package com.mibanco.repositorioTest.internaTest;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.RegistroAuditoria;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.repositorio.interna.AuditoriaRepositorioImplWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests específicos para AuditoriaRepositorioImpl
 * Valida la funcionalidad específica del repositorio de auditoría
 * Incluye tests de registro, búsquedas específicas y casos edge
 */
@DisplayName("AuditoriaRepositorioImpl Tests")
class AuditoriaRepositorioImplTest {
    
    private AuditoriaRepositorioImplWrapper repositorio;
    private RegistroAuditoria<Cliente, TipoOperacionCliente> registro1;
    private RegistroAuditoria<Cliente, TipoOperacionCliente> registro2;
    private Cliente cliente1;
    private Cliente cliente2;
    
    @BeforeEach
    void setUp() {
        // Crear datos de prueba
        cliente1 = Cliente.builder()
            .id(1L)
            .nombre("Juan")
            .apellido("Pérez")
            .dni("12345678")
            .email("juan@test.com")
            .telefono("123456789")
            .direccion("Calle Test 123")
            .fechaNacimiento(LocalDate.of(1990, 1, 1))
            .build();
            
        cliente2 = Cliente.builder()
            .id(2L)
            .nombre("María")
            .apellido("García")
            .dni("87654321")
            .email("maria@test.com")
            .telefono("987654321")
            .direccion("Avenida Test 456")
            .fechaNacimiento(LocalDate.of(1985, 5, 15))
            .build();
        
        // Crear registros de auditoría de prueba
        registro1 = RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
            .id(UUID.randomUUID())
            .tipoOperacion(TipoOperacionCliente.CREAR)
            .fechaHora(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
            .entidad(cliente1)
            .usuario("admin")
            .monto(null)
            .detalles("Creación de cliente Juan Pérez")
            .build();
            
        registro2 = RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
            .id(UUID.randomUUID())
            .tipoOperacion(TipoOperacionCliente.ACTUALIZAR)
            .fechaHora(LocalDateTime.of(2024, 1, 16, 14, 45, 0))
            .entidad(cliente2)
            .usuario("operador")
            .monto(1000.0)
            .detalles("Actualización de datos de María García")
            .build();
        
        // Crear repositorio
        repositorio = new AuditoriaRepositorioImplWrapper();
    }
    
    @Nested
    @DisplayName("Tests de operaciones básicas")
    class OperacionesBasicasTests {
        
        @Test
        @DisplayName("Debería registrar un nuevo registro de auditoría")
        void deberiaRegistrarNuevoRegistro() {
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.registrar(Optional.of(registro1));
            
            // Assert
            assertTrue(resultado.isPresent());
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroRegistrado = resultado.get();
            assertEquals(registro1.getId(), registroRegistrado.getId());
            assertEquals(TipoOperacionCliente.CREAR, registroRegistrado.getTipoOperacion());
            assertEquals("admin", registroRegistrado.getUsuario());
            assertEquals(cliente1, registroRegistrado.getEntidad());
        }
        
        @Test
        @DisplayName("Debería manejar registro con Optional.empty()")
        void deberiaManejarRegistroConOptionalEmpty() {
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.registrar(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería buscar registro por ID")
        void deberiaBuscarRegistroPorId() {
            // Arrange
            repositorio.registrar(Optional.of(registro1));
            
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.buscarPorId(Optional.of(registro1.getId()));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(registro1.getId(), resultado.get().getId());
            assertEquals(TipoOperacionCliente.CREAR, resultado.get().getTipoOperacion());
        }
        
        @Test
        @DisplayName("Debería retornar Optional.empty() cuando ID no existe")
        void deberiaRetornarEmptyCuandoIdNoExiste() {
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.buscarPorId(Optional.of(UUID.randomUUID()));
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por ID con Optional.empty()")
        void deberiaManejarBusquedaPorIdConOptionalEmpty() {
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.buscarPorId(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Tests de búsquedas específicas")
    class BusquedasEspecificasTests {
        
        @BeforeEach
        void setUp() {
            // Crear registros de prueba
            repositorio.registrar(Optional.of(registro1));
            repositorio.registrar(Optional.of(registro2));
        }
        
        @Test
        @DisplayName("Debería obtener historial por tipo de entidad e ID")
        void deberiaObtenerHistorialPorTipoEntidadEId() {
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.obtenerHistorial(Optional.of(Cliente.class), Optional.of(1L));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> historial = resultado.get();
            assertEquals(1, historial.size());
            assertEquals(cliente1.getId(), historial.get(0).getEntidad().getId());
        }
        
        @Test
        @DisplayName("Debería manejar historial con tipo de entidad null")
        void deberiaManejarHistorialConTipoEntidadNull() {
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.obtenerHistorial(Optional.empty(), Optional.of(1L));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> historial = resultado.get();
            assertEquals(0, historial.size());
        }
        
        @Test
        @DisplayName("Debería manejar historial con ID de entidad null")
        void deberiaManejarHistorialConIdEntidadNull() {
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.obtenerHistorial(Optional.of(Cliente.class), Optional.empty());
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> historial = resultado.get();
            assertEquals(0, historial.size());
        }
        
        @Test
        @DisplayName("Debería buscar registros por rango de fechas")
        void deberiaBuscarRegistrosPorRangoFechas() {
            // Act
            LocalDateTime desde = LocalDateTime.of(2024, 1, 15, 0, 0, 0);
            LocalDateTime hasta = LocalDateTime.of(2024, 1, 16, 23, 59, 59);
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorFechas(Optional.of(desde), Optional.of(hasta));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(2, registros.size());
            assertTrue(registros.stream().allMatch(r -> 
                !r.getFechaHora().isBefore(desde) && !r.getFechaHora().isAfter(hasta)
            ));
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por fechas con fecha desde null")
        void deberiaManejarBusquedaPorFechasConFechaDesdeNull() {
            // Act
            LocalDateTime hasta = LocalDateTime.of(2024, 1, 16, 23, 59, 59);
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorFechas(Optional.empty(), Optional.of(hasta));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(0, registros.size());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por fechas con fecha hasta null")
        void deberiaManejarBusquedaPorFechasConFechaHastaNull() {
            // Act
            LocalDateTime desde = LocalDateTime.of(2024, 1, 15, 0, 0, 0);
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorFechas(Optional.of(desde), Optional.empty());
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(0, registros.size());
        }
        
        @Test
        @DisplayName("Debería buscar registros por usuario")
        void deberiaBuscarRegistrosPorUsuario() {
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorUsuario(Optional.of("admin"));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(1, registros.size());
            assertEquals("admin", registros.get(0).getUsuario());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por usuario inexistente")
        void deberiaManejarBusquedaPorUsuarioInexistente() {
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorUsuario(Optional.of("usuario_inexistente"));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(0, registros.size());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por usuario con Optional.empty()")
        void deberiaManejarBusquedaPorUsuarioConOptionalEmpty() {
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorUsuario(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería buscar registros por tipo de operación")
        void deberiaBuscarRegistrosPorTipoOperacion() {
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorTipoOperacion(Optional.of(TipoOperacionCliente.CREAR), Optional.of(TipoOperacionCliente.class));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(1, registros.size());
            assertEquals(TipoOperacionCliente.CREAR, registros.get(0).getTipoOperacion());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por tipo de operación con tipo null")
        void deberiaManejarBusquedaPorTipoOperacionConTipoNull() {
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorTipoOperacion(Optional.empty(), Optional.of(TipoOperacionCliente.class));
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por tipo de operación con tipo enum null")
        void deberiaManejarBusquedaPorTipoOperacionConTipoEnumNull() {
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorTipoOperacion(Optional.of(TipoOperacionCliente.CREAR), Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
    
    @Nested
    @DisplayName("Tests de casos edge específicos")
    class CasosEdgeTests {
        
        @Test
        @DisplayName("Debería manejar registro con campos null")
        void deberiaManejarRegistroConCamposNull() {
            // Arrange
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroConNulls = RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
                .id(UUID.randomUUID())
                .tipoOperacion(null) // Campo null válido
                .fechaHora(LocalDateTime.now())
                .entidad(cliente1)
                .usuario(null) // Campo null válido
                .monto(null) // Campo null válido
                .detalles(null) // Campo null válido
                .build();
            
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.registrar(Optional.of(registroConNulls));
            
            // Assert
            assertTrue(resultado.isPresent());
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroCreado = resultado.get();
            assertNull(registroCreado.getTipoOperacion());
            assertNull(registroCreado.getUsuario());
            assertNull(registroCreado.getMonto());
            assertNull(registroCreado.getDetalles());
            assertEquals(cliente1, registroCreado.getEntidad());
        }
        
        @Test
        @DisplayName("Debería manejar registro con monto cero")
        void deberiaManejarRegistroConMontoCero() {
            // Arrange
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroMontoCero = RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
                .id(UUID.randomUUID())
                .tipoOperacion(TipoOperacionCliente.CREAR)
                .fechaHora(LocalDateTime.now())
                .entidad(cliente1)
                .usuario("admin")
                .monto(0.0)
                .detalles("Registro con monto cero")
                .build();
            
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.registrar(Optional.of(registroMontoCero));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(0.0, resultado.get().getMonto());
        }
        
        @Test
        @DisplayName("Debería manejar registro con monto negativo")
        void deberiaManejarRegistroConMontoNegativo() {
            // Arrange
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroMontoNegativo = RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
                .id(UUID.randomUUID())
                .tipoOperacion(TipoOperacionCliente.ACTUALIZAR)
                .fechaHora(LocalDateTime.now())
                .entidad(cliente1)
                .usuario("admin")
                .monto(-100.0)
                .detalles("Registro con monto negativo")
                .build();
            
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.registrar(Optional.of(registroMontoNegativo));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(-100.0, resultado.get().getMonto());
        }
        
        @Test
        @DisplayName("Debería manejar registro con fecha futura")
        void deberiaManejarRegistroConFechaFutura() {
            // Arrange
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroFechaFutura = RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
                .id(UUID.randomUUID())
                .tipoOperacion(TipoOperacionCliente.CREAR)
                .fechaHora(LocalDateTime.now().plusDays(30))
                .entidad(cliente1)
                .usuario("admin")
                .monto(100.0)
                .detalles("Registro con fecha futura")
                .build();
            
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.registrar(Optional.of(registroFechaFutura));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get().getFechaHora().isAfter(LocalDateTime.now()));
        }
        
        @Test
        @DisplayName("Debería manejar registro con usuario muy largo")
        void deberiaManejarRegistroConUsuarioMuyLargo() {
            // Arrange
            String usuarioLargo = "usuario_muy_largo_" + "a".repeat(100);
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroUsuarioLargo = RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
                .id(UUID.randomUUID())
                .tipoOperacion(TipoOperacionCliente.CREAR)
                .fechaHora(LocalDateTime.now())
                .entidad(cliente1)
                .usuario(usuarioLargo)
                .monto(100.0)
                .detalles("Registro con usuario muy largo")
                .build();
            
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.registrar(Optional.of(registroUsuarioLargo));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(usuarioLargo, resultado.get().getUsuario());
        }
        
        @Test
        @DisplayName("Debería manejar múltiples registros del mismo usuario")
        void deberiaManejarMultiplesRegistrosDelMismoUsuario() {
            // Arrange
            RegistroAuditoria<Cliente, TipoOperacionCliente> registro3 = RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
                .id(UUID.randomUUID())
                .tipoOperacion(TipoOperacionCliente.ELIMINAR)
                .fechaHora(LocalDateTime.now())
                .entidad(cliente1)
                .usuario("admin") // Mismo usuario que registro1
                .monto(500.0)
                .detalles("Eliminación de cliente")
                .build();
            
            // Act
            repositorio.registrar(Optional.of(registro1));
            repositorio.registrar(Optional.of(registro3));
            
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorUsuario(Optional.of("admin"));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(2, registros.size());
            assertTrue(registros.stream().allMatch(r -> "admin".equals(r.getUsuario())));
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por rango de fechas con fechas invertidas")
        void deberiaManejarBusquedaPorRangoFechasConFechasInvertidas() {
            // Arrange
            repositorio.registrar(Optional.of(registro1));
            
            // Act
            LocalDateTime desde = LocalDateTime.of(2024, 1, 16, 0, 0, 0); // Fecha posterior
            LocalDateTime hasta = LocalDateTime.of(2024, 1, 15, 23, 59, 59); // Fecha anterior
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorFechas(Optional.of(desde), Optional.of(hasta));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(0, registros.size()); // No hay registros en un rango inválido
        }
    }
    
    @Nested
    @DisplayName("Tests de cobertura de filtros específicos")
    class CoberturaFiltrosTests {
        
        @Test
        @DisplayName("Debería filtrar obtenerHistorial por tipo de entidad correcto")
        void deberiaFiltrarObtenerHistorialPorTipoEntidadCorrecto() {
            // Arrange
            repositorio.registrar(Optional.of(registro1));
            
            // Act - Buscar con tipo de entidad correcto
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.obtenerHistorial(Optional.of(Cliente.class), Optional.of(1L));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> historial = resultado.get();
            assertEquals(1, historial.size());
            assertTrue(historial.get(0).getEntidad() instanceof Cliente);
            assertEquals(1L, historial.get(0).getEntidad().getId());
        }
        
        @Test
        @DisplayName("Debería filtrar obtenerHistorial excluyendo entidades de tipo incorrecto")
        void deberiaFiltrarObtenerHistorialExcluyendoEntidadesDeTipoIncorrecto() {
            // Arrange - Crear registro con entidad de tipo diferente
            com.mibanco.modelo.Cuenta cuenta = com.mibanco.modelo.Cuenta.builder()
                .numeroCuenta("ES3412345678901234567890")
                .titular(cliente1)
                .tipo(com.mibanco.modelo.enums.TipoCuenta.CORRIENTE)
                .fechaCreacion(LocalDateTime.now())
                .saldoInicial(new java.math.BigDecimal("1000.00"))
                .saldo(new java.math.BigDecimal("1000.00"))
                .activa(true)
                .build();
                
            RegistroAuditoria<com.mibanco.modelo.Cuenta, TipoOperacionCliente> registroCuenta = 
                RegistroAuditoria.<com.mibanco.modelo.Cuenta, TipoOperacionCliente>builder()
                    .id(UUID.randomUUID())
                    .tipoOperacion(TipoOperacionCliente.CREAR)
                    .fechaHora(LocalDateTime.now())
                    .entidad(cuenta)
                    .usuario("admin")
                    .build();
            
            repositorio.registrar(Optional.of(registro1)); // Cliente
            repositorio.registrar(Optional.of(registroCuenta)); // Cuenta
            
            // Act - Buscar solo clientes
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.obtenerHistorial(Optional.of(Cliente.class), Optional.of(1L));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> historial = resultado.get();
            assertEquals(1, historial.size());
            assertTrue(historial.get(0).getEntidad() instanceof Cliente);
            // La cuenta no debe aparecer en el resultado
        }
        
        @Test
        @DisplayName("Debería filtrar obtenerHistorial excluyendo entidades con ID diferente")
        void deberiaFiltrarObtenerHistorialExcluyendoEntidadesConIdDiferente() {
            // Arrange
            repositorio.registrar(Optional.of(registro1)); // Cliente con ID 1
            repositorio.registrar(Optional.of(registro2)); // Cliente con ID 2
            
            // Act - Buscar solo cliente con ID 1
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.obtenerHistorial(Optional.of(Cliente.class), Optional.of(1L));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> historial = resultado.get();
            assertEquals(1, historial.size());
            assertEquals(1L, historial.get(0).getEntidad().getId());
            // El cliente con ID 2 no debe aparecer
        }
        
        @Test
        @DisplayName("Debería filtrar obtenerHistorial con entidad null")
        void deberiaFiltrarObtenerHistorialConEntidadNull() {
            // Arrange - Crear registro con entidad null
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroConEntidadNull = 
                RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
                    .id(UUID.randomUUID())
                    .tipoOperacion(TipoOperacionCliente.CREAR)
                    .fechaHora(LocalDateTime.now())
                    .entidad(null) // Entidad null
                    .usuario("admin")
                    .build();
            
            repositorio.registrar(Optional.of(registroConEntidadNull));
            
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.obtenerHistorial(Optional.of(Cliente.class), Optional.of(1L));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> historial = resultado.get();
            assertEquals(0, historial.size()); // No debe encontrar registros con entidad null
        }
        
        @Test
        @DisplayName("Debería filtrar buscarPorFechas con fecha exacta")
        void deberiaFiltrarBuscarPorFechasConFechaExacta() {
            // Arrange
            repositorio.registrar(Optional.of(registro1));
            
            // Act - Buscar con fecha exacta
            LocalDateTime fechaExacta = registro1.getFechaHora();
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorFechas(Optional.of(fechaExacta), Optional.of(fechaExacta));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(1, registros.size());
            assertEquals(fechaExacta, registros.get(0).getFechaHora());
        }
        
        @Test
        @DisplayName("Debería filtrar buscarPorFechas excluyendo fechas anteriores")
        void deberiaFiltrarBuscarPorFechasExcluyendoFechasAnteriores() {
            // Arrange
            repositorio.registrar(Optional.of(registro1)); // Fecha: 2024-01-15 10:30:00
            repositorio.registrar(Optional.of(registro2)); // Fecha: 2024-01-16 14:45:00
            
            // Act - Buscar desde fecha posterior al primer registro
            LocalDateTime desde = LocalDateTime.of(2024, 1, 15, 12, 0, 0); // Después del registro1
            LocalDateTime hasta = LocalDateTime.of(2024, 1, 17, 0, 0, 0);
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorFechas(Optional.of(desde), Optional.of(hasta));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(1, registros.size());
            assertEquals(registro2.getFechaHora(), registros.get(0).getFechaHora());
            // El registro1 no debe aparecer porque es anterior a 'desde'
        }
        
        @Test
        @DisplayName("Debería filtrar buscarPorFechas excluyendo fechas posteriores")
        void deberiaFiltrarBuscarPorFechasExcluyendoFechasPosteriores() {
            // Arrange
            repositorio.registrar(Optional.of(registro1)); // Fecha: 2024-01-15 10:30:00
            repositorio.registrar(Optional.of(registro2)); // Fecha: 2024-01-16 14:45:00
            
            // Act - Buscar hasta fecha anterior al segundo registro
            LocalDateTime desde = LocalDateTime.of(2024, 1, 15, 0, 0, 0);
            LocalDateTime hasta = LocalDateTime.of(2024, 1, 16, 12, 0, 0); // Antes del registro2
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorFechas(Optional.of(desde), Optional.of(hasta));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(1, registros.size());
            assertEquals(registro1.getFechaHora(), registros.get(0).getFechaHora());
            // El registro2 no debe aparecer porque es posterior a 'hasta'
        }
        
        @Test
        @DisplayName("Debería manejar buscarPorFechas con fechaHora null (solución implementada)")
        void deberiaManejarBuscarPorFechasConFechaHoraNull() {
            // Arrange - Crear registro con fechaHora null
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroConFechaNull = 
                RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
                    .id(UUID.randomUUID())
                    .tipoOperacion(TipoOperacionCliente.CREAR)
                    .fechaHora(null) // Fecha null
                    .entidad(cliente1)
                    .usuario("admin")
                    .build();
            
            repositorio.registrar(Optional.of(registroConFechaNull));
            
            // Act - Ahora el código debería manejar correctamente el null
            LocalDateTime desde = LocalDateTime.of(2024, 1, 15, 0, 0, 0);
            LocalDateTime hasta = LocalDateTime.of(2024, 1, 16, 23, 59, 59);
            
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorFechas(Optional.of(desde), Optional.of(hasta));
            
            // Assert - Debería retornar una lista vacía (ignora registros con fecha null)
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(0, registros.size()); // No debe encontrar registros con fecha null
        }
        
        @Test
        @DisplayName("Debería filtrar buscarPorFechas con fechas en los límites")
        void deberiaFiltrarBuscarPorFechasConFechasEnLosLimites() {
            // Arrange
            repositorio.registrar(Optional.of(registro1)); // Fecha: 2024-01-15 10:30:00
            repositorio.registrar(Optional.of(registro2)); // Fecha: 2024-01-16 14:45:00
            
            // Act - Buscar con fechas exactas en los límites
            LocalDateTime desde = registro1.getFechaHora(); // Límite inferior exacto
            LocalDateTime hasta = registro2.getFechaHora(); // Límite superior exacto
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorFechas(Optional.of(desde), Optional.of(hasta));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(2, registros.size()); // Ambos registros deben aparecer
            assertTrue(registros.stream().anyMatch(r -> r.getFechaHora().equals(registro1.getFechaHora())));
            assertTrue(registros.stream().anyMatch(r -> r.getFechaHora().equals(registro2.getFechaHora())));
        }
        
        @Test
        @DisplayName("Debería manejar buscarPorFechas con mezcla de registros válidos y null")
        void deberiaManejarBuscarPorFechasConMezclaDeRegistros() {
            // Arrange - Crear registro con fechaHora null
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroConFechaNull = 
                RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
                    .id(UUID.randomUUID())
                    .tipoOperacion(TipoOperacionCliente.ELIMINAR)
                    .fechaHora(null) // Fecha null
                    .entidad(cliente1)
                    .usuario("admin")
                    .build();
            
            // Registrar registros: uno válido, uno con fecha null, otro válido
            repositorio.registrar(Optional.of(registro1)); // Fecha válida: 2024-01-15 10:30:00
            repositorio.registrar(Optional.of(registroConFechaNull)); // Fecha null
            repositorio.registrar(Optional.of(registro2)); // Fecha válida: 2024-01-16 14:45:00
            
            // Act - Buscar en un rango que incluye ambos registros válidos
            LocalDateTime desde = LocalDateTime.of(2024, 1, 15, 0, 0, 0);
            LocalDateTime hasta = LocalDateTime.of(2024, 1, 17, 0, 0, 0);
            
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorFechas(Optional.of(desde), Optional.of(hasta));
            
            // Assert - Debería encontrar solo los registros con fechas válidas
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(2, registros.size()); // Solo los 2 registros con fechas válidas
            
            // Verificar que están los registros correctos
            assertTrue(registros.stream().anyMatch(r -> r.getId().equals(registro1.getId())));
            assertTrue(registros.stream().anyMatch(r -> r.getId().equals(registro2.getId())));
            
            // Verificar que NO está el registro con fecha null
            assertFalse(registros.stream().anyMatch(r -> r.getId().equals(registroConFechaNull.getId())));
        }
    }
    
    @Nested
    @DisplayName("Tests de integración y casos especiales")
    class IntegracionYEspecialesTests {
        
        @Test
        @DisplayName("Debería mantener múltiples registros en memoria")
        void deberiaMantenerMultiplesRegistrosEnMemoria() {
            // Arrange
            RegistroAuditoria<Cliente, TipoOperacionCliente> registro3 = RegistroAuditoria.<Cliente, TipoOperacionCliente>builder()
                .id(UUID.randomUUID())
                .tipoOperacion(TipoOperacionCliente.ELIMINAR)
                .fechaHora(LocalDateTime.now())
                .entidad(cliente1)
                .usuario("supervisor")
                .monto(750.0)
                .detalles("Eliminación definitiva")
                .build();
            
            // Act
            repositorio.registrar(Optional.of(registro1));
            repositorio.registrar(Optional.of(registro2));
            repositorio.registrar(Optional.of(registro3));
            
            // Verificar que todos los registros están disponibles
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado1 = 
                repositorio.buscarPorId(Optional.of(registro1.getId()));
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado2 = 
                repositorio.buscarPorId(Optional.of(registro2.getId()));
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado3 = 
                repositorio.buscarPorId(Optional.of(registro3.getId()));
            
            // Assert
            assertTrue(resultado1.isPresent());
            assertTrue(resultado2.isPresent());
            assertTrue(resultado3.isPresent());
            assertEquals(TipoOperacionCliente.CREAR, resultado1.get().getTipoOperacion());
            assertEquals(TipoOperacionCliente.ACTUALIZAR, resultado2.get().getTipoOperacion());
            assertEquals(TipoOperacionCliente.ELIMINAR, resultado3.get().getTipoOperacion());
        }
        
        @Test
        @DisplayName("Debería usar métodos factory de RegistroAuditoria")
        void deberiaUsarMetodosFactoryDeRegistroAuditoria() {
            // Arrange - Usar método factory básico
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroFactory = 
                RegistroAuditoria.of(
                    Optional.of(TipoOperacionCliente.CREAR),
                    Optional.of(cliente1),
                    Optional.of("admin")
                );
            
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.registrar(Optional.of(registroFactory));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertNotNull(resultado.get().getId());
            assertEquals(TipoOperacionCliente.CREAR, resultado.get().getTipoOperacion());
            assertEquals(cliente1, resultado.get().getEntidad());
            assertEquals("admin", resultado.get().getUsuario());
        }
        
        @Test
        @DisplayName("Debería usar método factory detallado de RegistroAuditoria")
        void deberiaUsarMetodoFactoryDetalladoDeRegistroAuditoria() {
            // Arrange - Usar método factory detallado
            RegistroAuditoria<Cliente, TipoOperacionCliente> registroDetallado = 
                RegistroAuditoria.ofDetallado(
                    Optional.of(TipoOperacionCliente.ACTUALIZAR),
                    Optional.of(cliente2),
                    Optional.of("operador"),
                    Optional.of(1500.0),
                    Optional.of("Actualización completa de datos")
                );
            
            // Act
            Optional<RegistroAuditoria<Cliente, TipoOperacionCliente>> resultado = 
                repositorio.registrar(Optional.of(registroDetallado));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertNotNull(resultado.get().getId());
            assertEquals(TipoOperacionCliente.ACTUALIZAR, resultado.get().getTipoOperacion());
            assertEquals(cliente2, resultado.get().getEntidad());
            assertEquals("operador", resultado.get().getUsuario());
            assertEquals(1500.0, resultado.get().getMonto());
            assertEquals("Actualización completa de datos", resultado.get().getDetalles());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por historial con entidad inexistente")
        void deberiaManejarBusquedaPorHistorialConEntidadInexistente() {
            // Arrange
            repositorio.registrar(Optional.of(registro1));
            
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.obtenerHistorial(Optional.of(Cliente.class), Optional.of(999L)); // ID inexistente
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> historial = resultado.get();
            assertEquals(0, historial.size());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por tipo de operación inexistente")
        void deberiaManejarBusquedaPorTipoOperacionInexistente() {
            // Arrange
            repositorio.registrar(Optional.of(registro1));
            
            // Act
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorTipoOperacion(Optional.of(TipoOperacionCliente.ELIMINAR), Optional.of(TipoOperacionCliente.class));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(0, registros.size());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por fechas exactas")
        void deberiaManejarBusquedaPorFechasExactas() {
            // Arrange
            repositorio.registrar(Optional.of(registro1));
            
            // Act
            LocalDateTime fechaExacta = registro1.getFechaHora();
            Optional<List<RegistroAuditoria<Cliente, TipoOperacionCliente>>> resultado = 
                repositorio.buscarPorFechas(Optional.of(fechaExacta), Optional.of(fechaExacta));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<RegistroAuditoria<Cliente, TipoOperacionCliente>> registros = resultado.get();
            assertEquals(1, registros.size());
            assertEquals(fechaExacta, registros.get(0).getFechaHora());
        }
    }
}
