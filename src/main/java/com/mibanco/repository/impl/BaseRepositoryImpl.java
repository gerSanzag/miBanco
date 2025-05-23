package com.mibanco.repository.impl;

import com.mibanco.model.Identificable;
import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.repository.BaseRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/**
 * Implementación base abstracta para repositorios
 * Proporciona funcionalidad común CRUD con manejo de auditoría
 * @param <T> Tipo de entidad que debe implementar Identificable
 * @param <ID> Tipo del identificador
 * @param <E> Tipo del enum para operaciones
 */
public abstract class BaseRepositoryImpl<T extends Identificable, ID, E extends Enum<E>> implements BaseRepository<T, ID> {
    
    // Lista para almacenar entidades en memoria
    protected final List<T> entities = new ArrayList<>();
    
    // Cache para restauración de entidades eliminadas
    protected final List<T> deletedEntities = new ArrayList<>();
    
    // Contador para generar IDs automáticamente
    protected final AtomicLong idCounter = new AtomicLong(1);
    
    // Repositorio de auditoría
    protected final AuditoriaRepository auditoriaRepository;
    
    // Usuario actual (en un sistema real vendría de un sistema de autenticación)
    protected String usuarioActual = "sistema";
    
    /**
     * Constructor que inicializa el repositorio de auditoría
     */
    protected BaseRepositoryImpl(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }
    
    /**
     * Establece el usuario actual que realiza las operaciones
     */
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    @Override
    public Optional<T> save(Optional<T> entityOpt) {
        return entityOpt
                .map(entity -> 
                    Optional.ofNullable(entity.getId())
                        .map(id -> {
                            T updatedEntity = update(entity);
                            registrarAuditoria(updatedEntity, getOperationType(OperationType.UPDATE));
                            return updatedEntity;
                        })
                        .orElseGet(() -> {
                            T newEntity = create(entity);
                            registrarAuditoria(newEntity, getOperationType(OperationType.CREATE));
                            return newEntity;
                        })
                );
    }
    
    /**
     * Método interno para actualizar una entidad
     */
    private T update(T entity) {
        entities.removeIf(e -> e.getId().equals(entity.getId()));
        entities.add(entity);
        return entity;
    }
    
    /**
     * Método interno para crear una nueva entidad
     */
    private T create(T entity) {
        T newEntity = createWithNewId(entity);
        entities.add(newEntity);
        return newEntity;
    }
    
    @Override
    public Optional<T> findById(Optional<ID> idOpt) {
        return idOpt.flatMap(id -> 
            entities.stream()
                .filter(entity -> entity.getId().equals(id))
                .findFirst()
        );
    }
    
    /**
     * Método genérico para buscar por cualquier predicado
     */
    protected Optional<T> findByPredicate(Predicate<T> predicate) {
        return entities.stream()
                .filter(predicate)
                .findFirst();
    }
    
    /**
     * Método genérico para buscar lista por cualquier predicado
     */
    protected Optional<List<T>> findAllByPredicate(Predicate<T> predicate) {
        return Optional.of(
            entities.stream()
                .filter(predicate)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
        );
    }
    
    @Override
    public Optional<List<T>> findAll() {
        return Optional.of(new ArrayList<>(entities));
    }
    
    @Override
    public Optional<T> deleteById(Optional<ID> idOpt) {
        return idOpt.flatMap(id -> {
            Optional<T> entityToDelete = findById(Optional.of(id));
            
            entityToDelete.ifPresent(entity -> {
                entities.remove(entity);
                deletedEntities.add(entity);
                registrarAuditoria(entity, getOperationType(OperationType.DELETE));
            });
            
            return entityToDelete;
        });
    }
    
    @Override
    public long count() {
        return entities.size();
    }
    
    /**
     * Restaura una entidad previamente eliminada
     */
    public Optional<T> restoreDeletedEntity(Optional<ID> idOpt) {
        return idOpt.flatMap(id -> {
            Optional<T> entityToRestore = deletedEntities.stream()
                    .filter(entity -> entity.getId().equals(id))
                    .findFirst();
                    
            entityToRestore.ifPresent(entity -> {
                entities.add(entity);
                deletedEntities.removeIf(e -> e.getId().equals(id));
                registrarAuditoria(entity, getOperationType(OperationType.RESTORE));
            });
            
            return entityToRestore;
        });
    }
    
    /**
     * Método para crear una nueva entidad con ID
     */
    protected abstract T createWithNewId(T entity);
    
    /**
     * Método para registrar auditoría
     */
    protected abstract void registrarAuditoria(T entity, E tipoOperacion);
    
    /**
     * Método para obtener el tipo de operación correspondiente
     */
    protected abstract E getOperationType(OperationType type);
    
    /**
     * Enum interno para tipos de operación base
     */
    protected enum OperationType {
        CREATE, UPDATE, DELETE, RESTORE
    }
} 