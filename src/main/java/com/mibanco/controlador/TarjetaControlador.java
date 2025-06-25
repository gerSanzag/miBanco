package com.mibanco.controlador;

/**
 * Interfaz del controlador para operaciones con Tarjetas
 * Coordina entre la vista y el servicio de tarjetas
 */
public interface TarjetaControlador {
    
    /**
     * Crea una nueva tarjeta
     */
    void crearTarjeta();
    
    /**
     * Busca una tarjeta por su número
     */
    void buscarTarjetaPorNumero();
    
    /**
     * Lista todas las tarjetas
     */
    void listarTodasLasTarjetas();
    
    /**
     * Actualiza una tarjeta existente
     */
    void actualizarTarjeta();
    
    /**
     * Elimina una tarjeta
     */
    void eliminarTarjeta();
    
    /**
     * Restaura una tarjeta previamente eliminada
     */
    void restaurarTarjeta();
    
    /**
     * Busca tarjetas por titular
     */
    void buscarTarjetasPorTitular();
    
    /**
     * Busca tarjetas por tipo
     */
    void buscarTarjetasPorTipo();
    
    /**
     * Busca tarjetas por cuenta asociada
     */
    void buscarTarjetasPorCuentaAsociada();
    
    /**
     * Lista tarjetas activas
     */
    void listarTarjetasActivas();
    
    /**
     * Muestra tarjetas eliminadas
     */
    void mostrarTarjetasEliminadas();
    
    /**
     * Cuenta el total de tarjetas
     */
    void contarTarjetas();
    
    /**
     * Muestra el menú principal de tarjetas
     */
    void mostrarMenuPrincipal();
} 