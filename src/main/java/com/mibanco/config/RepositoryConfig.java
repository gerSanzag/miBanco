package com.mibanco.config;

import com.mibanco.repository.ClienteRepository;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.repository.TarjetaRepository;
import com.mibanco.repository.TransaccionRepository;
import com.mibanco.repository.impl.ClienteRepositoryImpl;
import com.mibanco.repository.impl.CuentaRepositoryImpl;
import com.mibanco.repository.impl.TarjetaRepositoryImpl;
import com.mibanco.repository.impl.TransaccionRepositoryImpl;

/**
 * Configuración centralizada de repositorios
 * Implementa el patrón Singleton para garantizar una única instancia de cada repositorio
 */
public class RepositoryConfig {
    
    // Instancias únicas de los repositorios
    private static ClienteRepository clienteRepository;
    private static CuentaRepository cuentaRepository;
    private static TarjetaRepository tarjetaRepository;
    private static TransaccionRepository transaccionRepository;
    
    /**
     * Obtiene la única instancia del repositorio de clientes
     * @return Instancia del repositorio
     */
    public static synchronized ClienteRepository getClienteRepository() {
        if (clienteRepository == null) {
            clienteRepository = new ClienteRepositoryImpl();
        }
        return clienteRepository;
    }
    
    /**
     * Obtiene la única instancia del repositorio de cuentas
     * @return Instancia del repositorio
     */
    public static synchronized CuentaRepository getCuentaRepository() {
        if (cuentaRepository == null) {
            cuentaRepository = new CuentaRepositoryImpl();
        }
        return cuentaRepository;
    }
    
    /**
     * Obtiene la única instancia del repositorio de tarjetas
     * @return Instancia del repositorio
     */
    public static synchronized TarjetaRepository getTarjetaRepository() {
        if (tarjetaRepository == null) {
            tarjetaRepository = new TarjetaRepositoryImpl();
        }
        return tarjetaRepository;
    }
    
    /**
     * Obtiene la única instancia del repositorio de transacciones
     * @return Instancia del repositorio
     */
    public static synchronized TransaccionRepository getTransaccionRepository() {
        if (transaccionRepository == null) {
            transaccionRepository = new TransaccionRepositoryImpl();
        }
        return transaccionRepository;
    }
    
    /**
     * Constructor privado para prevenir instanciación
     */
    private RepositoryConfig() {
        // Constructor privado para evitar instanciación
    }
} 