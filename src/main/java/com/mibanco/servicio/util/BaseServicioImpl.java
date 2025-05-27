package com.mibanco.servicio.util;

import com.mibanco.modelo.Identificable;
import com.mibanco.repositorio.util.BaseRepositorio;
import com.mibanco.dto.mapeador.Mapeador;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Implementación base para servicios
 * @param <T> Tipo de DTO
 * @param <E> Tipo de entidad que debe implementar Identificable
 * @param <ID> Tipo del identificador
 * @param <O> Tipo del enum para operaciones
 */
public abstract class BaseServicioImpl<T, E extends Identificable, ID, O extends Enum<O>> {
    
    protected final BaseRepositorio<E, ID, O> repositorio;
    protected final Mapeador<E, T> mapeador;
    
    protected BaseServicioImpl(BaseRepositorio<E, ID, O> repositorio, Mapeador<E, T> mapeador) {
        this.repositorio = repositorio;
        this.mapeador = mapeador;
    }

    /**
     * Método común para guardar una entidad (crear o actualizar)
     */
    protected Optional<T> guardar(Optional<T> dto) {
        return dto
            .flatMap(d -> mapeador.aEntidad(Optional.of(d)))
            .flatMap(entidad -> {
                if (entidad.getId() == null) {
                    return repositorio.crear(Optional.of(entidad), obtenerTipoOperacionCrear());
                } else {
                    return repositorio.actualizar(Optional.of(entidad), obtenerTipoOperacionActualizar());
                }
            })
            .flatMap(e -> mapeador.aDto(Optional.of(e)));
    }

    /**
     * Método genérico para actualizar un campo específico de una entidad
     */
    protected <V> Optional<T> actualizarCampo(
            ID id,
            Optional<V> nuevoValor,
            Function<E, V> valorActual,
            BiFunction<E, V, E> actualizador) {
            
        return Optional.ofNullable(id)
            .flatMap(idValue -> repositorio.buscarPorId(Optional.of(idValue)))
            .map(entidad -> actualizador.apply(entidad, 
                nuevoValor.orElse(valorActual.apply(entidad))))
            .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
            .flatMap(dto -> guardar(Optional.of(dto)));
    }

    /**
     * Método abstracto para obtener el tipo de operación de creación
     */
    protected abstract O obtenerTipoOperacionCrear();

    /**
     * Método abstracto para obtener el tipo de operación de actualización
     */
    protected abstract O obtenerTipoOperacionActualizar();

    /**
     * Método abstracto para obtener el tipo de operación de eliminación
     */
    protected abstract O obtenerTipoOperacionEliminar();
} 