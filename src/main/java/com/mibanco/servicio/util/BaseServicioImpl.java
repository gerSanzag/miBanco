package com.mibanco.servicio.util;

import com.mibanco.dto.mapeador.Mapeador;
import com.mibanco.modelo.Identificable;
import com.mibanco.repositorio.util.BaseRepositorio;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Implementación base para servicios
 * Proporciona funcionalidad común para todos los servicios
 * @param <T> Tipo de DTO
 * @param <E> Tipo de entidad
 * @param <ID> Tipo de ID
 * @param <O> Tipo de operación
 * @param <R> Tipo de repositorio
 */
public abstract class BaseServicioImpl<T, E extends Identificable, ID, O extends Enum<O>, R extends BaseRepositorio<E, ID, O>> implements BaseServicio<T, E, ID, O> {
    
    protected final R repositorio;
    protected final Mapeador<E, T> mapeador;
    
    protected BaseServicioImpl(R repositorio, Mapeador<E, T> mapeador) {
        this.repositorio = repositorio;
        this.mapeador = mapeador;
    }
    
    @Override
    public Optional<T> guardar(O tipoOperacion, Optional<T> dto) {
        return dto.flatMap(d -> 
            mapeador.aEntidad(Optional.of(d))
                .flatMap(entidad -> repositorio.crear(Optional.of(entidad), tipoOperacion))
                .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
        );
    }
    
    @Override
    public <V> Optional<T> actualizarCampo(
            ID id,
            Optional<V> nuevoValor,
            Function<E, V> valorActual,
            BiFunction<E, V, E> actualizador) {
        return nuevoValor.flatMap(valor -> 
            repositorio.buscarPorId(Optional.of(id))
                .map(entidad -> actualizador.apply(entidad, valor))
                .flatMap(entidad -> repositorio.actualizar(Optional.of(entidad), null))
                .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
        );
    }
    
    @Override
    public Optional<T> actualizar(
            ID id,
            Optional<T> dto,
            O tipoOperacion,
            BiFunction<E, E, E> actualizador) {
        return dto.flatMap(d -> 
            mapeador.aEntidad(Optional.of(d))
                .flatMap(nuevaEntidad -> 
                    repositorio.buscarPorId(Optional.of(id))
                        .map(entidadExistente -> actualizador.apply(entidadExistente, nuevaEntidad))
                        .flatMap(entidadActualizada -> repositorio.actualizar(Optional.of(entidadActualizada), tipoOperacion))
                        .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
                )
        );
    }
    
    @Override
    public Optional<T> obtenerPorId(Optional<ID> id) {
        return id.flatMap(i -> 
            repositorio.buscarPorId(Optional.of(i))
                .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
        );
    }
    
    @Override
    public Optional<List<T>> obtenerTodos() {
        return repositorio.buscarTodos()
            .flatMap(entidades -> Optional.of(
                entidades.stream()
                    .map(entidad -> mapeador.aDto(Optional.of(entidad)).orElse(null))
                    .filter(java.util.Objects::nonNull)
                    .toList()
            ));
    }
    
    @Override
    public boolean eliminarPorId(Optional<ID> id, O tipoOperacion) {
        return id.flatMap(i -> 
            repositorio.eliminarPorId(Optional.of(i), tipoOperacion)
        ).isPresent();
    }
    
    @Override
    public long contarRegistros() {
        return repositorio.contarRegistros();
    }
    
    @Override
    public void establecerUsuarioActual(String usuario) {
        repositorio.setUsuarioActual(usuario);
    }
    
    @Override
    public List<T> obtenerEliminados() {
        return repositorio.obtenerEliminados().stream()
            .map(entidad -> mapeador.aDto(Optional.of(entidad)).orElse(null))
            .filter(java.util.Objects::nonNull)
            .toList();
    }
    
    @Override
    public Optional<T> restaurar(Optional<ID> id, O tipoOperacion) {
        return id.flatMap(i -> 
            repositorio.restaurar(Optional.of(i), tipoOperacion)
                .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
        );
    }
} 