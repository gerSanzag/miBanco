package com.mibanco.repositorioTest.internaTest;

import com.mibanco.repositorio.interna.BaseProcesadorJsonWrapper;
import com.mibanco.modelo.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de persistencia real para BaseProcesadorJson
 * Usa archivos reales en src/test/resources/data
 * Los tests deben ejecutarse en orden específico
 */
@DisplayName("BaseProcesadorJson - Tests de Persistencia Real")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BaseProcesadorJsonPersistenciaRealTest {
    
    private BaseProcesadorJsonWrapper<Cliente> procesadorJson;
    private Cliente cliente1;
    private Cliente cliente2;
    
    // Rutas reales para los archivos de test
    private static final String RUTA_BASE = "src/test/resources/data";
    private static final String ARCHIVO_CLIENTES = RUTA_BASE + "/test_clientes.json";
    private static final String ARCHIVO_CLIENTES_VACIO = RUTA_BASE + "/test_clientes_vacio.json";
    
    @BeforeEach
    void setUp() {
        procesadorJson = new BaseProcesadorJsonWrapper<>();
        
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
            .id(5L)
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
    @Order(1)
    @DisplayName("Debería crear archivo JSON si no existe")
    void deberiaCrearArchivoJsonSiNoExiste() throws IOException {
        // Arrange
        File archivo = new File(ARCHIVO_CLIENTES);
        
        // Limpiar archivo si existe (para test limpio)
        if (archivo.exists()) {
            archivo.delete();
        }
        
        List<Cliente> clientes = List.of(cliente1, cliente2);
        
        // Act
        procesadorJson.guardarJson(ARCHIVO_CLIENTES, clientes);
        
        // Assert
        assertTrue(archivo.exists(), "El archivo debería haberse creado");
        assertTrue(archivo.length() > 0, "El archivo debería tener contenido");
        
        // Verificar que el contenido es JSON válido
        String contenido = Files.readString(archivo.toPath());
        assertTrue(contenido.contains("Juan"), "El JSON debería contener el nombre del cliente");
        assertTrue(contenido.contains("María"), "El JSON debería contener el nombre del segundo cliente");
        assertTrue(contenido.startsWith("["), "El JSON debería empezar con array");
        assertTrue(contenido.endsWith("]"), "El JSON debería terminar con array");
    }
    
    @Test
    @Order(2)
    @DisplayName("Debería cargar datos desde archivo JSON existente")
    void deberiaCargarDatosDesdeArchivoExistente() {
        // Arrange - El archivo ya existe del test anterior
        File archivo = new File(ARCHIVO_CLIENTES);
        assertTrue(archivo.exists(), "El archivo debe existir del test anterior");
        
        // Act
        List<Cliente> clientesCargados = procesadorJson.cargarDatosCondicionalmente(
            ARCHIVO_CLIENTES, Cliente.class);
        
        // Assert
        assertNotNull(clientesCargados, "La lista cargada no debería ser null");
        assertEquals(2, clientesCargados.size(), "Deberían haberse cargado 2 clientes");
        
        // Verificar que los datos son correctos
        Cliente clienteCargado1 = clientesCargados.get(0);
        assertEquals(cliente1.getId(), clienteCargado1.getId(), "El ID del primer cliente debería coincidir");
        assertEquals(cliente1.getNombre(), clienteCargado1.getNombre(), "El nombre del primer cliente debería coincidir");
        assertEquals(cliente1.getDni(), clienteCargado1.getDni(), "El DNI del primer cliente debería coincidir");
        
        Cliente clienteCargado2 = clientesCargados.get(1);
        assertEquals(cliente2.getId(), clienteCargado2.getId(), "El ID del segundo cliente debería coincidir");
        assertEquals(cliente2.getNombre(), clienteCargado2.getNombre(), "El nombre del segundo cliente debería coincidir");
        assertEquals(cliente2.getDni(), clienteCargado2.getDni(), "El DNI del segundo cliente debería coincidir");
    }
    
    @Test
    @Order(3)
    @DisplayName("Debería actualizar archivo JSON existente")
    void deberiaActualizarArchivoJsonExistente() throws IOException {
        // Arrange - El archivo ya existe del test anterior
        File archivo = new File(ARCHIVO_CLIENTES);
        assertTrue(archivo.exists(), "El archivo debe existir del test anterior");
        
        // Obtener tamaño original
        long tamañoOriginal = archivo.length();
        
        // Crear nuevo cliente para agregar
        Cliente cliente3 = Cliente.builder()
            .id(10L)
            .nombre("Carlos")
            .apellido("López")
            .dni("11111111")
            .email("carlos@test.com")
            .telefono("555555555")
            .direccion("Calle Nueva 789")
            .fechaNacimiento(LocalDate.of(1995, 8, 20))
            .build();
        
        List<Cliente> clientesActualizados = List.of(cliente1, cliente2, cliente3);
        
        // Act
        procesadorJson.guardarJson(ARCHIVO_CLIENTES, clientesActualizados);
        
        // Assert
        assertTrue(archivo.exists(), "El archivo debería seguir existiendo");
        assertTrue(archivo.length() > tamañoOriginal, "El archivo debería ser más grande");
        
        // Verificar que el contenido actualizado es correcto
        String contenido = Files.readString(archivo.toPath());
        assertTrue(contenido.contains("Carlos"), "El JSON actualizado debería contener el nuevo cliente");
        assertTrue(contenido.contains("Juan"), "El JSON actualizado debería contener el primer cliente");
        assertTrue(contenido.contains("María"), "El JSON actualizado debería contener el segundo cliente");
    }
    
    @Test
    @Order(4)
    @DisplayName("Debería cargar datos actualizados desde archivo JSON")
    void deberiaCargarDatosActualizadosDesdeArchivo() {
        // Arrange - El archivo ya existe con datos actualizados del test anterior
        File archivo = new File(ARCHIVO_CLIENTES);
        assertTrue(archivo.exists(), "El archivo debe existir del test anterior");
        
        // Act
        List<Cliente> clientesCargados = procesadorJson.cargarDatosCondicionalmente(
            ARCHIVO_CLIENTES, Cliente.class);
        
        // Assert
        assertNotNull(clientesCargados, "La lista cargada no debería ser null");
        assertEquals(3, clientesCargados.size(), "Deberían haberse cargado 3 clientes");
        
        // Verificar que el tercer cliente está presente
        boolean cliente3Encontrado = clientesCargados.stream()
            .anyMatch(c -> "Carlos".equals(c.getNombre()) && "11111111".equals(c.getDni()));
        assertTrue(cliente3Encontrado, "El tercer cliente debería estar presente en los datos cargados");
    }
    
    @Test
    @Order(5)
    @DisplayName("Debería manejar archivo JSON vacío")
    void deberiaManejarArchivoJsonVacio() throws IOException {
        // Arrange
        File archivoVacio = new File(ARCHIVO_CLIENTES_VACIO);
        List<Cliente> listaVacia = List.of();
        
        // Crear archivo vacío
        procesadorJson.guardarJson(ARCHIVO_CLIENTES_VACIO, listaVacia);
        
        // Act
        List<Cliente> clientesCargados = procesadorJson.cargarDatosCondicionalmente(
            ARCHIVO_CLIENTES_VACIO, Cliente.class);
        
        // Assert
        assertNotNull(clientesCargados, "La lista cargada no debería ser null");
        assertTrue(clientesCargados.isEmpty(), "La lista debería estar vacía");
        
        // Verificar que el archivo existe y tiene contenido JSON válido
        assertTrue(archivoVacio.exists(), "El archivo vacío debería existir");
        String contenido = Files.readString(archivoVacio.toPath());
        assertEquals("[ ]", contenido.trim(), "El archivo debería contener un array JSON vacío");
    }
    
    @Test
    @Order(6)
    @DisplayName("Debería devolver lista vacía para archivo inexistente")
    void deberiaDevolverListaVaciaParaArchivoInexistente() {
        // Arrange
        String rutaInexistente = RUTA_BASE + "/archivo_que_no_existe.json";
        File archivoInexistente = new File(rutaInexistente);
        
        // Asegurar que el archivo no existe
        if (archivoInexistente.exists()) {
            archivoInexistente.delete();
        }
        
        // Act
        List<Cliente> clientesCargados = procesadorJson.cargarDatosCondicionalmente(
            rutaInexistente, Cliente.class);
        
        // Assert
        assertNotNull(clientesCargados, "La lista cargada no debería ser null");
        assertTrue(clientesCargados.isEmpty(), "La lista debería estar vacía para archivo inexistente");
    }
} 