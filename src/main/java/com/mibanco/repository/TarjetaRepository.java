package com.mibanco.repository;

import com.mibanco.model.Tarjeta;
import com.mibanco.model.enums.TipoTarjeta;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para operaciones CRUD de Tarjeta
 * Siguiendo enfoque estrictamente funcional con Optional
 * tanto para parámetros como para resultados
 */
public interface TarjetaRepository {
    
    /**
     * Guarda una tarjeta en el repositorio
     * @param tarjeta Optional con la tarjeta a guardar
     * @return Optional con la tarjeta guardada o Optional vacío si la tarjeta era null
     */
    Optional<Tarjeta> save(Optional<Tarjeta> tarjeta);
    
    /**
     * Busca una tarjeta por su número
     * @param numeroTarjeta Optional con el número de la tarjeta a buscar
     * @return Optional con la tarjeta si existe
     */
    Optional<Tarjeta> findByNumero(Optional<String> numeroTarjeta);
    
    /**
     * Busca tarjetas por el ID del titular
     * @param idTitular Optional con el ID del titular
     * @return Lista de tarjetas del titular
     */
    
    Optional<List<Tarjeta>> findByTitularId(Optional<Long> idTitular);
    
    /**
     * Busca tarjetas por el número de cuenta asociada
     * @param numeroCuenta Optional con el número de cuenta asociada
     * @return Lista de tarjetas asociadas a la cuenta
     */
    Optional<List<Tarjeta>> findByCuentaAsociada(Optional<String> numeroCuenta);
    
    /**
     * Busca tarjetas por tipo
     * @param tipo Optional con el tipo de tarjeta
     * @return Lista de tarjetas del tipo especificado
     */
    Optional<List<Tarjeta>> findByTipo(Optional<TipoTarjeta> tipo);
    
    /**
     * Obtiene todas las tarjetas
     * @return Lista de tarjetas
     */
    Optional<List<Tarjeta>> findAll();
    
    /**
     * Obtiene todas las tarjetas activas
     * @return Lista de tarjetas activas
     */
    Optional<List<Tarjeta>> findAllActivas();
    
    /**
     * Elimina una tarjeta por su número
     * @param numeroTarjeta Optional con el número de la tarjeta a eliminar
     * @return true si se eliminó la tarjeta, false si no existía
     */
    boolean deleteByNumero(Optional<String> numeroTarjeta);
    
    /**
     * Obtiene el número de tarjetas en el repositorio
     * @return Número de tarjetas
     */
    long count();
} 