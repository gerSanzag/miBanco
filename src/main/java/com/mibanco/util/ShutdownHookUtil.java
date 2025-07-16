package com.mibanco.util;

import com.mibanco.repositorio.interna.RepositorioFactoria;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Clase utilitaria para manejar el cierre de la aplicación
 * Registra un shutdown hook que fuerza el guardado de datos antes de cerrar
 */
public class ShutdownHookUtil {
    
    private static final AtomicBoolean hookRegistrado = new AtomicBoolean(false);
    
    /**
     * Registra un shutdown hook para guardar datos al cerrar la aplicación
     * Solo se registra una vez, incluso si se llama múltiples veces
     */
    public static void registrarShutdownHook() {
        if (hookRegistrado.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("🔄 Guardando datos antes de cerrar la aplicación...");
                
                try {
                    // Obtener instancia de la factoría de repositorios
                    RepositorioFactoria factoria = RepositorioFactoria.obtenerInstancia();
                    
                    // Guardar datos de todos los repositorios
                    factoria.obtenerRepositorioCliente().guardarDatos();
                    factoria.obtenerRepositorioCuenta().guardarDatos();
                    factoria.obtenerRepositorioTarjeta().guardarDatos();
                    factoria.obtenerRepositorioTransaccion().guardarDatos();
                    
                    System.out.println("✅ Datos guardados exitosamente");
                } catch (Exception e) {
                    System.err.println("❌ Error al guardar datos durante el cierre: " + e.getMessage());
                }
            }, "ShutdownHook-Guardado"));
            
            System.out.println("🔧 Shutdown hook registrado para guardado automático");
        }
    }
    
    /**
     * Verifica si el shutdown hook ya está registrado
     * @return true si está registrado, false en caso contrario
     */
    public static boolean estaRegistrado() {
        return hookRegistrado.get();
    }
} 