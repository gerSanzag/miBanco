package com.mibanco.dto.mapper;

import java.util.Optional;

/**
 * Interfaz genérica para mapeos entre entidades y DTOs
 * Sigue el principio de responsabilidad única y facilita el enfoque funcional
 * Utiliza Optional para manejar posibles valores nulos (enfoque funcional)
 * @param <E> Tipo de entidad
 * @param <D> Tipo de DTO
 */
public interface Mapper<E, D> {
    
    /**
     * Convierte una entidad a su DTO correspondiente
     * @param entidad Entidad a convertir (Optional)
     * @return DTO resultante (Optional)
     */
    Optional<D> aDto(Optional<E> entidad);
    
    /**
     * Convierte un DTO a su entidad correspondiente
     * @param dto DTO a convertir (Optional)
     * @return Entidad resultante (Optional)
     */
    Optional<E> aEntidad(Optional<D> dto);
} 