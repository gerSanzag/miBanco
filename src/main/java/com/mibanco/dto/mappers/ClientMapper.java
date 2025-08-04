package com.mibanco.dto.mappers;

import com.mibanco.dto.ClientDTO;
import com.mibanco.model.Client;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper implementation for Client using functional approach
 * No automatic ID generation (handled by the repository)
 */
public class ClientMapper implements Mapper<Client, ClientDTO> {

    /**
     * Converts a Client to ClientDTO
     * Strictly functional implementation with Optional
     */
    @Override
    public Optional<ClientDTO> toDto(Optional<Client> clientOpt) {
        return clientOpt.map(client -> ClientDTO.builder()
                .id(client.getId()) // id
                .firstName(client.getFirstName()) // nombre
                .lastName(client.getLastName()) // apellido
                .dni(client.getDni())
                .email(client.getEmail())
                .phone(client.getPhone()) // telefono
                .birthDate(client.getBirthDate()) // fechaNacimiento
                .address(client.getAddress()) // direccion
                .build());
    }

    /**
     * Converts a ClientDTO to Client
     * Strictly functional implementation with Optional
     * Copies all fields from the DTO, including ID if present
     */
    @Override
    public Optional<Client> toEntity(Optional<ClientDTO> dtoOpt) {
        return dtoOpt.map(dto -> Client.builder()
                .id(dto.getId()) // id
                .firstName(dto.getFirstName()) // nombre
                .lastName(dto.getLastName()) // apellido
                .dni(dto.getDni())
                .email(dto.getEmail())
                .phone(dto.getPhone()) // telefono
                .birthDate(dto.getBirthDate()) // fechaNacimiento
                .address(dto.getAddress()) // direccion
                .build());
    }
   
    /**
     * Overload that accepts Client directly
     * For convenience in contexts not using Optional
     */
    public Optional<ClientDTO> toDtoDirect(Client client) {
        return toDto(Optional.ofNullable(client));
    }
    
    /**
     * Overload that accepts ClientDTO directly
     * For convenience in contexts not using Optional
     */
    public Optional<Client> toEntityDirect(ClientDTO dto) {
        return toEntity(Optional.ofNullable(dto));
    }
    
    /**
     * Converts a list of Client to a list of ClientDTO
     * Using functional programming with streams
     * @return Optional with the list of DTOs or Optional.empty() if input is null
     */
    public Optional<List<ClientDTO>> toDtoList(Optional<List<Client>> clients) {
        return clients.map(list -> list.stream()
                        .map(client -> toDto(Optional.of(client)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Overload that accepts List<Client> directly
     */
    public Optional<List<ClientDTO>> toDtoList(List<Client> clients) {
        return toDtoList(Optional.ofNullable(clients));
    }
    
    /**
     * Converts a list of ClientDTO to a list of Client
     * Using functional programming with streams
     * @return Optional with the list of entities or Optional.empty() if input is null
     */
    public Optional<List<Client>> toEntityList(Optional<List<ClientDTO>> dtos) {
        return dtos.map(list -> list.stream()
                        .map(dto -> toEntity(Optional.of(dto)).orElse(null))
                        .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList()));
    }
    
    /**
     * Overload that accepts List<ClientDTO> directly
     */
    public Optional<List<Client>> toEntityList(List<ClientDTO> dtos) {
        return toEntityList(Optional.ofNullable(dtos));
    }
} 