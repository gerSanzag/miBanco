package com.mibanco.vista.util;

import java.util.Map;
import java.util.Optional;

/**
 * Interfaz base para todas las vistas
 * Define los métodos comunes que comparten todas las vistas
 * @param <T> Tipo de DTO que maneja la vista
 */
public interface BaseVista<T> {
    
    /**
     * Muestra el menú principal de la vista
     */
    void mostrarMenuPrincipal();
    
    /**
     * Obtiene la opción seleccionada por el usuario
     * @return Optional con la opción seleccionada o vacío si no es válida
     */
    Optional<Integer> obtenerOpcion();
    
    /**
     * Solicita los datos necesarios para crear una nueva entidad
     * @param mensaje Mensaje personalizado para mostrar en el encabezado
     * @return Map con los datos de la entidad o Map vacío si se cancela
     */
    Map<String, String> solicitarDatosParaCrear(String mensaje);
    
    /**
     * Solicita datos para actualizar una entidad existente
     * @param entidadActual La entidad actual que se va a modificar
     * @return Map con los datos a modificar o Map vacío si se cancela
     */
    Map<String, String> solicitarDatosParaActualizar(T entidadActual);
    
    /**
     * Muestra un mensaje al usuario
     * @param mensaje El mensaje a mostrar
     */
    void mostrarMensaje(String mensaje);
    
    /**
     * Muestra los datos de una entidad y solicita confirmación para una acción
     * @param entidad La entidad sobre la cual se realizará la acción
     * @param titulo El título de la operación
     * @param mensaje El mensaje de confirmación
     * @return true si confirma, false si cancela
     */
    boolean confirmarAccion(T entidad, String titulo, String mensaje);
} 