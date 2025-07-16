package com.mibanco;

import com.mibanco.util.ShutdownHookUtil;
import com.mibanco.repositorio.interna.RepositorioFactoria;
import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.enums.TipoCuenta;
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.servicio.CuentaServicio;
import com.mibanco.servicio.interna.FactoriaServicio;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase principal de la aplicación miBanco
 * Demuestra el funcionamiento del ShutdownHookUtil
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("🏦 ===== APLICACIÓN MIBANCO ===== 🏦");
        System.out.println("Iniciando sistema bancario...\n");
        
        // 🔧 PASO 1: Registrar el shutdown hook
        System.out.println("📋 PASO 1: Registrando shutdown hook...");
        ShutdownHookUtil.registrarShutdownHook();
        
        if (ShutdownHookUtil.estaRegistrado()) {
            System.out.println("✅ Shutdown hook registrado correctamente");
        } else {
            System.out.println("❌ Error al registrar shutdown hook");
        }
        
        // 🔄 PASO 2: Demostrar funcionamiento
        System.out.println("\n📋 PASO 2: Demostrando funcionamiento del sistema...");
        demostrarFuncionamiento();
        
        // ⏰ PASO 3: Simular uso de la aplicación
        System.out.println("\n📋 PASO 3: Simulando uso de la aplicación...");
        simularUsoAplicacion();
        
        // 🚪 PASO 4: Instrucciones para el usuario
        System.out.println("\n📋 PASO 4: Instrucciones para cerrar la aplicación");
        System.out.println("💡 Para ver el shutdown hook en acción:");
        System.out.println("   - Presiona Ctrl+C para cerrar la aplicación");
        System.out.println("   - O ejecuta: System.exit(0)");
        System.out.println("   - El sistema guardará automáticamente todos los datos");
        
        // 🔄 Mantener la aplicación corriendo para demostración
        mantenerAplicacionActiva();
    }
    
    /**
     * Demuestra el funcionamiento básico del sistema
     */
    private static void demostrarFuncionamiento() {
        try {
            // Obtener servicios a través de la factoría
            ClienteServicio clienteServicio = FactoriaServicio.obtenerInstancia().obtenerServicioCliente();
            CuentaServicio cuentaServicio = FactoriaServicio.obtenerInstancia().obtenerServicioCuenta();
            
            // Crear datos de cliente usando Map (como espera el servicio)
            Map<String, String> datosCliente = new HashMap<>();
            datosCliente.put("nombre", "Juan");
            datosCliente.put("apellido", "Pérez");
            datosCliente.put("dni", "12345678");
            datosCliente.put("email", "juan@ejemplo.com");
            datosCliente.put("telefono", "123456789");
            datosCliente.put("direccion", "Calle Ejemplo 123");
            datosCliente.put("fechaNacimiento", "1990-01-01");
            
            // Crear cliente usando el servicio
            clienteServicio.crearClienteDto(datosCliente)
                .ifPresent(clienteCreado -> {
                    System.out.println("✅ Cliente creado: " + clienteCreado.getNombre() + " " + clienteCreado.getApellido());
                    System.out.println("   - ID: " + clienteCreado.getId());
                    System.out.println("   - DNI: " + clienteCreado.getDni());
                    System.out.println("   - Email: " + clienteCreado.getEmail());
                });
            
        } catch (Exception e) {
            System.err.println("❌ Error en demostración: " + e.getMessage());
        }
    }
    
    /**
     * Simula el uso normal de la aplicación
     */
    private static void simularUsoAplicacion() {
        System.out.println("🔄 Simulando operaciones bancarias...");
        
        try {
            // Simular algunas operaciones
            Thread.sleep(1000);
            System.out.println("💳 Procesando transacciones...");
            Thread.sleep(500);
            System.out.println("📊 Generando reportes...");
            Thread.sleep(500);
            System.out.println("🔐 Verificando seguridad...");
            Thread.sleep(500);
            System.out.println("✅ Sistema operativo correctamente");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("❌ Interrupción durante simulación");
        }
    }
    
    /**
     * Mantiene la aplicación activa para demostración
     */
    private static void mantenerAplicacionActiva() {
        System.out.println("\n🔄 Aplicación en ejecución...");
        System.out.println("⏳ Esperando instrucciones del usuario...");
        
        try {
            // Mantener la aplicación corriendo
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("\n👋 Aplicación interrumpida por el usuario");
        }
    }
} 