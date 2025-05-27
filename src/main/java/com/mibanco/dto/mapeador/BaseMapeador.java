package com.mibanco.dto.mapeador;

import java.util.Optional;

/**
 * Interfaz base para mapeadores que extiende la interfaz Mapeador
 * @param <T> Tipo de DTO
 * @param <E> Tipo de entidad
 */
public interface BaseMapeador<T, E> extends Mapeador<E, T> {
    // No se necesitan métodos adicionales ya que hereda todo de Mapeador
} 