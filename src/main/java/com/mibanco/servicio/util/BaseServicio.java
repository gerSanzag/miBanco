package com.mibanco.servicio.util;

import com.mibanco.modelo.Identificable;
import java.util.Optional;
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
            Function<E, V> valorActual,
            BiFunction<E, V, E> actualizador);

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
            BiFunction<E, E, E> actualizador);
} 