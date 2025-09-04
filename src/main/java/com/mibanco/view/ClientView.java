package com.mibanco.view;

import java.util.Optional;
import java.util.Map;
import java.util.List;
import com.mibanco.dto.ClientDTO;
import com.mibanco.model.Client;

/**
 * Interface for client management view.
 * Handles user interaction for client operations through console input/output.
 * Uses functional programming approach with Optionals and lambdas.
 */
public interface ClientView {
    
    /**
     * Shows the main client management menu.
     * Returns the selected option for the controller to handle.
     * 
     * @return Optional with the selected menu option, or empty if user wants to exit
     */
    Optional<String> showClientMenu();
    
    /**
     * Shows the client update submenu.
     * Returns the selected option for the controller to handle.
     * 
     * @return Optional with the selected menu option, or empty if user wants to exit
     */
    Optional<String> showUpdateClientMenu();
    
    /**
     * Captures client data for creating a new client.
     * 
     * @return Optional with Map containing client data ready for the service, or empty if creation was cancelled
     */
    Optional<Map<String, String>> captureDataClient();
    
    /**
     * Searches for a client by ID or DNI.
     * 
     * @return Optional with search criteria (ID or DNI), or empty if search was cancelled
     */
    Optional<Map<String, String>> searchClient();
    
    /**
     * Updates client email.
     * 
     * @return Optional with Map containing update data (id and newEmail), or empty if update was cancelled
     */
    Optional<Map<String, String>> updateClientEmail();
    
    /**
     * Updates client phone.
     * 
     * @return Optional with Map containing update data (id and newPhone), or empty if update was cancelled
     */
    Optional<Map<String, String>> updateClientPhone();
    
    /**
     * Updates client address.
     * 
     * @return Optional with Map containing update data (id and newValue), or empty if update was cancelled
     */
    Optional<Map<String, String>> updateClientAddress();
    
    /**
     * Updates multiple client fields simultaneously.
     * 
     * @return Optional with Map containing update data for multiple fields, or empty if update was cancelled
     */
    Optional<Map<String, Object>> updateClientMultipleFields();
    
    /**
     * Deletes a client.
     * 
     * @return Optional with client ID as string, or empty if deletion was cancelled
     */
    Optional<String> deleteClient();
    
    /**
     * Restores a previously deleted client.
     * 
     * @return Optional with client ID as string, or empty if restoration was cancelled
     */
    Optional<String> restoreClient();
    
    /**
     * Lists all clients.
     * 
     * @param clients list of client DTOs to display
     */
    void listAllClients(List<ClientDTO> clients);
    
    /**
     * Displays client information and asks for confirmation.
     * 
     * @param client the client entity to display
     * @return true if user confirms the information is correct, false otherwise
     */
    boolean displayClient(Client client);

    /**
     * Shows a message to the user
     * @param message the message to display
     */
    void showMessage(String message);
}
