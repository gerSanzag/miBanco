package com.mibanco.repository.util;

import com.mibanco.model.Identificable;
import com.mibanco.repository.AuditoriaRepository;
import com.mibanco.config.factory.RepositoryFactory;
import com.mibanco.util.AuditoriaUtil;
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
public abstract class BaseRepositoryImpl<T extends Identificable, ID, E extends Enum<E>> implements BaseRepository<T, ID, E> {
    
    // Lista para almacenar entidades en memoria
    protected final List<T> entities = new ArrayList<>();
    
    // Cache para restauración de entidades eliminadas
    protected final List<T> deletedEntities = new ArrayList<>();
    
    // Contador para generar IDs automáticamente
    protected final AtomicLong idCounter = new AtomicLong(1);
    
    // Repositorio de auditoría obtenido de la factory
    protected final AuditoriaRepository auditoriaRepository;
    
    // Usuario actual (en un sistema real vendría de un sistema de autenticación)
    protected String usuarioActual = "sistema";
    
    /**
     * Constructor que obtiene el repositorio de auditoría de la factory
     */
    protected BaseRepositoryImpl() {
        this.auditoriaRepository = RepositoryFactory.obtenerInstancia().obtenerRepositorioAuditoria();
    }
    
    /**
     * Establece el usuario actual que realiza las operaciones
     */
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    /**
     * Crea una nueva entidad
     */
    public Optional<T> crear(Optional<T> entityOpt, E tipoOperacion) {
        return entityOpt.map(entity -> {
            T newEntity = crearConNuevoId(entity);
            entities.add(newEntity);
            registrarAuditoria(newEntity, tipoOperacion);
            return Optional.of(newEntity);
        }).orElse(Optional.empty());
    }
    
    /**
     * Actualiza una entidad existente
     */
    public Optional<T> actualizar(Optional<T> entityOpt, E tipoOperacion) {
        return entityOpt.flatMap(entity -> 
            Optional.ofNullable(entity.getId())
                .map(id -> {
                    entities.removeIf(e -> e.getId().equals(id));
                    entities.add(entity);
                    registrarAuditoria(entity, tipoOperacion);
                    return entity;
                })
        );
    }
    
    @Override
    public Optional<T> buscarPorId(Optional<ID> idOpt) {
        return idOpt.flatMap(id -> 
            entities.stream()
                .filter(entity -> entity.getId().equals(id))
                .findFirst()
        );
    }
    
    /**
     * Método genérico para buscar por cualquier predicado
     */
    protected Optional<T> buscarPorPredicado(Predicate<T> predicado) {
        return entities.stream()
                .filter(predicado)
                .findFirst();
    }
    
    /**
     * Método genérico para buscar lista por cualquier predicado
     */
    protected Optional<List<T>> buscarTodosPorPredicado(Predicate<T> predicado) {
        return Optional.of(
            entities.stream()
                .filter(predicado)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
        );
    }
    
    @Override
    public Optional<List<T>> buscarTodos() {
        return Optional.of(new ArrayList<>(entities));
    }
    
    @Override
    public Optional<T> eliminarPorId(Optional<ID> idOpt, E tipoOperacion) {
        return idOpt.flatMap(id -> {
            Optional<T> entityToDelete = buscarPorId(Optional.of(id));
            
            entityToDelete.ifPresent(entity -> {
                entities.remove(entity);
                deletedEntities.add(entity);
                registrarAuditoria(entity, tipoOperacion);
            });
            
            return entityToDelete;
        });
    }
    
    @Override
    public long contarRegistros() {
        return entities.size();
    }
    
    /**
     * Restaura una entidad previamente eliminada
     */
    public Optional<T> restaurar(Optional<ID> idOpt, E tipoOperacion) {
        return idOpt.flatMap(id -> {
            Optional<T> entityToRestore = deletedEntities.stream()
                    .filter(entity -> entity.getId().equals(id))
                    .findFirst();
                    
            entityToRestore.ifPresent(entity -> {
                entities.add(entity);
                deletedEntities.removeIf(e -> e.getId().equals(id));
                registrarAuditoria(entity, tipoOperacion);
            });
            
            return entityToRestore;
        });
    }
    
    /**
     * Método para crear una nueva entidad con ID
     */
    protected abstract T crearConNuevoId(T entity);
    
    /**
     * Método para registrar auditoría
     * Implementado directamente en la base ya que la lógica es la misma para todos los repositorios
     */
    private void registrarAuditoria(T entity, E tipoOperacion) {
        AuditoriaUtil.registrarOperacion(
            auditoriaRepository,
            tipoOperacion,
            entity,
            usuarioActual
        );
    }
} 