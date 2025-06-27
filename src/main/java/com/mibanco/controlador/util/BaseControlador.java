package com.mibanco.controlador.util;

/**
 * Interfaz base para todos los controladores
 * Define los métodos comunes que comparten todos los controladores
 */
public interface BaseControlador {
    
    /**
     * Crea una nueva entidad
     */
    void crear();
    
    /**
     * Lista todas las entidades
     */
    void listarTodos();
    
    /**
     * Actualiza una entidad existente
     */
    void actualizar();
    
    /**
     * Elimina una entidad
     */
    void eliminar();
    
    /**
     * Restaura una entidad eliminada
     */
    void restaurar();
    
    /**
     * Lista las entidades eliminadas
     */
    void listarEliminados();
    
    /**
     * Muestra el menú principal
     */
    void mostrarMenuPrincipal();
} 