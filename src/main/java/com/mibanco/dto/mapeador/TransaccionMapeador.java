package com.mibanco.dto.mapeador;

import com.mibanco.dto.TransaccionDTO;
import com.mibanco.modelo.Transaccion;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de Mapper para Transaccion utilizando enfoque funcional
 * Sin generación automática de IDs (manejada por el repositorio)
 */
public class TransaccionMapeador implements Mapeador<Transaccion, TransaccionDTO> {

    /**
     * Convierte una transacción a TransaccionDTO
     * Implementación estrictamente funcional con Optional
     */
    @Override
    public Optional<TransaccionDTO> aDto(Optional<Transaccion> transaccionOpt) {
        return transaccionOpt.map(transaccion -> TransaccionDTO.builder()
            .id(transaccion.getId())
            .numeroCuenta(transaccion.getNumeroCuenta())
            .numeroCuentaDestino(transaccion.getNumeroCuentaDestino())
            .tipo(transaccion.getTipo())
            .monto(transaccion.getMonto())
            .fecha(transaccion.getFecha())
            .descripcion(transaccion.getDescripcion())
            .build());
    }

    /**
     * Convierte un TransaccionDTO a Transaccion
     * Implementación estrictamente funcional con Optional
     * Sin generación automática de IDs (manejada por el repositorio)
     */
    @Override
    public Optional<Transaccion> aEntidad(Optional<TransaccionDTO> dtoOpt) {
        return dtoOpt.map(dto -> Transaccion.builder()
            .id(dto.getId())
            .numeroCuenta(dto.getNumeroCuenta())
            .numeroCuentaDestino(dto.getNumeroCuentaDestino())
            .tipo(dto.getTipo())
            .monto(dto.getMonto())
            .fecha(dto.getFecha())
            .descripcion(dto.getDescripcion())
            .build());
    }
   
    /**
     * Sobrecarga que acepta Transaccion directamente
     * Para facilitar el uso en contextos donde no se trabaja con Optional
     */
    public Optional<TransaccionDTO> aDtoDirecto(Transaccion transaccion) {
        return aDto(Optional.ofNullable(transaccion));
    }
    
    /**
     * Sobrecarga que acepta TransaccionDTO directamente
     * Para facilitar el uso en contextos donde no se trabaja con Optional
     */
    public Optional<Transaccion> aEntidadDirecta(TransaccionDTO dto) {
        return aEntidad(Optional.ofNullable(dto));
    }
    
    /**
     * Convierte una lista de Transaccion a lista de TransaccionDTO
     * Utilizando programación funcional con streams
     * @return Optional con la lista de DTOs o Optional.empty() si la entrada es nula
     */
    public Optional<List<TransaccionDTO>> aListaDto(Optional<List<Transaccion>> transacciones) {
        return transacciones.map(list -> list.stream()
                        .map(transaccion -> aDto(Optional.of(transaccion)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Sobrecarga que acepta List<Transaccion> directamente
     */
    public Optional<List<TransaccionDTO>> aListaDto(List<Transaccion> transacciones) {
        return aListaDto(Optional.ofNullable(transacciones));
    }
    
    /**
     * Convierte una lista de TransaccionDTO a lista de Transaccion
     * Utilizando programación funcional con streams
     * @return Optional con la lista de entidades o Optional.empty() si la entrada es nula
     */
    public Optional<List<Transaccion>> aListaEntidad(Optional<List<TransaccionDTO>> dtos) {
        return dtos.map(list -> list.stream()
                        .map(dto -> aEntidad(Optional.of(dto)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Sobrecarga que acepta List<TransaccionDTO> directamente
     */
    public Optional<List<Transaccion>> aListaEntidad(List<TransaccionDTO> dtos) {
        return aListaEntidad(Optional.ofNullable(dtos));
    }
} 