package com.mibanco.vista.interna;

import java.util.Scanner;

import com.mibanco.vista.util.Consola;

/**
 * Implementación de la interfaz Consola para manejar la entrada/salida en consola
 */
class ConsolaImpl implements Consola {
    private final Scanner scanner;
    
    public ConsolaImpl(Scanner scanner) {
        this.scanner = scanner;
    }
    
    @Override
    public String leerLinea() {
        return scanner.nextLine().trim();
    }
    
    @Override
    public void mostrar(String mensaje) {
        System.out.println(mensaje);
    }
} 