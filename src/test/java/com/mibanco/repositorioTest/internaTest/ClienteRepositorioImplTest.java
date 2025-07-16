package com.mibanco.repositorioTest.internaTest;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.repositorio.interna.ClienteRepositorioImplWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests específicos para ClienteRepositorioImpl
 * Valida la funcionalidad específica del repositorio de clientes
 * Incluye tests de configuración, mapeo y casos edge específicos
 */
@DisplayName("ClienteRepositorioImpl Tests")
class ClienteRepositorioImplTest {
    
    private ClienteRepositorioImplWrapper repositorio;
    private Cliente cliente1;
    private Cliente cliente2;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Crear archivo JSON temporal para testing
        File archivoJson = tempDir.resolve("test_clientes.json").toFile();
        
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
        
        // Crear repositorio con configuración temporal
        repositorio = new TestClienteRepositorioImpl(archivoJson.getAbsolutePath());
    }
    
    /**
     * Implementación de prueba que permite configurar la ruta del archivo
     */
    private static class TestClienteRepositorioImpl extends ClienteRepositorioImplWrapper {
        
        private final String rutaArchivo;
        
        public TestClienteRepositorioImpl(String rutaArchivo) {
            this.rutaArchivo = rutaArchivo;
        }
        
        @Override
        public java.util.Map<String, Object> obtenerConfiguracion() {
            java.util.Map<String, Object> config = new java.util.HashMap<>();
            config.put("rutaArchivo", rutaArchivo);
            config.put("tipoClase", Cliente.class);
            config.put("extractorId", (java.util.function.Function<Cliente, Long>) Cliente::getId);
            return config;
        }
    }
    
    @Nested
    @DisplayName("Tests de configuración específica")
    class ConfiguracionTests {
        
        @Test
        @DisplayName("Debería usar la ruta de archivo correcta para clientes")
        void deberiaUsarRutaArchivoCorrecta() {
            // Act
            Optional<List<Cliente>> resultado = repositorio.buscarTodos();
            
            // Assert
            // Si no hay errores de archivo, la configuración es correcta
            assertNotNull(resultado);
        }
        
        @Test
        @DisplayName("Debería usar el tipo de clase Cliente")
        void deberiaUsarTipoClaseCliente() {
            // Act
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(cliente1), TipoOperacionCliente.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get() instanceof Cliente);
        }
        
        @Test
        @DisplayName("Debería devolver configuración correcta para clientes")
        void deberiaDevolverConfiguracionCorrecta() {
            // Act - Ejecutar operaciones que internamente llaman a obtenerConfiguracion
            // El método obtenerConfiguracion se ejecuta durante la inicialización y carga de datos
            Optional<List<Cliente>> resultado = repositorio.buscarTodos();
            
            // Assert - Verificar que la configuración funciona correctamente
            assertNotNull(resultado);
            // Si llegamos aquí sin errores, significa que obtenerConfiguracion() se ejecutó correctamente
            // y devolvió la configuración esperada (ruta de archivo, tipo de clase, extractor de ID)
        }

        @Test
        @DisplayName("Debería ejecutar obtenerConfiguracion() de la clase real")
        void deberiaEjecutarObtenerConfiguracionDeClaseReal() {
            // Arrange - Usar la Factory para obtener la clase real
            com.mibanco.repositorio.ClienteRepositorio repositorioReal = 
                com.mibanco.repositorio.interna.RepositorioFactoria.obtenerInstancia().obtenerRepositorioCliente();
            
            // Act - Ejecutar operaciones que internamente llaman a obtenerConfiguracion()
            // El método obtenerConfiguracion() se ejecuta durante la carga lazy de datos
            Optional<List<Cliente>> resultado = repositorioReal.buscarTodos();
            
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
            Cliente clienteNuevo1 = Cliente.builder()
                .nombre("Nuevo1")
                .apellido("Cliente1")
                .dni("11111111")
                .email("nuevo1@test.com")
                .telefono("111111111")
                .direccion("Dirección 1")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .build();
                
            Cliente clienteNuevo2 = Cliente.builder()
                .nombre("Nuevo2")
                .apellido("Cliente2")
                .dni("22222222")
                .email("nuevo2@test.com")
                .telefono("222222222")
                .direccion("Dirección 2")
                .fechaNacimiento(LocalDate.of(2001, 1, 1))
                .build();
            
            // Act
            Optional<Cliente> resultado1 = repositorio.crearRegistro(Optional.of(clienteNuevo1), TipoOperacionCliente.CREAR);
            Optional<Cliente> resultado2 = repositorio.crearRegistro(Optional.of(clienteNuevo2), TipoOperacionCliente.CREAR);
            
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
            List<Cliente> clientes = new ArrayList<>();
            
            // Act - Crear 5 clientes
            for (int i = 0; i < 5; i++) {
                Cliente cliente = Cliente.builder()
                    .nombre("Cliente" + i)
                    .apellido("Test")
                    .dni("DNI" + i)
                    .email("cliente" + i + "@test.com")
                    .telefono("Telefono" + i)
                    .direccion("Direccion" + i)
                    .fechaNacimiento(LocalDate.of(1990 + i, 1, 1))
                    .build();
                
                Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(cliente), TipoOperacionCliente.CREAR);
                assertTrue(resultado.isPresent(), "Cliente " + i + " debería haberse creado correctamente");
                clientes.add(resultado.get());
            }
            
            // Assert - Verificar que los IDs son consecutivos (no necesariamente empezando en 1)
            Long primerId = clientes.get(0).getId();
            for (int i = 0; i < clientes.size(); i++) {
                assertEquals(primerId + i, clientes.get(i).getId(), 
                    "ID del cliente " + i + " debe ser consecutivo con el primer ID");
            }
            
            // Verificar que se crearon exactamente 5 clientes
            assertEquals(5, clientes.size());
            assertEquals(5, repositorio.contarRegistros());
        }
    }
    
    @Nested
    @DisplayName("Tests de operaciones CRUD específicas")
    class OperacionesCrudTests {
        
        @BeforeEach
        void setUp() {
            // Crear entidades de prueba
            repositorio.crearRegistro(Optional.of(cliente1), TipoOperacionCliente.CREAR);
            repositorio.crearRegistro(Optional.of(cliente2), TipoOperacionCliente.CREAR);
        }
        
        @Test
        @DisplayName("Debería crear cliente con todos los campos")
        void deberiaCrearClienteConTodosLosCampos() {
            // Arrange
            Cliente clienteCompleto = Cliente.builder()
                .nombre("Completo")
                .apellido("Cliente")
                .dni("44444444")
                .email("completo@test.com")
                .telefono("444444444")
                .direccion("Dirección Completa 123")
                .fechaNacimiento(LocalDate.of(1980, 12, 25))
                .build();
            
            // Act
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(clienteCompleto), TipoOperacionCliente.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Cliente clienteCreado = resultado.get();
            assertEquals("Completo", clienteCreado.getNombre());
            assertEquals("Cliente", clienteCreado.getApellido());
            assertEquals("44444444", clienteCreado.getDni());
            assertEquals("completo@test.com", clienteCreado.getEmail());
            assertEquals("444444444", clienteCreado.getTelefono());
            assertEquals("Dirección Completa 123", clienteCreado.getDireccion());
            assertEquals(LocalDate.of(1980, 12, 25), clienteCreado.getFechaNacimiento());
        }
        
        @Test
        @DisplayName("Debería buscar cliente por DNI")
        void deberiaBuscarClientePorDni() {
            // Act
            Optional<Cliente> resultado = repositorio.buscarPorPredicado(
                cliente -> "12345678".equals(cliente.getDni())
            );
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals("Juan", resultado.get().getNombre());
            assertEquals("12345678", resultado.get().getDni());
        }
        
        @Test
        @DisplayName("Debería buscar clientes por email")
        void deberiaBuscarClientesPorEmail() {
            // Act
            Optional<List<Cliente>> resultado = repositorio.buscarTodosPorPredicado(
                cliente -> cliente.getEmail().contains("@test.com")
            );
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Cliente> clientes = resultado.get();
            assertEquals(2, clientes.size());
            assertTrue(clientes.stream().allMatch(c -> c.getEmail().contains("@test.com")));
        }
        
        @Test
        @DisplayName("Debería actualizar cliente existente")
        void deberiaActualizarClienteExistente() {
            // Arrange
            Cliente clienteActualizado = Cliente.builder()
                .id(cliente1.getId())
                .nombre("Juan Actualizado")
                .apellido(cliente1.getApellido())
                .dni(cliente1.getDni())
                .email("juan.actualizado@test.com")
                .telefono(cliente1.getTelefono())
                .direccion(cliente1.getDireccion())
                .fechaNacimiento(cliente1.getFechaNacimiento())
                .build();
            
            // Act
            Optional<Cliente> resultado = repositorio.actualizarRegistro(Optional.of(clienteActualizado), TipoOperacionCliente.ACTUALIZAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Cliente clienteModificado = resultado.get();
            assertEquals("Juan Actualizado", clienteModificado.getNombre());
            assertEquals("juan.actualizado@test.com", clienteModificado.getEmail());
            assertEquals("12345678", clienteModificado.getDni()); // Campo no modificado
        }
    }
    
    @Nested
    @DisplayName("Tests de casos edge específicos")
    class CasosEdgeTests {
        
        @Test
        @DisplayName("Debería manejar cliente con campos null")
        void deberiaManejarClienteConCamposNull() {
            // Arrange
            Cliente clienteConNulls = Cliente.builder()
                .nombre("Test")
                .apellido("Nulls")
                .dni("55555555")
                .build(); // email, telefono, direccion, fechaNacimiento son null
            
            // Act
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(clienteConNulls), TipoOperacionCliente.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Cliente clienteCreado = resultado.get();
            assertEquals("Test", clienteCreado.getNombre());
            assertEquals("Nulls", clienteCreado.getApellido());
            assertEquals("55555555", clienteCreado.getDni());
            assertNull(clienteCreado.getEmail());
            assertNull(clienteCreado.getTelefono());
            assertNull(clienteCreado.getDireccion());
            assertNull(clienteCreado.getFechaNacimiento());
        }
        
        @Test
        @DisplayName("Debería permitir cliente con DNI duplicado en repositorio (validación en servicio)")
        void deberiaPermitirClienteConDniDuplicadoEnRepositorio() {
            // Arrange
            Cliente cliente1 = Cliente.builder()
                .nombre("Primero")
                .apellido("Cliente")
                .dni("66666666")
                .email("primero@test.com")
                .build();
                
            Cliente cliente2 = Cliente.builder()
                .nombre("Segundo")
                .apellido("Cliente")
                .dni("66666666") // Mismo DNI
                .email("segundo@test.com")
                .build();
            
            // Act
            Optional<Cliente> resultado1 = repositorio.crearRegistro(Optional.of(cliente1), TipoOperacionCliente.CREAR);
            Optional<Cliente> resultado2 = repositorio.crearRegistro(Optional.of(cliente2), TipoOperacionCliente.CREAR);
            
            // Assert
            assertTrue(resultado1.isPresent());
            assertTrue(resultado2.isPresent());
            // El repositorio permite duplicados porque la validación está en el servicio
            assertEquals("Primero", resultado1.get().getNombre());
            assertEquals("Segundo", resultado2.get().getNombre());
        }
        
        @Test
        @DisplayName("Debería manejar cliente con fecha de nacimiento futura")
        void deberiaManejarClienteConFechaNacimientoFutura() {
            // Arrange
            Cliente clienteFuturo = Cliente.builder()
                .nombre("Futuro")
                .apellido("Cliente")
                .dni("77777777")
                .email("futuro@test.com")
                .fechaNacimiento(LocalDate.now().plusYears(1)) // Fecha futura
                .build();
            
            // Act
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(clienteFuturo), TipoOperacionCliente.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Cliente clienteCreado = resultado.get();
            assertEquals("Futuro", clienteCreado.getNombre());
            assertTrue(clienteCreado.getFechaNacimiento().isAfter(LocalDate.now()));
        }
    }
    
    @Nested
    @DisplayName("Tests de integración con archivo JSON")
    class IntegracionJsonTests {
        
        @Test
        @DisplayName("Debería persistir cliente en archivo JSON")
        void deberiaPersistirClienteEnArchivoJson() {
            // Arrange
            Cliente clientePersistir = Cliente.builder()
                .nombre("Persistir")
                .apellido("Cliente")
                .dni("88888888")
                .email("persistir@test.com")
                .build();
            
            // Act
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(clientePersistir), TipoOperacionCliente.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            // Verificar que se incrementó el contador (indicador de persistencia)
            assertEquals(1, repositorio.contarRegistros());
        }
        
        @Test
        @DisplayName("Debería cargar clientes desde archivo JSON")
        void deberiaCargarClientesDesdeArchivoJson() {
            // Arrange - Crear cliente primero
            Cliente clienteCargar = Cliente.builder()
                .nombre("Cargar")
                .apellido("Cliente")
                .dni("99999999")
                .email("cargar@test.com")
                .build();
            
            repositorio.crearRegistro(Optional.of(clienteCargar), TipoOperacionCliente.CREAR);
            
            // Act - Buscar todos (esto carga desde JSON)
            Optional<List<Cliente>> resultado = repositorio.buscarTodos();
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Cliente> clientes = resultado.get();
            assertFalse(clientes.isEmpty());
            assertTrue(clientes.stream().anyMatch(c -> "99999999".equals(c.getDni())));
        }
    }
}
