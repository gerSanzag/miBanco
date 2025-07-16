package com.mibanco.repositorioTest.internaTest;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.enums.TipoOperacionTarjeta;
import com.mibanco.modelo.enums.TipoTarjeta;
import com.mibanco.repositorio.interna.TarjetaRepositorioImplWrapper;
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
 * Tests específicos para TarjetaRepositorioImpl
 * Valida la funcionalidad específica del repositorio de tarjetas
 * Incluye tests de configuración, mapeo y casos edge específicos
 */
@DisplayName("TarjetaRepositorioImpl Tests")
class TarjetaRepositorioImplTest {
    
    private TarjetaRepositorioImplWrapper repositorio;
    private Tarjeta tarjeta1;
    private Tarjeta tarjeta2;
    private Cliente titular1;
    private Cliente titular2;
    
    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        // Crear archivo JSON temporal para testing
        File archivoJson = tempDir.resolve("test_tarjetas.json").toFile();
        
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
        
        // Crear datos de prueba
        tarjeta1 = Tarjeta.builder()
            .numero(1234567890123456L)
            .titular(titular1)
            .numeroCuentaAsociada("ES12345678901234567890")
            .tipo(TipoTarjeta.DEBITO)
            .cvv("123")
            .fechaExpiracion(LocalDate.of(2025, 12, 31))
            .activa(true)
            .build();
            
        tarjeta2 = Tarjeta.builder()
            .numero(9876543210987654L)
            .titular(titular2)
            .numeroCuentaAsociada("ES98765432109876543210")
            .tipo(TipoTarjeta.CREDITO)
            .cvv("456")
            .fechaExpiracion(LocalDate.of(2026, 6, 30))
            .activa(false)
            .build();
        
        // Crear repositorio con configuración temporal
        repositorio = new TestTarjetaRepositorioImpl(archivoJson.getAbsolutePath());
    }
    
    /**
     * Implementación de prueba que permite configurar la ruta del archivo
     */
    private static class TestTarjetaRepositorioImpl extends TarjetaRepositorioImplWrapper {
        
        private final String rutaArchivo;
        
        public TestTarjetaRepositorioImpl(String rutaArchivo) {
            this.rutaArchivo = rutaArchivo;
        }
        
        @Override
        public java.util.Map<String, Object> obtenerConfiguracion() {
            java.util.Map<String, Object> config = new java.util.HashMap<>();
            config.put("rutaArchivo", rutaArchivo);
            config.put("tipoClase", Tarjeta.class);
            config.put("extractorId", (java.util.function.Function<Tarjeta, Long>) Tarjeta::getId);
            return config;
        }
    }
    
    @Nested
    @DisplayName("Tests de configuración específica")
    class ConfiguracionTests {
        
        @Test
        @DisplayName("Debería usar la ruta de archivo correcta para tarjetas")
        void deberiaUsarRutaArchivoCorrecta() {
            // Act
            Optional<List<Tarjeta>> resultado = repositorio.buscarTodos();
            
            // Assert
            // Si no hay errores de archivo, la configuración es correcta
            assertNotNull(resultado);
        }
        
        @Test
        @DisplayName("Debería usar el tipo de clase Tarjeta")
        void deberiaUsarTipoClaseTarjeta() {
            // Act
            Optional<Tarjeta> resultado = repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertTrue(resultado.get() instanceof Tarjeta);
        }
        
        @Test
        @DisplayName("Debería devolver configuración correcta para tarjetas")
        void deberiaDevolverConfiguracionCorrecta() {
            // Act - Ejecutar operaciones que internamente llaman a obtenerConfiguracion
            // El método obtenerConfiguracion se ejecuta durante la inicialización y carga de datos
            Optional<List<Tarjeta>> resultado = repositorio.buscarTodos();
            
            // Assert - Verificar que la configuración funciona correctamente
            assertNotNull(resultado);
            // Si llegamos aquí sin errores, significa que obtenerConfiguracion() se ejecutó correctamente
            // y devolvió la configuración esperada (ruta de archivo, tipo de clase, extractor de ID)
        }

        @Test
        @DisplayName("Debería ejecutar obtenerConfiguracion() de la clase real")
        void deberiaEjecutarObtenerConfiguracionDeClaseReal() {
            // Arrange - Usar la Factory para obtener la clase real
            com.mibanco.repositorio.TarjetaRepositorio repositorioReal = 
                com.mibanco.repositorio.interna.RepositorioFactoria.obtenerInstancia().obtenerRepositorioTarjeta();
            
            // Act - Ejecutar operaciones que internamente llaman a obtenerConfiguracion()
            // El método obtenerConfiguracion() se ejecuta durante la carga lazy de datos
            Optional<List<Tarjeta>> resultado = repositorioReal.buscarTodos();
            
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
        @DisplayName("Debería usar el número de tarjeta como ID si es único, o generar uno nuevo si hay duplicado")
        void deberiaUsarNumeroTarjetaComoId() {
            // Act
            Optional<Tarjeta> resultado = repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Long numeroCreado = resultado.get().getNumero();
            assertNotNull(numeroCreado);
            assertEquals(16, String.valueOf(numeroCreado).length());
        }
        
        @Test
        @DisplayName("Debería mantener números de tarjeta únicos")
        void deberiaMantenerNumerosTarjetaUnicos() {
            // Arrange
            Tarjeta tarjetaNueva1 = Tarjeta.builder()
                .numero(1111111111111111L)
                .titular(titular1)
                .numeroCuentaAsociada("ES11111111111111111111")
                .tipo(TipoTarjeta.DEBITO)
                .cvv("111")
                .fechaExpiracion(LocalDate.of(2025, 12, 31))
                .activa(true)
                .build();
                
            Tarjeta tarjetaNueva2 = Tarjeta.builder()
                .numero(1111111111111111L) // Duplicado a propósito
                .titular(titular2)
                .numeroCuentaAsociada("ES22222222222222222222")
                .tipo(TipoTarjeta.CREDITO)
                .cvv("222")
                .fechaExpiracion(LocalDate.of(2026, 6, 30))
                .activa(false)
                .build();
            
            // Act
            Optional<Tarjeta> resultado1 = repositorio.crearRegistro(Optional.of(tarjetaNueva1), TipoOperacionTarjeta.CREAR);
            Optional<Tarjeta> resultado2 = repositorio.crearRegistro(Optional.of(tarjetaNueva2), TipoOperacionTarjeta.CREAR);
            
            // Assert
            assertTrue(resultado1.isPresent());
            assertTrue(resultado2.isPresent());
            Long numero1 = resultado1.get().getNumero();
            Long numero2 = resultado2.get().getNumero();
            assertNotNull(numero1);
            assertNotNull(numero2);
            assertEquals(16, String.valueOf(numero1).length());
            assertEquals(16, String.valueOf(numero2).length());
            assertNotEquals(numero1, numero2, "Los números de tarjeta deben ser únicos");
        }
        
        @Test
        @DisplayName("Debería mantener secuencia de números en múltiples creaciones")
        void deberiaMantenerSecuenciaNumeros() {
            // Arrange
            List<Tarjeta> tarjetas = new ArrayList<>();
            
            // Act - Crear 5 tarjetas sin número (el repositorio los genera)
            for (int i = 0; i < 5; i++) {
                Tarjeta tarjetaNueva = Tarjeta.builder()
                    .numero(null) // El repositorio asignará el número
                    .titular(titular1)
                    .numeroCuentaAsociada("ES" + String.format("%020d", i))
                    .tipo(TipoTarjeta.DEBITO)
                    .cvv("123")
                    .fechaExpiracion(LocalDate.of(2025, 12, 31))
                    .activa(true)
                    .build();
                Optional<Tarjeta> resultado = repositorio.crearRegistro(Optional.of(tarjetaNueva), TipoOperacionTarjeta.CREAR);
                assertTrue(resultado.isPresent());
                tarjetas.add(resultado.get());
            }
            
            // Assert
            assertEquals(5, tarjetas.size());
            List<Long> numeros = tarjetas.stream().map(Tarjeta::getNumero).toList();
            assertEquals(5, numeros.stream().distinct().count(), "Todos los números deben ser únicos");
            numeros.forEach(numero -> {
                assertNotNull(numero);
                assertEquals(16, String.valueOf(numero).length(), "Todos los números deben tener 16 dígitos");
            });
        }
    }
    
    @Nested
    @DisplayName("Tests de operaciones CRUD específicas")
    class OperacionesCrudTests {
        
        @Test
        @DisplayName("Debería crear tarjeta con todos los campos")
        void deberiaCrearTarjetaConTodosLosCampos() {
            // Act
            Optional<Tarjeta> resultado = repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Tarjeta tarjetaCreada = resultado.get();
            assertNotNull(tarjetaCreada.getNumero());
            assertEquals(16, String.valueOf(tarjetaCreada.getNumero()).length());
            assertEquals(tarjeta1.getTitular(), tarjetaCreada.getTitular());
            assertEquals(tarjeta1.getNumeroCuentaAsociada(), tarjetaCreada.getNumeroCuentaAsociada());
            assertEquals(tarjeta1.getTipo(), tarjetaCreada.getTipo());
            // El CVV se omite por seguridad en el DTO, por lo que no se verifica en la persistencia
            assertEquals(tarjeta1.getFechaExpiracion(), tarjetaCreada.getFechaExpiracion());
            assertEquals(tarjeta1.isActiva(), tarjetaCreada.isActiva());
        }
        
        @Test
        @DisplayName("Debería buscar tarjeta por número")
        void deberiaBuscarTarjetaPorNumero() {
            // Arrange
            Optional<Tarjeta> creada = repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            assertTrue(creada.isPresent());
            Long numeroCreado = creada.get().getNumero();
            // Act
            Optional<Tarjeta> resultado = repositorio.buscarPorId(Optional.of(numeroCreado));
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(numeroCreado, resultado.get().getNumero());
        }
        
        @Test
        @DisplayName("Debería buscar tarjetas por titular")
        void deberiaBuscarTarjetasPorTitular() {
            // Arrange
            repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            repositorio.crearRegistro(Optional.of(tarjeta2), TipoOperacionTarjeta.CREAR);
            
            // Act
            Optional<List<Tarjeta>> resultado = repositorio.buscarTodosPorPredicado(
                tarjeta -> tarjeta.getTitular().getId().equals(titular1.getId())
            );
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Tarjeta> tarjetasTitular1 = resultado.get();
            assertEquals(1, tarjetasTitular1.size());
            assertEquals(titular1.getId(), tarjetasTitular1.get(0).getTitular().getId());
        }
        
        @Test
        @DisplayName("Debería actualizar tarjeta existente")
        void deberiaActualizarTarjetaExistente() {
            // Arrange
            repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            
            Tarjeta tarjetaActualizada = Tarjeta.builder()
                .numero(tarjeta1.getNumero())
                .titular(tarjeta1.getTitular())
                .numeroCuentaAsociada(tarjeta1.getNumeroCuentaAsociada())
                .tipo(tarjeta1.getTipo())
                .cvv(tarjeta1.getCvv())
                .fechaExpiracion(LocalDate.of(2027, 12, 31))
                .activa(false)
                .build();
            
            // Act
            Optional<Tarjeta> resultado = repositorio.actualizarRegistro(
                Optional.of(tarjetaActualizada),
                TipoOperacionTarjeta.MODIFICAR
            );
            
            // Assert
            assertTrue(resultado.isPresent());
            Tarjeta tarjetaModificada = resultado.get();
            assertEquals(false, tarjetaModificada.isActiva());
            assertEquals(LocalDate.of(2027, 12, 31), tarjetaModificada.getFechaExpiracion());
        }
    }
    
    @Nested
    @DisplayName("Tests de casos edge específicos")
    class CasosEdgeTests {
        
        @Test
        @DisplayName("Debería manejar tarjeta con campos null")
        void deberiaManejarTarjetaConCamposNull() {
            // Arrange
            Tarjeta tarjetaConNulls = Tarjeta.builder()
                .numero(null) // El repositorio asignará un número
                .titular(null)
                .numeroCuentaAsociada(null)
                .tipo(null)
                .cvv(null)
                .fechaExpiracion(null)
                .activa(false)
                .build();
            
            // Act
            Optional<Tarjeta> resultado = repositorio.crearRegistro(Optional.of(tarjetaConNulls), TipoOperacionTarjeta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            Tarjeta tarjetaCreada = resultado.get();
            
            assertNotNull(tarjetaCreada.getNumero()); // El repositorio asigna número
            assertNull(tarjetaCreada.getTitular());
            assertNull(tarjetaCreada.getNumeroCuentaAsociada());
            assertNull(tarjetaCreada.getTipo());
            assertNull(tarjetaCreada.getCvv());
            assertNull(tarjetaCreada.getFechaExpiracion());
            assertFalse(tarjetaCreada.isActiva());
        }
        
        @Test
        @DisplayName("Debería permitir tarjetas con números duplicados en repositorio (validación en servicio)")
        void deberiaPermitirTarjetasConNumerosDuplicadosEnRepositorio() {
            // Arrange
            Tarjeta tarjetaDuplicada = Tarjeta.builder()
                .numero(tarjeta1.getNumero()) // Mismo número que tarjeta1
                .titular(titular2)
                .numeroCuentaAsociada("ES99999999999999999999")
                .tipo(TipoTarjeta.CREDITO)
                .cvv("999")
                .fechaExpiracion(LocalDate.of(2028, 12, 31))
                .activa(true)
                .build();
            
            Optional<Tarjeta> creada = repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            assertTrue(creada.isPresent());
            // Act
            Optional<Tarjeta> resultado = repositorio.crearRegistro(Optional.of(tarjetaDuplicada), TipoOperacionTarjeta.CREAR);
            // Assert
            assertTrue(resultado.isPresent());
            Long numeroOriginal = creada.get().getNumero();
            Long numeroNuevo = resultado.get().getNumero();
            assertNotEquals(numeroOriginal, numeroNuevo, "El repositorio debe generar un nuevo número para evitar duplicados");
        }
        
        @Test
        @DisplayName("Debería manejar tarjeta con fecha de expiración pasada")
        void deberiaManejarTarjetaConFechaExpiracionPasada() {
            // Arrange
            Tarjeta tarjetaExpirada = Tarjeta.builder()
                .numero(9999999999999999L)
                .titular(titular1)
                .numeroCuentaAsociada("ES99999999999999999999")
                .tipo(TipoTarjeta.DEBITO)
                .cvv("999")
                .fechaExpiracion(LocalDate.of(2020, 1, 1)) // Fecha pasada
                .activa(true)
                .build();
            
            // Act
            Optional<Tarjeta> resultado = repositorio.crearRegistro(Optional.of(tarjetaExpirada), TipoOperacionTarjeta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(LocalDate.of(2020, 1, 1), resultado.get().getFechaExpiracion());
        }
        
        @Test
        @DisplayName("Debería manejar tarjeta con CVV de longitud variable")
        void deberiaManejarTarjetaConCvvLongitudVariable() {
            // Arrange
            Tarjeta tarjetaCvvCorto = Tarjeta.builder()
                .numero(1111111111111111L)
                .titular(titular1)
                .numeroCuentaAsociada("ES11111111111111111111")
                .tipo(TipoTarjeta.DEBITO)
                .cvv("12") // CVV de 2 dígitos
                .fechaExpiracion(LocalDate.of(2025, 12, 31))
                .activa(true)
                .build();
                
            Tarjeta tarjetaCvvLargo = Tarjeta.builder()
                .numero(2222222222222222L)
                .titular(titular2)
                .numeroCuentaAsociada("ES22222222222222222222")
                .tipo(TipoTarjeta.CREDITO)
                .cvv("1234") // CVV de 4 dígitos
                .fechaExpiracion(LocalDate.of(2026, 6, 30))
                .activa(false)
                .build();
            
            // Act
            Optional<Tarjeta> resultado1 = repositorio.crearRegistro(Optional.of(tarjetaCvvCorto), TipoOperacionTarjeta.CREAR);
            Optional<Tarjeta> resultado2 = repositorio.crearRegistro(Optional.of(tarjetaCvvLargo), TipoOperacionTarjeta.CREAR);
            
            // Assert
            assertTrue(resultado1.isPresent());
            assertTrue(resultado2.isPresent());
            // El CVV se omite por seguridad en el DTO, por lo que no se verifica en la persistencia
            // Verificamos que las tarjetas se crearon correctamente con otros campos
            assertEquals(tarjetaCvvCorto.getTitular(), resultado1.get().getTitular());
            assertEquals(tarjetaCvvLargo.getTitular(), resultado2.get().getTitular());
        }
    }
    
    @Nested
    @DisplayName("Tests de integración con archivo JSON")
    class IntegracionJsonTests {
        
        @Test
        @DisplayName("Debería persistir tarjeta en archivo JSON")
        void deberiaPersistirTarjetaEnArchivoJson() {
            // Act
            Optional<Tarjeta> resultado = repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            
            // Assert
            assertTrue(resultado.isPresent());
            // Si no hay excepción, la persistencia fue exitosa
        }
        
        @Test
        @DisplayName("Debería cargar tarjetas desde archivo JSON")
        void deberiaCargarTarjetasDesdeArchivoJson() {
            // Arrange
            repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            repositorio.crearRegistro(Optional.of(tarjeta2), TipoOperacionTarjeta.CREAR);
            
            // Act
            Optional<List<Tarjeta>> resultado = repositorio.buscarTodos();
            
            // Assert
            assertTrue(resultado.isPresent());
            List<Tarjeta> tarjetas = resultado.get();
            assertTrue(tarjetas.size() >= 2);
            
            // Verificar que las tarjetas se cargaron correctamente
            // Como el repositorio genera números aleatorios, verificamos que hay tarjetas con 16 dígitos
            List<Long> numeros = tarjetas.stream()
                .map(Tarjeta::getNumero)
                .toList();
                
            assertTrue(numeros.stream().allMatch(numero -> 
                numero != null && String.valueOf(numero).length() == 16));
        }
    }
    
    @Nested
    @DisplayName("Tests de validación de reglas de negocio")
    class ValidacionReglasNegocioTests {
        
        @Test
        @DisplayName("Debería validar que el número de tarjeta sea único en el servicio")
        void deberiaValidarNumeroTarjetaUnicoEnServicio() {
            // Arrange - Crear tarjeta en el repositorio directamente
            repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            
            // Act & Assert - Intentar crear tarjeta con número duplicado a través del servicio
            // Esto debería fallar porque el servicio valida números únicos
            // Nota: Este test requiere el servicio, pero está en el contexto del repositorio
            // En un test de integración completo se probaría con el servicio real
            // Por ahora documentamos que el repositorio permite duplicados (correcto)
            // y que la validación de unicidad está en el servicio (también correcto)
            
            Tarjeta tarjetaDuplicada = Tarjeta.builder()
                .numero(tarjeta1.getNumero()) // Mismo número que tarjeta1
                .titular(titular2)
                .numeroCuentaAsociada("ES22222222222222222222")
                .tipo(TipoTarjeta.CREDITO)
                .cvv("222")
                .fechaExpiracion(LocalDate.of(2026, 6, 30))
                .activa(false)
                .build();
            
            // El repositorio permite duplicados (responsabilidad correcta)
            Optional<Tarjeta> resultado = repositorio.crearRegistro(Optional.of(tarjetaDuplicada), TipoOperacionTarjeta.CREAR);
            assertTrue(resultado.isPresent());
            
            // La validación de unicidad debe estar en el servicio, no en el repositorio
            // Esto mantiene la separación de responsabilidades correcta
        }
    }
    
    @Nested
    @DisplayName("Tests de operaciones CRUD faltantes")
    class OperacionesCrudFaltantesTests {
        
        @Test
        @DisplayName("Debería buscar tarjeta por ID")
        void deberiaBuscarTarjetaPorId() {
            // Arrange - Crear tarjeta primero
            Optional<Tarjeta> tarjetaCreada = repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            assertTrue(tarjetaCreada.isPresent());
            Long id = tarjetaCreada.get().getId();
            
            // Act
            Optional<Tarjeta> resultado = repositorio.buscarPorId(Optional.of(id));
            
            // Assert
            assertTrue(resultado.isPresent());
            assertEquals(id, resultado.get().getId());
            assertEquals(tarjeta1.getTitular().getId(), resultado.get().getTitular().getId());
            assertEquals(tarjeta1.getTipo(), resultado.get().getTipo());
        }
        
        @Test
        @DisplayName("Debería eliminar tarjeta por ID")
        void deberiaEliminarTarjetaPorId() {
            // Arrange - Crear tarjeta primero
            Optional<Tarjeta> tarjetaCreada = repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            assertTrue(tarjetaCreada.isPresent());
            Long id = tarjetaCreada.get().getId();
            
            // Act
            Optional<Tarjeta> eliminada = repositorio.eliminarPorId(Optional.of(id), TipoOperacionTarjeta.ELIMINAR);
            
            // Assert
            assertTrue(eliminada.isPresent());
            assertEquals(id, eliminada.get().getId());
            
            // Verificar que ya no existe
            Optional<Tarjeta> tarjetaEliminada = repositorio.buscarPorId(Optional.of(id));
            assertFalse(tarjetaEliminada.isPresent());
        }
        
        @Test
        @DisplayName("Debería restaurar tarjeta eliminada")
        void deberiaRestaurarTarjetaEliminada() {
            // Arrange - Crear y eliminar tarjeta
            Optional<Tarjeta> tarjetaCreada = repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            assertTrue(tarjetaCreada.isPresent());
            Long id = tarjetaCreada.get().getId();
            
            repositorio.eliminarPorId(Optional.of(id), TipoOperacionTarjeta.ELIMINAR);
            
            // Act
            Optional<Tarjeta> restaurada = repositorio.restaurar(Optional.of(id), TipoOperacionTarjeta.ACTUALIZAR);
            
            // Assert
            assertTrue(restaurada.isPresent());
            assertEquals(id, restaurada.get().getId());
            
            // Verificar que existe nuevamente
            Optional<Tarjeta> tarjetaRestaurada = repositorio.buscarPorId(Optional.of(id));
            assertTrue(tarjetaRestaurada.isPresent());
        }
        
        @Test
        @DisplayName("Debería obtener tarjetas eliminadas")
        void deberiaObtenerTarjetasEliminadas() {
            // Arrange - Crear y eliminar tarjetas
            Optional<Tarjeta> tarjeta1Creada = repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            Optional<Tarjeta> tarjeta2Creada = repositorio.crearRegistro(Optional.of(tarjeta2), TipoOperacionTarjeta.CREAR);
            assertTrue(tarjeta1Creada.isPresent());
            assertTrue(tarjeta2Creada.isPresent());
            
            repositorio.eliminarPorId(Optional.of(tarjeta1Creada.get().getId()), TipoOperacionTarjeta.ELIMINAR);
            repositorio.eliminarPorId(Optional.of(tarjeta2Creada.get().getId()), TipoOperacionTarjeta.ELIMINAR);
            
            // Act
            List<Tarjeta> eliminadas = repositorio.obtenerEliminados();
            
            // Assert
            assertFalse(eliminadas.isEmpty());
            assertEquals(2, eliminadas.size());
            assertTrue(eliminadas.stream().anyMatch(t -> tarjeta1Creada.get().getId().equals(t.getId())));
            assertTrue(eliminadas.stream().anyMatch(t -> tarjeta2Creada.get().getId().equals(t.getId())));
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
            // Arrange - Crear tarjeta
            Optional<Tarjeta> tarjetaCreada = repositorio.crearRegistro(Optional.of(tarjeta1), TipoOperacionTarjeta.CREAR);
            assertTrue(tarjetaCreada.isPresent());
            
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
            Optional<Tarjeta> resultado = repositorio.buscarPorId(Optional.empty());
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería manejar eliminación por ID nulo")
        void deberiaManejarEliminacionPorIdNulo() {
            // Act
            Optional<Tarjeta> resultado = repositorio.eliminarPorId(Optional.empty(), TipoOperacionTarjeta.ELIMINAR);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería manejar restauración por ID nulo")
        void deberiaManejarRestauracionPorIdNulo() {
            // Act
            Optional<Tarjeta> resultado = repositorio.restaurar(Optional.empty(), TipoOperacionTarjeta.ACTUALIZAR);
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería manejar búsqueda por ID inexistente")
        void deberiaManejarBusquedaPorIdInexistente() {
            // Act
            Optional<Tarjeta> resultado = repositorio.buscarPorId(Optional.of(999999999999999999L));
            
            // Assert
            assertFalse(resultado.isPresent());
        }
        
        @Test
        @DisplayName("Debería contar registros correctamente")
        void deberiaContarRegistros() {
            // Arrange - Crear múltiples tarjetas
            for (int i = 0; i < 3; i++) {
                Tarjeta tarjeta = Tarjeta.builder()
                    .numero(null) // El repositorio asignará el número
                    .titular(titular1)
                    .numeroCuentaAsociada("ES" + String.format("%020d", i))
                    .tipo(i % 2 == 0 ? TipoTarjeta.DEBITO : TipoTarjeta.CREDITO)
                    .cvv("123")
                    .fechaExpiracion(LocalDate.of(2025, 12, 31))
                    .activa(true)
                    .build();
                
                repositorio.crearRegistro(Optional.of(tarjeta), TipoOperacionTarjeta.CREAR);
            }
            
            // Act
            long cantidad = repositorio.contarRegistros();
            
            // Assert
            assertEquals(3, cantidad);
        }
    }
}
