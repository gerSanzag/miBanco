package com.mibanco.repositorio.interna;

import com.mibanco.repositorio.AuditoriaRepositorio;
import com.mibanco.repositorio.ClienteRepositorio;
import com.mibanco.repositorio.CuentaRepositorio;
import com.mibanco.repositorio.TarjetaRepositorio;
import com.mibanco.repositorio.TransaccionRepositorio;

/**
 * Configuración centralizada de repositorios
 * Esta es la única clase que tiene acceso a las implementaciones
 * Al estar en el mismo paquete que las implementaciones, puede instanciarlas
 */
public final class RepositoryFactory {
    
    private static volatile RepositoryFactory instancia;
    private final AuditoriaRepositorio auditoriaRepository;
    private final ClienteRepositorio clienteRepository;
    private final CuentaRepositorio cuentaRepository;
    private final TarjetaRepositorio tarjetaRepository;
    private final TransaccionRepositorio transaccionRepository;
    
    /**
     * Constructor privado que inicializa todas las implementaciones
     * Al estar en el mismo paquete, tiene acceso a las clases package-private
     */
    private RepositoryFactory() {
        this.auditoriaRepository = new AuditoriaRepositoryImpl();
        this.clienteRepository = new ClienteRepositoryImpl();
        this.cuentaRepository = new CuentaRepositoryImpl();
        this.tarjetaRepository = new TarjetaRepositoryImpl();
        this.transaccionRepository = new TransaccionRepositoryImpl();
    }
    
    /**
     * Obtiene la única instancia de RepositoryFactory
     * Este es el único punto de acceso a las implementaciones de repositorios
     */
    public static RepositoryFactory obtenerInstancia() {
        if (instancia == null) {
            synchronized (RepositoryFactory.class) {
                if (instancia == null) {
                    instancia = new RepositoryFactory();
                }
            }
        }
        return instancia;
    }
    
    /**
     * Obtiene el repositorio de auditoría
     * @return Interfaz pública del repositorio de auditoría
     */
    public AuditoriaRepositorio obtenerRepositorioAuditoria() {
        return auditoriaRepository;
    }
    
    /**
     * Obtiene el repositorio de clientes
     * @return Interfaz pública del repositorio de clientes
     */
    public ClienteRepositorio obtenerRepositorioCliente() {
        return clienteRepository;
    }
    
    /**
     * Obtiene el repositorio de cuentas
     * @return Interfaz pública del repositorio de cuentas
     */
    public CuentaRepositorio obtenerRepositorioCuenta() {
        return cuentaRepository;
    }
    
    /**
     * Obtiene el repositorio de tarjetas
     * @return Interfaz pública del repositorio de tarjetas
     */
    public TarjetaRepositorio obtenerRepositorioTarjeta() {
        return tarjetaRepository;
    }
    
    /**
     * Obtiene el repositorio de transacciones
     * @return Interfaz pública del repositorio de transacciones
     */
    public TransaccionRepositorio obtenerRepositorioTransaccion() {
        return transaccionRepository;
    }
} 