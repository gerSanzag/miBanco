package com.mibanco.controller.internal;

import com.mibanco.controller.ClientController;
import com.mibanco.controller.util.ControllerValidationUtil;
import com.mibanco.dto.ClientDTO;
import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Client;
import com.mibanco.service.ClientService;
import com.mibanco.view.ClientView;

import java.util.Map;
import java.util.Optional;

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
        return clientView.updateClientEmail()
            .map(updateData -> {
                String idStr = updateData.get("id");
                String newEmail = updateData.get("newEmail");
                
                try {
                    Long id = Long.parseLong(idStr);
                    
                    // Llamar método privado para procesar la actualización
                    return Optional.of(processClientUpdate(id, newEmail))
                        .filter(confirmed -> confirmed)
                        .flatMap(confirmed -> clientService.updateClientEmail(id, Optional.of(newEmail)))
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
     * Método privado que procesa la actualización del cliente.
     * Busca el cliente por ID y pide confirmación antes de proceder.
     * 
     * @param id ID del cliente a actualizar
     * @param newEmail nuevo email para el cliente
     * @return true si el usuario confirma, false en caso contrario
     */
    private boolean processClientUpdate(Long id, String newEmail) {
        return clientService.getClientById(Optional.of(id))
            .flatMap(clientDto -> clientMapper.toEntityDirect(clientDto))
            .map(clientEntity -> clientView.displayClient(clientEntity))
            .orElse(false);
    }
    
    @Override
    public boolean updateClientPhone() {
        // TODO: Implement real logic
        // 1. Get client ID and new phone from view
        // 2. Call clientService.updateClientPhone()
        // 3. Handle result
        return true; // Hardcoded for now
    }
    
    @Override
    public boolean updateClientAddress() {
        // TODO: Implement real logic
        // 1. Get client ID and new address from view
        // 2. Call clientService.updateClientAddress()
        // 3. Handle result
        return true; // Hardcoded for now
    }
    
    @Override
    public boolean updateClientMultipleFields() {
        // TODO: Implement real logic
        // 1. Get client ID and multiple fields from view
        // 2. Call clientService.updateMultipleFields()
        // 3. Handle result
        return true; // Hardcoded for now
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
