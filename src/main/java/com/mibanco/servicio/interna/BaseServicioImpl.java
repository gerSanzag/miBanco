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
 * @param <T> Tipo de DTO
 * @param <E> Tipo de entidad que debe implementar Identificable
 * @param <ID> Tipo del identificador
 * @param <O> Tipo del enum para operaciones
 */
abstract class BaseServicioImpl<T, E extends Identificable, ID, O extends Enum<O>> implements BaseServicio<T, E, ID, O> {
    
    protected final BaseRepositorio<E, ID, O> repositorio;
    protected final Mapeador<E, T> mapeador;
    
    protected BaseServicioImpl(BaseRepositorio<E, ID, O> repositorio, Mapeador<E, T> mapeador) {
        this.repositorio = repositorio;
        this.mapeador = mapeador;
    }

    @Override
    public Optional<T> guardar(O tipoOperacion, Optional<T> dto) {
     return dto
            .flatMap(d -> mapeador.aEntidad(Optional.of(d)))
            .flatMap(entidad -> {
                if (entidad.getId() == null) {
                    return repositorio.crear(Optional.of(entidad), tipoOperacion);
                } else {
                    return repositorio.actualizar(Optional.of(entidad), tipoOperacion);
                }
            })
            .flatMap(e -> mapeador.aDto(Optional.of(e)));
    }

    @Override
    public <V> Optional<T> actualizarCampo(
            ID id,
            Optional<V> nuevoValor,
            Function<E, V> valorActual,
            BiFunction<E, V, E> actualizador) {
            
        return Optional.ofNullable(id)
            .flatMap(idValue -> repositorio.buscarPorId(Optional.of(idValue)))
            .map(entidad -> actualizador.apply(entidad, 
                nuevoValor.orElse(valorActual.apply(entidad))))
            .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)));
    }

    @Override
    public Optional<T> actualizar(
            ID id,
            Optional<T> dto,
            O tipoOperacion,
            BiFunction<E, E, E> actualizador) {
            
        return Optional.ofNullable(id)
            .flatMap(idValue -> repositorio.buscarPorId(Optional.of(idValue)))
            .map(entidadExistente -> 
                dto.flatMap(d -> mapeador.aEntidad(Optional.of(d)))
                    .map(entidadNueva -> actualizador.apply(entidadExistente, entidadNueva))
                    .orElse(entidadExistente)
            )
            .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
            .flatMap(dtoActualizado -> guardar(tipoOperacion, Optional.of(dtoActualizado)));
    }

    /**
     * Método genérico para obtener una entidad por su ID
     * @param id Optional con el ID de la entidad a buscar
     * @return Optional con el DTO de la entidad encontrada
     */
    public Optional<T> obtenerPorId(Optional<ID> id) {
        return id.flatMap(idValue -> 
            repositorio.buscarPorId(Optional.of(idValue))
                .flatMap(entidad -> mapeador.aDto(Optional.of(entidad)))
        );
    }

    /**
     * Método genérico para obtener todas las entidades
     * @return Optional con la lista de DTOs encontrados
     */
    public Optional<List<T>> obtenerTodos() {
        return repositorio.buscarTodos()
            .map(entidades -> entidades.stream()
                .map(entidad -> mapeador.aDto(Optional.of(entidad)).orElse(null))
                .filter(java.util.Objects::nonNull)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll)
            );
    }

    /**
     * Método genérico para eliminar una entidad por su ID
     * @param id Optional con el ID de la entidad a eliminar
     * @param tipoOperacion Tipo de operación para auditoría
     * @return true si la entidad fue eliminada, false en caso contrario
     */
    public boolean eliminarPorId(Optional<ID> id, O tipoOperacion) {
        return id.flatMap(idValue -> 
            repositorio.eliminarPorId(Optional.of(idValue), tipoOperacion)
        ).isPresent();
    }

    /**
     * Método genérico para contar el número total de registros
     * @return número total de registros
     */
    public long contarRegistros() {
        return repositorio.contarRegistros();
    }

    /**
     * Método genérico para establecer el usuario actual en el repositorio
     * @param usuario nombre del usuario actual
     */
    public void establecerUsuarioActual(String usuario) {
        repositorio.setUsuarioActual(usuario);
    }

    /**
     * Método genérico para obtener las entidades eliminadas
     * @return Lista de DTOs de las entidades eliminadas
     */
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