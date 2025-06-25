package com.mibanco.vista;

import com.mibanco.dto.TarjetaDTO;
import com.mibanco.modelo.enums.TipoTarjeta;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interfaz para la vista de Tarjetas
 * Define los métodos para interactuar con el usuario en operaciones de tarjetas
 */
public interface TarjetaVista {
    
    /**
     * Muestra el menú principal de gestión de tarjetas
     */
    void mostrarMenuPrincipal();
    
    /**
     * Obtiene la opción seleccionada por el usuario
     * @return Optional con la opción seleccionada o vacío si no es válida
     */
    Optional<Integer> obtenerOpcion();
    
    /**
     * Solicita los datos necesarios para crear una nueva tarjeta
     * @return Map con los datos de la tarjeta o Map vacío si se cancela
     */
    Map<String, String> solicitarDatosParaCrear();
    
    /**
     * Solicita el número de tarjeta para operaciones de búsqueda
     * @return un Optional con el número de tarjeta, o un Optional vacío si la entrada no es válida.
     */
    Optional<String> solicitarNumeroTarjeta();
    
    /**
     * Solicita datos para actualizar una tarjeta existente
     * @param tarjetaActual La tarjeta actual que se va a modificar
     * @return Map con los datos a modificar o Map vacío si se cancela
     */
    Map<String, String> solicitarDatosParaActualizar(TarjetaDTO tarjetaActual);
    
    /**
     * Muestra información detallada de una tarjeta
     * @param tarjetaOpt Optional con la tarjeta a mostrar
     */
    void mostrarTarjeta(Optional<TarjetaDTO> tarjetaOpt);
    
    /**
     * Muestra una lista de tarjetas
     * @param tarjetas Lista de tarjetas a mostrar
     */
    void mostrarListaTarjetas(List<TarjetaDTO> tarjetas);
    
    /**
     * Muestra un mensaje al usuario
     * @param mensaje El mensaje a mostrar
     */
    void mostrarMensaje(String mensaje);
    
    /**
     * Solicita confirmación para una operación específica
     * @param tarjeta La tarjeta sobre la que se realizará la operación
     * @param titulo El título de la operación
     * @param mensaje El mensaje de confirmación
     * @return true si confirma, false si cancela
     */
    boolean confirmarAccion(TarjetaDTO tarjeta, String titulo, String mensaje);
    
    /**
     * Solicita el ID del cliente titular
     * @return Optional con el ID del cliente o vacío si se cancela
     */
    Optional<Long> solicitarIdTitular();
    
    /**
     * Solicita el tipo de tarjeta
     * @return Optional con el tipo de tarjeta o vacío si se cancela
     */
    Optional<String> solicitarTipoTarjeta();
    
    /**
     * Solicita el CVV de la tarjeta
     * @return Optional con el CVV o vacío si se cancela
     */
    Optional<String> solicitarCvv();
 
    /**
     * Solicita el número de cuenta asociada
     * @return Optional con el número de cuenta o vacío si se cancela
     */
    Optional<String> solicitarNumeroCuentaAsociada();
    
    /**
     * Solicita una fecha
     * @return Optional con la fecha o vacío si se cancela
     */
    Optional<LocalDate> solicitarFecha();
    
    /**
     * Solicita el nuevo estado activo/inactivo
     * @return Optional con el nuevo estado o vacío si se cancela
     */
    Optional<Boolean> solicitarNuevoEstado();
    
 
    
    /**
     * Muestra las tarjetas eliminadas
     * @param tarjetas Lista de tarjetas eliminadas
     */
    void mostrarTarjetasEliminadas(List<TarjetaDTO> tarjetas);
    
    /**
     * Muestra el total de tarjetas registradas
     * @param total Número total de tarjetas
     */
    void mostrarTotalTarjetas(long total);
} 