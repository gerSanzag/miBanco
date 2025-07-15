package com.mibanco.repositorioTest.internaTest;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.enums.TipoOperacionCliente;
import com.mibanco.dto.mapeador.ClienteMapeador;
import com.mibanco.repositorio.interna.BaseRepositorioImplWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para verificar el guardado automático basado en el tamaño de la lista
 */
@DisplayName("BaseRepositorio Guardado por Tamaño Tests")
class BaseRepositorioGuardadoPorTamañoTest {
    
    private TestRepositorioConGuardadoPorTamaño repositorio;
    private Cliente cliente1;
    private Cliente cliente2;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        String rutaArchivo = tempDir.resolve("test_clientes_tamaño.json").toString();
        repositorio = new TestRepositorioConGuardadoPorTamaño(rutaArchivo);
        
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
    @DisplayName("Debería guardar automáticamente cuando el tamaño llega a 10")
    void deberiaGuardarAutomaticamenteEnTamaño10() {
        // Crear 10 clientes para activar el guardado automático
        for (int i = 0; i < 10; i++) {
            Cliente cliente = Cliente.builder()
                .nombre("Cliente" + i)
                .apellido("Test" + i)
                .dni("DNI" + i)
                .email("cliente" + i + "@test.com")
                .telefono("123456789")
                .direccion("Dirección " + i)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
                
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(cliente), TipoOperacionCliente.CREAR);
            assertTrue(resultado.isPresent());
        }
        
        // Verificar que se guardó automáticamente (el archivo debería existir)
        assertTrue(repositorio.getArchivoExiste());
        assertEquals(10, repositorio.contarRegistros());
    }
    
    @Test
    @DisplayName("Debería guardar automáticamente cuando el tamaño llega a 20")
    void deberiaGuardarAutomaticamenteEnTamaño20() {
        // Crear 20 clientes para activar el segundo guardado automático
        for (int i = 0; i < 20; i++) {
            Cliente cliente = Cliente.builder()
                .nombre("Cliente" + i)
                .apellido("Test" + i)
                .dni("DNI" + i)
                .email("cliente" + i + "@test.com")
                .telefono("123456789")
                .direccion("Dirección " + i)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
                
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(cliente), TipoOperacionCliente.CREAR);
            assertTrue(resultado.isPresent());
        }
        
        // Verificar que se guardó automáticamente
        assertTrue(repositorio.getArchivoExiste());
        assertEquals(20, repositorio.contarRegistros());
    }
    
    @Test
    @DisplayName("No debería guardar automáticamente en tamaños que no son múltiplos de 10")
    void noDeberiaGuardarAutomaticamenteEnTamañosNoMultiplos() {
        // Crear 9 clientes (no múltiplo de 10)
        for (int i = 0; i < 9; i++) {
            Cliente cliente = Cliente.builder()
                .nombre("Cliente" + i)
                .apellido("Test" + i)
                .dni("DNI" + i)
                .email("cliente" + i + "@test.com")
                .telefono("123456789")
                .direccion("Dirección " + i)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
                
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(cliente), TipoOperacionCliente.CREAR);
            assertTrue(resultado.isPresent());
        }
        
        // Verificar que NO se guardó automáticamente
        assertFalse(repositorio.getArchivoExiste());
        assertEquals(9, repositorio.contarRegistros());
    }
    
    @Test
    @DisplayName("Debería permitir guardado manual con guardarDatos()")
    void deberiaPermitirGuardadoManual() {
        // Crear 5 clientes (no múltiplo de 10)
        for (int i = 0; i < 5; i++) {
            Cliente cliente = Cliente.builder()
                .nombre("Cliente" + i)
                .apellido("Test" + i)
                .dni("DNI" + i)
                .email("cliente" + i + "@test.com")
                .telefono("123456789")
                .direccion("Dirección " + i)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
                
            Optional<Cliente> resultado = repositorio.crearRegistro(Optional.of(cliente), TipoOperacionCliente.CREAR);
            assertTrue(resultado.isPresent());
        }
        
        // Verificar que NO se guardó automáticamente
        assertFalse(repositorio.getArchivoExiste());
        
        // Forzar guardado manual
        repositorio.guardarDatos();
        
        // Verificar que ahora sí existe el archivo
        assertTrue(repositorio.getArchivoExiste());
        assertEquals(5, repositorio.contarRegistros());
    }
    
    @Test
    @DisplayName("No debería guardar si la lista está vacía")
    void noDeberiaGuardarSiListaVacia() {
        // No crear ningún cliente
        assertEquals(0, repositorio.contarRegistros());
        
        // Forzar guardado manual
        repositorio.guardarDatos();
        
        // Verificar que NO se creó el archivo porque la lista está vacía
        assertFalse(repositorio.getArchivoExiste());
    }
    
    /**
     * Clase de prueba que extiende BaseRepositorioImpl para testing
     */
    private static class TestRepositorioConGuardadoPorTamaño extends BaseRepositorioImplWrapper<Cliente, Long, TipoOperacionCliente> {
        
        private final String rutaArchivo;
        
        public TestRepositorioConGuardadoPorTamaño(String rutaArchivo) {
            this.rutaArchivo = rutaArchivo;
        }
        
        @Override
        protected Cliente crearConNuevoId(Cliente entidad) {
            ClienteMapeador mapeador = new ClienteMapeador();
            return mapeador.aDtoDirecto(entidad)
                .map(dto -> dto.toBuilder()
                    .id(1L)
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
        
        public boolean getArchivoExiste() {
            return new File(rutaArchivo).exists();
        }
    }
} 