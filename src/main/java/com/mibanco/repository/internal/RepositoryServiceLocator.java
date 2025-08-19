package com.mibanco.repository.internal;

import com.mibanco.repository.AccountRepository;
import com.mibanco.repository.AuditRepository;
import com.mibanco.repository.CardRepository;
import com.mibanco.repository.ClientRepository;
import com.mibanco.repository.TransactionRepository;

import java.util.HashMap;
import java.util.Map;

/**
 * Service Locator Pattern for repositories
 * Centralized registry for all repository implementations
 * Replaces RepositoryFactory with a more flexible approach
 */
public final class RepositoryServiceLocator {
    
    // Singleton: Map that holds all repository instances
    private static final Map<Class<?>, Object> services = new HashMap<>();
    
    // Flag to track if services have been initialized
    private static volatile boolean initialized = false;
    
    /**
     * Private constructor to prevent instantiation
     * This class uses static methods only
     */
    private RepositoryServiceLocator() {
        // Prevent instantiation
    }
    
    /**
     * Initializes all repository services
     * This method should be called once at application startup
     */
    public static synchronized void initializeServices() {
        if (!initialized) {
            // Register all repository implementations
            registerService(AuditRepository.class, new AuditRepositoryImpl());
            registerService(ClientRepository.class, new ClientRepositoryImpl());
            registerService(AccountRepository.class, new AccountRepositoryImpl());
            registerService(CardRepository.class, new CardRepositoryImpl());
            registerService(TransactionRepository.class, new TransactionRepositoryImpl());
            
            initialized = true;
        }
    }
    
    /**
     * Registers a service with its interface class
     * @param serviceClass The interface class of the service
     * @param service The service implementation
     */
    public static void registerService(Class<?> serviceClass, Object service) {
        services.put(serviceClass, service);
    }
    
    /**
     * Gets a service by its interface class
     * @param serviceClass The interface class of the service to retrieve
     * @param <T> The type of the service
     * @return The service implementation
     * @throws IllegalStateException if services are not initialized
     */
    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> serviceClass) {
        if (!initialized) {
            throw new IllegalStateException("Repository services not initialized. Call initializeServices() first.");
        }
        
        Object service = services.get(serviceClass);
        if (service == null) {
            throw new IllegalArgumentException("Service not found for class: " + serviceClass.getName());
        }
        
        return (T) service;
    }
    
    /**
     * Checks if a service is registered
     * @param serviceClass The interface class to check
     * @return true if the service is registered, false otherwise
     */
    public static boolean hasService(Class<?> serviceClass) {
        return services.containsKey(serviceClass);
    }
    
    /**
     * Clears all registered services
     * Useful for testing or resetting the service locator
     */
    public static synchronized void clearServices() {
        services.clear();
        initialized = false;
    }
}
