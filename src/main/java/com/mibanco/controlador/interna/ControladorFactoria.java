package com.mibanco.controlador.interna;

import com.mibanco.controlador.ClienteControlador;
import com.mibanco.controlador.CuentaControlador;

/**
 * Fábrica de controladores que expone las implementaciones.
 * Esta es la única clase que tiene acceso a las implementaciones internas de los controladores.
 * Sigue el patrón Singleton para garantizar una única instancia.
 */
public final class ControladorFactoria {
    
    private static volatile ControladorFactoria instancia;
    private final ClienteControlador clienteControlador;
    private final CuentaControlador cuentaControlador;
    
    /**
     * Constructor privado que inicializa todas las implementaciones de controladores.
     * Al estar en el mismo paquete, tiene acceso a las clases package-private.
     */
    private ControladorFactoria() {
        this.clienteControlador = new ClienteControladorImpl();
        this.cuentaControlador = new CuentaControladorImpl();
    }
    
    /**
     * Obtiene la única instancia de ControladorFactoria.
     * Este es el único punto de acceso a las implementaciones de controladores.
     * 
     * @return la instancia única de ControladorFactoria
     */
    public static ControladorFactoria obtenerInstancia() {
        if (instancia == null) {
            synchronized (ControladorFactoria.class) {
                if (instancia == null) {
                    instancia = new ControladorFactoria();
                }
            }
        }
        return instancia;
    }
    
    /**
     * Obtiene el controlador de cliente.
     * 
     * @return la interfaz pública del controlador de cliente
     */
    public ClienteControlador obtenerControladorCliente() {
        return clienteControlador;
    }
    
    /**
     * Obtiene el controlador de cuenta.
     * 
     * @return la interfaz pública del controlador de cuenta
     */
    public CuentaControlador obtenerControladorCuenta() {
        return cuentaControlador;
    }
} 