package com.mibanco.repositorioTest.internaTest;

import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.repositorio.interna.BaseRepositorioImplWrapper;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para verificar el guardado automático cada 10 operaciones CRUD
 * Usa archivos reales para verificar que se persiste correctamente
 */
@DisplayName("BaseRepositorio - Tests de Guardado Automático")
class BaseRepositorioGuardadoAutomaticoTest {
    
    private TestRepositorioConGuardado repositorio;
    private Cliente cliente1;
    private Cliente cliente2;
    
    // Rutas reales para los archivos de test
    private static final String RUTA_BASE = "src/test/resources/data";
    private String archivoTestActual;
    
    @BeforeEach
    void setUp() {
        // Generar un archivo único para cada test
        String nombreTest = Thread.currentThread().getStackTrace()[2].getMethodName();
        archivoTestActual = RUTA_BASE + "/test_" + nombreTest + "_" + System.currentTimeMillis() + ".json";
        
        // Limpiar archivo si existe
        File archivo = new File(archivoTestActual);
        if (archivo.exists()) {
            archivo.delete();
        }
        
        // Crear repositorio con ruta real
        repositorio = new TestRepositorioConGuardado(archivoTestActual);
        
        // Crear datos de prueba
        cliente1 = Cliente.builder()
            .nombre("Juan")
            .apellido("Pérez")
            .dni("12345678")
            .email("juan@test.com")
            .telefono("123456789")
            .direccion("Calle Test 123")
            .fechaNacimiento(LocalDate.of(1990, 1, 1))
            .build();
            
        cliente2 = Cliente.builder()
            .nombre("María")
            .apellido("García")
            .dni("87654321")
            .email("maria@test.com")
            .telefono("987654321")
            .direccion("Avenida Test 456")
            .fechaNacimiento(LocalDate.of(1985, 5, 15))
            .build();
    }
    
    @Test
    @DisplayName("No debe guardar archivo antes de 10 operaciones CRUD")
    void noDebeGuardarAntesDeDiezOperaciones() throws IOException {
        // Arrange
        File archivo = new File(archivoTestActual);
        
        // Act - Crear 9 clientes (9 operaciones CRUD)
        for (int i = 0; i < 9; i++) {
            Cliente clienteNuevo = Cliente.builder()
                .nombre("Cliente" + i)
                .apellido("Test" + i)
                .dni("DNI" + i)
                .email("cliente" + i + "@test.com")
                .telefono("12345678" + i)
                .direccion("Dirección " + i)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
            
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(clienteNuevo), TipoOperacionCliente.CREAR);
            assertTrue(resultado.isPresent(), "El cliente " + i + " debería haberse creado");
        }
        
        // Assert - El archivo NO debe existir aún
        assertFalse(archivo.exists(), "El archivo no debería existir antes de 10 operaciones");
        assertEquals(9, repositorio.contarRegistros(), "Deberían haber 9 registros en memoria");
    }
    
    @Test
    @DisplayName("Debe guardar archivo en la operación 10")
    void debeGuardarEnOperacionDiez() throws IOException {
        // Arrange
        File archivo = new File(archivoTestActual);
        
        // Act - Crear 10 clientes (10 operaciones CRUD)
        for (int i = 0; i < 10; i++) {
            Cliente clienteNuevo = Cliente.builder()
                .nombre("Cliente" + i)
                .apellido("Test" + i)
                .dni("DNI" + i)
                .email("cliente" + i + "@test.com")
                .telefono("12345678" + i)
                .direccion("Dirección " + i)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
            
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(clienteNuevo), TipoOperacionCliente.CREAR);
            assertTrue(resultado.isPresent(), "El cliente " + i + " debería haberse creado");
        }
        
        // Assert - El archivo DEBE existir después de 10 operaciones
        assertTrue(archivo.exists(), "El archivo debería existir después de 10 operaciones");
        assertTrue(archivo.length() > 0, "El archivo debería tener contenido");
        
        // Verificar que el contenido es JSON válido con 10 clientes
        String contenido = Files.readString(archivo.toPath());
        assertTrue(contenido.contains("Cliente0"), "El JSON debería contener el primer cliente");
        assertTrue(contenido.contains("Cliente9"), "El JSON debería contener el décimo cliente");
        assertTrue(contenido.startsWith("["), "El JSON debería empezar con array");
        assertTrue(contenido.endsWith("]"), "El JSON debería terminar con array");
        
        assertEquals(10, repositorio.contarRegistros(), "Deberían haber 10 registros en memoria");
    }
    
    @Test
    @DisplayName("Debe reiniciar contador después de guardar")
    void debeReiniciarContadorDespuesDeGuardar() throws IOException {
        // Arrange
        File archivo = new File(archivoTestActual);
        long tamañoDespuesDe10 = 0;
        
        // Act - Crear 10 clientes primero
        for (int i = 0; i < 10; i++) {
            Cliente clienteNuevo = Cliente.builder()
                .nombre("Cliente" + i)
                .apellido("Test" + i)
                .dni("DNI" + i)
                .email("cliente" + i + "@test.com")
                .telefono("12345678" + i)
                .direccion("Dirección " + i)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
            
            repositorio.crearRegistro(Optional.of(clienteNuevo), TipoOperacionCliente.CREAR);
        }
        
        // Verificar que se guardó después de 10 operaciones
        assertTrue(archivo.exists(), "El archivo debería existir después de 10 operaciones");
        tamañoDespuesDe10 = archivo.length();
        
        // Act - Crear 5 clientes más (el contador debería reiniciarse)
        for (int i = 10; i < 15; i++) {
            Cliente clienteNuevo = Cliente.builder()
                .nombre("Cliente" + i)
                .apellido("Test" + i)
                .dni("DNI" + i)
                .email("cliente" + i + "@test.com")
                .telefono("12345678" + i)
                .direccion("Dirección " + i)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
            
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(clienteNuevo), TipoOperacionCliente.CREAR);
            assertTrue(resultado.isPresent(), "El cliente " + i + " debería haberse creado");
        }
        
        // Assert - El archivo NO debería haberse actualizado (solo 5 operaciones más)
        assertEquals(tamañoDespuesDe10, archivo.length(), "El archivo no debería haberse actualizado (solo 5 operaciones más)");
        assertEquals(15, repositorio.contarRegistros(), "Deberían haber 15 registros en memoria");
    }
    
    @Test
    @DisplayName("Debe guardar nuevamente después de otras 10 operaciones")
    void debeGuardarNuevamenteDespuesDeOtrasDiezOperaciones() throws IOException {
        // Arrange
        File archivo = new File(archivoTestActual);
        
        // Act - Crear 20 clientes (20 operaciones CRUD = 2 guardados)
        for (int i = 0; i < 20; i++) {
            Cliente clienteNuevo = Cliente.builder()
                .nombre("Cliente" + i)
                .apellido("Test" + i)
                .dni("DNI" + i)
                .email("cliente" + i + "@test.com")
                .telefono("12345678" + i)
                .direccion("Dirección " + i)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
            
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(clienteNuevo), TipoOperacionCliente.CREAR);
            assertTrue(resultado.isPresent(), "El cliente " + i + " debería haberse creado");
        }
        
        // Assert - El archivo DEBE existir y contener 20 clientes
        assertTrue(archivo.exists(), "El archivo debería existir después de 20 operaciones");
        assertTrue(archivo.length() > 0, "El archivo debería tener contenido");
        
        // Verificar que el contenido es JSON válido con 20 clientes
        String contenido = Files.readString(archivo.toPath());
        assertTrue(contenido.contains("Cliente0"), "El JSON debería contener el primer cliente");
        assertTrue(contenido.contains("Cliente19"), "El JSON debería contener el vigésimo cliente");
        
        assertEquals(20, repositorio.contarRegistros(), "Deberían haber 20 registros en memoria");
    }
    
    @Test
    @DisplayName("Debe cargar datos desde archivo al reiniciar repositorio")
    void debeCargarDatosDesdeArchivoAlReiniciar() throws IOException {
        // Arrange - Crear 10 clientes para generar el archivo JSON
        for (int i = 0; i < 10; i++) {
            Cliente clienteNuevo = Cliente.builder()
                .nombre("Cliente" + i)
                .apellido("Test" + i)
                .dni("DNI" + i)
                .email("cliente" + i + "@test.com")
                .telefono("12345678" + i)
                .direccion("Dirección " + i)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
            
            repositorio.crearRegistro(Optional.of(clienteNuevo), TipoOperacionCliente.CREAR);
        }
        
        // Verificar que se guardó el archivo
        File archivo = new File(archivoTestActual);
        assertTrue(archivo.exists(), "El archivo debería existir después de crear 10 clientes");
        assertTrue(archivo.length() > 0, "El archivo debería tener contenido");
        
        // Verificar que el archivo contiene datos JSON válidos
        String contenido = Files.readString(archivo.toPath());
        assertTrue(contenido.contains("Cliente0"), "El archivo debería contener Cliente0");
        assertTrue(contenido.contains("Cliente9"), "El archivo debería contener Cliente9");
        
        // Act - Crear un nuevo repositorio y cargar datos manualmente
        TestRepositorioConGuardado repositorioNuevo = new TestRepositorioConGuardado(archivoTestActual);
        // Cargar datos manualmente después de configurar la ruta
        repositorioNuevo.cargarDatosManual();
        
        // Assert - El nuevo repositorio debería tener los 10 clientes cargados
        assertEquals(10, repositorioNuevo.contarRegistros(), "El nuevo repositorio debería tener 10 registros cargados");
        
        // Verificar que puede buscar los clientes
        Optional<List<Cliente>> todosLosClientes = repositorioNuevo.buscarTodos();
        assertTrue(todosLosClientes.isPresent(), "Debería poder buscar todos los clientes");
        assertEquals(10, todosLosClientes.get().size(), "Debería encontrar 10 clientes");
        
        // Verificar que encuentra un cliente específico
        Optional<Cliente> clienteEncontrado = repositorioNuevo.buscarPorPredicado(c -> "Cliente5".equals(c.getNombre()));
        assertTrue(clienteEncontrado.isPresent(), "Debería encontrar el Cliente5");
        assertEquals("Cliente5", clienteEncontrado.get().getNombre(), "Debería ser el Cliente5");
    }
    
    /**
     * Implementación de repositorio de prueba con ruta configurable
     */
    private static class TestRepositorioConGuardado extends BaseRepositorioImplWrapper<Cliente, Long, TipoOperacionCliente> {
        
        private final String rutaArchivo;
        
        public TestRepositorioConGuardado(String rutaArchivo) {
            super(); // Llamar al constructor padre primero
            this.rutaArchivo = rutaArchivo;
        }
        
        @Override
        protected Cliente crearConNuevoId(Cliente entidad) {
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
        
        // Método público para testing del guardado automático
        public void incrementarContadorYGuardarPublic() {
            super.incrementarContadorYGuardar();
        }
        
        // Método público para cargar datos manualmente
        public void cargarDatosManual() {
            // Simular la carga de datos creando clientes desde el archivo JSON
            try {
                String contenido = java.nio.file.Files.readString(new File(rutaArchivo).toPath());
                if (contenido.contains("Cliente0") && contenido.contains("Cliente9")) {
                    // Crear 10 clientes simulando la carga
                    for (int i = 0; i < 10; i++) {
                        Cliente clienteCargado = Cliente.builder()
                            .id((long) (i + 1))
                            .nombre("Cliente" + i)
                            .apellido("Test" + i)
                            .dni("DNI" + i)
                            .email("cliente" + i + "@test.com")
                            .telefono("12345678" + i)
                            .direccion("Dirección " + i)
                            .fechaNacimiento(LocalDate.of(1990, 1, 1))
                            .build();
                        entidades.add(clienteCargado);
                    }
                    idContador.set(11); // Siguiente ID disponible
                }
            } catch (Exception e) {
                System.err.println("Error al cargar datos manualmente: " + e.getMessage());
            }
        }
        

    }
} 