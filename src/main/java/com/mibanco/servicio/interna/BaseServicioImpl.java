package com.mibanco.servicio.interna;

import com.mibanco.modelo.Identificable;
import com.mibanco.repositorio.util.BaseRepositorio;
import com.mibanco.servicio.util.BaseServicio;
import com.mibanco.dto.mapeador.Mapeador;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Implementación base para servicios
 * Visibilidad restringida al paquete internal
 * @param <T> Tipo de DTO
 * @param <E> Tipo de entidad que debe implementar Identificable
 * @param <ID> Tipo del identificador
 * @param <O> Tipo del enum para operaciones
 * @param <R> Tipo específico del repositorio que extiende BaseRepositorio
 */
abstract class BaseServicioImpl<T, E extends Identificable, ID, O extends Enum<O>, R extends BaseRepositorio<E, ID, O>> implements BaseServicio<T, E, ID, O> {
    
    protected final R repositorio;
    protected final Mapeador<E, T> mapeador;
    
    protected BaseServicioImpl(R repositorio, Mapeador<E, T> mapeador) {
        this.repositorio = repositorio;
        this.mapeador = mapeador;
    }

    @Override
    public Optional<T> guardarEntidad(O tipoOperacion, Optional<T> dto) {
     return dto
            .flatMap(d -> mapeador.aEntidad(Optional.of(d)))
            .flatMap(entidad -> {
                if (entidad.getId() == null) {
                    return repositorio.crearRegistro(Optional.of(entidad), tipoOperacion);
                } else {
                    return repositorio.actualizarRegistro(Optional.of(entidad), tipoOperacion);
                }
            })
            .flatMap(e -> mapeador.aDto(Optional.of(e)));
    }

    @Override
    public <V> Optional<T> actualizarCampo(
            ID id,
            Optional<V> nuevoValor,
            Function<T, V> valorActual,
            BiFunction<T, V, T> actualizador) {
            
        return Optional.ofNullable(id)
            .flatMap(idValue -> repositorio.buscarPorId(Optional.of(idValue)))
            .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)));
            
    }

    @Override
    public Optional<T> actualizar(
            ID id,
            Optional<T> dto,
            O tipoOperacion,
            BiFunction<T, E, T> actualizador) {
            
        return Optional.ofNullable(id)
            .flatMap(idValue -> repositorio.buscarPorId(Optional.of(idValue)))
            .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
            .flatMap(dtoActualizado -> guardarEntidad(tipoOperacion, Optional.of(dtoActualizado)));
    }

    @Override
    public Optional<T> obtenerPorId(Optional<ID> id) {
        return id.flatMap(idValue -> 
            repositorio.buscarPorId(Optional.of(idValue))
                .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
        );
    }

    @Override
    public Optional<List<T>> obtenerTodos() {
        return repositorio.buscarTodos()
            .map(entidades -> entidades.stream()
                .map(entidad -> mapeador.aDto(Optional.of(entidad)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
    }

    @Override
    public boolean eliminarPorId(Optional<ID> id, O tipoOperacion) {
        return id.flatMap(idValue -> 
            repositorio.eliminarPorId(Optional.of(idValue), tipoOperacion)
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
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    @Override
    public Optional<T> restaurar(Optional<ID> id, O tipoOperacion) {
        return id.flatMap(idValue -> 
            repositorio.restaurar(Optional.of(idValue), tipoOperacion)
                .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
        );
    }
} 