package com.mibanco.servicio.interna;

import com.mibanco.servicio.AuditoriaServicio;
import com.mibanco.servicio.ClienteServicio;
import com.mibanco.servicio.CuentaServicio;
import com.mibanco.servicio.TarjetaServicio;
import com.mibanco.servicio.TransaccionCrudServicio;
import com.mibanco.servicio.TransaccionOperacionesServicio;

/**
 * Fábrica de servicios que expone las implementaciones
 * Esta es la única clase que tiene acceso a las implementaciones internas
 */
public final class ServicioFactoria {
    
    private static volatile ServicioFactoria instancia;
    private final ClienteServicio clienteServicio;
    private final CuentaServicio cuentaServicio;
    private final TarjetaServicio tarjetaServicio;
    private final TransaccionCrudServicio transaccionCrudServicio;
    private final TransaccionOperacionesServicio transaccionOperacionesServicio;
    private final AuditoriaServicio auditoriaServicio;
    
    /**
     * Constructor privado que inicializa todas las implementaciones
     * Al estar en el mismo paquete, tiene acceso a las clases package-private
     */
    private ServicioFactoria() {
        this.clienteServicio = new ClienteServicioImpl();
        this.cuentaServicio = new CuentaServicioImpl();
        this.tarjetaServicio = new TarjetaServicioImpl();
        this.transaccionCrudServicio = new TransaccionCrudServicioImpl();
        this.transaccionOperacionesServicio = new TransaccionOperacionesServicioImpl();
        this.auditoriaServicio = new AuditoriaServicioImpl();
    }
    
    /**
     * Obtiene la única instancia de ServicioFactoria
     * Este es el único punto de acceso a las implementaciones de servicios
     */
    public static ServicioFactoria obtenerInstancia() {
        if (instancia == null) {
            synchronized (ServicioFactoria.class) {
                if (instancia == null) {
                    instancia = new ServicioFactoria();
                }
            }
        }
        return instancia;
    }
    
    /**
     * Obtiene el servicio de clientes
     * @return Interfaz pública del servicio de clientes
     */
    public ClienteServicio obtenerServicioCliente() {
        return clienteServicio;
    }
    
    /**
     * Obtiene el servicio de cuentas
     * @return Interfaz pública del servicio de cuentas
     */
    public CuentaServicio obtenerServicioCuenta() {
        return cuentaServicio;
    }
    
    /**
     * Obtiene el servicio de tarjetas
     * @return Interfaz pública del servicio de tarjetas
     */
    public TarjetaServicio obtenerServicioTarjeta() {
        return tarjetaServicio;
    }
    
    /**
     * Obtiene el servicio CRUD de transacciones
     * @return Interfaz pública del servicio CRUD de transacciones
     */
    public TransaccionCrudServicio obtenerServicioTransaccionCrud() {
        return transaccionCrudServicio;
    }
    
    /**
     * Obtiene el servicio de operaciones de transacciones
     * @return Interfaz pública del servicio de operaciones de transacciones
     */
    public TransaccionOperacionesServicio obtenerServicioTransaccionOperaciones() {
        return transaccionOperacionesServicio;
    }
    
    /**
     * Obtiene el servicio de auditoría
     * @return Interfaz pública del servicio de auditoría
     */
    public AuditoriaServicio obtenerServicioAuditoria() {
        return auditoriaServicio;
    }
} 