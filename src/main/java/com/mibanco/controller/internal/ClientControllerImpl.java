package com.mibanco.controller.internal;

import com.mibanco.controller.ClientController;
import com.mibanco.controller.util.ControllerValidationUtil;
import com.mibanco.controller.util.ControllerUpdateUtil;
import com.mibanco.dto.ClientDTO;
import com.mibanco.dto.mappers.ClientMapper;
import com.mibanco.model.Client;
import com.mibanco.service.ClientService;
import com.mibanco.view.ClientView;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

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
        return ControllerUpdateUtil.updateEntityField(
            () -> clientView.updateClientEmail(),
            (id, email) -> clientService.updateClientEmail(id, Optional.of(email)),
            clientId -> clientService.getClientById(Optional.of(clientId)),
            clientDto -> this.clientMapper.toEntityDirect(clientDto),
            clientEntity -> clientView.displayClient(clientEntity)
        );
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
        return ControllerUpdateUtil.updateEntityField(
            () -> clientView.updateClientPhone(),
            (id, phone) -> clientService.updateClientPhone(id, Optional.of(phone)),
            clientId -> clientService.getClientById(Optional.of(clientId)),
            clientDto -> this.clientMapper.toEntityDirect(clientDto),
            clientEntity -> clientView.displayClient(clientEntity)
        );
    }
    
    @Override
    public boolean updateClientAddress() {
        return ControllerUpdateUtil.updateEntityField(
            () -> clientView.updateClientAddress(),
            (id, address) -> clientService.updateClientAddress(id, Optional.of(address)),
            clientId -> clientService.getClientById(Optional.of(clientId)),
            clientDto -> this.clientMapper.toEntityDirect(clientDto),
            clientEntity -> clientView.displayClient(clientEntity)
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
        // Functional approach to menu navigation
        generateClientMenuLoop().run();
    }
    
    /**
     * Generates the client menu loop using functional approach.
     * Handles navigation between menu options and delegates to appropriate controller methods.
     * 
     * @return Runnable representing the menu loop
     */
    private Runnable generateClientMenuLoop() {
        return () -> {
            AtomicBoolean running = new AtomicBoolean(true);
            
            while (running.get()) {
                clientView.showClientMenu()
                    .ifPresentOrElse(
                        option -> processMainClientMenuOption(option, running),
                        () -> running.set(false) // User wants to exit
                    );
            }
        };
    }
    
    /**
     * Processes the selected main client menu option using functional approach.
     * 
     * @param option the selected option
     * @param running the running state controller
     */
    private void processMainClientMenuOption(String option, AtomicBoolean running) {
        // Functional approach to option processing using Map of actions
        Map<String, Runnable> menuActions = Map.of(
            "1", this::createClient,
            "2", this::searchClient,
            "3", this::showUpdateClientMenu,
            "4", this::deleteClient,
            "5", this::restoreClient,
            "6", this::listAllClients
        );
        
        Optional.of(option)
            .map(menuActions::get)
            .ifPresentOrElse(
                Runnable::run,
                () -> clientView.showMessage("Opción inválida. Por favor, elige una opción del 1 al 7.")
            );
    }
    
    @Override
    public void showUpdateClientMenu() {
        // Functional approach to update menu navigation
        generateUpdateClientMenuLoop().run();
    }
    
    /**
     * Generates the update client menu loop using functional approach.
     * Handles navigation between update menu options and delegates to appropriate controller methods.
     * 
     * @return Runnable representing the update menu loop
     */
    private Runnable generateUpdateClientMenuLoop() {
        return () -> {
            AtomicBoolean running = new AtomicBoolean(true);
            
            while (running.get()) {
                clientView.showUpdateClientMenu()
                    .ifPresentOrElse(
                        option -> processUpdateClientMenuOption(option, running),
                        () -> running.set(false) // User wants to exit
                    );
            }
        };
    }
    
    /**
     * Processes the selected update client menu option using functional approach.
     * 
     * @param option the selected option
     * @param running the running state controller
     */
    private void processUpdateClientMenuOption(String option, AtomicBoolean running) {
        // Functional approach to option processing using Map of actions
        Map<String, Runnable> updateMenuActions = Map.of(
            "1", this::updateClientEmail,
            "2", this::updateClientPhone,
            "3", this::updateClientAddress,
            "4", this::updateClientMultipleFields
        );
        
        Optional.of(option)
            .map(updateMenuActions::get)
            .ifPresentOrElse(
                Runnable::run,
                () -> clientView.showMessage("Opción inválida. Por favor, elige una opción del 1 al 5.")
            );
    }
}
