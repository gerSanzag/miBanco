package com.mibanco.controller;

import com.mibanco.dto.ClientDTO;
import com.mibanco.service.ClientService;
import com.mibanco.view.ClientView;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller interface for Client operations
 * Acts as a bridge between ClientView and ClientService
 */
public interface ClientController {
    
    /**
     * Creates a new client using data from the view
     * @return true if client was created successfully, false otherwise
     */
    boolean createClient();
    
    /**
     * Searches for a client by ID or DNI
     * @return true if client was found and displayed, false otherwise
     */
    boolean searchClient();
    
    /**
     * Updates client's email
     * @return true if email was updated successfully, false otherwise
     */
    boolean updateClientEmail();
    
    /**
     * Updates client's phone
     * @return true if phone was updated successfully, false otherwise
     */
    boolean updateClientPhone();
    
    /**
     * Updates client's address
     * @return true if address was updated successfully, false otherwise
     */
    boolean updateClientAddress();
    
    /**
     * Updates multiple client fields at once
     * @return true if fields were updated successfully, false otherwise
     */
    boolean updateClientMultipleFields();
    
    /**
     * Deletes a client by ID
     * @return true if client was deleted successfully, false otherwise
     */
    boolean deleteClient();
    
    /**
     * Restores a previously deleted client
     * @return true if client was restored successfully, false otherwise
     */
    boolean restoreClient();
    
    /**
     * Lists all clients
     * @return true if clients were displayed successfully, false otherwise
     */
    boolean listAllClients();
    
    /**
     * Shows the main client menu
     */
    void showClientMenu();
    
    /**
     * Shows the update client submenu
     */
    void showUpdateClientMenu();
}
