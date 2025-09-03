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
                    
                                    // Call private method to show client and get confirmation
                return Optional.of(showClientAndConfirm(id, "UPDATE"))
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
     * Generic method that shows client information and asks for user confirmation.
     * Can be used for any operation that requires showing client data and getting user approval.
     * 
     * @param id ID of the client to show and confirm
     * @param operationDescription description of the operation for context (e.g., "UPDATE", "DELETE")
     * @return true if user confirms the operation, false otherwise
     */
    private boolean showClientAndConfirm(Long id, String operationDescription) {
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
        return clientView.deleteClient()
            .map(idStr -> {
                try {
                    Long id = Long.parseLong(idStr);
                    
                    // Show client and ask for confirmation (using existing pattern)
                    boolean confirmed = showClientAndConfirm(id, "DELETE");
                    
                    if (confirmed) {
                        // Call service to delete client
                        boolean deleted = clientService.deleteClient(Optional.of(id));
                        
                        if (deleted) {
                            clientView.showMessage("Cliente eliminado exitosamente.");
                        } else {
                            clientView.showMessage("Error: No se pudo eliminar el cliente.");
                        }
                        
                        return deleted;
                    } else {
                        clientView.showMessage("Eliminación cancelada por el usuario.");
                        return false;
                    }
                    
                } catch (NumberFormatException e) {
                    clientView.showMessage("Error: ID de cliente inválido.");
                    return false;
                }
            })
            .orElse(false);
    }
    
    @Override
    public boolean restoreClient() {
        return clientView.restoreClient()
            .map(idStr -> {
                try {
                    Long id = Long.parseLong(idStr);
                    
                    // Show client and ask for confirmation (using existing pattern)
                    boolean confirmed = showClientAndConfirm(id, "RESTORE");
                    
                    if (confirmed) {
                        // Call service to restore client
                        Optional<ClientDTO> restoredClient = clientService.restoreClient(Optional.of(id));
                        
                        if (restoredClient.isPresent()) {
                            clientView.showMessage("Cliente restaurado exitosamente.");
                            // Show restored client information
                            Client clientEntity = clientMapper.toEntityDirect(restoredClient.get()).orElse(null);
                            if (clientEntity != null) {
                                clientView.displayClient(clientEntity);
                            }
                            return true;
                        } else {
                            clientView.showMessage("Error: No se pudo restaurar el cliente.");
                            return false;
                        }
                    } else {
                        clientView.showMessage("Restauración cancelada por el usuario.");
                        return false;
                    }
                    
                } catch (NumberFormatException e) {
                    clientView.showMessage("Error: ID de cliente inválido.");
                    return false;
                }
            })
            .orElse(false);
    }
    
    @Override
    public boolean listAllClients() {
        return clientService.getAllClients()
            .map(clientList -> {
                // Call view to display the client list
                clientView.listAllClients(clientList);
                return true;
            })
            .orElse(false);
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
