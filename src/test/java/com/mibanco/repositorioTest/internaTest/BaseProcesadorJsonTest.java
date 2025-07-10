package com.mibanco.repositorioTest.internaTest;

import com.mibanco.repositorio.interna.BaseProcesadorJsonWrapper;
import com.mibanco.modelo.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para BaseProcesadorJson usando Test-Specific Subclass
 * Valida el procesamiento de archivos JSON de forma genérica
 */
@DisplayName("BaseProcesadorJson Tests")
class BaseProcesadorJsonTest {
    
    private BaseProcesadorJsonWrapper<Cliente> procesadorJson;
    private Cliente cliente1;
    private Cliente cliente2;
    
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
    
    @Nested
    @DisplayName("Tests para calcularMaximoId")
    class CalcularMaximoIdTests {
        
        @Test
        @DisplayName("Debería calcular el ID máximo de una lista con elementos")
        void deberiaCalcularMaximoIdConElementos() {
            // Arrange
            List<Cliente> clientes = List.of(cliente1, cliente2);
            Function<Cliente, Long> extractorId = Cliente::getId;
            
            // Act
            Long maxId = procesadorJson.calcularMaximoId(clientes, extractorId);
            
            // Assert
            assertEquals(5L, maxId);
        }
        
        @Test
        @DisplayName("Debería devolver 0 para lista vacía")
        void deberiaDevolverCeroParaListaVacia() {
            // Arrange
            List<Cliente> clientesVacios = List.of();
            Function<Cliente, Long> extractorId = Cliente::getId;
            
            // Act
            Long maxId = procesadorJson.calcularMaximoId(clientesVacios, extractorId);
            
            // Assert
            assertEquals(0L, maxId);
        }
        
        @Test
        @DisplayName("Debería manejar lista con un solo elemento")
        void deberiaManejarListaConUnElemento() {
            // Arrange
            List<Cliente> unCliente = List.of(cliente1);
            Function<Cliente, Long> extractorId = Cliente::getId;
            
            // Act
            Long maxId = procesadorJson.calcularMaximoId(unCliente, extractorId);
            
            // Assert
            assertEquals(1L, maxId);
        }
    }
    
    @Nested
    @DisplayName("Tests para guardarJson")
    class GuardarJsonTests {
        
        @Test
        @DisplayName("Debería guardar lista de clientes en archivo JSON")
        void deberiaGuardarListaEnJson(@TempDir Path tempDir) throws IOException {
            // Arrange
            List<Cliente> clientes = List.of(cliente1, cliente2);
            File archivoJson = tempDir.resolve("clientes_test.json").toFile();
            
            // Act
            procesadorJson.guardarJson(archivoJson.getAbsolutePath(), clientes);
            
            // Assert
            assertTrue(archivoJson.exists());
            assertTrue(archivoJson.length() > 0);
        }
        
        @Test
        @DisplayName("Debería guardar lista vacía en archivo JSON")
        void deberiaGuardarListaVaciaEnJson(@TempDir Path tempDir) throws IOException {
            // Arrange
            List<Cliente> clientesVacios = List.of();
            File archivoJson = tempDir.resolve("clientes_vacios_test.json").toFile();
            
            // Act
            procesadorJson.guardarJson(archivoJson.getAbsolutePath(), clientesVacios);
            
            // Assert
            assertTrue(archivoJson.exists());
        }
        
        @Test
        @DisplayName("Debería crear directorio si no existe")
        void deberiaCrearDirectorioSiNoExiste(@TempDir Path tempDir) throws IOException {
            // Arrange
            List<Cliente> clientes = List.of(cliente1);
            File directorioNuevo = tempDir.resolve("nuevo_directorio").toFile();
            File archivoJson = new File(directorioNuevo, "clientes_test.json");
            
            // Act
            procesadorJson.guardarJson(archivoJson.getAbsolutePath(), clientes);
            
            // Assert
            assertTrue(directorioNuevo.exists());
            assertTrue(archivoJson.exists());
        }
    }
    
    @Nested
    @DisplayName("Tests para cargarDatosCondicionalmente")
    class CargarDatosCondicionalmenteTests {
        
        @Test
        @DisplayName("Debería cargar datos desde archivo JSON existente")
        void deberiaCargarDatosDesdeArchivoExistente(@TempDir Path tempDir) throws IOException {
            // Arrange
            List<Cliente> clientesOriginales = List.of(cliente1, cliente2);
            File archivoJson = tempDir.resolve("clientes_cargar_test.json").toFile();
            
            // Guardar datos primero
            procesadorJson.guardarJson(archivoJson.getAbsolutePath(), clientesOriginales);
            
            // DEBUG: Verificar que el archivo se creó y tiene contenido
            System.out.println("=== DEBUG: Contenido del archivo JSON ===");
            System.out.println("Archivo existe: " + archivoJson.exists());
            System.out.println("Tamaño del archivo: " + archivoJson.length() + " bytes");
            if (archivoJson.exists()) {
                String contenido = java.nio.file.Files.readString(archivoJson.toPath());
                System.out.println("Contenido del JSON:");
                System.out.println(contenido);
            }
            System.out.println("=========================================");
            
            // Act
            List<Cliente> clientesCargados = procesadorJson.cargarDatosCondicionalmente(
                archivoJson.getAbsolutePath(), Cliente.class);
            
            // DEBUG: Verificar qué se cargó
            System.out.println("=== DEBUG: Resultado de carga ===");
            System.out.println("Clientes cargados: " + clientesCargados.size());
            System.out.println("Lista cargada: " + clientesCargados);
            System.out.println("=================================");
            
            // Assert
            assertNotNull(clientesCargados);
            assertEquals(2, clientesCargados.size());
            assertEquals(cliente1.getId(), clientesCargados.get(0).getId());
            assertEquals(cliente2.getId(), clientesCargados.get(1).getId());
        }
        
        @Test
        @DisplayName("Debería devolver lista vacía para archivo inexistente")
        void deberiaDevolverListaVaciaParaArchivoInexistente() {
            // Arrange
            String rutaInexistente = "/ruta/que/no/existe/archivo.json";
            
            // Act
            List<Cliente> clientesCargados = procesadorJson.cargarDatosCondicionalmente(
                rutaInexistente, Cliente.class);
            
            // Assert
            assertNotNull(clientesCargados);
            assertTrue(clientesCargados.isEmpty());
        }
        
        @Test
        @DisplayName("Debería cargar lista vacía desde archivo JSON vacío")
        void deberiaCargarListaVaciaDesdeArchivoVacio(@TempDir Path tempDir) throws IOException {
            // Arrange
            List<Cliente> clientesVacios = List.of();
            File archivoJson = tempDir.resolve("clientes_vacios_cargar_test.json").toFile();
            
            // Guardar lista vacía
            procesadorJson.guardarJson(archivoJson.getAbsolutePath(), clientesVacios);
            
            // Act
            List<Cliente> clientesCargados = procesadorJson.cargarDatosCondicionalmente(
                archivoJson.getAbsolutePath(), Cliente.class);
            
            // Assert
            assertNotNull(clientesCargados);
            assertTrue(clientesCargados.isEmpty());
        }
        
        @Test
        @DisplayName("Debería manejar archivo JSON con formato inválido")
        void deberiaManejarArchivoJsonInvalido(@TempDir Path tempDir) throws IOException {
            // Arrange
            File archivoJson = tempDir.resolve("archivo_invalido.json").toFile();
            
            // Crear archivo con contenido inválido
            java.nio.file.Files.write(archivoJson.toPath(), "contenido inválido".getBytes());
            
            // Act
            List<Cliente> clientesCargados = procesadorJson.cargarDatosCondicionalmente(
                archivoJson.getAbsolutePath(), Cliente.class);
            
            // Assert
            assertNotNull(clientesCargados);
            assertTrue(clientesCargados.isEmpty()); // Debería devolver lista vacía en caso de error
        }
    }
} 