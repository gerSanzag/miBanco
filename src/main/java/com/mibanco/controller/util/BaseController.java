package com.mibanco.controller.util;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Abstract base class for entity controllers.
 * Provides common functionality for update operations across different entity controllers.
 * Follows the same pattern as BaseServiceImpl and BaseRepositoryImpl.
 * 
 * @param <T> Entity type
 * @param <DTO> DTO type
 */
public abstract class BaseController<T, DTO> {
    
    /**
     * Generic method for updating entity fields.
     * Handles the common logic for all field updates across different entities.
     * 
     * @param viewMethod Supplier that captures update data from view
     * @param serviceMethod Function that calls the appropriate service method
     * @param getEntityMethod Function to get entity by ID for confirmation
     * @param mapper Function to map DTO to entity
     * @param displayMethod Function that displays entity and returns confirmation boolean
     * @return true if update was successful, false otherwise
     */
    protected boolean updateEntityField(
            Supplier<Optional<Map<String, String>>> viewMethod,
            BiFunction<Long, String, Optional<DTO>> serviceMethod,
            Function<Long, Optional<DTO>> getEntityMethod,
            Function<DTO, Optional<T>> mapper,
            Function<T, Boolean> displayMethod
    ) {
        return viewMethod.get()
            .map(updateData -> {
                String idStr = updateData.get("id");
                String newValue = updateData.get("newValue");
                
                try {
                    Long id = Long.parseLong(idStr);
                    
                    // Call private method to show entity and get confirmation
                    return Optional.of(showEntityAndConfirm(id, getEntityMethod, mapper, displayMethod))
                            .filter(confirmed -> confirmed)
                            .flatMap(confirmed -> serviceMethod.apply(id, newValue))
                            .map(dto -> {
                                T entity = mapper.apply(dto).orElse(null);
                                if (entity != null) {
                                    displayMethod.apply(entity);
                                }
                                return true;
                            })
                            .orElse(false);
                    
                } catch (NumberFormatException e) {
                    return false;
                }
            })
            .orElse(false);
    }
    
    /**
     * Generic method that shows entity information and asks for user confirmation.
     * Can be used for any operation that requires showing entity data and getting user approval.
     * 
     * @param id ID of the entity to show and confirm
     * @param getEntityMethod Function to get entity by ID
     * @param mapper Function to map DTO to entity
     * @param displayMethod Function that displays entity and returns confirmation boolean
     * @return true if user confirms the operation, false otherwise
     */
    protected boolean showEntityAndConfirm(
            Long id,
            Function<Long, Optional<DTO>> getEntityMethod,
            Function<DTO, Optional<T>> mapper,
            Function<T, Boolean> displayMethod
    ) {
        return getEntityMethod.apply(id)
            .flatMap(dto -> mapper.apply(dto))
            .map(entity -> displayMethod.apply(entity))
            .orElse(false);
    }
}

