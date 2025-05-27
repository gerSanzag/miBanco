package com.mibanco.repositorio.interna;

import com.mibanco.modelo.Identificable;
import com.mibanco.repositorio.AuditoriaRepositorio;
import com.mibanco.repositorio.util.BaseRepositorio;
import com.mibanco.util.AuditoriaUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.function.Function;

/**
 * Implementación base abstracta para repositorios con acceso restringido
 * @param <T> Tipo de entidad que debe implementar Identificable
 * @param <ID> Tipo del identificador
 * @param <E> Tipo del enum para operaciones
 */
abstract class BaseRepositorioImpl<T extends Identificable, ID, E extends Enum<E>> implements BaseRepositorio<T, ID, E> {
    
    // Lista para almacenar entidades en memoria
    protected final List<T> entidades = new ArrayList<>();
    
    // Cache para restauración de entidades eliminadas
    protected final List<T> entidadesEliminadas = new ArrayList<>();
    
    // Contador para generar IDs automáticamente
    protected final AtomicLong idContador = new AtomicLong(1);
    
    // Repositorio de auditoría
    protected final AuditoriaRepositorio auditoriaRepository;
    
    // Usuario actual
    protected String usuarioActual = "sistema";
    
    /**
     * Constructor protegido que obtiene el repositorio de auditoría
     */
    protected BaseRepositorioImpl() {
        this.auditoriaRepository = RepositoryFactory.obtenerInstancia().obtenerRepositorioAuditoria();
    }
    
    @Override
    public void setUsuarioActual(String usuario) {
        this.usuarioActual = usuario;
    }
    
    @Override
    public Optional<T> crear(Optional<T> entityOpt, E tipoOperacion) {
        return entityOpt.map(entity -> {
            T nuevaEntidad = crearConNuevoId(entity);
            entidades.add(nuevaEntidad);
            registrarAuditoria(nuevaEntidad, tipoOperacion);
            return Optional.of(nuevaEntidad);
        }).orElse(Optional.empty());
    }
    
    @Override
    public Optional<T> actualizar(Optional<T> entityOpt, E tipoOperacion) {
        return entityOpt.flatMap(entidad -> 
            Optional.ofNullable(entidad.getId())
                .map(id -> {
                    entidades.removeIf(e -> e.getId().equals(id));
                    entidades.add(entidad);
                    registrarAuditoria(entidad, tipoOperacion);
                    return entidad;
                })
        );
    }
    
    @Override
    public Optional<T> buscarPorId(Optional<ID> idOpt) {
        return idOpt.flatMap(id -> 
            entidades.stream()
                .filter(entidad -> entidad.getId().equals(id))
                .findFirst()
        );
    }
    
    /**
     * Método protegido para búsquedas por predicado
     */
    protected Optional<T> buscarPorPredicado(Predicate<T> predicado) {
        return entidades.stream()
                .filter(predicado)
                .findFirst();
    }
    
    /**
     * Método protegido para búsquedas de lista por predicado
     */
    protected Optional<List<T>> buscarTodosPorPredicado(Predicate<T> predicado) {
        return Optional.of(
            entidades.stream()
                .filter(predicado)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
        );
    }
    
    @Override
    public Optional<List<T>> buscarTodos() {
        return Optional.of(new ArrayList<>(entidades));
    }
    
    @Override
    public Optional<T> eliminarPorId(Optional<ID> idOpt, E tipoOperacion) {
        return idOpt.flatMap(id -> {
            Optional<T> entidadAEliminarOpt = buscarPorId(Optional.of(id));
            
            entidadAEliminarOpt.ifPresent(entity -> {
                entidades.remove(entity);
                entidadesEliminadas.add(entity);
                registrarAuditoria(entity, tipoOperacion);
            });
            
            return entidadAEliminarOpt;
        });
    }
    
    @Override
    public long contarRegistros() {
        return entidades.size();
    }
    
    @Override
    public Optional<T> restaurar(Optional<ID> idOpt, E tipoOperacion) {
        return idOpt.flatMap(id -> {
            Optional<T> entidadAResturarOpt = entidadesEliminadas.stream()
                    .filter(entidad -> entidad.getId().equals(id))
                    .findFirst();
                    
            entidadAResturarOpt.ifPresent(entity -> {
                entidades.add(entity);
                entidadesEliminadas.removeIf(e -> e.getId().equals(id));
                registrarAuditoria(entity, tipoOperacion);
            });
            
            return entidadAResturarOpt;
        });
    }
    
    /**
     * Método abstracto protegido para crear entidad con nuevo ID
     */
    protected abstract T crearConNuevoId(T entity);
    
    /**
     * Método privado para registrar auditoría
     */
    private void registrarAuditoria(T entidad, E tipoOperacion) {
        AuditoriaUtil.registrarOperacion(
            auditoriaRepository,
            tipoOperacion,
            entidad,
            usuarioActual
        );
    }
   
} 