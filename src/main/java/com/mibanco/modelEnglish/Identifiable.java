package com.mibanco.modelEnglish;

/**
 * Interface for entities that have a unique identifier
 * Allows restricting generic types to entities that can be identified
 */
public interface Identifiable {
    /**
     * Gets the unique identifier of the entity
     * @return The unique ID of the entity
     */
    Long getId();
} 