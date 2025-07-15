
package com.mibanco.repositorio.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.mibanco.modelo.Identificable;

/**
 * Interfaz base para repositorios
 * Define operaciones CRUD genéricas
 * El acceso a las implementaciones está restringido por la estructura de paquetes
 * @param <T> Tipo de entidad que debe implementar Identificable
 * @param <ID> Tipo del identificador
 * @param <E> Tipo del enum para operaciones
 */
public interface BaseRepositorio<T extends Identificable, ID, E extends Enum<E>> {
    
    /**
     * Crea una nueva entidad
     * @param entity Entidad a crear
     * @param tipoOperacion Tipo de operación para auditoría
     * @return Optional con la entidad creada
     */
    Optional<T> crearRegistro(Optional<T> entity, E tipoOperacion);
    
    /**
     * Actualiza una entidad existente
     * @param entity Entidad a actualizar
     * @param tipoOperacion Tipo de operación para auditoría
     * @return Optional con la entidad actualizada
     */
    Optional<T> actualizarRegistro(Optional<T> entity, E tipoOperacion);
    
    /**
     * Busca una entidad por su ID
     * @param id ID de la entidad
     * @return Optional con la entidad si existe
     */
    Optional<T> buscarPorId(Optional<ID> id);
   

    /**
     * Busca una entidad por un predicado
     * @param predicado Predicado para buscar la entidad
     * @return Optional con la entidad si existe
     */
    Optional<T> buscarPorPredicado(Predicate<T> predicado);
   
    /**
     * Busca todas las entidades por un predicado
    Optional<List<T>> buscarTodosPorPredicado(Predicate<T> predicado);
     * @return Optional con la lista de entidades si existe
     */
    Optional<List<T>> buscarTodosPorPredicado(Predicate<T> predicado);
    /**
     * Obtiene todas las entidades
     * @return Optional con la lista de entidades
     */
    Optional<List<T>> buscarTodos();
    
    /**
     * Elimina una entidad
     * @param id ID de la entidad a eliminar
     * @param tipoOperacion Tipo de operación para auditoría
     * @return Optional con la entidad eliminada
     */
    Optional<T> eliminarPorId(Optional<ID> id, E tipoOperacion);
    
    /**
     * Restaura una entidad eliminada
     * @param id ID de la entidad a restaurar
     * @param tipoOperacion Tipo de operación para auditoría
     * @return Optional con la entidad restaurada
     */
    Optional<T> restaurar(Optional<ID> id, E tipoOperacion);

   
    
    /**
     * Cuenta el número de registros
     * @return Número de registros
     */
    long contarRegistros();
    
    /**
     * Establece el usuario actual para operaciones de auditoría
     * @param usuario Usuario actual
     */
    void setUsuarioActual(String usuario);

    /**
     * Obtiene las entidades eliminadas
     * @return Lista de entidades eliminadas
     */
    List<T> obtenerEliminados();
    
    /**
     * Método público para guardado forzado de datos
     * Útil para cierre de aplicación o guardado manual
     */
    void guardarDatos();
} 