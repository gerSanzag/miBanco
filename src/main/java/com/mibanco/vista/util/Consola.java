package com.mibanco.vista.util;

/**
 * Interfaz para manejar la entrada y salida de datos en la consola
 */
public interface Consola {
    
    /**
     * Lee una línea de texto de la consola
     * @return La línea leída
     */
    String leerLinea();
    
    /**
     * Muestra un mensaje en la consola
     * @param mensaje Mensaje a mostrar
     */
    void mostrar(String mensaje);
} 