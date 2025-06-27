package com.mibanco;

import com.mibanco.modelo.Cliente;
import com.mibanco.modelo.Cuenta;
import com.mibanco.modelo.Tarjeta;
import com.mibanco.util.ReflexionUtil;

import java.util.List;

/**
 * Test simple para verificar ReflexionUtil
 */
public class TestReflexion {
    
    public static void main(String[] args) {
        System.out.println("=== TEST DE REFLEXIÓN ===\n");
        
        // Test 1: Campos requeridos del modelo Cliente
        System.out.println("--- Campos requeridos de Cliente ---");
        List<String> camposCliente = ReflexionUtil.obtenerCamposRequeridos(Cliente.class);
        System.out.println("Campos requeridos: " + camposCliente);
        
        // Test 2: Campos requeridos del modelo Cuenta
        System.out.println("\n--- Campos requeridos de Cuenta ---");
        List<String> camposCuenta = ReflexionUtil.obtenerCamposRequeridos(Cuenta.class);
        System.out.println("Campos requeridos: " + camposCuenta);
        
        // Test 3: Campos requeridos del modelo Tarjeta
        System.out.println("\n--- Campos requeridos de Tarjeta ---");
        List<String> camposTarjeta = ReflexionUtil.obtenerCamposRequeridos(Tarjeta.class);
        System.out.println("Campos requeridos: " + camposTarjeta);
        
        System.out.println("\n=== FIN DE TESTS ===");
    }
} 