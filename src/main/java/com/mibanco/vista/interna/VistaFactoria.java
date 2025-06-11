package com.mibanco.vista.interna;

import com.mibanco.vista.VistaCliente;
import com.mibanco.vista.util.Consola;

/**
 * Fábrica para obtener las implementaciones de las vistas
 */
public class VistaFactoria {
    private static VistaFactoria instancia;
    private final VistaClienteImpl vistaCliente;
    
    private VistaFactoria(Consola consola) {
        this.vistaCliente = new VistaClienteImpl(consola);
    }
    
    public static VistaFactoria obtenerInstancia(Consola consola) {
        if (instancia == null) {
            instancia = new VistaFactoria(consola);
        }
        return instancia;
    }
    
    public VistaCliente obtenerVistaCliente() {
        return vistaCliente;
    }
} 