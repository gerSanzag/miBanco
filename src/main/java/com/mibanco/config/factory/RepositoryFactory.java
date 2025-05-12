package com.mibanco.config.factory;

import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.ClienteRepository;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.repository.TarjetaRepository;
import com.mibanco.repository.TransaccionRepository;
import com.mibanco.repository.impl.AuditoriaRepositoryImpl;
import com.mibanco.repository.impl.ClienteRepositoryImpl;
import com.mibanco.repository.impl.CuentaRepositoryImpl;
import com.mibanco.repository.impl.TarjetaRepositoryImpl;
import com.mibanco.repository.impl.TransaccionRepositoryImpl;

/**
 * Configuración centralizada de repositorios
 * Implementa el patrón Singleton para garantizar una única instancia de cada repositorio
 * También implementa el patrón Factory Method para la creación controlada de instancias
 */
public class RepositoryFactory {
    
    // Instancias únicas de los repositorios
    private static AuditoriaRepository auditoriaRepository;
    private static ClienteRepository clienteRepository;
    private static CuentaRepository cuentaRepository;
    private static TarjetaRepository tarjetaRepository;
    private static TransaccionRepository transaccionRepository;
    
    /**
     * Obtiene la única instancia del repositorio de auditoría
     * @return Instancia del repositorio
     */
    public static synchronized AuditoriaRepository getAuditoriaRepository() {
        if (auditoriaRepository == null) {
            auditoriaRepository = new AuditoriaRepositoryImpl();
        }
        return auditoriaRepository;
    }
    
    /**
     * Obtiene la única instancia del repositorio de clientes
     * @return Instancia del repositorio
     */
    public static synchronized ClienteRepository getClienteRepository() {
        if (clienteRepository == null) {
            // Inicializamos el repositorio de clientes con el repositorio de auditoría
            clienteRepository = new ClienteRepositoryImpl(getAuditoriaRepository());
        }
        return clienteRepository;
    }
    
    /**
     * Obtiene la única instancia del repositorio de cuentas
     * @return Instancia del repositorio
     */
    public static synchronized CuentaRepository getCuentaRepository() {
        if (cuentaRepository == null) {
            // Inicializamos el repositorio de cuentas con el repositorio de auditoría
            cuentaRepository = new CuentaRepositoryImpl(getAuditoriaRepository());
        }
        return cuentaRepository;
    }
    
    /**
     * Obtiene la única instancia del repositorio de tarjetas
     * @return Instancia del repositorio
     */
    public static synchronized TarjetaRepository getTarjetaRepository() {
        if (tarjetaRepository == null) {
            // Inicializamos el repositorio de tarjetas con el repositorio de auditoría
            tarjetaRepository = new TarjetaRepositoryImpl(getAuditoriaRepository());
        }
        return tarjetaRepository;
    }
    
    /**
     * Obtiene la única instancia del repositorio de transacciones
     * @return Instancia del repositorio
     */
    public static synchronized TransaccionRepository getTransaccionRepository() {
        if (transaccionRepository == null) {
            // Inicializamos el repositorio de transacciones con el repositorio de auditoría
            transaccionRepository = new TransaccionRepositoryImpl(getAuditoriaRepository());
        }
        return transaccionRepository;
    }
    
    /**
     * Constructor privado para prevenir instanciación
     */
    private RepositoryFactory() {
        // Constructor privado para evitar instanciación
    }
} 