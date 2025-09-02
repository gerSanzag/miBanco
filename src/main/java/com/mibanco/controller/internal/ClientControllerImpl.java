package com.mibanco.controller.internal;

import com.mibanco.controller.ClientController;
import com.mibanco.controller.util.ControllerValidationUtil;
import com.mibanco.dto.ClientDTO;
import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Client;
import com.mibanco.service.ClientService;
import com.mibanco.view.ClientView;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.function.BiFunction;

/**
 * Implementation of ClientController
 * Hardcoded methods for now to see the structure
 */
public class ClientControllerImpl implements ClientController {
    
    private final ClientService clientService;
    private final ClientView clientView;
    private final ClientMapper clientMapper;
    
    public ClientControllerImpl(ClientService clientService, ClientView clientView) {
        this.clientService = clientService;
        this.clientView = clientView;
        this.clientMapper = new ClientMapper();
    }
    
    @Override
    public boolean createClient() {
        return clientView.captureDataClient()
            .filter(data -> ControllerValidationUtil.hasRequiredFields(data, Client.class))
            .flatMap(clientService::createClientDto)
            .isPresent();
    }
    
    @Override
    public boolean searchClient() {
        return clientView.searchClient()
            .map(searchCriteria -> {
                String searchType = searchCriteria.get("searchType");
                String searchValue = searchCriteria.get("searchValue");
                
                Optional<ClientDTO> foundClient = switch (searchType) {
                    case "id" -> {
                        try {
                            Long id = Long.parseLong(searchValue);
                            yield clientService.getClientById(Optional.of(id));
                        } catch (NumberFormatException e) {
                            yield Optional.empty();
                        }
                    }
                    case "dni" -> clientService.getClientByDni(Optional.of(searchValue));
                    default -> Optional.empty();
                };
                
                // Si se encontró el cliente, mapearlo a entidad y mostrarlo
                foundClient.ifPresent(clientDto -> {
                    Client clientEntity = clientMapper.toEntityDirect(clientDto).orElse(null);
                    if (clientEntity != null) {
                        clientView.displayClient(clientEntity);
                    }
                });
                
                return foundClient.isPresent();
            })
            .orElse(false);
    }
    
    @Override
    public boolean updateClientEmail() {
        return updateClientField(
            () -> clientView.updateClientEmail(),
            (id, email) -> clientService.updateClientEmail(id, email)
        );
    }
    
    /**
     * Generic method for updating client fields.
     * Handles the common logic for all field updates (email, phone, address, etc.)
     * 
     * @param viewMethod Supplier that captures update data from view
     * @param serviceMethod Function that calls the appropriate service method
     * @return true if update was successful, false otherwise
     */
    private boolean updateClientField(
            Supplier<Optional<Map<String, String>>> viewMethod,
            BiFunction<Long, Optional<String>, Optional<ClientDTO>> serviceMethod
    ) {
        return viewMethod.get()
            .map(updateData -> {
                String idStr = updateData.get("id");
                String newValue = updateData.get("newValue");
                
                try {
                    Long id = Long.parseLong(idStr);
                    
                    // Call private method to process client update
                    return Optional.of(processClientUpdate(id, newValue))
                        .filter(confirmed -> confirmed)
                        .flatMap(confirmed -> serviceMethod.apply(id, Optional.of(newValue)))
                        .map(clientDto -> {
                            Client clientEntity = clientMapper.toEntityDirect(clientDto).orElse(null);
                            if (clientEntity != null) {
                                clientView.displayClient(clientEntity);
                            }
                            return true;
                        })
                        .orElse(false);
                    
                } catch (NumberFormatException e) {
                    return false;
                }
            })
            .orElse(false);
    }
    
    /**
     * Private method that processes client update.
     * Searches for client by ID and asks for confirmation before proceeding.
     * 
     * @param id ID of the client to update
     * @param newValue new value for the client field
     * @return true if user confirms, false otherwise
     */
    private boolean processClientUpdate(Long id, String newValue) {
        return clientService.getClientById(Optional.of(id))
            .flatMap(clientDto -> clientMapper.toEntityDirect(clientDto))
            .map(clientEntity -> clientView.displayClient(clientEntity))
            .orElse(false);
    }
    
    @Override
    public boolean updateClientPhone() {
        return updateClientField(
            () -> clientView.updateClientPhone(),
            (id, phone) -> clientService.updateClientPhone(id, phone)
        );
    }
    
    @Override
    public boolean updateClientAddress() {
        return updateClientField(
            () -> clientView.updateClientAddress(),
            (id, address) -> clientService.updateClientAddress(id, address)
        );
    }
    
    @Override
    public boolean updateClientMultipleFields() {
        return clientView.updateClientMultipleFields()
            .map(updates -> {
                try {
                    // Extract client ID from the updates map
                    Object idObj = updates.get("id");
                    if (idObj == null) {
                        return false;
                    }
                    
                    Long id;
                    if (idObj instanceof Long) {
                        id = (Long) idObj;
                    } else if (idObj instanceof String) {
                        id = Long.parseLong((String) idObj);
                    } else {
                        return false;
                    }
                    
                    // Remove ID from updates before sending to service
                    Map<String, Object> serviceUpdates = new HashMap<>(updates);
                    serviceUpdates.remove("id");
                    
                    return clientService.updateMultipleFields(id, serviceUpdates)
                        .map(clientDto -> {
                            Client clientEntity = clientMapper.toEntityDirect(clientDto).orElse(null);
                            if (clientEntity != null) {
                                clientView.displayClient(clientEntity);
                            }
                            return true;
                        })
                        .orElse(false);
                    
                } catch (Exception e) {
                    return false;
                }
            })
            .orElse(false);
    }
    
    @Override
    public boolean deleteClient() {
        // TODO: Implement real logic
        // 1. Get client ID from view
        // 2. Call clientService.deleteClient()
        // 3. Handle result
        return true; // Hardcoded for now
    }
    
    @Override
    public boolean restoreClient() {
        // TODO: Implement real logic
        // 1. Get client ID from view
        // 2. Call clientService.restoreClient()
        // 3. Handle result
        return true; // Hardcoded for now
    }
    
    @Override
    public boolean listAllClients() {
        // TODO: Implement real logic
        // 1. Call clientService.getAllClients()
        // 2. Display results through view
        return true; // Hardcoded for now
    }
    
    @Override
    public void showClientMenu() {
        // TODO: Implement real logic
        // 1. Call clientView.showClientMenu()
        // 2. Handle menu navigation
    }
    
    @Override
    public void showUpdateClientMenu() {
        // TODO: Implement real logic
        // 1. Call clientView.showUpdateClientMenu()
        // 2. Handle submenu navigation
    }
}
