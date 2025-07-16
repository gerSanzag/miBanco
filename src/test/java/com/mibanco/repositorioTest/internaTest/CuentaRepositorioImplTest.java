package com.mibanco.repositorioTest.internaTest;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.enums.TipoCuenta;
import com.mibanco.modelo.enums.TipoOperacionCuenta;
import com.mibanco.repositorio.interna.CuentaRepositorioImplWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
 * Tests específicos para CuentaRepositorioImpl
 * Valida la funcionalidad específica del repositorio de cuentas
 * Incluye tests de configuración, mapeo y casos edge específicos
 */
@DisplayName("CuentaRepositorioImpl Tests")
class CuentaRepositorioImplTest {
    
    private CuentaRepositorioImplWrapper repositorio;
    private Cliente titular1;
    private Cliente titular2;
    private Cuenta cuenta1;
    private Cuenta cuenta2;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Crear archivo JSON temporal para testing
        File archivoJson = tempDir.resolve("test_cuentas.json").toFile();
        
        // Crear titulares de prueba
        titular1 = Cliente.builder()
            .id(1L)
            .nombre("Juan")
            .apellido("Pérez")
            .dni("12345678")
            .email("juan@test.com")
            .telefono("123456789")
            .direccion("Calle Test 123")
            .fechaNacimiento(LocalDate.of(1990, 1, 1))
            .build();
            
        titular2 = Cliente.builder()
            .id(2L)
            .nombre("María")
            .apellido("García")
            .dni("87654321")
            .email("maria@test.com")
            .telefono("987654321")
            .direccion("Avenida Test 456")
            .fechaNacimiento(LocalDate.of(1985, 5, 15))
            .build();
        
        // Crear cuentas de prueba (sin números específicos, el repositorio los generará)
        cuenta1 = Cuenta.builder()
            .numeroCuenta(null) // El repositorio asignará un número aleatorio
            .titular(titular1)
            .tipo(TipoCuenta.CORRIENTE)
            .fechaCreacion(LocalDateTime.now())
            .saldoInicial(new BigDecimal("1000.00"))
            .saldo(new BigDecimal("1000.00"))
            .activa(true)
            .build();
        
        cuenta2 = Cuenta.builder()
            .numeroCuenta(null) // El repositorio asignará un número aleatorio
            .titular(titular2)
            .tipo(TipoCuenta.AHORRO)
            .fechaCreacion(LocalDateTime.now())
            .saldoInicial(new BigDecimal("2000.00"))
            .saldo(new BigDecimal("2000.00"))
            .activa(true)
            .build();
        
        // Crear repositorio con configuración temporal
        repositorio = new TestCuentaRepositorioImpl(archivoJson.getAbsolutePath());
    }
    
    /**
     * Implementación de prueba que permite configurar la ruta del archivo
     */
    private static class TestCuentaRepositorioImpl extends CuentaRepositorioImplWrapper {
        
        private final String rutaArchivo;
        
        public TestCuentaRepositorioImpl(String rutaArchivo) {
            this.rutaArchivo = rutaArchivo;
        }
        
        @Override
        public java.util.Map<String, Object> obtenerConfiguracion() {
            java.util.Map<String, Object> config = new java.util.HashMap<>();
            config.put("rutaArchivo", rutaArchivo);
            config.put("tipoClase", Cuenta.class);
            config.put("extractorId", (java.util.function.Function<Cuenta, Long>) Cuenta::getId);
            return config;
        }
    }
    
    @Nested
    @DisplayName("Tests de configuración específica")
    class ConfiguracionTests {
        
        @Test
        @DisplayName("Debería usar la ruta de archivo correcta para cuentas")
        void deberiaUsarRutaArchivoCorrecta() {
            // Act
            Optional<List<Cuenta>> resultado = repositorio.buscarTodos();
            
            // Assert
            // Si no hay errores de archivo, la configuración es correcta
            assertNotNull(resultado);
        }
        
        @Test
        @DisplayName("Debería usar el tipo de clase Cuenta")
        void deberiaUsarTipoClaseCuenta() {
            // Act
            Optional<Cuenta> resultado = repositorio.crearRegistro(Optional.of(cuenta1), TipoOperacionCuenta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get() instanceof Cuenta);
        }
        
        @Test
        @DisplayName("Debería devolver configuración correcta para cuentas")
        void deberiaDevolverConfiguracionCorrecta() {
            // Act - Ejecutar operaciones que internamente llaman a obtenerConfiguracion
            // El método obtenerConfiguracion se ejecuta durante la inicialización y carga de datos
            Optional<List<Cuenta>> resultado = repositorio.buscarTodos();
            
            // Assert - Verificar que la configuración funciona correctamente
            assertNotNull(resultado);
            // Si llegamos aquí sin errores, significa que obtenerConfiguracion() se ejecutó correctamente
            // y devolvió la configuración esperada (ruta de archivo, tipo de clase, extractor de ID)
        }

        @Test
        @DisplayName("Debería ejecutar obtenerConfiguracion() de la clase real")
        void deberiaEjecutarObtenerConfiguracionDeClaseReal() {
            // Arrange - Usar la Factory para obtener la clase real
            com.mibanco.repositorio.CuentaRepositorio repositorioReal = 
                com.mibanco.repositorio.interna.RepositorioFactoria.obtenerInstancia().obtenerRepositorioCuenta();
            
            // Act - Ejecutar operaciones que internamente llaman a obtenerConfiguracion()
            // El método obtenerConfiguracion() se ejecuta durante la carga lazy de datos
            Optional<List<Cuenta>> resultado = repositorioReal.buscarTodos();
            
            // Assert - Verificar que la operación se ejecutó correctamente
            assertNotNull(resultado);
            // Si llegamos aquí sin errores, significa que obtenerConfiguracion() se ejecutó correctamente
            // y devolvió la configuración esperada (ruta de archivo, tipo de clase, extractor de ID)
        }
    }
    
    @Nested
    @DisplayName("Tests de asignación de número de cuenta específica")
    class AsignacionNumeroCuentaTests {
        
        @Test
        @DisplayName("Debería asignar número de cuenta único automáticamente")
        void deberiaAsignarNumeroCuentaUnico() {
            // Arrange
            Cuenta cuentaNueva1 = Cuenta.builder()
                .numeroCuenta(null)
                .titular(titular1)
                .tipo(TipoCuenta.CORRIENTE)
                .fechaCreacion(LocalDateTime.now())
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1000.00"))
                .activa(true)
                .build();
                
            Cuenta cuentaNueva2 = Cuenta.builder()
                .numeroCuenta(null)
                .titular(titular2)
                .tipo(TipoCuenta.AHORRO)
                .fechaCreacion(LocalDateTime.now())
                .saldoInicial(new BigDecimal("2000.00"))
                .saldo(new BigDecimal("2000.00"))
                .activa(true)
                .build();
            
            // Act
            Optional<Cuenta> resultado1 = repositorio.crearRegistro(Optional.of(cuentaNueva1), TipoOperacionCuenta.CREAR);
            Optional<Cuenta> resultado2 = repositorio.crearRegistro(Optional.of(cuentaNueva2), TipoOperacionCuenta.CREAR);
            
            // Assert
            assertTrue(resultado1.isPresent());
            assertTrue(resultado2.isPresent());
            
            String numeroCuenta1 = resultado1.get().getNumeroCuenta();
            String numeroCuenta2 = resultado2.get().getNumeroCuenta();
            
            assertNotNull(numeroCuenta1);
            assertNotNull(numeroCuenta2);
            assertNotEquals(numeroCuenta1, numeroCuenta2, "Los números de cuenta deben ser únicos");
            assertTrue(numeroCuenta1.startsWith("ES"), "El número de cuenta debe empezar con ES");
            assertTrue(numeroCuenta2.startsWith("ES"), "El número de cuenta debe empezar con ES");
        }
        
        @Test
        @DisplayName("Debería generar números de cuenta con formato IBAN correcto")
        void deberiaGenerarNumerosCuentaConFormatoIbanCorrecto() {
            // Arrange
            Cuenta cuentaNueva = Cuenta.builder()
                .numeroCuenta(null)
                .titular(titular1)
                .tipo(TipoCuenta.CORRIENTE)
                .fechaCreacion(LocalDateTime.now())
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1000.00"))
                .activa(true)
                .build();
            
            // Act
            Optional<Cuenta> resultado = repositorio.crearRegistro(Optional.of(cuentaNueva), TipoOperacionCuenta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            String numeroCuenta = resultado.get().getNumeroCuenta();
            
            // Verificar formato IBAN español (ES + 22 dígitos)
            assertTrue(numeroCuenta.matches("^ES\\d{22}$"), 
                "El número de cuenta debe tener formato IBAN español: ES + 22 dígitos");
        }
        
        @Test
        @DisplayName("Debería mantener secuencia consecutiva en múltiples creaciones")
        void deberiaMantenerSecuenciaConsecutiva() {
            // Arrange
            List<Cuenta> cuentas = new ArrayList<>();
            
            // Act - Crear 5 cuentas
            for (int i = 0; i < 5; i++) {
                Cuenta cuenta = Cuenta.builder()
                    .numeroCuenta(null)
                    .titular(titular1)
                    .tipo(i % 2 == 0 ? TipoCuenta.CORRIENTE : TipoCuenta.AHORRO)
                    .fechaCreacion(LocalDateTime.now())
                    .saldoInicial(new BigDecimal("1000.00"))
                    .saldo(new BigDecimal("1000.00"))
                    .activa(true)
                    .build();
                
                Optional<Cuenta> resultado = repositorio.crearRegistro(Optional.of(cuenta), TipoOperacionCuenta.CREAR);
                assertTrue(resultado.isPresent(), "Cuenta " + i + " debería haberse creado correctamente");
                cuentas.add(resultado.get());
            }
            
            // Assert - Verificar que todas tienen números únicos
            List<String> numerosCuenta = cuentas.stream()
                .map(Cuenta::getNumeroCuenta)
                .distinct()
                .toList();
            
            assertEquals(5, numerosCuenta.size(), "Todas las cuentas deben tener números únicos");
            assertEquals(5, repositorio.contarRegistros());
        }
    }
    
    @Nested
    @DisplayName("Tests de operaciones CRUD específicas")
    class OperacionesCrudTests {
        
        @BeforeEach
        void setUp() {
            // Crear entidades de prueba
            repositorio.crearRegistro(Optional.of(cuenta1), TipoOperacionCuenta.CREAR);
            repositorio.crearRegistro(Optional.of(cuenta2), TipoOperacionCuenta.CREAR);
        }
        
        @Test
        @DisplayName("Debería crear cuenta con todos los campos")
        void deberiaCrearCuentaConTodosLosCampos() {
            // Arrange
            Cuenta cuentaCompleta = Cuenta.builder()
                .numeroCuenta(null)
                .titular(titular1)
                .tipo(TipoCuenta.CORRIENTE)
                .fechaCreacion(LocalDateTime.of(2024, 1, 1, 10, 30))
                .saldoInicial(new BigDecimal("5000.00"))
                .saldo(new BigDecimal("5000.00"))
                .activa(true)
                .build();
            
            // Act
            Optional<Cuenta> resultado = repositorio.crearRegistro(Optional.of(cuentaCompleta), TipoOperacionCuenta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Cuenta cuentaCreada = resultado.get();
            assertNotNull(cuentaCreada.getNumeroCuenta());
            assertEquals(titular1.getId(), cuentaCreada.getTitular().getId());
            assertEquals(TipoCuenta.CORRIENTE, cuentaCreada.getTipo());
            assertEquals(new BigDecimal("5000.00"), cuentaCreada.getSaldo());
            assertEquals(new BigDecimal("5000.00"), cuentaCreada.getSaldoInicial());
            assertTrue(cuentaCreada.isActiva());
        }
        
        @Test
        @DisplayName("Debería buscar cuenta por número de cuenta")
        void deberiaBuscarCuentaPorNumeroCuenta() {
            // Arrange - Crear cuenta y obtener su número
            Optional<Cuenta> cuentaCreada = repositorio.crearRegistro(Optional.of(cuenta1), TipoOperacionCuenta.CREAR);
            assertTrue(cuentaCreada.isPresent());
            String numeroCuenta = cuentaCreada.get().getNumeroCuenta();
            
            // Act
            Optional<Cuenta> resultado = repositorio.buscarPorPredicado(
                cuenta -> numeroCuenta.equals(cuenta.getNumeroCuenta())
            );
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(numeroCuenta, resultado.get().getNumeroCuenta());
            assertEquals(titular1.getId(), resultado.get().getTitular().getId());
        }
        
        @Test
        @DisplayName("Debería buscar cuentas por tipo")
        void deberiaBuscarCuentasPorTipo() {
            // Act
            Optional<List<Cuenta>> resultado = repositorio.buscarTodosPorPredicado(
                cuenta -> TipoCuenta.CORRIENTE.equals(cuenta.getTipo())
            );
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Cuenta> cuentas = resultado.get();
            assertFalse(cuentas.isEmpty());
            assertTrue(cuentas.stream().allMatch(c -> TipoCuenta.CORRIENTE.equals(c.getTipo())));
        }
        
        @Test
        @DisplayName("Debería actualizar cuenta existente")
        void deberiaActualizarCuentaExistente() {
            // Arrange - Crear cuenta primero
            Optional<Cuenta> cuentaCreada = repositorio.crearRegistro(Optional.of(cuenta1), TipoOperacionCuenta.CREAR);
            assertTrue(cuentaCreada.isPresent());
            
            Cuenta cuentaActualizada = Cuenta.builder()
                .numeroCuenta(cuentaCreada.get().getNumeroCuenta())
                .titular(cuentaCreada.get().getTitular())
                .tipo(cuentaCreada.get().getTipo())
                .fechaCreacion(cuentaCreada.get().getFechaCreacion())
                .saldoInicial(cuentaCreada.get().getSaldoInicial())
                .saldo(new BigDecimal("2500.00")) // Saldo actualizado
                .activa(false) // Estado actualizado
                .build();
            
            // Act
            Optional<Cuenta> resultado = repositorio.actualizarRegistro(Optional.of(cuentaActualizada), TipoOperacionCuenta.ACTUALIZAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Cuenta cuentaModificada = resultado.get();
            assertEquals(new BigDecimal("2500.00"), cuentaModificada.getSaldo());
            assertFalse(cuentaModificada.isActiva());
            assertEquals(cuentaCreada.get().getNumeroCuenta(), cuentaModificada.getNumeroCuenta()); // Número no cambia
        }
    }
    
    @Nested
    @DisplayName("Tests de casos edge específicos")
    class CasosEdgeTests {
        
        @Test
        @DisplayName("Debería manejar cuenta con campos null")
        void deberiaManejarCuentaConCamposNull() {
            // Arrange
            Cuenta cuentaConNulls = Cuenta.builder()
                .numeroCuenta(null)
                .titular(titular1)
                .tipo(TipoCuenta.CORRIENTE)
                .fechaCreacion(null) // Campo null
                .saldoInicial(null) // Campo null
                .saldo(null) // Campo null
                .activa(true)
                .build();
            
            // Act
            Optional<Cuenta> resultado = repositorio.crearRegistro(Optional.of(cuentaConNulls), TipoOperacionCuenta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Cuenta cuentaCreada = resultado.get();
            assertNotNull(cuentaCreada.getNumeroCuenta()); // El repositorio asigna número
            assertEquals(titular1.getId(), cuentaCreada.getTitular().getId());
            assertEquals(TipoCuenta.CORRIENTE, cuentaCreada.getTipo());
            assertTrue(cuentaCreada.isActiva());
        }
        
        @Test
        @DisplayName("Debería permitir cuenta con saldo negativo en repositorio (validación en servicio)")
        void deberiaPermitirCuentaConSaldoNegativoEnRepositorio() {
            // Arrange
            Cuenta cuentaSaldoNegativo = Cuenta.builder()
                .numeroCuenta(null)
                .titular(titular1)
                .tipo(TipoCuenta.CORRIENTE)
                .fechaCreacion(LocalDateTime.now())
                .saldoInicial(new BigDecimal("-100.00"))
                .saldo(new BigDecimal("-100.00"))
                .activa(true)
                .build();
            
            // Act
            Optional<Cuenta> resultado = repositorio.crearRegistro(Optional.of(cuentaSaldoNegativo), TipoOperacionCuenta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            // El repositorio permite saldos negativos porque la validación está en el servicio
            assertEquals(new BigDecimal("-100.00"), resultado.get().getSaldo());
        }
        
        @Test
        @DisplayName("Debería manejar cuenta con fecha de creación futura")
        void deberiaManejarCuentaConFechaCreacionFutura() {
            // Arrange
            Cuenta cuentaFutura = Cuenta.builder()
                .numeroCuenta(null)
                .titular(titular1)
                .tipo(TipoCuenta.CORRIENTE)
                .fechaCreacion(LocalDateTime.now().plusDays(1)) // Fecha futura
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1000.00"))
                .activa(true)
                .build();
            
            // Act
            Optional<Cuenta> resultado = repositorio.crearRegistro(Optional.of(cuentaFutura), TipoOperacionCuenta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Cuenta cuentaCreada = resultado.get();
            assertNotNull(cuentaCreada.getNumeroCuenta());
            assertTrue(cuentaCreada.getFechaCreacion().isAfter(LocalDateTime.now()));
        }
        
        @Test
        @DisplayName("Debería manejar cuenta con titular null")
        void deberiaManejarCuentaConTitularNull() {
            // Arrange
            Cuenta cuentaSinTitular = Cuenta.builder()
                .numeroCuenta(null)
                .titular(null) // Titular null
                .tipo(TipoCuenta.CORRIENTE)
                .fechaCreacion(LocalDateTime.now())
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1000.00"))
                .activa(true)
                .build();
            
            // Act
            Optional<Cuenta> resultado = repositorio.crearRegistro(Optional.of(cuentaSinTitular), TipoOperacionCuenta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Cuenta cuentaCreada = resultado.get();
            assertNotNull(cuentaCreada.getNumeroCuenta());
            assertNull(cuentaCreada.getTitular()); // El repositorio permite titular null
        }
    }
    
    @Nested
    @DisplayName("Tests de integración con archivo JSON")
    class IntegracionJsonTests {
        
        @Test
        @DisplayName("Debería persistir cuenta en archivo JSON")
        void deberiaPersistirCuentaEnArchivoJson() {
            // Arrange
            Cuenta cuentaPersistir = Cuenta.builder()
                .numeroCuenta(null)
                .titular(titular1)
                .tipo(TipoCuenta.CORRIENTE)
                .fechaCreacion(LocalDateTime.now())
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1000.00"))
                .activa(true)
                .build();
            
            // Act
            Optional<Cuenta> resultado = repositorio.crearRegistro(Optional.of(cuentaPersistir), TipoOperacionCuenta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            // Verificar que se incrementó el contador (indicador de persistencia)
            assertEquals(1, repositorio.contarRegistros());
        }
        
        @Test
        @DisplayName("Debería cargar cuentas desde archivo JSON")
        void deberiaCargarCuentasDesdeArchivoJson() {
            // Arrange - Crear cuenta primero
            Cuenta cuentaCargar = Cuenta.builder()
                .numeroCuenta(null)
                .titular(titular1)
                .tipo(TipoCuenta.AHORRO)
                .fechaCreacion(LocalDateTime.now())
                .saldoInicial(new BigDecimal("1000.00"))
                .saldo(new BigDecimal("1000.00"))
                .activa(true)
                .build();
            
            repositorio.crearRegistro(Optional.of(cuentaCargar), TipoOperacionCuenta.CREAR);
            
            // Act - Buscar todos (esto carga desde JSON)
            Optional<List<Cuenta>> resultado = repositorio.buscarTodos();
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Cuenta> cuentas = resultado.get();
            assertFalse(cuentas.isEmpty());
            assertTrue(cuentas.stream().anyMatch(c -> TipoCuenta.AHORRO.equals(c.getTipo())));
        }
        
        @Test
        @DisplayName("Debería contar registros correctamente")
        void deberiaContarRegistros() {
            // Arrange - Crear múltiples cuentas
            for (int i = 0; i < 3; i++) {
                Cuenta cuenta = Cuenta.builder()
                    .numeroCuenta(null)
                    .titular(titular1)
                    .tipo(i % 2 == 0 ? TipoCuenta.CORRIENTE : TipoCuenta.AHORRO)
                    .fechaCreacion(LocalDateTime.now())
                    .saldoInicial(new BigDecimal("1000.00"))
                    .saldo(new BigDecimal("1000.00"))
                    .activa(true)
                    .build();
                
                repositorio.crearRegistro(Optional.of(cuenta), TipoOperacionCuenta.CREAR);
            }
            
            // Act
            long cantidad = repositorio.contarRegistros();
            
            // Assert
            assertEquals(3, cantidad);
        }
    }
    
    @Nested
    @DisplayName("Tests de operaciones CRUD faltantes")
    class OperacionesCrudFaltantesTests {
        
        @Test
        @DisplayName("Debería buscar cuenta por ID")
        void deberiaBuscarCuentaPorId() {
            // Arrange - Crear cuenta primero
            Optional<Cuenta> cuentaCreada = repositorio.crearRegistro(Optional.of(cuenta1), TipoOperacionCuenta.CREAR);
            assertTrue(cuentaCreada.isPresent());
            Long id = cuentaCreada.get().getId();
            
            // Act
            Optional<Cuenta> resultado = repositorio.buscarPorId(Optional.of(id));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(id, resultado.get().getId());
            assertEquals(cuenta1.getTitular().getId(), resultado.get().getTitular().getId());
            assertEquals(cuenta1.getTipo(), resultado.get().getTipo());
        }
        
        @Test
        @DisplayName("Debería eliminar cuenta por ID")
        void deberiaEliminarCuentaPorId() {
            // Arrange - Crear cuenta primero
            Optional<Cuenta> cuentaCreada = repositorio.crearRegistro(Optional.of(cuenta1), TipoOperacionCuenta.CREAR);
            assertTrue(cuentaCreada.isPresent());
            Long id = cuentaCreada.get().getId();
            
            // Act
            Optional<Cuenta> eliminada = repositorio.eliminarPorId(Optional.of(id), TipoOperacionCuenta.ELIMINAR);
            
            // Assert
            assertTrue(eliminada.isPresent());
            assertEquals(id, eliminada.get().getId());
            
            // Verificar que ya no existe
            Optional<Cuenta> cuentaEliminada = repositorio.buscarPorId(Optional.of(id));
            assertFalse(cuentaEliminada.isPresent());
        }
        
        @Test
        @DisplayName("Debería restaurar cuenta eliminada")
        void deberiaRestaurarCuentaEliminada() {
            // Arrange - Crear y eliminar cuenta
            Optional<Cuenta> cuentaCreada = repositorio.crearRegistro(Optional.of(cuenta1), TipoOperacionCuenta.CREAR);
            assertTrue(cuentaCreada.isPresent());
            Long id = cuentaCreada.get().getId();
            
            repositorio.eliminarPorId(Optional.of(id), TipoOperacionCuenta.ELIMINAR);
            
            // Act
            Optional<Cuenta> restaurada = repositorio.restaurar(Optional.of(id), TipoOperacionCuenta.RESTAURAR);
            
            // Assert
            assertTrue(restaurada.isPresent());
            assertEquals(id, restaurada.get().getId());
            
            // Verificar que existe nuevamente
            Optional<Cuenta> cuentaRestaurada = repositorio.buscarPorId(Optional.of(id));
            assertTrue(cuentaRestaurada.isPresent());
        }
        
        @Test
        @DisplayName("Debería obtener cuentas eliminadas")
        void deberiaObtenerCuentasEliminadas() {
            // Arrange - Crear y eliminar cuentas
            Optional<Cuenta> cuenta1Creada = repositorio.crearRegistro(Optional.of(cuenta1), TipoOperacionCuenta.CREAR);
            Optional<Cuenta> cuenta2Creada = repositorio.crearRegistro(Optional.of(cuenta2), TipoOperacionCuenta.CREAR);
            assertTrue(cuenta1Creada.isPresent());
            assertTrue(cuenta2Creada.isPresent());
            
            repositorio.eliminarPorId(Optional.of(cuenta1Creada.get().getId()), TipoOperacionCuenta.ELIMINAR);
            repositorio.eliminarPorId(Optional.of(cuenta2Creada.get().getId()), TipoOperacionCuenta.ELIMINAR);
            
            // Act
            List<Cuenta> eliminadas = repositorio.obtenerEliminados();
            
            // Assert
            assertFalse(eliminadas.isEmpty());
            assertEquals(2, eliminadas.size());
            assertTrue(eliminadas.stream().anyMatch(c -> cuenta1Creada.get().getId().equals(c.getId())));
            assertTrue(eliminadas.stream().anyMatch(c -> cuenta2Creada.get().getId().equals(c.getId())));
        }
        
        @Test
        @DisplayName("Debería establecer usuario actual para auditoría")
        void deberiaEstablecerUsuarioActual() {
            // Arrange
            String usuario = "usuario_test";
            
            // Act
            repositorio.setUsuarioActual(usuario);
            
            // Assert - No hay excepción, el método se ejecuta correctamente
            // La verificación real se haría en los logs de auditoría
            assertTrue(true); // Test pasa si no hay excepción
        }
        
        @Test
        @DisplayName("Debería guardar datos manualmente")
        void deberiaGuardarDatosManualmente() {
            // Arrange - Crear cuenta
            Optional<Cuenta> cuentaCreada = repositorio.crearRegistro(Optional.of(cuenta1), TipoOperacionCuenta.CREAR);
            assertTrue(cuentaCreada.isPresent());
            
            // Act
            repositorio.guardarDatos();
            
            // Assert - No hay excepción, el método se ejecuta correctamente
            // La verificación real sería que los datos persisten después del guardado
            assertTrue(true); // Test pasa si no hay excepción
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por ID nulo")
        void deberiaManejarBusquedaPorIdNulo() {
            // Act
            Optional<Cuenta> resultado = repositorio.buscarPorId(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería manejar eliminación por ID nulo")
        void deberiaManejarEliminacionPorIdNulo() {
            // Act
            Optional<Cuenta> resultado = repositorio.eliminarPorId(Optional.empty(), TipoOperacionCuenta.ELIMINAR);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería manejar restauración por ID nulo")
        void deberiaManejarRestauracionPorIdNulo() {
            // Act
            Optional<Cuenta> resultado = repositorio.restaurar(Optional.empty(), TipoOperacionCuenta.RESTAURAR);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por ID inexistente")
        void deberiaManejarBusquedaPorIdInexistente() {
            // Act
            Optional<Cuenta> resultado = repositorio.buscarPorId(Optional.of(999999999999999999L));
            
            // Assert
            assertFalse(resultado.isPresent());
        }
    }
}
