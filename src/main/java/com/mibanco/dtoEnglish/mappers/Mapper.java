package com.mibanco.dtoEnglish.mappers;

import java.util.Optional;

/**
 * Generic interface for mapping between entities and DTOs
 * Follows the single responsibility principle and facilitates the functional approach
 * Uses Optional to handle possible null values (functional approach)
 * @param <E> Entity type
 * @param <D> DTO type
 */
public interface Mapper<E, D> {
    /**
     * Converts an entity to its corresponding DTO
     * @param entity Entity to convert (Optional)
     * @return Resulting DTO (Optional)
     */
    Optional<D> toDto(Optional<E> entity);

    /**
     * Converts a DTO to its corresponding entity
     * @param dto DTO to convert (Optional)
     * @return Resulting entity (Optional)
     */
    Optional<E> toEntity(Optional<D> dto);
} 