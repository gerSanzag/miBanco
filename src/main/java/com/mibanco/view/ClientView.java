package com.mibanco.view;

import java.util.Optional;
import java.util.Map;
import com.mibanco.dto.ClientDTO;

/**
 * Interface for client management view.
 * Handles user interaction for client operations through console input/output.
 * Uses functional programming approach with Optionals and lambdas.
 */
public interface ClientView {
    
    /**
     * Shows the main client management menu and handles user interaction.
     */
    void showClientMenu();
    
    /**
     * Shows the client update submenu and handles user interaction.
     */
    void showUpdateClientMenu();
    
    /**
     * Captures client data for creating a new client.
     * 
     * @return Map with client data ready for the service, or empty map if creation was cancelled
     */
    Map<String, String> createClient();
    
    /**
     * Searches for a client by ID or DNI.
     * 
     * @return true if client was found and displayed, false otherwise
     */
    boolean searchClient();
    
    /**
     * Updates client email.
     * 
     * @return true if email was updated successfully, false otherwise
     */
    boolean updateClientEmail();
    
    /**
     * Updates client phone.
     * 
     * @return true if phone was updated successfully, false otherwise
     */
    boolean updateClientPhone();
    
    /**
     * Updates client address.
     * 
     * @return true if address was updated successfully, false otherwise
     */
    boolean updateClientAddress();
    
    /**
     * Updates multiple client fields simultaneously.
     * 
     * @return true if fields were updated successfully, false otherwise
     */
    boolean updateClientMultipleFields();
    
    /**
     * Deletes a client.
     * 
     * @return true if client was deleted successfully, false otherwise
     */
    boolean deleteClient();
    
    /**
     * Restores a previously deleted client.
     * 
     * @return true if client was restored successfully, false otherwise
     */
    boolean restoreClient();
    
    /**
     * Lists all clients.
     * 
     * @return true if clients were displayed successfully, false otherwise
     */
    boolean listAllClients();
}
