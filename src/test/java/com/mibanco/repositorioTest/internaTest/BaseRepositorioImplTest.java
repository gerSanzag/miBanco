package com.mibanco.repositorioTest.internaTest;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.repositorio.interna.BaseRepositorioImplWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para BaseRepositorioImpl usando una implementación concreta para testing
 * Valida toda la funcionalidad base de repositorios
 */
@DisplayName("BaseRepositorioImpl Tests")
class BaseRepositorioImplTest {
    
    private TestRepositorioImpl repositorio;
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
        repositorio = new TestRepositorioImpl(archivoJson.getAbsolutePath());
    }
    
    /**
     * Implementación concreta de BaseRepositorioImpl para testing
     */
    private static class TestRepositorioImpl extends BaseRepositorioImplWrapper<Cliente, Long, TipoOperacionCliente> {
        
        private final String rutaArchivo;
        
        public TestRepositorioImpl(String rutaArchivo) {
            this.rutaArchivo = java.util.Objects.requireNonNull(rutaArchivo, "La ruta del archivo no puede ser null");
        }
        
        @Override
        protected Cliente crearConNuevoId(Cliente entidad) {
            // Simular asignación de ID usando el contador
            // Enfoque funcional puro con Optional
            ClienteMapeador mapeador = new ClienteMapeador();
            
            return mapeador.aDtoDirecto(entidad)
                .map(dto -> dto.toBuilder()
                    .id(idContador.getAndIncrement())
                    .build())
                .flatMap(mapeador::aEntidadDirecta)
                .orElseThrow(() -> new IllegalStateException("No se pudo procesar la entidad Cliente"));
        }
        
        @Override
        protected Map<String, Object> obtenerConfiguracion() {
            Map<String, Object> config = new HashMap<>();
            config.put("rutaArchivo", rutaArchivo);
            config.put("tipoClase", Cliente.class);
            config.put("extractorId", (Function<Cliente, Long>) Cliente::getId);
            return config;
        }
    }
    
    @Nested
    @DisplayName("Tests para crear")
    class CrearTests {
        
        @Test
        @DisplayName("Debería crear una nueva entidad con ID automático")
        void deberiaCrearEntidadConIdAutomatico() {
            // Arrange
            Cliente clienteNuevo = Cliente.builder()
                .nombre("Nuevo")
                .apellido("Cliente")
                .dni("11111111")
                .email("nuevo@test.com")
                .telefono("111111111")
                .direccion("Nueva Dirección")
                .fechaNacimiento(LocalDate.of(2000, 1, 1))
                .build();
            
            // Act
            Optional<Cliente> resultado = repositorio.crear(Optional.of(clienteNuevo), TipoOperacionCliente.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Cliente clienteCreado = resultado.get();
            assertNotNull(clienteCreado.getId());
            assertEquals("Nuevo", clienteCreado.getNombre());
            assertEquals(1, repositorio.contarRegistros());
        }
        
        @Test
        @DisplayName("Debería devolver empty para entidad null")
        void deberiaDevolverEmptyParaEntidadNull() {
            // Act
            Optional<Cliente> resultado = repositorio.crear(Optional.empty(), TipoOperacionCliente.CREAR);
            
            // Assert
            assertFalse(resultado.isPresent());
            assertEquals(0, repositorio.contarRegistros());
        }
    }
    
    @Nested
    @DisplayName("Tests para buscar")
    class BuscarTests {
        
        @BeforeEach
        void setUp() {
            // Crear entidades de prueba
            repositorio.crear(Optional.of(cliente1), TipoOperacionCliente.CREAR);
            repositorio.crear(Optional.of(cliente2), TipoOperacionCliente.CREAR);
        }
        
        @Test
        @DisplayName("Debería buscar entidad por ID existente")
        void deberiaBuscarPorIdExistente() {
            // Act
            Optional<Cliente> resultado = repositorio.buscarPorId(Optional.of(1L));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals("Juan", resultado.get().getNombre());
        }
        
        @Test
        @DisplayName("Debería devolver empty para ID inexistente")
        void deberiaDevolverEmptyParaIdInexistente() {
            // Act
            Optional<Cliente> resultado = repositorio.buscarPorId(Optional.of(999L));
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería devolver empty para ID null")
        void deberiaDevolverEmptyParaIdNull() {
            // Act
            Optional<Cliente> resultado = repositorio.buscarPorId(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería buscar todas las entidades")
        void deberiaBuscarTodasLasEntidades() {
            // Act
            Optional<List<Cliente>> resultado = repositorio.buscarTodos();
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Cliente> clientes = resultado.get();
            assertEquals(2, clientes.size());
            assertTrue(clientes.stream().anyMatch(c -> c.getNombre().equals("Juan")));
            assertTrue(clientes.stream().anyMatch(c -> c.getNombre().equals("María")));
        }
        
        @Test
        @DisplayName("Debería contar registros correctamente")
        void deberiaContarRegistrosCorrectamente() {
            // Act
            long cantidad = repositorio.contarRegistros();
            
            // Assert
            assertEquals(2, cantidad);
        }
    }
    
    @Nested
    @DisplayName("Tests para actualizar")
    class ActualizarTests {
        
        private Cliente clienteCreado;
        
        @BeforeEach
        void setUp() {
            
        }
        
        @Test
        @DisplayName("Debería actualizar entidad existente")
        void deberiaActualizarEntidadExistente() {
            // Arrange
            ClienteMapeador mapeador = new ClienteMapeador();
            
            // 1. Mapear cliente1 a DTO
            ClienteDTO dtoOriginal = mapeador.aDtoDirecto(cliente1).orElseThrow();
            
            // 2. Crear DTO actualizado usando toBuilder (manteniendo el mismo ID)
            ClienteDTO dtoActualizado = dtoOriginal.toBuilder()
                .nombre("Juan Actualizado")
                .email("juan.actualizado@test.com")
                .build();
            
            // 3. Mapear DTO actualizado a entidad
            Cliente clienteActualizado = mapeador.aEntidadDirecta(dtoActualizado).orElseThrow();
            
            // Act
            Optional<Cliente> resultado = repositorio.actualizar(Optional.of(clienteActualizado), TipoOperacionCliente.ACTUALIZAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            
            // ✅ Verificar que el ID se mantiene igual
            assertEquals(cliente1.getId(), resultado.get().getId());
            
            // ✅ Verificar que los datos se actualizaron
            assertEquals("Juan Actualizado", resultado.get().getNombre());
            assertEquals("juan.actualizado@test.com", resultado.get().getEmail());
            
            // ✅ Verificar que otros campos no cambiaron
            assertEquals(cliente1.getApellido(), resultado.get().getApellido());
            assertEquals(cliente1.getDni(), resultado.get().getDni());
            assertEquals(cliente1.getTelefono(), resultado.get().getTelefono());
            assertEquals(cliente1.getDireccion(), resultado.get().getDireccion());
            assertEquals(cliente1.getFechaNacimiento(), resultado.get().getFechaNacimiento());
            
            // ✅ Verificar que solo hay un registro
            assertEquals(1, repositorio.contarRegistros());
        }
        
       
    }
    
    @Nested
    @DisplayName("Tests para eliminar")
    class EliminarTests {
        
        private Cliente clienteCreado;
        
        @BeforeEach
        void setUp() {
            // Crear entidad de prueba
            Optional<Cliente> resultado = repositorio.crear(Optional.of(cliente1), TipoOperacionCliente.CREAR);
            clienteCreado = resultado.get();
        }
        
        @Test
        @DisplayName("Debería eliminar entidad por ID")
        void deberiaEliminarPorId() {
            // Act
            Optional<Cliente> resultado = repositorio.eliminarPorId(Optional.of(clienteCreado.getId()), TipoOperacionCliente.ELIMINAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals("Juan", resultado.get().getNombre());
            assertEquals(0, repositorio.contarRegistros());
            assertEquals(1, repositorio.obtenerEliminados().size());
        }
        
        @Test
        @DisplayName("Debería devolver empty para ID inexistente")
        void deberiaDevolverEmptyParaIdInexistente() {
            // Act
            Optional<Cliente> resultado = repositorio.eliminarPorId(Optional.of(999L), TipoOperacionCliente.ELIMINAR);
            
            // Assert
            assertFalse(resultado.isPresent());
            assertEquals(1, repositorio.contarRegistros());
        }
    }
    
    @Nested
    @DisplayName("Tests para restaurar")
    class RestaurarTests {
        
        private Cliente clienteEliminado;
        
        @BeforeEach
        void setUp() {
            // Crear y eliminar entidad de prueba
            Optional<Cliente> resultado = repositorio.crear(Optional.of(cliente1), TipoOperacionCliente.CREAR);
            Cliente clienteCreado = resultado.get();
            clienteEliminado = repositorio.eliminarPorId(Optional.of(clienteCreado.getId()), TipoOperacionCliente.ELIMINAR).get();
            // clienteEliminado = clienteCreado;
        }
        
        @Test
        @DisplayName("Debería restaurar entidad eliminada")
        void deberiaRestaurarEntidadEliminada() {
            // Act
            Optional<Cliente> resultado = repositorio.restaurar(Optional.of(clienteEliminado.getId()), TipoOperacionCliente.RESTAURAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals("Juan", resultado.get().getNombre());
            assertEquals(1, repositorio.contarRegistros());
            assertEquals(0, repositorio.obtenerEliminados().size());
        }
        
        @Test
        @DisplayName("Debería devolver empty para ID inexistente en eliminados")
        void deberiaDevolverEmptyParaIdInexistenteEnEliminados() {
            // Act
            Optional<Cliente> resultado = repositorio.restaurar(Optional.of(999L), TipoOperacionCliente.RESTAURAR);
            
            // Assert
            assertFalse(resultado.isPresent());
            assertEquals(0, repositorio.contarRegistros());
            assertEquals(1, repositorio.obtenerEliminados().size());
        }
    }
    
    @Nested
    @DisplayName("Tests para usuario actual")
    class UsuarioActualTests {
        
        @Test
        @DisplayName("Debería establecer usuario actual")
        void deberiaEstablecerUsuarioActual() {
            // Arrange
            String nuevoUsuario = "usuario_test";
            
            // Act
            repositorio.setUsuarioActual(nuevoUsuario);
            
            // Assert
            // Nota: No podemos acceder directamente al campo usuarioActual porque es protected
            // Pero podemos verificar que el método se ejecuta sin errores
            assertDoesNotThrow(() -> repositorio.setUsuarioActual(nuevoUsuario));
        }
    }
}
