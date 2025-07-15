package com.mibanco.servicio;

import com.mibanco.dto.TarjetaDTO;
import com.mibanco.modelo.enums.TipoTarjeta;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Interfaz de servicio para operaciones con Tarjetas
 */
public interface TarjetaServicio {
    
    /**
     * Crea una nueva tarjeta a partir de datos crudos
     * @param datosTarjeta Mapa con los datos crudos de la tarjeta
     * @return Optional con el DTO de la tarjeta creada
     */
    Optional<TarjetaDTO> crearTarjetaDto(Map<String, String> datosTarjeta);
    
    
    /**
     * Actualiza la información completa de una tarjeta
     * @param numeroTarjeta Número de tarjeta a actualizar
     * @param tarjetaDTO Optional con los nuevos datos de la tarjeta
     * @return Optional con el DTO de la tarjeta actualizada
     */
    Optional<TarjetaDTO> actualizarVariosCampos(Long numeroTarjeta, Optional<TarjetaDTO> tarjetaDTO);
    
    /**
     * Obtiene una tarjeta por su número
     * @param numeroTarjeta Optional con el número de tarjeta
     * @return Optional con el DTO de la tarjeta
     */
    Optional<TarjetaDTO> obtenerTarjetaPorNumero(Optional<Long> numeroTarjeta);
    
    /**
     * Obtiene todas las tarjetas
     * @return Optional con la lista de DTOs de tarjetas
     */
    Optional<List<TarjetaDTO>> obtenerTodasLasTarjetas();
    
    /**
     * Actualiza la fecha de expiración de una tarjeta
     * @param numeroTarjeta Número de tarjeta
     * @param nuevaFecha Optional con la nueva fecha de expiración
     * @return Optional con el DTO de la tarjeta actualizada
     */
    Optional<TarjetaDTO> actualizarFechaExpiracion(Long numeroTarjeta, Optional<LocalDate> nuevaFecha);
    
    /**
     * Actualiza el estado activo de una tarjeta
     * @param numeroTarjeta Número de tarjeta
     * @param nuevaActiva Optional con el nuevo estado
     * @return Optional con el DTO de la tarjeta actualizada
     */
    Optional<TarjetaDTO> actualizarEstadoTarjeta(Long numeroTarjeta, Optional<Boolean> nuevaActiva);
    
    /**
     * Actualiza el titular de una tarjeta
     * @param numeroTarjeta Número de tarjeta
     * @param nuevoTitular Optional con el nuevo titular
     * @return Optional con el DTO de la tarjeta actualizada
     */
    Optional<TarjetaDTO> actualizarTitularTarjeta(Long numeroTarjeta, Optional<TarjetaDTO> nuevoTitular);
    
    /**
     * Elimina una tarjeta por su número
     * @param numeroTarjeta Optional con el número de tarjeta a eliminar
     * @return true si se eliminó correctamente, false si no
     */
    boolean eliminarTarjeta(Optional<Long> numeroTarjeta);

    /**
     * Elimina una tarjeta por su número y devuelve la tarjeta eliminada
     * @param numeroTarjeta Optional con el número de tarjeta a eliminar
     * @return Optional con el DTO de la tarjeta eliminada
     */
    Optional<TarjetaDTO> eliminarPorNumero(Optional<Long> numeroTarjeta);

    /**
     * Restaura una tarjeta previamente eliminada
     * @param numeroTarjeta Optional con el número de tarjeta a restaurar
     * @return Optional con el DTO de la tarjeta restaurada
     */
    Optional<TarjetaDTO> restaurarTarjeta(Optional<Long> numeroTarjeta);

    /**
     * Obtiene el número total de tarjetas
     * @return Número de tarjetas
     */
    long contarTarjetas();

    /**
     * Establece el usuario actual para operaciones de auditoría
     * @param usuario Usuario actual
     */
    void establecerUsuarioActual(String usuario);

    /**
     * Busca tarjetas por el ID del cliente titular
     * @param idCliente Optional con el ID del cliente
     * @return Optional con la lista de DTOs de tarjetas del cliente
     */
    Optional<List<TarjetaDTO>> buscarPorTitularId(Optional<Long> idCliente);

    /**
     * Busca tarjetas por el tipo
     * @param tipo Optional con el tipo de tarjeta
     * @return Optional con la lista de DTOs de tarjetas del tipo especificado
     */
    Optional<List<TarjetaDTO>> buscarPorTipo(Optional<TipoTarjeta> tipo);

    /**
     * Obtiene todas las tarjetas activas
     * @return Optional con la lista de DTOs de tarjetas activas
     */
    Optional<List<TarjetaDTO>> buscarActivas();

    /**
     * Busca tarjetas por el número de cuenta asociada
     * @param numeroCuenta Optional con el número de cuenta
     * @return Optional con la lista de DTOs de tarjetas asociadas a la cuenta
     */
    Optional<List<TarjetaDTO>> buscarPorCuentaAsociada(Optional<String> numeroCuenta);
} 