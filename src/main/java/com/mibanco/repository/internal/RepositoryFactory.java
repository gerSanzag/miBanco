package com.mibanco.repository.internal;

import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.ClienteRepository;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.repository.TarjetaRepository;
import com.mibanco.repository.TransaccionRepository;

/**
 * Configuración centralizada de repositorios
 * Esta es la única clase que tiene acceso a las implementaciones
 * Al estar en el mismo paquete que las implementaciones, puede instanciarlas
 */
public final class RepositoryFactory {
    
    private static volatile RepositoryFactory instancia;
    private final AuditoriaRepository auditoriaRepository;
    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    private final TarjetaRepository tarjetaRepository;
    private final TransaccionRepository transaccionRepository;
    
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
    public AuditoriaRepository obtenerRepositorioAuditoria() {
        return auditoriaRepository;
    }
    
    /**
     * Obtiene el repositorio de clientes
     * @return Interfaz pública del repositorio de clientes
     */
    public ClienteRepository obtenerRepositorioCliente() {
        return clienteRepository;
    }
    
    /**
     * Obtiene el repositorio de cuentas
     * @return Interfaz pública del repositorio de cuentas
     */
    public CuentaRepository obtenerRepositorioCuenta() {
        return cuentaRepository;
    }
    
    /**
     * Obtiene el repositorio de tarjetas
     * @return Interfaz pública del repositorio de tarjetas
     */
    public TarjetaRepository obtenerRepositorioTarjeta() {
        return tarjetaRepository;
    }
    
    /**
     * Obtiene el repositorio de transacciones
     * @return Interfaz pública del repositorio de transacciones
     */
    public TransaccionRepository obtenerRepositorioTransaccion() {
        return transaccionRepository;
    }
} 