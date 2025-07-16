package com.mibanco.dto.mapeador;

import com.mibanco.dto.TarjetaDTO;
import com.mibanco.modelo.Tarjeta;
import com.mibanco.modelo.Cliente;
import com.mibanco.dto.ClienteDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de Mapper para Tarjeta utilizando enfoque funcional
 */
public class TarjetaMapeador implements Mapeador<Tarjeta, TarjetaDTO> {
    private final Mapeador<Cliente, ClienteDTO> clienteMapeador;

    public TarjetaMapeador(Mapeador<Cliente, ClienteDTO> clienteMapeador) {
        this.clienteMapeador = clienteMapeador;
    }
    /**
     * Convierte una tarjeta a TarjetaDTO
     * Implementación estrictamente funcional con Optional
     */
    @Override
    public Optional<TarjetaDTO> aDto(Optional<Tarjeta> tarjetaOpt) {
        return tarjetaOpt.map(tarjeta -> TarjetaDTO.builder()
            .numero(tarjeta.getNumero())
            .titular(tarjeta.getTitular() != null ? 
                clienteMapeador.aDto(Optional.of(tarjeta.getTitular())).orElse(null) : null)
            .numeroCuentaAsociada(tarjeta.getNumeroCuentaAsociada())
            .tipo(tarjeta.getTipo())
            .fechaExpiracion(tarjeta.getFechaExpiracion())
            .activa(tarjeta.isActiva())
            .build());
    }

    /**
     * Convierte un TarjetaDTO a Tarjeta
     * Implementación estrictamente funcional con Optional
     * Copia todos los campos del DTO, incluyendo el número si existe
     */
    @Override
    public Optional<Tarjeta> aEntidad(Optional<TarjetaDTO> dtoOpt) {
        return dtoOpt.map(dto -> Tarjeta.builder()
            .numero(dto.getNumero())
            .titular(dto.getTitular() != null ? 
                clienteMapeador.aEntidad(Optional.of(dto.getTitular())).orElse(null) : null)
            .numeroCuentaAsociada(dto.getNumeroCuentaAsociada())
            .tipo(dto.getTipo())
            .fechaExpiracion(dto.getFechaExpiracion())
            .activa(dto.isActiva())
            .build());
    }
   
    /**
     * Sobrecarga que acepta Tarjeta directamente
     * Para facilitar el uso en contextos donde no se trabaja con Optional
     */
    public Optional<TarjetaDTO> aDtoDirecto(Tarjeta tarjeta) {
        return aDto(Optional.ofNullable(tarjeta));
    }
    
    /**
     * Sobrecarga que acepta TarjetaDTO directamente
     * Para facilitar el uso en contextos donde no se trabaja con Optional
     */
    public Optional<Tarjeta> aEntidadDirecta(TarjetaDTO dto) {
        return aEntidad(Optional.ofNullable(dto));
    }
    
    /**
     * Convierte una lista de Tarjeta a lista de TarjetaDTO
     * Utilizando programación funcional con streams
     * @return Optional con la lista de DTOs o Optional.empty() si la entrada es nula
     */
    public Optional<List<TarjetaDTO>> aListaDto(Optional<List<Tarjeta>> tarjetas) {
        return tarjetas.map(list -> list.stream()
                        .map(tarjeta -> aDto(Optional.of(tarjeta)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Sobrecarga que acepta List<Tarjeta> directamente
     */
    public Optional<List<TarjetaDTO>> aListaDto(List<Tarjeta> tarjetas) {
        return aListaDto(Optional.ofNullable(tarjetas));
    }
    
    /**
     * Convierte una lista de TarjetaDTO a lista de Tarjeta
     * Utilizando programación funcional con streams
     * @return Optional con la lista de entidades o Optional.empty() si la entrada es nula
     */
    public Optional<List<Tarjeta>> aListaEntidad(Optional<List<TarjetaDTO>> dtos) {
        return dtos.map(list -> list.stream()
                        .map(dto -> aEntidad(Optional.of(dto)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Sobrecarga que acepta List<TarjetaDTO> directamente
     */
    public Optional<List<Tarjeta>> aListaEntidad(List<TarjetaDTO> dtos) {
        return aListaEntidad(Optional.ofNullable(dtos));
    }
} 