package com.mibanco.repositorioTest.internaTest;

import com.mibanco.repositorio.AuditoriaRepositorio;
import com.mibanco.repositorio.ClienteRepositorio;
import com.mibanco.repositorio.CuentaRepositorio;
import com.mibanco.repositorio.TarjetaRepositorio;
import com.mibanco.repositorio.TransaccionRepositorio;
import com.mibanco.repositorio.interna.RepositorioFactoria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para RepositorioFactoria
 * Verifica el patrón Singleton con Double-Checked Locking
 */
@DisplayName("RepositorioFactoria Tests")
class RepositorioFactoriaTest {

    @Nested
    @DisplayName("Tests para obtenerInstancia")
    class ObtenerInstanciaTests {

        @Test
        @DisplayName("Debería retornar la misma instancia en llamadas consecutivas")
        void deberiaRetornarMismaInstancia() {
            // Act
            RepositorioFactoria instancia1 = RepositorioFactoria.obtenerInstancia();
            RepositorioFactoria instancia2 = RepositorioFactoria.obtenerInstancia();
            RepositorioFactoria instancia3 = RepositorioFactoria.obtenerInstancia();

            // Assert
            assertNotNull(instancia1);
            assertSame(instancia1, instancia2);
            assertSame(instancia2, instancia3);
        }

        @Test
        @DisplayName("Debería manejar concurrencia correctamente (cubrir el segundo if)")
        void deberiaManejarConcurrenciaCorrectamente() throws InterruptedException {
            // Arrange
            int numThreads = 10;
            int numCallsPerThread = 100;
            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            CountDownLatch latch = new CountDownLatch(numThreads);
            List<RepositorioFactoria> instancias = new ArrayList<>();
            AtomicInteger contadorCreaciones = new AtomicInteger(0);

            // Act - Crear múltiples hilos que llamen obtenerInstancia simultáneamente
            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < numCallsPerThread; j++) {
                            RepositorioFactoria instancia = RepositorioFactoria.obtenerInstancia();
                            synchronized (instancias) {
                                instancias.add(instancia);
                            }
                            // Pequeña pausa para aumentar la probabilidad de race condition
                            Thread.sleep(1);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            // Esperar a que todos los hilos terminen
            latch.await();
            executor.shutdown();

            // Assert
            assertFalse(instancias.isEmpty());
            
            // Todas las instancias deben ser la misma (Singleton)
            RepositorioFactoria primeraInstancia = instancias.get(0);
            for (RepositorioFactoria instancia : instancias) {
                assertSame(primeraInstancia, instancia);
            }

            // Verificar que solo se creó una instancia
            assertEquals(1, instancias.stream().distinct().count());
        }
    }

    @Nested
    @DisplayName("Tests para obtener repositorios")
    class ObtenerRepositoriosTests {

        @Test
        @DisplayName("Debería obtener repositorio de auditoría")
        void deberiaObtenerRepositorioAuditoria() {
            // Arrange
            RepositorioFactoria factoria = RepositorioFactoria.obtenerInstancia();

            // Act
            AuditoriaRepositorio repositorio = factoria.obtenerRepositorioAuditoria();

            // Assert
            assertNotNull(repositorio);
        }

        @Test
        @DisplayName("Debería obtener repositorio de clientes")
        void deberiaObtenerRepositorioCliente() {
            // Arrange
            RepositorioFactoria factoria = RepositorioFactoria.obtenerInstancia();

            // Act
            ClienteRepositorio repositorio = factoria.obtenerRepositorioCliente();

            // Assert
            assertNotNull(repositorio);
        }

        @Test
        @DisplayName("Debería obtener repositorio de cuentas")
        void deberiaObtenerRepositorioCuenta() {
            // Arrange
            RepositorioFactoria factoria = RepositorioFactoria.obtenerInstancia();

            // Act
            CuentaRepositorio repositorio = factoria.obtenerRepositorioCuenta();

            // Assert
            assertNotNull(repositorio);
        }

        @Test
        @DisplayName("Debería obtener repositorio de tarjetas")
        void deberiaObtenerRepositorioTarjeta() {
            // Arrange
            RepositorioFactoria factoria = RepositorioFactoria.obtenerInstancia();

            // Act
            TarjetaRepositorio repositorio = factoria.obtenerRepositorioTarjeta();

            // Assert
            assertNotNull(repositorio);
        }

        @Test
        @DisplayName("Debería obtener repositorio de transacciones")
        void deberiaObtenerRepositorioTransaccion() {
            // Arrange
            RepositorioFactoria factoria = RepositorioFactoria.obtenerInstancia();

            // Act
            TransaccionRepositorio repositorio = factoria.obtenerRepositorioTransaccion();

            // Assert
            assertNotNull(repositorio);
        }
    }
}
