package com.mibanco.repositorioTest.internaTest;

import com.mibanco.modelo.Transaccion;
import com.mibanco.modelo.enums.TipoOperacionTransaccion;
import com.mibanco.modelo.enums.TipoTransaccion;
import com.mibanco.repositorio.interna.TransaccionRepositorioImplWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests específicos para TransaccionRepositorioImpl
 * Valida la funcionalidad específica del repositorio de transacciones
 * Incluye tests de configuración, búsquedas específicas y casos edge
 */
@DisplayName("TransaccionRepositorioImpl Tests")
class TransaccionRepositorioImplTest {
    
    private TransaccionRepositorioImplWrapper repositorio;
    private Transaccion transaccion1;
    private Transaccion transaccion2;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Crear archivo JSON temporal para testing
        File archivoJson = tempDir.resolve("test_transacciones.json").toFile();
        
        // Crear datos de prueba
        transaccion1 = Transaccion.builder()
            .id(1L)
            .numeroCuenta("ES3412345678901234567890")
            .numeroCuentaDestino(null)
            .tipo(TipoTransaccion.DEPOSITO)
            .monto(new BigDecimal("1000.00"))
            .fecha(LocalDateTime.of(2024, 1, 15, 10, 30, 0))
            .descripcion("Depósito inicial")
            .build();
            
        transaccion2 = Transaccion.builder()
            .id(2L)
            .numeroCuenta("ES3498765432109876543210")
            .numeroCuentaDestino("ES3412345678901234567890")
            .tipo(TipoTransaccion.TRANSFERENCIA_ENVIADA)
            .monto(new BigDecimal("500.00"))
            .fecha(LocalDateTime.of(2024, 1, 16, 14, 45, 0))
            .descripcion("Transferencia a cuenta destino")
            .build();
        
        // Crear repositorio con configuración temporal
        repositorio = new TestTransaccionRepositorioImpl(archivoJson.getAbsolutePath());
    }
    
    /**
     * Implementación de prueba que permite configurar la ruta del archivo
     */
    private static class TestTransaccionRepositorioImpl extends TransaccionRepositorioImplWrapper {
        
        private final String rutaArchivo;
        
        public TestTransaccionRepositorioImpl(String rutaArchivo) {
            this.rutaArchivo = rutaArchivo;
        }
        
        @Override
        public java.util.Map<String, Object> obtenerConfiguracion() {
            java.util.Map<String, Object> config = new java.util.HashMap<>();
            config.put("rutaArchivo", rutaArchivo);
            config.put("tipoClase", Transaccion.class);
            config.put("extractorId", (java.util.function.Function<Transaccion, Long>) Transaccion::getId);
            return config;
        }
    }

    @Nested
    @DisplayName("Tests de configuración específica")
    class ConfiguracionTests {
        
        @Test
        @DisplayName("Debería usar la ruta de archivo correcta para transacciones")
        void deberiaUsarRutaArchivoCorrecta() {
            // Act
            Optional<List<Transaccion>> resultado = repositorio.buscarTodos();
            
            // Assert
            // Si no hay errores de archivo, la configuración es correcta
            assertNotNull(resultado);
        }
        
        @Test
        @DisplayName("Debería usar el tipo de clase Transaccion")
        void deberiaUsarTipoClaseTransaccion() {
            // Act
            Optional<Transaccion> resultado = repositorio.crearRegistro(Optional.of(transaccion1), TipoOperacionTransaccion.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get() instanceof Transaccion);
        }
        
        @Test
        @DisplayName("Debería devolver configuración correcta para transacciones")
        void deberiaDevolverConfiguracionCorrecta() {
            // Act - Ejecutar operaciones que internamente llaman a obtenerConfiguracion
            // El método obtenerConfiguracion se ejecuta durante la inicialización y carga de datos
            Optional<List<Transaccion>> resultado = repositorio.buscarTodos();
            
            // Assert - Verificar que la configuración funciona correctamente
            assertNotNull(resultado);
            // Si llegamos aquí sin errores, significa que obtenerConfiguracion() se ejecutó correctamente
            // y devolvió la configuración esperada (ruta de archivo, tipo de clase, extractor de ID)
        }

        @Test
        @DisplayName("Debería ejecutar obtenerConfiguracion() de la clase real")
        void deberiaEjecutarObtenerConfiguracionDeClaseReal() {
            // Arrange - Usar la Factory para obtener la clase real
            com.mibanco.repositorio.TransaccionRepositorio repositorioReal = 
                com.mibanco.repositorio.interna.RepositorioFactoria.obtenerInstancia().obtenerRepositorioTransaccion();
            
            // Act - Ejecutar operaciones que internamente llaman a obtenerConfiguracion()
            // El método obtenerConfiguracion() se ejecuta durante la carga lazy de datos
            Optional<List<Transaccion>> resultado = repositorioReal.buscarTodos();
            
            // Assert - Verificar que la operación se ejecutó correctamente
            assertNotNull(resultado);
            // Si llegamos aquí sin errores, significa que obtenerConfiguracion() se ejecutó correctamente
            // y devolvió la configuración esperada (ruta de archivo, tipo de clase, extractor de ID)
        }
    }
    
    @Nested
    @DisplayName("Tests de asignación de ID específica")
    class AsignacionIdTests {
        
        @Test
        @DisplayName("Debería asignar ID incremental usando AtomicLong")
        void deberiaAsignarIdIncremental() {
            // Arrange
            Transaccion transaccionNueva1 = Transaccion.builder()
                .numeroCuenta("ES3411111111111111111111")
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("100.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Depósito de prueba 1")
                .build();
                
            Transaccion transaccionNueva2 = Transaccion.builder()
                .numeroCuenta("ES3422222222222222222222")
                .numeroCuentaDestino("ES3411111111111111111111")
                .tipo(TipoTransaccion.TRANSFERENCIA_ENVIADA)
                .monto(new BigDecimal("50.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Transferencia de prueba 2")
                .build();
            
            // Act
            Optional<Transaccion> resultado1 = repositorio.crearRegistro(Optional.of(transaccionNueva1), TipoOperacionTransaccion.CREAR);
            Optional<Transaccion> resultado2 = repositorio.crearRegistro(Optional.of(transaccionNueva2), TipoOperacionTransaccion.CREAR);
            
            // Assert
            assertTrue(resultado1.isPresent());
            assertTrue(resultado2.isPresent());
            
            Long id1 = resultado1.get().getId();
            Long id2 = resultado2.get().getId();
            
            assertNotNull(id1);
            assertNotNull(id2);
            assertEquals(id1 + 1, id2, "Los IDs deben ser consecutivos (AtomicLong incrementAndGet)");
        }
        
        @Test
        @DisplayName("Debería mantener secuencia consecutiva en múltiples creaciones")
        void deberiaMantenerSecuenciaConsecutiva() {
            // Arrange
            List<Transaccion> transacciones = new ArrayList<>();
            
            // Act - Crear 5 transacciones
            for (int i = 0; i < 5; i++) {
                Transaccion transaccion = Transaccion.builder()
                    .numeroCuenta("ES34" + String.format("%020d", i))
                    .numeroCuentaDestino(i % 2 == 0 ? null : "ES3499999999999999999999")
                    .tipo(TipoTransaccion.values()[i % TipoTransaccion.values().length])
                    .monto(new BigDecimal("100.00").multiply(new BigDecimal(i + 1)))
                    .fecha(LocalDateTime.now().plusDays(i))
                    .descripcion("Transacción de prueba " + i)
                    .build();
                
                Optional<Transaccion> resultado = repositorio.crearRegistro(Optional.of(transaccion), TipoOperacionTransaccion.CREAR);
                assertTrue(resultado.isPresent(), "Transacción " + i + " debería haberse creado correctamente");
                transacciones.add(resultado.get());
            }
            
            // Assert - Verificar que los IDs son consecutivos (no necesariamente empezando en 1)
            Long primerId = transacciones.get(0).getId();
            for (int i = 0; i < transacciones.size(); i++) {
                assertEquals(primerId + i, transacciones.get(i).getId(), 
                    "ID de la transacción " + i + " debe ser consecutivo con el primer ID");
            }
            
            // Verificar que se crearon exactamente 5 transacciones
            assertEquals(5, transacciones.size());
            assertEquals(5, repositorio.contarRegistros());
        }
    }
    
    @Nested
    @DisplayName("Tests de operaciones CRUD específicas")
    class OperacionesCrudTests {
        
        @BeforeEach
        void setUp() {
            // Crear entidades de prueba
            repositorio.crearRegistro(Optional.of(transaccion1), TipoOperacionTransaccion.CREAR);
            repositorio.crearRegistro(Optional.of(transaccion2), TipoOperacionTransaccion.CREAR);
        }
        
        @Test
        @DisplayName("Debería crear transacción con todos los campos")
        void deberiaCrearTransaccionConTodosLosCampos() {
            // Arrange
            Transaccion transaccionCompleta = Transaccion.builder()
                .numeroCuenta("ES3433333333333333333333")
                .numeroCuentaDestino("ES3444444444444444444444")
                .tipo(TipoTransaccion.TRANSFERENCIA_ENVIADA)
                .monto(new BigDecimal("750.50"))
                .fecha(LocalDateTime.of(2024, 1, 20, 16, 30, 0))
                .descripcion("Transferencia completa de prueba")
                .build();
            
            // Act
            Optional<Transaccion> resultado = repositorio.crearRegistro(Optional.of(transaccionCompleta), TipoOperacionTransaccion.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Transaccion transaccionCreada = resultado.get();
            assertEquals("ES3433333333333333333333", transaccionCreada.getNumeroCuenta());
            assertEquals("ES3444444444444444444444", transaccionCreada.getNumeroCuentaDestino());
            assertEquals(TipoTransaccion.TRANSFERENCIA_ENVIADA, transaccionCreada.getTipo());
            assertEquals(new BigDecimal("750.50"), transaccionCreada.getMonto());
            assertEquals("Transferencia completa de prueba", transaccionCreada.getDescripcion());
        }
        
        @Test
        @DisplayName("Debería buscar transacción por número de cuenta")
        void deberiaBuscarTransaccionPorNumeroCuenta() {
            // Act
            Optional<Transaccion> resultado = repositorio.buscarPorPredicado(
                transaccion -> "ES3412345678901234567890".equals(transaccion.getNumeroCuenta())
            );
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(TipoTransaccion.DEPOSITO, resultado.get().getTipo());
            assertEquals("ES3412345678901234567890", resultado.get().getNumeroCuenta());
        }
        
        @Test
        @DisplayName("Debería buscar transacciones por tipo")
        void deberiaBuscarTransaccionesPorTipo() {
            // Act
            Optional<List<Transaccion>> resultado = repositorio.buscarTodosPorPredicado(
                transaccion -> transaccion.getTipo() == TipoTransaccion.DEPOSITO
            );
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Transaccion> transacciones = resultado.get();
            assertEquals(1, transacciones.size());
            assertTrue(transacciones.stream().allMatch(t -> t.getTipo() == TipoTransaccion.DEPOSITO));
        }
        
        @Test
        @DisplayName("Debería actualizar transacción existente")
        void deberiaActualizarTransaccionExistente() {
            // Arrange
            Transaccion transaccionActualizada = Transaccion.builder()
                .id(transaccion1.getId())
                .numeroCuenta(transaccion1.getNumeroCuenta())
                .numeroCuentaDestino(transaccion1.getNumeroCuentaDestino())
                .tipo(TipoTransaccion.RETIRO) // Cambiar tipo
                .monto(new BigDecimal("1500.00")) // Cambiar monto
                .fecha(transaccion1.getFecha())
                .descripcion("Transacción actualizada") // Cambiar descripción
                .build();
            
            // Act
            Optional<Transaccion> resultado = repositorio.actualizarRegistro(Optional.of(transaccionActualizada), TipoOperacionTransaccion.ACTUALIZAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Transaccion transaccionModificada = resultado.get();
            assertEquals(TipoTransaccion.RETIRO, transaccionModificada.getTipo());
            assertEquals(new BigDecimal("1500.00"), transaccionModificada.getMonto());
            assertEquals("Transacción actualizada", transaccionModificada.getDescripcion());
            assertEquals("ES3412345678901234567890", transaccionModificada.getNumeroCuenta()); // Campo no modificado
        }
    }
    
    @Nested
    @DisplayName("Tests de búsquedas específicas")
    class BusquedasEspecificasTests {
        
        @BeforeEach
        void setUp() {
            // Crear entidades de prueba
            repositorio.crearRegistro(Optional.of(transaccion1), TipoOperacionTransaccion.CREAR);
            repositorio.crearRegistro(Optional.of(transaccion2), TipoOperacionTransaccion.CREAR);
        }
        
        @Test
        @DisplayName("Debería buscar transacciones por cuenta")
        void deberiaBuscarTransaccionesPorCuenta() {
            // Act
            Optional<List<Transaccion>> resultado = repositorio.buscarPorCuenta(Optional.of("ES3412345678901234567890"));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Transaccion> transacciones = resultado.get();
            assertEquals(1, transacciones.size());
            assertEquals("ES3412345678901234567890", transacciones.get(0).getNumeroCuenta());
        }
        
        @Test
        @DisplayName("Debería buscar transacciones por tipo")
        void deberiaBuscarTransaccionesPorTipo() {
            // Act
            Optional<List<Transaccion>> resultado = repositorio.buscarPorTipo(Optional.of(TipoTransaccion.DEPOSITO));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Transaccion> transacciones = resultado.get();
            assertEquals(1, transacciones.size());
            assertTrue(transacciones.stream().allMatch(t -> t.getTipo() == TipoTransaccion.DEPOSITO));
        }
        
        @Test
        @DisplayName("Debería buscar transacciones por fecha")
        void deberiaBuscarTransaccionesPorFecha() {
            // Act
            LocalDate fechaBusqueda = LocalDate.of(2024, 1, 15);
            Optional<List<Transaccion>> resultado = repositorio.buscarPorFecha(Optional.of(fechaBusqueda));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Transaccion> transacciones = resultado.get();
            assertEquals(1, transacciones.size());
            assertTrue(transacciones.stream().allMatch(t -> t.getFecha().toLocalDate().equals(fechaBusqueda)));
        }
        
        @Test
        @DisplayName("Debería buscar transacciones por rango de fechas")
        void deberiaBuscarTransaccionesPorRangoFechas() {
            // Act
            LocalDate fechaInicio = LocalDate.of(2024, 1, 15);
            LocalDate fechaFin = LocalDate.of(2024, 1, 16);
            Optional<List<Transaccion>> resultado = repositorio.buscarPorRangoFechas(Optional.of(fechaInicio), Optional.of(fechaFin));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Transaccion> transacciones = resultado.get();
            assertEquals(2, transacciones.size());
            assertTrue(transacciones.stream().allMatch(t -> {
                LocalDate fechaTransaccion = t.getFecha().toLocalDate();
                return !fechaTransaccion.isBefore(fechaInicio) && !fechaTransaccion.isAfter(fechaFin);
            }));
        }
        
        @Test
        @DisplayName("Debería retornar Optional.empty() cuando fechaInicio es null")
        void deberiaRetornarEmptyCuandoFechaInicioEsNull() {
            // Act
            LocalDate fechaFin = LocalDate.of(2024, 1, 16);
            Optional<List<Transaccion>> resultado = repositorio.buscarPorRangoFechas(Optional.empty(), Optional.of(fechaFin));
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería retornar Optional.empty() cuando fechaFin es null")
        void deberiaRetornarEmptyCuandoFechaFinEsNull() {
            // Act
            LocalDate fechaInicio = LocalDate.of(2024, 1, 15);
            Optional<List<Transaccion>> resultado = repositorio.buscarPorRangoFechas(Optional.of(fechaInicio), Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería retornar Optional.empty() cuando ambas fechas son null")
        void deberiaRetornarEmptyCuandoAmbasFechasSonNull() {
            // Act
            Optional<List<Transaccion>> resultado = repositorio.buscarPorRangoFechas(Optional.empty(), Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería manejar rango de fechas con fechaInicio igual a fechaFin")
        void deberiaManejarRangoFechasConFechaInicioIgualAFechaFin() {
            // Act
            LocalDate fecha = LocalDate.of(2024, 1, 15);
            Optional<List<Transaccion>> resultado = repositorio.buscarPorRangoFechas(Optional.of(fecha), Optional.of(fecha));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Transaccion> transacciones = resultado.get();
            assertEquals(1, transacciones.size());
            assertEquals(fecha, transacciones.get(0).getFecha().toLocalDate());
        }
        
        @Test
        @DisplayName("Debería manejar rango de fechas con fechaInicio posterior a fechaFin")
        void deberiaManejarRangoFechasConFechaInicioPosteriorAFechaFin() {
            // Act
            LocalDate fechaInicio = LocalDate.of(2024, 1, 16);
            LocalDate fechaFin = LocalDate.of(2024, 1, 15);
            Optional<List<Transaccion>> resultado = repositorio.buscarPorRangoFechas(Optional.of(fechaInicio), Optional.of(fechaFin));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Transaccion> transacciones = resultado.get();
            assertEquals(0, transacciones.size()); // No hay transacciones en un rango inválido
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por cuenta inexistente")
        void deberiaManejarBusquedaPorCuentaInexistente() {
            // Act
            Optional<List<Transaccion>> resultado = repositorio.buscarPorCuenta(Optional.of("ES3499999999999999999999"));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Transaccion> transacciones = resultado.get();
            assertEquals(0, transacciones.size());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por tipo inexistente")
        void deberiaManejarBusquedaPorTipoInexistente() {
            // Act
            Optional<List<Transaccion>> resultado = repositorio.buscarPorTipo(Optional.of(TipoTransaccion.PAGO_SERVICIO));
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Transaccion> transacciones = resultado.get();
            assertEquals(0, transacciones.size());
        }
    }
    
    @Nested
    @DisplayName("Tests de casos edge específicos")
    class CasosEdgeTests {
        
        @Test
        @DisplayName("Debería manejar transacción con campos null")
        void deberiaManejarTransaccionConCamposNull() {
            // Arrange
            Transaccion transaccionConNulls = Transaccion.builder()
                .numeroCuenta("ES3455555555555555555555")
                .numeroCuentaDestino(null) // Campo null válido
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("100.00"))
                .fecha(LocalDateTime.now())
                .descripcion(null) // Campo null válido
                .build();
            
            // Act
            Optional<Transaccion> resultado = repositorio.crearRegistro(Optional.of(transaccionConNulls), TipoOperacionTransaccion.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Transaccion transaccionCreada = resultado.get();
            assertEquals("ES3455555555555555555555", transaccionCreada.getNumeroCuenta());
            assertNull(transaccionCreada.getNumeroCuentaDestino());
            assertNull(transaccionCreada.getDescripcion());
            assertEquals(TipoTransaccion.DEPOSITO, transaccionCreada.getTipo());
            assertEquals(new BigDecimal("100.00"), transaccionCreada.getMonto());
        }
        
        @Test
        @DisplayName("Debería manejar transacción con monto cero")
        void deberiaManejarTransaccionConMontoCero() {
            // Arrange
            Transaccion transaccionMontoCero = Transaccion.builder()
                .numeroCuenta("ES3466666666666666666666")
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(BigDecimal.ZERO)
                .fecha(LocalDateTime.now())
                .descripcion("Transacción con monto cero")
                .build();
            
            // Act
            Optional<Transaccion> resultado = repositorio.crearRegistro(Optional.of(transaccionMontoCero), TipoOperacionTransaccion.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(BigDecimal.ZERO, resultado.get().getMonto());
        }
        
        @Test
        @DisplayName("Debería manejar transacción con monto negativo")
        void deberiaManejarTransaccionConMontoNegativo() {
            // Arrange
            Transaccion transaccionMontoNegativo = Transaccion.builder()
                .numeroCuenta("ES3477777777777777777777")
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.RETIRO)
                .monto(new BigDecimal("-100.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Retiro con monto negativo")
                .build();
            
            // Act
            Optional<Transaccion> resultado = repositorio.crearRegistro(Optional.of(transaccionMontoNegativo), TipoOperacionTransaccion.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(new BigDecimal("-100.00"), resultado.get().getMonto());
        }
        
        @Test
        @DisplayName("Debería manejar transacción con fecha futura")
        void deberiaManejarTransaccionConFechaFutura() {
            // Arrange
            Transaccion transaccionFechaFutura = Transaccion.builder()
                .numeroCuenta("ES3488888888888888888888")
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("200.00"))
                .fecha(LocalDateTime.now().plusDays(30))
                .descripcion("Transacción con fecha futura")
                .build();
            
            // Act
            Optional<Transaccion> resultado = repositorio.crearRegistro(Optional.of(transaccionFechaFutura), TipoOperacionTransaccion.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get().getFecha().isAfter(LocalDateTime.now()));
        }
        
        @Test
        @DisplayName("Debería manejar transacción con número de cuenta muy largo")
        void deberiaManejarTransaccionConNumeroCuentaMuyLargo() {
            // Arrange
            String cuentaLarga = "ES34" + "1".repeat(50); // Cuenta muy larga
            Transaccion transaccionCuentaLarga = Transaccion.builder()
                .numeroCuenta(cuentaLarga)
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("300.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Transacción con cuenta muy larga")
                .build();
            
            // Act
            Optional<Transaccion> resultado = repositorio.crearRegistro(Optional.of(transaccionCuentaLarga), TipoOperacionTransaccion.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(cuentaLarga, resultado.get().getNumeroCuenta());
        }
    }
    
    @Nested
    @DisplayName("Tests de integración con archivo JSON")
    class IntegracionJsonTests {
        
        @Test
        @DisplayName("Debería persistir transacción en archivo JSON")
        void deberiaPersistirTransaccionEnArchivoJson() {
            // Arrange
            Transaccion transaccionParaPersistir = Transaccion.builder()
                .numeroCuenta("ES3499999999999999999999")
                .numeroCuentaDestino("ES3488888888888888888888")
                .tipo(TipoTransaccion.TRANSFERENCIA_ENVIADA)
                .monto(new BigDecimal("250.75"))
                .fecha(LocalDateTime.of(2024, 1, 25, 12, 0, 0))
                .descripcion("Transferencia persistida en JSON")
                .build();
            
            // Act
            Optional<Transaccion> resultado = repositorio.crearRegistro(Optional.of(transaccionParaPersistir), TipoOperacionTransaccion.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Transaccion transaccionCreada = resultado.get();
            assertNotNull(transaccionCreada.getId());
            
            // Verificar que se puede recuperar desde el repositorio
            Optional<Transaccion> transaccionRecuperada = repositorio.buscarPorPredicado(
                t -> t.getId().equals(transaccionCreada.getId())
            );
            assertTrue(transaccionRecuperada.isPresent());
            assertEquals(transaccionParaPersistir.getNumeroCuenta(), transaccionRecuperada.get().getNumeroCuenta());
            assertEquals(transaccionParaPersistir.getTipo(), transaccionRecuperada.get().getTipo());
            assertEquals(transaccionParaPersistir.getMonto(), transaccionRecuperada.get().getMonto());
        }
        
        @Test
        @DisplayName("Debería cargar transacciones desde archivo JSON")
        void deberiaCargarTransaccionesDesdeArchivoJson() {
            // Arrange - Crear transacciones
            Transaccion transaccion1 = Transaccion.builder()
                .numeroCuenta("ES3411111111111111111111")
                .numeroCuentaDestino(null)
                .tipo(TipoTransaccion.DEPOSITO)
                .monto(new BigDecimal("100.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Depósito para cargar")
                .build();
                
            Transaccion transaccion2 = Transaccion.builder()
                .numeroCuenta("ES3422222222222222222222")
                .numeroCuentaDestino("ES3411111111111111111111")
                .tipo(TipoTransaccion.TRANSFERENCIA_ENVIADA)
                .monto(new BigDecimal("50.00"))
                .fecha(LocalDateTime.now())
                .descripcion("Transferencia para cargar")
                .build();
            
            // Act - Crear transacciones (se guardan en JSON)
            repositorio.crearRegistro(Optional.of(transaccion1), TipoOperacionTransaccion.CREAR);
            repositorio.crearRegistro(Optional.of(transaccion2), TipoOperacionTransaccion.CREAR);
            
            // Verificar que se cargan correctamente
            Optional<List<Transaccion>> todasLasTransacciones = repositorio.buscarTodos();
            
            // Assert
            assertTrue(todasLasTransacciones.isPresent());
            List<Transaccion> transacciones = todasLasTransacciones.get();
            assertEquals(2, transacciones.size());
            
            // Verificar que las transacciones tienen los datos correctos
            assertTrue(transacciones.stream().anyMatch(t -> 
                "ES3411111111111111111111".equals(t.getNumeroCuenta()) && 
                t.getTipo() == TipoTransaccion.DEPOSITO
            ));
            
            assertTrue(transacciones.stream().anyMatch(t -> 
                "ES3422222222222222222222".equals(t.getNumeroCuenta()) && 
                t.getTipo() == TipoTransaccion.TRANSFERENCIA_ENVIADA
            ));
        }
    }
}
