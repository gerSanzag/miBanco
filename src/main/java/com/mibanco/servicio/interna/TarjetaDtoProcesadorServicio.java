package com.mibanco.servicio.interna;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.dto.TarjetaDTO;
import com.mibanco.servicio.ClienteServicio;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio especializado en procesar la creación de TarjetaDTO
 * Aplica el Principio de Responsabilidad Única (SRP)
 * Se encarga únicamente de transformar datos crudos en TarjetaDTO válidos
 * y procesar operaciones relacionadas con la tarjeta
 */
public class TarjetaDtoProcesadorServicio {
    
    private final ClienteServicio clienteServicio;
    
    public TarjetaDtoProcesadorServicio(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }
    
    /**
     * Procesa los datos crudos y crea un TarjetaDTO válido
     * @param datosCrudos Mapa con los datos de la tarjeta
     * @return Optional con el TarjetaDTO procesado o vacío si hay errores
     */
    public Optional<TarjetaDTO> procesarTarjetaDto(Map<String, String> datosCrudos) {
        return Optional.of(datosCrudos)
            .flatMap(this::obtenerTitularPorId)
            .flatMap(titular -> construirTarjetaDTO(datosCrudos, titular));
    }
    
    /**
     * Extrae el ID del titular y obtiene el cliente en un solo método funcional
     */
    private Optional<ClienteDTO> obtenerTitularPorId(Map<String, String> datosCrudos) {
        return Optional.ofNullable(datosCrudos.get("idTitular"))
            .map(Long::parseLong)
            .flatMap(idTitular -> clienteServicio.obtenerClientePorId(Optional.of(idTitular)));
    }
    
    /**
     * Construye el TarjetaDTO a partir de los datos crudos y el titular
     */
    private Optional<TarjetaDTO> construirTarjetaDTO(Map<String, String> datosCrudos, ClienteDTO titular) {
        try {
            TarjetaDTO.TarjetaDTOBuilder builder = TarjetaDTO.builder()
                .titular(titular);

            // Aplicar transformaciones funcionales con validaciones
            Optional.ofNullable(datosCrudos.get("numeroCuentaAsociada"))
                .ifPresent(builder::numeroCuentaAsociada);

            Optional.ofNullable(datosCrudos.get("tipo"))
                .map(com.mibanco.modelo.enums.TipoTarjeta::valueOf)
                .ifPresent(builder::tipo);

            Optional.ofNullable(datosCrudos.get("fechaExpiracion"))
                .map(fecha -> LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE))
                .ifPresent(builder::fechaExpiracion);

            // Estado activo por defecto
            Optional.ofNullable(datosCrudos.get("activa"))
                .map(Boolean::parseBoolean)
                .or(() -> Optional.of(true))
                .ifPresent(builder::activa);

            return Optional.of(builder.build());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
} 