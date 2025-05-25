package com.mibanco.dto.mapper;

import com.mibanco.dto.CuentaDTO;
import com.mibanco.model.Cuenta;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper para transformar entre Cuenta (entidad) y CuentaDTO
 * Utilizando enfoque funcional con Optional
 */
public class CuentaMapper implements Mapper<Cuenta, CuentaDTO> {

    private final ClienteMapper clienteMapper;

    /**
     * Constructor para inyección de dependencias
     * @param clienteMapper Mapper para convertir Cliente/ClienteDTO
     */
    public CuentaMapper(ClienteMapper clienteMapper) {
        this.clienteMapper = clienteMapper;
    }

    /**
     * Convierte una entidad Cuenta a DTO
     * @param entidad Optional con la entidad a convertir
     * @return Optional con el DTO convertido
     */
    @Override
    public Optional<CuentaDTO> aDto(Optional<Cuenta> entidad) {
        return entidad.map(cuenta -> 
            CuentaDTO.builder()
                .numeroCuenta(cuenta.getNumeroCuenta())
                .titular(clienteMapper.aDtoDirecto(cuenta.getTitular()).orElse(null))
                .tipo(cuenta.getTipo())
                .saldo(cuenta.getSaldo())
                .fechaCreacion(cuenta.getFechaCreacion())
                .activa(cuenta.isActiva())
                .build()
        );
    }

    /**
     * Convierte directamente una entidad Cuenta a DTO sin envolver en Optional
     * @param cuenta Entidad a convertir
     * @return Optional con el DTO convertido
     */
    public Optional<CuentaDTO> aDtoDirecto(Cuenta cuenta) {
        return aDto(Optional.ofNullable(cuenta));
    }

    /**
     * Convierte un DTO a entidad Cuenta
     * @param dto Optional con el DTO a convertir
     * @return Optional con la entidad convertida
     */
    @Override
    public Optional<Cuenta> aEntidad(Optional<CuentaDTO> dto) {
        return dto.map(cuentaDTO -> 
            Cuenta.builder()
                .numeroCuenta(cuentaDTO.getNumeroCuenta())
                .titular(clienteMapper.aEntidadDirecta(cuentaDTO.getTitular()).orElse(null))
                .tipo(cuentaDTO.getTipo())
                .saldo(cuentaDTO.getSaldo())
                .fechaCreacion(cuentaDTO.getFechaCreacion())
                .activa(cuentaDTO.isActiva())
                .build()
        );
    }

    /**
     * Convierte directamente un DTO a entidad Cuenta sin envolver en Optional
     * @param cuentaDTO DTO a convertir
     * @return Optional con la entidad convertida
     */
    public Optional<Cuenta> aEntidadDirecta(CuentaDTO cuentaDTO) {
        return aEntidad(Optional.ofNullable(cuentaDTO));
    }

    /**
     * Convierte una lista de entidades a lista de DTOs
     * @param entidades Optional con la lista de entidades
     * @return Optional con la lista de DTOs
     */
    public Optional<List<CuentaDTO>> aListaDto(Optional<List<Cuenta>> entidades) {
        return entidades.map(lista -> 
            lista.stream()
                .map(cuenta -> aDtoDirecto(cuenta).orElse(null))
                .filter(dto -> dto != null)
                .collect(Collectors.toList())
        );
    }
    
    /**
     * Sobrecarga que acepta List<Cuenta> directamente
     */
    public Optional<List<CuentaDTO>> aListaDto(List<Cuenta> cuentas) {
        return aListaDto(Optional.ofNullable(cuentas));
    }
    
    /**
     * Convierte una lista de DTOs a lista de entidades
     * @param dtos Optional con la lista de DTOs
     * @return Optional con la lista de entidades
     */
    public Optional<List<Cuenta>> aListaEntidad(Optional<List<CuentaDTO>> dtos) {
        return dtos.map(lista -> 
            lista.stream()
                .map(dto -> aEntidadDirecta(dto).orElse(null))
                .filter(entidad -> entidad != null)
                .collect(Collectors.toList())
        );
    }
    
    /**
     * Sobrecarga que acepta List<CuentaDTO> directamente
     */
    public Optional<List<Cuenta>> aListaEntidad(List<CuentaDTO> dtos) {
        return aListaEntidad(Optional.ofNullable(dtos));
    }
} 