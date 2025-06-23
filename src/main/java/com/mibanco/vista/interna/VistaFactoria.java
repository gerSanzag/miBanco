package com.mibanco.vista.interna;

import com.mibanco.vista.VistaCliente;

/**
 * Fábrica de vistas que expone las implementaciones.
 * Esta es la única clase que tiene acceso a las implementaciones internas de las vistas.
 * Sigue el patrón Singleton para garantizar una única instancia.
 */
public final class VistaFactoria {
    
    private static volatile VistaFactoria instancia;
    private final VistaCliente vistaCliente;
    
    /**
     * Constructor privado que inicializa todas las implementaciones de vistas.
     * Al estar en el mismo paquete, tiene acceso a las clases package-private.
     */
    private VistaFactoria() {
        this.vistaCliente = new VistaClienteImpl();
    }
    
    /**
     * Obtiene la única instancia de VistaFactoria.
     * Este es el único punto de acceso a las implementaciones de vistas.
     * 
     * @return la instancia única de VistaFactoria
     */
    public static VistaFactoria obtenerInstancia() {
        if (instancia == null) {
            synchronized (VistaFactoria.class) {
                if (instancia == null) {
                    instancia = new VistaFactoria();
                }
            }
        }
        return instancia;
    }
    
    /**
     * Obtiene la vista de cliente.
     * 
     * @return la interfaz pública de la vista de cliente
     */
    public VistaCliente obtenerVistaCliente() {
        return vistaCliente;
    }
} 