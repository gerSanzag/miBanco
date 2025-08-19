package com.mibanco.service.internal;

import com.mibanco.dto.mappers.Mapper;
import com.mibanco.model.Identifiable;
import com.mibanco.repository.util.BaseRepository;
import com.mibanco.service.util.BaseService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Base implementation for services
 * Package-private visibility
 * @param <T> DTO type
 * @param <E> Entity type that must implement Identifiable
 * @param <ID> Identifier type
 * @param <O> Enum type for operations
 * @param <R> Specific repository type that extends BaseRepository
 */
abstract class BaseServiceImpl<T, E extends Identifiable, ID, O extends Enum<O>, R extends BaseRepository<E, ID, O>> implements BaseService<T, E, ID, O> {
    
    protected final R repository;
    protected final Mapper<E, T> mapper;
    
    protected BaseServiceImpl(R repository, Mapper<E, T> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<T> saveEntity(O operationType, Optional<T> dto) {
     return dto
            .flatMap(d -> mapper.toEntity(Optional.of(d)))
            .flatMap(entity -> {
                if (entity.getId() == null) {
                    return repository.createRecord(Optional.of(entity), operationType);
                } else {
                    return repository.updateRecord(Optional.of(entity), operationType);
                }
            })
            .flatMap(e -> mapper.toDto(Optional.of(e)));
    }

    @Override
    public <V> Optional<T> updateField(
            ID id,
            Optional<V> newValue,
            Function<T, V> currentValue,
            BiFunction<T, V, T> updater) {
            
        return Optional.ofNullable(id)
            .flatMap(idValue -> repository.findById(Optional.of(idValue)))
            .flatMap(entity -> mapper.toDto(Optional.of(entity)))
            .flatMap(dto -> newValue
                .map(newVal -> updater.apply(dto, newVal))
                .flatMap(updatedDto -> {
                    // For now, we'll return the updated DTO without saving
                    // This is a simplified implementation
                    return Optional.of(updatedDto);
                })
            );
            
    }

    @Override
    public Optional<T> updateMultipleFields(
            ID id,
            Map<String, Object> updates,
            O operationType,
            BiFunction<T, Map<String, Object>, T> updater) {
            
        return Optional.ofNullable(id)
            .flatMap(idValue -> repository.findById(Optional.of(idValue)))
            .flatMap(entity -> mapper.toDto(Optional.of(entity))
                .flatMap(dto -> {
                    T updatedDto = updater.apply(dto, updates);
                    return saveEntity(operationType, Optional.of(updatedDto));
                })
            );
    }

    @Override
    public Optional<T> findById(Optional<ID> id) {
        return id.flatMap(idValue -> 
            repository.findById(Optional.of(idValue))
                .flatMap(entity -> mapper.toDto(Optional.of(entity)))
        );
    }

    @Override
    public Optional<List<T>> findAll() {
        return repository.findAll()
            .map(entities -> entities.stream()
                .map(entity -> mapper.toDto(Optional.of(entity)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
    }

    @Override
    public boolean deleteById(Optional<ID> id, O operationType) {
        return id.flatMap(idValue -> 
            repository.deleteById(Optional.of(idValue), operationType)
        ).isPresent();
    }

    @Override
    public long countRecords() {
        return repository.countRecords();
    }

    @Override
    public void setCurrentUser(String user) {
        repository.setCurrentUser(user);
    }

    @Override
    public List<T> getDeleted() {
        return repository.getDeleted().stream()
            .map(entity -> mapper.toDto(Optional.of(entity)).orElse(null))
            .filter(java.util.Objects::nonNull)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    public Optional<T> restore(Optional<ID> id, O operationType) {
        return id.flatMap(idValue -> 
            repository.restore(Optional.of(idValue), operationType)
                .flatMap(entity -> mapper.toDto(Optional.of(entity)))
        );
    }
} 