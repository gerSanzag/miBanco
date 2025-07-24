package com.mibanco.utilEnglish;

import com.mibanco.repositorio.interna.RepositorioFactoria;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility class to handle application shutdown
 * Registers a shutdown hook that forces data saving before closing
 */
public class ShutdownHookUtil {
    
    private static final AtomicBoolean hookRegistered = new AtomicBoolean(false);
    
    /**
     * Registers a shutdown hook to save data when closing the application
     * Only registers once, even if called multiple times
     */
    public static void registerShutdownHook() {
        if (hookRegistered.compareAndSet(false, true)) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("🔄 Guardando datos antes de cerrar la aplicación...");
                
                try {
                    // Get repository factory instance
                    RepositorioFactoria factory = RepositorioFactoria.obtenerInstancia();
                    
                    // Save data from all repositories
                    factory.obtenerRepositorioCliente().guardarDatos();
                    factory.obtenerRepositorioCuenta().guardarDatos();
                    factory.obtenerRepositorioTarjeta().guardarDatos();
                    factory.obtenerRepositorioTransaccion().guardarDatos();
                    
                    System.out.println("✅ Datos guardados exitosamente");
                } catch (Exception e) {
                    System.err.println("❌ Error al guardar datos durante el cierre: " + e.getMessage());
                }
            }, "ShutdownHook-Guardado"));
            
            System.out.println("🔧 Shutdown hook registrado para guardado automático");
        }
    }
    
    /**
     * Checks if the shutdown hook is already registered
     * @return true if registered, false otherwise
     */
    public static boolean isRegistered() {
        return hookRegistered.get();
    }
} 