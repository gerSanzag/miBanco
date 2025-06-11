package com.mibanco.vista.util;

import java.util.Scanner;

/**
 * Implementación de la interfaz Consola para manejar la entrada/salida en consola
 */
public class ConsolaImpl implements Consola {
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