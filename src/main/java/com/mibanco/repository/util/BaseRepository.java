package com.mibanco.repository.util;

import com.mibanco.model.Identificable;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz base para repositorios
 * Define operaciones CRUD genéricas
 * @param <T> Tipo de entidad que debe implementar Identificable
 * @param <ID> Tipo del identificador
 * @param <E> Tipo del enum para operaciones
 */
public interface BaseRepository<T extends Identificable, ID, E extends Enum<E>> {
    
    Optional<T> crear(Optional<T> entity, E tipoOperacion);
    
    Optional<T> actualizar(Optional<T> entity, E tipoOperacion);
    
    Optional<T> buscarPorId(Optional<ID> id);
    
    Optional<List<T>> buscarTodos();
    
    Optional<T> eliminarPorId(Optional<ID> id, E tipoOperacion);
    
    Optional<T> restaurar(Optional<ID> id, E tipoOperacion);
    
    long contarRegistros();
    
    void setUsuarioActual(String usuario);
} 