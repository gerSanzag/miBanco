package com.mibanco.repository.internal;

import com.mibanco.model.Identifiable;
import com.mibanco.repository.AuditRepository;
import com.mibanco.repository.util.BaseRepository;
import com.mibanco.util.AuditUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.Objects;

/**
 * Abstract base implementation for repositories with restricted access
 * @param <T> Entity type that must implement Identifiable
 * @param <ID> Identifier type
 * @param <E> Enum type for operations
 */
abstract class BaseRepositoryImpl<T extends Identifiable, ID, E extends Enum<E>> implements BaseRepository<T, ID, E> {
    
    // List to store entities in memory
    protected final List<T> entities = new ArrayList<>(); // entidades
    
    // Cache for restoration of deleted entities
    protected final List<T> deletedEntities = new ArrayList<>(); // entidadesEliminadas
    
    // Counter to generate IDs automatically
    protected final AtomicLong idCounter = new AtomicLong(1); // idContador
    
    // Audit repository - now with lazy loading
    private AuditRepository auditRepository; // auditoriaRepository
    
    // Current user
    protected String currentUser = "sistema"; // usuarioActual
    
    // Generic JSON processor
    private final BaseJsonProcessor<T> jsonProcessor; // jsonProcesador
    
    /**
     * Protected constructor without automatic loading
     * Data will be loaded lazily when needed
     */
    protected BaseRepositoryImpl() {
        // Initialize JSON processor
        this.jsonProcessor = new BaseJsonProcessor<>();
        // ❌ DO NOT load data automatically - it will be done lazily
    }
    
    /**
     * Lazy method to get the audit repository
     */
    private AuditRepository getAudit() {
        if (auditRepository == null) {
            auditRepository = RepositoryFactory.getInstance().getAuditRepository();
        }
        return auditRepository;
    }
    
    /**
     * Public method to load data manually
     * Useful for testing and cases where explicit control is needed
     */
    public void loadData() {
        loadDataFromJson();
    }
    
    /**
     * Loads data from JSON automatically when creating the repository
     * Data is loaded into the entities list
     */
    private void loadDataFromJson() {
        Map<String, Object> config = getConfiguration();
        
        // Defensive validation for critical fields
        Class<T> classType = Objects.requireNonNull(
            (Class<T>) config.get("classType"), 
            "CRITICAL ERROR: Class type not configured"
        );
        
        Function<T, Long> idExtractor = Objects.requireNonNull(
            (Function<T, Long>) config.get("idExtractor"), 
            "CRITICAL ERROR: ID extractor not configured"
        );
        
        // Optional field: if null, don't load data
        String path = (String) config.get("filePath");
        if (path == null) {
            System.err.println("WARNING: File path not configured, skipping data loading");
            return;
        }
        
        // Load data from JSON
        List<T> loadedData = jsonProcessor.loadDataConditionally(path, classType);
        
        // Add loaded data to entities list
        entities.addAll(loadedData);
        
        // Update counter if data was loaded
        if (!loadedData.isEmpty()) {
            long maxId = loadedData.stream()
                .mapToLong(entity -> idExtractor.apply(entity))
                .max()
                .orElse(0);
            idCounter.set(maxId + 1);
        }
    }
    
    @Override
    public void setCurrentUser(String user) {
        this.currentUser = user;
    }
    
    @Override
    public Optional<T> createRecord(Optional<T> entityOpt, E operationType) {
        if (entityOpt.isEmpty()) {
            return Optional.empty();
        }
        
        T entity = entityOpt.get();
        
        // Generate new ID if not present
        if (entity.getId() == null) {
            entity = createWithNewId(entity);
    }
    
        // Add to entities list
        entities.add(entity);
        
        // Register audit
        registerAudit(entity, operationType);
        
        return Optional.of(entity);
    }
    
    protected abstract T createWithNewId(T entity);
    
    protected abstract Map<String, Object> getConfiguration();
    
    @Override
    public Optional<T> updateRecord(Optional<T> entityOpt, E operationType) {
        if (entityOpt.isEmpty()) {
            return Optional.empty();
        }
        
        T entity = entityOpt.get();
        
        // Find and update existing entity
        for (int i = 0; i < entities.size(); i++) {
            if (Objects.equals(entities.get(i).getId(), entity.getId())) {
                entities.set(i, entity);
                registerAudit(entity, operationType);
                return Optional.of(entity);
            }
        }
        
        return Optional.empty();
    }
    
    @Override
    public Optional<T> findById(Optional<ID> idOpt) {
        if (idOpt.isEmpty()) {
            return Optional.empty();
        }
        
        ID id = idOpt.get();
        return entities.stream()
            .filter(entity -> Objects.equals(entity.getId(), id))
            .findFirst();
    }
    
    @Override
    public Optional<T> findByPredicate(Predicate<T> predicate) {
        return entities.stream()
            .filter(predicate)
                .findFirst();
    }
    
    @Override
    public Optional<List<T>> findAllByPredicate(Predicate<T> predicate) {
        List<T> filteredEntities = entities.stream()
            .filter(predicate)
            .toList();
        return Optional.of(filteredEntities);
    }
    
    @Override
    public Optional<List<T>> findAll() {
        return Optional.of(new ArrayList<>(entities));
    }
    
    @Override
    public Optional<T> deleteById(Optional<ID> idOpt, E operationType) {
        if (idOpt.isEmpty()) {
            return Optional.empty();
        }
        
        ID id = idOpt.get();
            
        for (int i = 0; i < entities.size(); i++) {
            T entity = entities.get(i);
            if (Objects.equals(entity.getId(), id)) {
                entities.remove(i);
                deletedEntities.add(entity);
                registerAudit(entity, operationType);
                return Optional.of(entity);
            }
        }
            
        return Optional.empty();
    }
    
    @Override
    public long countRecords() {
        return entities.size();
    }
    
    @Override
    public Optional<T> restore(Optional<ID> idOpt, E operationType) {
        if (idOpt.isEmpty()) {
            return Optional.empty();
        }
        
        ID id = idOpt.get();
        
        for (int i = 0; i < deletedEntities.size(); i++) {
            T entity = deletedEntities.get(i);
            if (Objects.equals(entity.getId(), id)) {
                deletedEntities.remove(i);
                entities.add(entity);
                registerAudit(entity, operationType);
                return Optional.of(entity);
            }
        }
        
        return Optional.empty();
    }
    
    private void registerAudit(T entity, E operationType) {
        AuditUtil.registerOperation(
            entity.getClass().getSimpleName(),
            entity.getId(),
            operationType.name(),
            currentUser
        );
    }

    @Override
    public List<T> getDeleted() {
        return new ArrayList<>(deletedEntities);
    }
    
    protected void incrementCounterAndSave() {
        idCounter.incrementAndGet();
        saveData();
        }
    
    @Override
    public void saveData() {
        Map<String, Object> config = getConfiguration();
        String path = (String) config.get("filePath");
        
        if (path != null) {
            jsonProcessor.saveData(entities, path);
        }
    }
} 