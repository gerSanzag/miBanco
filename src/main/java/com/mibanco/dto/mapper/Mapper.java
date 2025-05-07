package com.mibanco.dto.mapper;

/**
 * Interfaz genérica para mapeos entre entidades y DTOs
 * Sigue el principio de responsabilidad única y facilita el enfoque funcional
 * @param <E> Tipo de entidad
 * @param <D> Tipo de DTO
 */
public interface Mapper<E, D> {
    
    /**
     * Convierte una entidad a su DTO correspondiente
     * @param entity Entidad a convertir
     * @return DTO resultante
     */
    D toDto(E entity);
    
    /**
     * Convierte un DTO a su entidad correspondiente
     * @param dto DTO a convertir
     * @return Entidad resultante
     */
    E toEntity(D dto);
} 