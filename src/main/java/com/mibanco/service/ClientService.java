package com.mibanco.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mibanco.dto.ClientDTO;

/**
 * Service interface for Client operations
 */
public interface ClientService {
    
    /**
     * Creates a new client from raw data.
     * The service is responsible for creating the DTO internally.
     * 
     * @param clientData Map with raw client data
     * @return Optional with the created client DTO
     */
    Optional<ClientDTO> createClientDto(Map<String, String> clientData);
    

    
    /**
     * Updates complete client information using the generic updateMultipleFields method
     * @param id ID of the client to update
     * @param updates Map with raw update data
     * @return Optional with the updated client DTO
     */
    Optional<ClientDTO> updateMultipleFields(Long id, Map<String, Object> updates);
    
    /**
     * Gets a client by its ID
     * @param id Optional with client ID
     * @return Optional with client DTO
     */
    Optional<ClientDTO> getClientById(Optional<Long> id);
    
    /**
     * Gets a client by its DNI
     * @param dni Optional with client DNI
     * @return Optional with client DTO
     */
    Optional<ClientDTO> getClientByDni(Optional<String> dni);
    
    /**
     * Gets all clients
     * @return Optional with list of client DTOs
     */
    Optional<List<ClientDTO>> getAllClients();
    
    /**
     * Updates a client's email
     * @param id Client ID
     * @param newEmail Optional with new email
     * @return Optional with updated client DTO
     */
    Optional<ClientDTO> updateClientEmail(Long id, Optional<String> newEmail);
    
    /**
     * Updates a client's phone
     * @param id Client ID
     * @param newPhone Optional with new phone
     * @return Optional with updated client DTO
     */
    Optional<ClientDTO> updateClientPhone(Long id, Optional<String> newPhone);
    
    /**
     * Updates a client's address
     * @param id Client ID
     * @param newAddress Optional with new address
     * @return Optional with updated client DTO
     */
    Optional<ClientDTO> updateClientAddress(Long id, Optional<String> newAddress);
    
    /**
     * Deletes a client by its ID
     * @param id Optional with ID of client to delete
     * @return true if deleted successfully, false if not
     */
    boolean deleteClient(Optional<Long> id);

    /**
     * Restores a previously deleted client
     * @param id Optional with ID of client to restore
     * @return Optional with restored client DTO
     */
    Optional<ClientDTO> restoreClient(Optional<Long> id);

    /**
     * Gets the total number of clients
     * @return Number of clients
     */
    long countClients();

    /**
     * Sets the current user for audit operations
     * @param user Current user
     */
    void setCurrentUser(String user);

    /**
     * Generic method to update a specific field of a client
     * @param id ID of the client to update
     * @param newValue Optional with the new value
     * @param currentValue Function to get the current value
     * @param updater Function to update the value
     * @return Optional with the updated client DTO
     */
    <V> Optional<ClientDTO> updateField(
            Long id,
            Optional<V> newValue,
            java.util.function.Function<ClientDTO, V> currentValue,
            java.util.function.BiFunction<ClientDTO, V, ClientDTO> updater);



    /**
     * Gets the list of deleted clients
     * @return List of deleted client DTOs
     */
    java.util.List<ClientDTO> getDeleted();
} 