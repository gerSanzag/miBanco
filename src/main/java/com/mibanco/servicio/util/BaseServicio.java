package com.mibanco.servicio.util;

import com.mibanco.modelo.Identificable;
import java.util.Optional;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Interfaz base para servicios
 * @param <T> Tipo de DTO
 * @param <E> Tipo de entidad que debe implementar Identificable
 * @param <ID> Tipo del identificador
 * @param <O> Tipo del enum para operaciones
 */
public interface BaseServicio<T, E extends Identificable, ID, O extends Enum<O>> {
    
    /**
     * Método común para guardar una entidad (crear o actualizar)
     * @param tipoOperacion Tipo de operación a realizar
     * @param dto Optional con los datos a guardar
     * @return Optional con el DTO guardado
     */
    Optional<T> guardar(O tipoOperacion, Optional<T> dto);

    /**
     * Método genérico para actualizar un campo específico de una entidad
     * @param id ID de la entidad a actualizar
     * @param nuevoValor Optional con el nuevo valor
     * @param valorActual Función para obtener el valor actual
     * @param actualizador Función para actualizar el valor
     * @return Optional con el DTO actualizado
     */
    <V> Optional<T> actualizarCampo(
            ID id,
            Optional<V> nuevoValor,
            Function<T, V> valorActual,
            BiFunction<T, V, T> actualizador);

    /**
     * Método genérico para actualizar una entidad existente
     * @param id ID de la entidad a actualizar
     * @param dto Optional con los nuevos datos
     * @param tipoOperacion Tipo de operación para auditoría
     * @param actualizador Función que actualiza la entidad existente con los nuevos datos
     * @return Optional con el DTO actualizado
     */
    Optional<T> actualizar(
            ID id,
            Optional<T> dto,
            O tipoOperacion,
            BiFunction<T, E, T> actualizador);

    /**
     * Método genérico para obtener una entidad por su ID
     * @param id Optional con el ID de la entidad a buscar
     * @return Optional con el DTO de la entidad encontrada
     */
    Optional<T> obtenerPorId(Optional<ID> id);

    /**
     * Método genérico para obtener todas las entidades
     * @return Optional con la lista de DTOs encontrados
     */
    Optional<List<T>> obtenerTodos();

    /**
     * Método genérico para eliminar una entidad por su ID
     * @param id Optional con el ID de la entidad a eliminar
     * @param tipoOperacion Tipo de operación para auditoría
     * @return true si la entidad fue eliminada, false en caso contrario
     */
    boolean eliminarPorId(Optional<ID> id, O tipoOperacion);

    /**
     * Método genérico para contar el número total de registros
     * @return número total de registros
     */
    long contarRegistros();

    /**
     * Método genérico para establecer el usuario actual en el repositorio
     * @param usuario nombre del usuario actual
     */
    void establecerUsuarioActual(String usuario);

    /**
     * Método genérico para obtener las entidades eliminadas
     * @return Lista de DTOs de las entidades eliminadas
     */
    List<T> obtenerEliminados();

    /**
     * Método genérico para restaurar una entidad previamente eliminada
     * @param id Optional con el ID de la entidad a restaurar
     * @param tipoOperacion Tipo de operación para auditoría
     * @return Optional con el DTO de la entidad restaurada
     */
    Optional<T> restaurar(Optional<ID> id, O tipoOperacion);
} 