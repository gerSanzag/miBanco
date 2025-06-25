package com.mibanco;

import com.mibanco.controlador.TarjetaControlador;
import com.mibanco.controlador.interna.ControladorFactoria;

/**
 * Test simple para verificar la funcionalidad de Tarjetas
 */
public class TestTarjeta {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DE FUNCIONALIDADES DE LA TARJETA ===\n");
        
        TarjetaControlador controlador = ControladorFactoria.obtenerInstancia().obtenerControladorTarjeta();
        
        // Test 1: Contar tarjetas (debe ser 0 inicialmente)
        System.out.println("--- Test 1: Contar Tarjetas ---");
        controlador.contarTarjetas();
        
        // Test 2: Listar todas las tarjetas (debe estar vacío)
        System.out.println("\n--- Test 2: Listar Todas las Tarjetas ---");
        controlador.listarTodasLasTarjetas();
        
        // Test 3: Listar tarjetas activas (debe estar vacío)
        System.out.println("\n--- Test 3: Listar Tarjetas Activas ---");
        controlador.listarTarjetasActivas();
        
        // Test 4: Mostrar tarjetas eliminadas (debe estar vacío)
        System.out.println("\n--- Test 4: Mostrar Tarjetas Eliminadas ---");
        controlador.mostrarTarjetasEliminadas();
        
        System.out.println("\n=== FIN DE TESTS ===");
    }
} 