package com.mibanco.config.factory;

import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.ClienteRepository;
import com.mibanco.repository.CuentaRepository;
import com.mibanco.repository.impl.AuditoriaRepositoryImpl;
import com.mibanco.repository.impl.ClienteRepositoryImpl;
import com.mibanco.repository.impl.CuentaRepositoryImpl;
import java.util.Optional;

/**
 * Configuración centralizada de repositorios
 * Implementa el patrón Singleton para garantizar una única instancia de cada repositorio
 * Utiliza Optional y programación funcional para el manejo de instancias
 */
public class RepositoryFactory {
    
    private static volatile RepositoryFactory instance;
    private final AuditoriaRepository auditoriaRepository;
    private final ClienteRepository clienteRepository;
    private final CuentaRepository cuentaRepository;
    
    private RepositoryFactory() {
        this.auditoriaRepository = new AuditoriaRepositoryImpl();
        this.clienteRepository = new ClienteRepositoryImpl();
        this.cuentaRepository = new CuentaRepositoryImpl();
    }
    
    /**
     * Obtiene la única instancia de RepositoryFactory
     * @return Instancia única de RepositoryFactory
     */
    public static RepositoryFactory getInstance() {
        if (instance == null) {
            synchronized (RepositoryFactory.class) {
                if (instance == null) {
                    instance = new RepositoryFactory();
                }
            }
        }
        return instance;
    }
    
    public AuditoriaRepository getAuditoriaRepository() {
        return auditoriaRepository;
    }
    
    public ClienteRepository getClienteRepository() {
        return clienteRepository;
    }
    
    public CuentaRepository getCuentaRepository() {
        return cuentaRepository;
    }
} 