package com.mibanco.dto.mapper;

import com.mibanco.dto.ClienteDTO;
import com.mibanco.model.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación de Mapper para Cliente utilizando enfoque funcional
 */
public class ClienteMapper implements Mapper<Cliente, ClienteDTO> {

    /**
     * Convierte un Cliente a ClienteDTO
     * Implementación estrictamente funcional con Optional
     */
    @Override
    public Optional<ClienteDTO> aDto(Optional<Cliente> clienteOpt) {
        return clienteOpt.map(cliente -> ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .dni(cliente.getDni())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .fechaNacimiento(cliente.getFechaNacimiento())
                .direccion(cliente.getDireccion())
                .build());
    }

    /**
     * Convierte un ClienteDTO a Cliente
     * Implementación estrictamente funcional con Optional
     */
    @Override
    public Optional<Cliente> aEntidad(Optional<ClienteDTO> dtoOpt) {
        return dtoOpt.map(dto -> Cliente.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .fechaNacimiento(dto.getFechaNacimiento())
                .direccion(dto.getDireccion())
                .build());
    }
   
    /**
     * Sobrecarga que acepta Cliente directamente
     * Para facilitar el uso en contextos donde no se trabaja con Optional
     */
    public Optional<ClienteDTO> aDtoDirecto(Cliente cliente) {
        return aDto(Optional.ofNullable(cliente));
    }
    
    /**
     * Sobrecarga que acepta ClienteDTO directamente
     * Para facilitar el uso en contextos donde no se trabaja con Optional
     */
    public Optional<Cliente> aEntidadDirecta(ClienteDTO dto) {
        return aEntidad(Optional.ofNullable(dto));
    }
    
    /**
     * Convierte una lista de Cliente a lista de ClienteDTO
     * Utilizando programación funcional con streams
     * @return Optional con la lista de DTOs o Optional.empty() si la entrada es nula
     */
    public Optional<List<ClienteDTO>> aListaDto(Optional<List<Cliente>> clientes) {
        return clientes.map(list -> list.stream()
                        .map(cliente -> aDto(Optional.of(cliente)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Sobrecarga que acepta List<Cliente> directamente
     */
    public Optional<List<ClienteDTO>> aListaDto(List<Cliente> clientes) {
        return aListaDto(Optional.ofNullable(clientes));
    }
    
    /**
     * Convierte una lista de ClienteDTO a lista de Cliente
     * Utilizando programación funcional con streams
     * @return Optional con la lista de entidades o Optional.empty() si la entrada es nula
     */
    public Optional<List<Cliente>> aListaEntidad(Optional<List<ClienteDTO>> dtos) {
        return dtos.map(list -> list.stream()
                        .map(dto -> aEntidad(Optional.of(dto)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Sobrecarga que acepta List<ClienteDTO> directamente
     */
    public Optional<List<Cliente>> aListaEntidad(List<ClienteDTO> dtos) {
        return aListaEntidad(Optional.ofNullable(dtos));
    }
} 