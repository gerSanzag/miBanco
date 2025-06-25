package com.mibanco.vista.interna;

import com.mibanco.vista.ClienteVista;
import com.mibanco.vista.CuentaVista;
import com.mibanco.vista.TarjetaVista;

/**
 * Fábrica de vistas que expone las implementaciones.
 * Esta es la única clase que tiene acceso a las implementaciones internas de las vistas.
 * Sigue el patrón Singleton para garantizar una única instancia.
 */
public final class factoriaVista {
    
    private static volatile factoriaVista instancia;
    private final ClienteVista vistaCliente;
    private final CuentaVista vistaCuenta;
    private final TarjetaVista vistaTarjeta;
    
    /**
     * Constructor privado que inicializa todas las implementaciones de vistas.
     * Al estar en el mismo paquete, tiene acceso a las clases package-private.
     */
    private factoriaVista() {
        this.vistaCliente = new ClienteVistaImpl();
        this.vistaCuenta = new CuentaVistaImpl();
        this.vistaTarjeta = new TarjetaVistaImpl();
    }
    
    /**
     * Obtiene la única instancia de VistaFactoria.
     * Este es el único punto de acceso a las implementaciones de vistas.
     * 
     * @return la instancia única de VistaFactoria
     */
    public static factoriaVista obtenerInstancia() {
        if (instancia == null) {
            synchronized (factoriaVista.class) {
                if (instancia == null) {
                    instancia = new factoriaVista();
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
    public ClienteVista obtenerVistaCliente() {
        return vistaCliente;
    }
    
    /**
     * Obtiene la vista de cuenta.
     * 
     * @return la interfaz pública de la vista de cuenta
     */
    public CuentaVista obtenerVistaCuenta() {
        return vistaCuenta;
    }
    
    /**
     * Obtiene la vista de tarjeta.
     * 
     * @return la interfaz pública de la vista de tarjeta
     */
    public TarjetaVista obtenerVistaTarjeta() {
        return vistaTarjeta;
    }
} 