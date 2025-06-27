package com.mibanco.controlador.interna;

import com.mibanco.controlador.util.BaseControlador;
import com.mibanco.vista.util.BaseVista;
import com.mibanco.servicio.util.BaseServicio;
import com.mibanco.modelo.Identificable;
import java.util.Optional;

/**
 * Implementación base para todos los controladores
 * Proporciona la funcionalidad común que comparten todos los controladores
 * @param <T> Tipo de DTO que maneja el controlador
 * @param <E> Tipo de entidad que debe implementar Identificable
 * @param <ID> Tipo del identificador
 * @param <O> Tipo del enum para operaciones
 */
abstract class BaseControladorImpl<T, E extends Identificable, ID, O extends Enum<O>> implements BaseControlador {
    
    protected final BaseServicio<T, E, ID, O> servicio;
    protected final BaseVista<T> vista;
    
    /**
     * Constructor protegido que inicializa el servicio y la vista
     * @param servicio Servicio a utilizar
     * @param vista Vista a utilizar
     */
    protected BaseControladorImpl(BaseServicio<T, E, ID, O> servicio, BaseVista<T> vista) {
        this.servicio = servicio;
        this.vista = vista;
    }
    
    /**
     * Método protegido para manejar operaciones que devuelven Optional
     * @param resultado Optional con el resultado de la operación
     * @param mensajeExito Mensaje a mostrar en caso de éxito
     * @param mensajeError Mensaje a mostrar en caso de error
     */
    protected void manejarResultado(Optional<?> resultado, String mensajeExito, String mensajeError) {
        resultado.ifPresentOrElse(
            r -> vista.mostrarMensaje(mensajeExito),
            () -> vista.mostrarMensaje(mensajeError)
        );
    }
    
    /**
     * Método protegido para confirmar una acción sobre una entidad
     * @param entidad Entidad sobre la que se realizará la acción
     * @param titulo Título de la operación
     * @param mensaje Mensaje de confirmación
     * @return true si se confirma la acción, false en caso contrario
     */
    protected boolean confirmarOperacion(T entidad, String titulo, String mensaje) {
        return vista.confirmarAccion(entidad, titulo, mensaje);
    }
    
    /**
     * Método protegido para ejecutar una operación con confirmación
     * @param entidad Entidad sobre la que se realizará la operación
     * @param operacion Operación a realizar
     * @param titulo Título de la operación
     * @param mensajeConfirmacion Mensaje de confirmación
     * @param mensajeExito Mensaje de éxito
     * @param mensajeError Mensaje de error
     */
    protected void ejecutarOperacionConConfirmacion(
            T entidad,
            Runnable operacion,
            String titulo,
            String mensajeConfirmacion,
            String mensajeExito,
            String mensajeError) {
        
        if (confirmarOperacion(entidad, titulo, mensajeConfirmacion)) {
            try {
                operacion.run();
                vista.mostrarMensaje(mensajeExito);
            } catch (Exception e) {
                vista.mostrarMensaje(mensajeError + ": " + e.getMessage());
            }
        }
    }
} 