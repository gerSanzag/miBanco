package com.mibanco.repositorio;

import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.enums.TipoOperacionTarjeta;
import com.mibanco.modelo.enums.TipoTarjeta;
import com.mibanco.repositorio.util.BaseRepositorio;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el repositorio de Tarjetas
 * Extiende la interfaz base para heredar operaciones CRUD genéricas
 */
public interface TarjetaRepositorio extends BaseRepositorio<Tarjeta, String, TipoOperacionTarjeta> {
    
    /**
     * Busca una tarjeta por su número
     * @param numeroTarjeta Optional con el número de la tarjeta a buscar
     * @return Optional con la tarjeta si existe
     */
    Optional<Tarjeta> buscarPorNumero(Optional<String> numeroTarjeta);
    
    /**
     * Busca tarjetas por el ID del cliente titular
     * @param idCliente Optional con el ID del cliente
     * @return Optional con la lista de tarjetas del cliente
     */
    Optional<List<Tarjeta>> buscarPorTitularId(Optional<Long> idCliente);
    
    /**
     * Busca tarjetas por el número de cuenta asociada
     * @param numeroCuenta Optional con el número de cuenta asociada
     * @return Lista de tarjetas asociadas a la cuenta
     */
    Optional<List<Tarjeta>> buscarPorCuentaAsociada(Optional<String> numeroCuenta);
    
    /**
     * Busca tarjetas por tipo
     * @param tipo Optional con el tipo de tarjeta
     * @return Lista de tarjetas del tipo especificado
     */
    Optional<List<Tarjeta>> buscarPorTipo(Optional<TipoTarjeta> tipo);
    
    /**
     * Obtiene todas las tarjetas activas
     * @return Lista de tarjetas activas
     */
    Optional<List<Tarjeta>> buscarActivas();
} 