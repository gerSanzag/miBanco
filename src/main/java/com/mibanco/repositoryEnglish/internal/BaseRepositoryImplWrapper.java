package com.mibanco.repositoryEnglish.internal;

import com.mibanco.modelEnglish.Identifiable;


/**
 * Wrapper for testing BaseRepositoryImpl
 * Allows access to package-private methods for testing
 * Pattern: Test-Specific Subclass
 */
public abstract class BaseRepositoryImplWrapper<T extends Identifiable, ID, E extends Enum<E>> extends BaseRepositoryImpl<T, ID, E> {
    // Empty class - automatically inherits all public methods
    // We don't need to write anything else
} 