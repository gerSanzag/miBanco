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
     * Implementación funcional
     */
    @Override
    public ClienteDTO toDto(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        
        return ClienteDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .dni(cliente.getDni())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .fechaNacimiento(cliente.getFechaNacimiento())
                .direccion(cliente.getDireccion())
                .build();
    }

    /**
     * Convierte un ClienteDTO a Cliente
     * Implementación funcional
     */
    @Override
    public Cliente toEntity(ClienteDTO dto) {
        if (dto == null) {
            return null;
        }
        
        return Cliente.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .fechaNacimiento(dto.getFechaNacimiento())
                .direccion(dto.getDireccion())
                .build();
    }
    
    /**
     * Convierte una lista de Cliente a lista de ClienteDTO
     * Utilizando programación funcional con streams
     */
    public List<ClienteDTO> toDtoList(List<Cliente> clientes) {
        return Optional.ofNullable(clientes)
                .map(list -> list.stream()
                        .map(this::toDto)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }
    
    /**
     * Convierte una lista de ClienteDTO a lista de Cliente
     * Utilizando programación funcional con streams
     */
    public List<Cliente> toEntityList(List<ClienteDTO> dtos) {
        return Optional.ofNullable(dtos)
                .map(list -> list.stream()
                        .map(this::toEntity)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }
} 